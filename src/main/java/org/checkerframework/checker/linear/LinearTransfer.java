package org.checkerframework.checker.linear;

import com.sun.source.tree.ExpressionTree;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import org.checkerframework.checker.linear.qual.Disappear;
import org.checkerframework.checker.linear.qual.Unique;
import org.checkerframework.dataflow.analysis.TransferInput;
import org.checkerframework.dataflow.analysis.TransferResult;
import org.checkerframework.dataflow.cfg.node.AssignmentNode;
import org.checkerframework.dataflow.cfg.node.LocalVariableNode;
import org.checkerframework.dataflow.cfg.node.MethodInvocationNode;
import org.checkerframework.dataflow.cfg.node.Node;
import org.checkerframework.dataflow.expression.JavaExpression;
import org.checkerframework.framework.flow.*;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.javacutil.AnnotationUtils;

public class LinearTransfer extends CFAbstractTransfer<CFValue, CFStore, LinearTransfer> {

    private final LinearAnnotatedTypeFactory atypeFactory;
    private final ProcessingEnvironment env;

    /** The @{@link Disappear} annotation. */
    public LinearTransfer(LinearAnalysis analysis) {
        super(analysis, true);
        this.atypeFactory = (LinearAnnotatedTypeFactory) analysis.getTypeFactory();
        env = atypeFactory.getChecker().getProcessingEnvironment();
    }

    @Override
    public TransferResult<CFValue, CFStore> visitMethodInvocation(
            MethodInvocationNode n, TransferInput<CFValue, CFStore> in) {
        TransferResult<CFValue, CFStore> superResult = super.visitMethodInvocation(n, in);

        List<Node> args = n.getArguments();
        System.out.println("------------------------visit method invocation-----------");
        for (Node arg : args) {
            JavaExpression targetExpr = JavaExpression.fromNode(arg);
            CFValue targetValue = in.getRegularStore().getValue(targetExpr);
        }

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
        System.out.println("Transfer VisitAssignment --------------------------------");
        System.out.println(in.toString());
        CFValue oldLhsValue = in.getRegularStore().getValue((LocalVariableNode) n.getTarget());

        TransferResult<CFValue, CFStore> superResult = super.visitAssignment(n, in);
        Node rhs = n.getExpression();
        Node lhs = n.getTarget();
        CFStore store = in.getRegularStore();
        if (!(rhs instanceof LocalVariableNode) || !(lhs instanceof LocalVariableNode)) {
            return superResult;
        }
        JavaExpression lhsExpr = JavaExpression.fromNode(lhs);
        CFValue lhsValue = store.getValue(lhsExpr);
        AnnotationMirror lhsAnnoMirror =
                this.atypeFactory.getAnnotationByClass(lhsValue.getAnnotations(), Unique.class);
        ExpressionTree valueExp = (ExpressionTree) rhs.getTree();
        AnnotatedTypeMirror valueType = this.atypeFactory.getAnnotatedType(valueExp);
        CFValue rhsValue = in.getValueOfSubNode(rhs);
        Set<AnnotationMirror> rhsAnnotations = rhsValue.getAnnotations();
        Set<AnnotationMirror> newSet = AnnotationUtils.createAnnotationSet();
        newSet.add(this.atypeFactory.DISAPPEAR);
        CFValue newRhsValue = analysis.createAbstractValue(newSet, rhsValue.getUnderlyingType());
        for (AnnotationMirror annoMirror : rhsAnnotations) {
            // Update RHS node CFValue
            if (AnnotationUtils.areSameByName(this.atypeFactory.UNIQUE, annoMirror)) {
                store.updateForAssignment(rhs, newRhsValue);
                break;
            }

            // let new assignment take effect later. keep lhs value as it is in input
            if (AnnotationUtils.areSameByName(this.atypeFactory.DISAPPEAR, annoMirror)) {
                store.updateForAssignment(lhs, oldLhsValue);
                superResult.setResultValue(newRhsValue);
                break;
            }
        }
        //        System.out.println(superResult.toString());
        return superResult;
    }
}
