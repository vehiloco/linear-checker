package org.checkerframework.checker.linear;

import com.sun.source.tree.Tree;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import org.checkerframework.checker.linear.qual.*;
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
    public TransferResult<CFValue, CFStore> visitMethodInvocation(
            MethodInvocationNode n, TransferInput<CFValue, CFStore> in) {
        TransferResult<CFValue, CFStore> superResult = super.visitMethodInvocation(n, in);

        //        List<Node> args = n.getArguments();
        //        System.out.println("------------------------visit method invocation-----------");
        //        for (Node arg : args) {
        //            JavaExpression targetExpr = JavaExpression.fromNode(arg);
        //            CFValue targetValue = in.getRegularStore().getValue(targetExpr);
        //        }

        //        Node receiver = n.getTarget().getReceiver();
        //        // TODO: add restrictions
        //        if (receiver != null) {
        //            String methodName = n.getTarget().getMethod().getSimpleName().toString();
        //            JavaExpression target = JavaExpression.fromNode(receiver);
        //            if (methodName.equals("nextBytesSimulator")) {
        //                List<Node> args = n.getArguments();
        //                for (Node arg : args) {
        //                    CFValue previousValue = in.getValueOfSubNode(arg);
        //                    if (previousValue != null) {
        //                        for (AnnotationMirror anno : previousValue.getAnnotations()) {
        //                            List<String> oldValues =
        //                                    AnnotationUtils.getElementValueArray(
        //                                            anno, this.atypeFactory.uniqueElements,
        // String.class);
        //                            AnnotationBuilder builder = new AnnotationBuilder(env,
        // Unique.class);
        //                            builder.setValue("value", new String[] {"initialized new"});
        //                            AnnotationMirror newAnno = builder.build();
        //                            JavaExpression param = JavaExpression.fromNode(arg);
        //                            // I don't know whats the meaning of insert value. just for
        // updating?
        //                            superResult.getElseStore().insertValue(param, newAnno);
        //                            superResult.getThenStore().insertValue(param, newAnno);
        //                            superResult.getRegularStore().insertValue(param, newAnno);
        //                            // CFValue
        //                            Set<AnnotationMirror> newSet =
        // AnnotationUtils.createAnnotationSet();
        //                            newSet.add(newAnno);
        //                            CFValue newValue =
        //                                    analysis.createAbstractValue(
        //                                            newSet,
        //
        // superResult.getResultValue().getUnderlyingType());
        //                            superResult.setResultValue(newValue);
        //                        }
        //                    }
        //                }
        //            }
        //        }
        return superResult;
    }

    @Override
    public TransferResult<CFValue, CFStore> visitAssignment(
            AssignmentNode n, TransferInput<CFValue, CFStore> in) {
        CFValue oldLhsValue = null;
        if (n.getTarget() instanceof LocalVariableNode) {
            oldLhsValue = in.getRegularStore().getValue((LocalVariableNode) n.getTarget());
        }
        if (n.getTarget() instanceof FieldAccessNode) {
            oldLhsValue = in.getRegularStore().getValue((FieldAccessNode) n.getTarget());
        }
        Node rhs = n.getExpression();
        Node lhs = n.getTarget();
        CFStore store = in.getRegularStore();
        CFValue rhsValue = in.getValueOfSubNode(rhs);
        if (rhsValue == null) {
            return super.visitAssignment(n, in);
        }
        Set<AnnotationMirror> rhsAnnotations = rhsValue.getAnnotations();
        Set<AnnotationMirror> newRhsSet = AnnotationUtils.createAnnotationSet();
        newRhsSet.add(atypeFactory.DISAPPEAR);
        CFValue newRhsValue = analysis.createAbstractValue(newRhsSet, rhsValue.getUnderlyingType());
        // Check rhs type, if rhs is not array,field access or local variable, just return super
        // result
        JavaExpression je = JavaExpression.fromNode(rhs);
        if (!(je instanceof ArrayAccess
                || je instanceof FieldAccess
                || je instanceof LocalVariable)) {
            return super.visitAssignment(n, in);
        }
        for (AnnotationMirror rhsAnnoMirror : rhsAnnotations) {
            // Update RHS node CFValue
            if (AnnotationUtils.areSameByName(atypeFactory.UNIQUE, rhsAnnoMirror)) {
                store.updateForAssignment(rhs, newRhsValue);
                // Update lhs states
                // To use the latest value of lhs, first check whether oldLhsValue exists.
                List<String> lhsStatesList = null;
                Tree lhsTree = lhs.getTree();
                if (oldLhsValue != null) {
                    Set<AnnotationMirror> lhsAnnotations = oldLhsValue.getAnnotations();
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
                if (oldLhsValue != null) {
                    store.updateForAssignment(lhs, oldLhsValue);
                }
                //                superResult.setResultValue(newRhsValue);
                break;
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
