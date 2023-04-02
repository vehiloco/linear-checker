package org.checkerframework.checker.linear;

import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.Tree;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import org.checkerframework.checker.linear.qual.*;
import org.checkerframework.dataflow.analysis.RegularTransferResult;
import org.checkerframework.dataflow.analysis.TransferInput;
import org.checkerframework.dataflow.analysis.TransferResult;
import org.checkerframework.dataflow.cfg.node.*;
import org.checkerframework.framework.flow.*;
import org.checkerframework.framework.util.Contract;
import org.checkerframework.framework.util.ContractsFromMethod;
import org.checkerframework.javacutil.AnnotationBuilder;
import org.checkerframework.javacutil.AnnotationUtils;

public class LinearTransfer extends CFAbstractTransfer<CFValue, CFStore, LinearTransfer> {

    private final LinearAnnotatedTypeFactory atypeFactory;
    private final ProcessingEnvironment env;

    /** The @{@link Disappear} annotation. */
    public LinearTransfer(LinearAnalysis analysis) {
        super(analysis, false);
        this.atypeFactory = (LinearAnnotatedTypeFactory) analysis.getTypeFactory();
        env = atypeFactory.getChecker().getProcessingEnvironment();
    }

    @Override
    public TransferResult<CFValue, CFStore> visitAssignment(
            AssignmentNode n, TransferInput<CFValue, CFStore> in) {

        // Process RHS
        CFStore store = in.getRegularStore();
        Node rhs = n.getExpression();
        // TODO: why don't we get value from the store first?
        // Check rhs type, if rhs is not array,field access or local variable, just return super
        // result.
        CFValue rhsValue = in.getValueOfSubNode(rhs);
        if (rhs instanceof FieldAccessNode && store.getValue((FieldAccessNode) rhs) != null) {
            rhsValue = store.getValue((FieldAccessNode) rhs);
        }
        if (rhsValue == null
                || !(rhs instanceof ArrayAccessNode
                        || rhs instanceof FieldAccessNode
                        || rhs instanceof LocalVariableNode)) {
            return super.visitAssignment(n, in);
        }
        Set<AnnotationMirror> rhsAnnotations = rhsValue.getAnnotations();

        // Process LHS
        Node lhs = n.getTarget();
        CFValue lhsValue = null;
        if (lhs instanceof LocalVariableNode) {
            lhsValue = store.getValue((LocalVariableNode) lhs);
        }
        if (lhs instanceof FieldAccessNode) {
            lhsValue = store.getValue((FieldAccessNode) lhs);
        }
        if (lhs instanceof ArrayAccessNode) {
            lhsValue = store.getValue((ArrayAccessNode) lhs);
        }
        AnnotationMirror lhsAnnotationMirror = null;
        if (lhsValue != null) {
            if (lhsValue.getAnnotations() != null) {
                for (AnnotationMirror lhsAM : lhsValue.getAnnotations()) {
                    lhsAnnotationMirror = lhsAM;
                }
            }
        }

        // determine the current value type of rhs, for rhs, we need to use the current value in the
        // store.
        AnnotationMirror rhsAnnotationMirror = null;
        boolean isRhsShared = false;
        boolean isRhsUnique = false;
        for (AnnotationMirror rhsAnnoMirror : rhsAnnotations) {
            rhsAnnotationMirror = rhsAnnoMirror;
            if (AnnotationUtils.areSameByName(atypeFactory.SHARED, rhsAnnoMirror)) {
                isRhsShared = true;
                break;
            } else if (AnnotationUtils.areSameByName(atypeFactory.UNIQUE, rhsAnnoMirror)) {
                isRhsUnique = true;
                break;
            } else if (AnnotationUtils.areSameByName(atypeFactory.DISAPPEAR, rhsAnnoMirror)) {
                return new RegularTransferResult(null, store);
            }
        }
        // Determine the lhs value type. As we don't update rhs annotations(i.e., from unique to
        // disappear), we can check this easily
        boolean isLhsShared = false;
        boolean isLhsUnique = false;
        if (atypeFactory.getAnnotationMirror(lhs.getTree(), Shared.class) != null) {
            isLhsShared = true;
        } else if (atypeFactory.getAnnotationMirror(lhs.getTree(), Unique.class) != null) {
            isLhsUnique = true;
        }

        /* ALGORITHMS START...................................
         * */
        // 1. RHS is Unique
        if (isRhsUnique) {
            // Set RHS node value to disappear if it is Unique before assignment
            store.updateForAssignment(rhs, buildNewValue(rhs.getTree(), Disappear.class));
            // Transfer states from rhs to lhs directly if lhs is also unique.
            if (isLhsUnique) {
                store.updateForAssignment(
                        lhs,
                        buildNewValue(
                                lhs.getTree(),
                                Unique.class,
                                AnnotationUtils.getElementValueArray(
                                        rhsAnnotationMirror, "value", String.class, true)));
            }
            if (isLhsShared) {
                if (lhsAnnotationMirror != null) {
                    // Merge
                    store.updateForAssignment(
                            lhs,
                            buildNewValue(
                                    lhs.getTree(),
                                    Shared.class,
                                    AnnotationUtils.getElementValueArray(
                                            lhsAnnotationMirror, "value", String.class, true),
                                    AnnotationUtils.getElementValueArray(
                                            rhsAnnotationMirror, "value", String.class, true)));
                } else {
                    // Just transfer
                    store.updateForAssignment(
                            lhs,
                            buildNewValue(
                                    lhs.getTree(),
                                    Shared.class,
                                    AnnotationUtils.getElementValueArray(
                                            rhsAnnotationMirror, "value", String.class, true)));
                }
            }
        }
        // 2. RHS is Shared
        if (isRhsShared && isRhsShared) {
            if (lhsAnnotationMirror != null && rhsAnnotationMirror != null) {
                store.updateForAssignment(
                        lhs,
                        buildNewValue(
                                lhs.getTree(),
                                Shared.class,
                                AnnotationUtils.getElementValueArray(
                                        lhsAnnotationMirror, "value", String.class, true),
                                AnnotationUtils.getElementValueArray(
                                        rhsAnnotationMirror, "value", String.class, true)));
            } else if (lhsAnnotationMirror == null && rhsAnnotationMirror != null) {
                store.updateForAssignment(
                        lhs,
                        buildNewValue(
                                lhs.getTree(),
                                Shared.class,
                                AnnotationUtils.getElementValueArray(
                                        rhsAnnotationMirror, "value", String.class, true)));
            } else if (lhsAnnotationMirror != null) {
                store.updateForAssignment(
                        lhs,
                        buildNewValue(
                                lhs.getTree(),
                                Shared.class,
                                AnnotationUtils.getElementValueArray(
                                        lhsAnnotationMirror, "value", String.class, true)));
            }
            return new RegularTransferResult(null, store);
        }
        TransferResult<CFValue, CFStore> superResult = super.visitAssignment(n, in);

        return superResult;
    }

