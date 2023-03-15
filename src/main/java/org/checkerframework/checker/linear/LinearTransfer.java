package org.checkerframework.checker.linear;

import com.sun.source.tree.Tree;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import org.checkerframework.checker.linear.qual.*;
import org.checkerframework.dataflow.analysis.RegularTransferResult;
import org.checkerframework.dataflow.analysis.TransferInput;
import org.checkerframework.dataflow.analysis.TransferResult;
import org.checkerframework.dataflow.cfg.node.*;
import org.checkerframework.dataflow.expression.ArrayAccess;
import org.checkerframework.dataflow.expression.FieldAccess;
import org.checkerframework.dataflow.expression.JavaExpression;
import org.checkerframework.dataflow.expression.LocalVariable;
import org.checkerframework.framework.flow.*;
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
        Node rhs = n.getExpression();
        Node lhs = n.getTarget();
        CFValue rhsValue = in.getValueOfSubNode(rhs);
        if (rhsValue == null) {
            return super.visitAssignment(n, in);
        }
        // Check rhs type, if rhs is not array,field access or local variable, just return super
        // result
        JavaExpression je = JavaExpression.fromNode(rhs);
        if (!(je instanceof ArrayAccess
                || je instanceof FieldAccess
                || je instanceof LocalVariable)) {
            return super.visitAssignment(n, in);
        }

        CFValue lhsValue = null;
        if (n.getTarget() instanceof LocalVariableNode) {
            lhsValue = in.getRegularStore().getValue((LocalVariableNode) lhs);
        }
        if (n.getTarget() instanceof FieldAccessNode) {
            lhsValue = in.getRegularStore().getValue((FieldAccessNode) lhs);
        }
        CFStore store = in.getRegularStore();

        Set<AnnotationMirror> newRhsSet = AnnotationUtils.createAnnotationSet();
        newRhsSet.add(atypeFactory.DISAPPEAR);
        CFValue rhsValueDisappear =
                analysis.createAbstractValue(newRhsSet, rhsValue.getUnderlyingType());

        Set<AnnotationMirror> rhsAnnotations = rhsValue.getAnnotations();
        for (AnnotationMirror rhsAnnoMirror : rhsAnnotations) {
            // TODO: there is a case for shared
            if (AnnotationUtils.areSameByName(atypeFactory.UNIQUE, rhsAnnoMirror)) {
                // Set RHS node value to disappear if it is Unique before assignment
                store.updateForAssignment(rhs, rhsValueDisappear);
                // Update lhs states
                // To use the latest value of lhs, first check whether oldLhsValue exists.
                List<String> lhsStatesList = null;
                Tree lhsTree = lhs.getTree();
                if (lhsValue != null) {
                    Set<AnnotationMirror> lhsAnnotations = lhsValue.getAnnotations();
                    for (AnnotationMirror lhsAnnoMirror : lhsAnnotations) {
                        if (AnnotationUtils.areSameByName(atypeFactory.SHARED, lhsAnnoMirror)) {
                            lhsStatesList =
                                    AnnotationUtils.getElementValueArray(
                                            lhsAnnoMirror, "value", String.class, true);
                            break;
                        }
                    }
                } else {
                    AnnotationMirror lhsAnnotationMirror =
                            atypeFactory.getAnnotationMirror(lhsTree, Shared.class);
                    if (lhsAnnotationMirror != null) {
                        // combine states with rhs.
                        lhsStatesList =
                                AnnotationUtils.getElementValueArray(
                                        lhsAnnotationMirror, "value", String.class, true);
                    }
                }
                if (lhsStatesList != null) {
                    List<String> rhsStatesList =
                            AnnotationUtils.getElementValueArray(
                                    rhsAnnoMirror, "value", String.class, true);
                    lhsStatesList.addAll(rhsStatesList);
                    // create new lhs value and update
                    AnnotationMirror newLhsAnnoMirror;
                    AnnotationBuilder builder = new AnnotationBuilder(env, Shared.class);
                    builder.setValue("value", lhsStatesList);
                    newLhsAnnoMirror = builder.build();
                    Set<AnnotationMirror> newLhsSet = AnnotationUtils.createAnnotationSet();
                    newLhsSet.add(newLhsAnnoMirror);
                    CFValue newLhsValue =
                            analysis.createAbstractValue(
                                    newLhsSet,
                                    atypeFactory.getAnnotatedType(lhsTree).getUnderlyingType());
                    store.updateForAssignment(lhs, newLhsValue);
                }
                break;
            }

            // let new assignment take effect later. keep lhs value as it is in input
            if (AnnotationUtils.areSameByName(this.atypeFactory.DISAPPEAR, rhsAnnoMirror)) {
                if (lhsValue != null) {
                    store.updateForAssignment(lhs, lhsValue);
                }
                return new RegularTransferResult(null, store);
            }
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
}
