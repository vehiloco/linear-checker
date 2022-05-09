package org.checkerframework.checker.linear;

import com.sun.source.tree.ExpressionTree;
import java.util.Iterator;
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
import org.checkerframework.javacutil.AnnotationBuilder;
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
        Node receiver = n.getTarget().getReceiver();
        // TODO: add restrictions
        if (receiver != null) {
            String methodName = n.getTarget().getMethod().getSimpleName().toString();
            JavaExpression target = JavaExpression.fromNode(receiver);
            if (methodName.equals("nextBytesSimulator")) {
                List<Node> args = n.getArguments();
                for (Node arg : args) {
                    System.out.println(arg.toStringDebug());
                    CFValue previousValue = in.getValueOfSubNode(arg);
                    if (previousValue != null) {
                        for (AnnotationMirror anno : previousValue.getAnnotations()) {
                            List<String> oldValues =
                                    AnnotationUtils.getElementValueArray(
                                            anno, this.atypeFactory.uniqueElements, String.class);
                            AnnotationBuilder builder = new AnnotationBuilder(env, Unique.class);
                            builder.setValue("value", new String[] {"a", "initialized"});
                            AnnotationMirror newAnno = builder.build();
                            JavaExpression param = JavaExpression.fromNode(arg);
                            System.out.println("----------------------");
                            System.out.println(newAnno.toString());

                            superResult.getElseStore().insertValue(param, newAnno);
                            System.out.println(superResult.getElseStore().toString());
                            superResult.getThenStore().insertValue(param, newAnno);
                        }
                    }
                }
                //                System.out.println(superResult.toString());
            }
        }

        return superResult;
    }

    @Override
    public TransferResult<CFValue, CFStore> visitAssignment(
            AssignmentNode n, TransferInput<CFValue, CFStore> in) {
        TransferResult<CFValue, CFStore> superResult = super.visitAssignment(n, in);
        Node rhs = n.getExpression();
        if (!(rhs instanceof LocalVariableNode)) {
            return superResult;
        }
        ExpressionTree valueExp = (ExpressionTree) rhs.getTree();
        AnnotatedTypeMirror valueType = this.atypeFactory.getAnnotatedType(valueExp);
        AnnotationMirror rhsUnique = valueType.getAnnotation(Unique.class);
        AnnotationMirror rhsDisappear = valueType.getAnnotation(Disappear.class);
        CFValue rhsValue = in.getValueOfSubNode(rhs);
        Set<AnnotationMirror> rhsAnnotations = rhsValue.getAnnotations();
        Iterator<AnnotationMirror> it = rhsAnnotations.iterator();
        CFStore store = in.getRegularStore();
        AnnotationMirror newAddedAnno = this.atypeFactory.DISAPPEAR;
        Set<AnnotationMirror> newSet = AnnotationUtils.createAnnotationSet();
        newSet.add(newAddedAnno);
        CFValue newRhsValue = analysis.createAbstractValue(newSet, rhsValue.getUnderlyingType());
        for (AnnotationMirror annoMirror : rhsAnnotations) {
            if (AnnotationUtils.areSameByName(this.atypeFactory.UNIQUE, annoMirror)) {
                store.updateForAssignment(rhs, newRhsValue);
                break;
            }

            if (AnnotationUtils.areSameByName(this.atypeFactory.DISAPPEAR, annoMirror)) {
                superResult.setResultValue(newRhsValue);
                break;
            }
        }
        return superResult;
    }

    //    @Override
    //    public TransferResult<CFValue, CFStore> visitLocalVariable(
    //            LocalVariableNode n, TransferInput<CFValue, CFStore> in) {
    //        //        System.out.println("---------------------Start Linear Transfer
    //        // visitLocalVariable");
    //        //        System.out.println(n.toStringDebug());
    //        //        System.out.println(n.getReceiver());
    //        //        System.out.println(n.isLValue());
    //        //        System.out.println(n.getTree().getKind());
    //        TransferResult<CFValue, CFStore> superResult = super.visitLocalVariable(n, in);
    //        // When visitMethodInvocation, things are different.
    //        if (isRhs) {
    //            CFAbstractStore store = (CFAbstractStore) in.getRegularStore();
    //            CFValue oldValue = (CFValue) store.getValue(n);
    //            System.out.println(oldValue.toStringFullyQualified());
    //            AnnotationMirror newAddedAnno = this.atypeFactory.UNIQUE;
    //            Set<AnnotationMirror> newSet = AnnotationUtils.createAnnotationSet();
    //            newSet.add(newAddedAnno);
    //            CFValue newValue = analysis.createAbstractValue(newSet,
    // oldValue.getUnderlyingType());
    //            store.updateForAssignment(n, newValue);
    //            superResult.setResultValue(newValue);
    //            System.out.println("---------------------End Linear Transfer visitLocalVariable");
    //        }
    //        return superResult;
    //    }

    //    @Override
    //    public void processCommonAssignment(
    //            TransferInput in, Node lhs, Node rhs, CFStore store, CFValue rhsValue) {
    //        // update information in the store
    //        AnnotationMirror newAddedAnno = this.atypeFactory.USEDUP;
    //        Set<AnnotationMirror> newSet = AnnotationUtils.createAnnotationSet();
    //        newSet.add(newAddedAnno);
    //        CFValue c = analysis.createAbstractValue(newSet, rhsValue.getUnderlyingType());
    //        super.processCommonAssignment(in, lhs, rhs, store, c);
    //    }
}