    @Override
    protected void processCommonAssignment(
            TransferInput<CFValue, CFStore> in,
            Node lhs,
            Node rhs,
            CFStore store,
            CFValue rhsValue) {
        Tree lhsTree = lhs.getTree();
        AnnotationMirror lhsAnnotationMirror =
                atypeFactory.getAnnotationMirror(lhsTree, Shared.class);
        Tree rhsTree = rhs.getTree();
        AnnotationMirror rhsAnnotationMirror =
                atypeFactory.getAnnotationMirror(rhsTree, Unique.class);
        // Do not update in this situation.
        if (lhsAnnotationMirror != null && rhsAnnotationMirror != null) {
            return;
        }
        super.processCommonAssignment(in, lhs, rhs, store, rhsValue);
    }

    protected CFValue buildNewValue(
            Tree tree, Class<? extends Annotation> anno, List<String>... states) {
        AnnotationMirror newAnnoMirror;
        AnnotationBuilder builder = new AnnotationBuilder(env, anno);
        // process states
        if (states.length > 0) {
            List<String> states1 = states[0];
            if (states.length > 1) {
                states1.addAll(states[1]);
            }
            builder.setValue("value", states1);
        }
        newAnnoMirror = builder.build();
        Set<AnnotationMirror> newLhsSet = AnnotationUtils.createAnnotationSet();
        newLhsSet.add(newAnnoMirror);
        return analysis.createAbstractValue(
                newLhsSet, atypeFactory.getAnnotatedType(tree).getUnderlyingType());
    }

    @Override
    protected void processPostconditions(
            Node n, CFStore store, ExecutableElement executableElement, ExpressionTree tree) {
        // TODO: this is not good?
        if (executableElement.getSimpleName().toString().equals("super")
                || executableElement.getSimpleName().toString().equals("<init>")
                || executableElement.getSimpleName().toString().equals("invocation")) {
            super.processPostconditions(n, store, executableElement, tree);
            return;
        }
        if (atypeFactory.atomoton != null) {
            Map<String, Map<String, Map<String, String>>> operations =
                    (Map<String, Map<String, Map<String, String>>>)
                            atypeFactory.atomoton.get("operations");
            // Check operations are invalid
            if (!operations.containsKey(executableElement.getSimpleName().toString())) {
                atypeFactory.getChecker().reportError(n, "typestate.operation.invalid");
            }

            Map<String, Map<String, String>> transition =
                    operations.get(executableElement.getSimpleName().toString());
            // Whether preconditions hold

            // Whether the postconditions hold
            ContractsFromMethod contractsUtils = atypeFactory.getContractsFromMethod();
            Contract.Postcondition[] postconditions =
                    contractsUtils
                            .getPostconditions(executableElement)
                            .toArray(
                                    new Contract.Postcondition
                                            [contractsUtils
                                                    .getPostconditions(executableElement)
                                                    .size()]);
            for (int i = 0; i < postconditions.length; i++) {
                AnnotationMirror annotationMirror = postconditions[i].annotation;
                List<String> postStates =
                        AnnotationUtils.getElementValueArray(
                                annotationMirror, "value", String.class, true);
                if (!postStates.contains(transition.get("after"))) {
                    atypeFactory.getChecker().reportError(tree, "typestate.operation.invalid");
                }
            }
        }
        super.processPostconditions(n, store, executableElement, tree);
    }
}
