package org.checkerframework.checker.linear;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.AnnotationMirror;
import org.checkerframework.checker.linear.qual.Unique;
import org.checkerframework.dataflow.analysis.TransferInput;
import org.checkerframework.dataflow.analysis.TransferResult;
import org.checkerframework.dataflow.cfg.node.MethodInvocationNode;
import org.checkerframework.dataflow.cfg.node.Node;
import org.checkerframework.dataflow.expression.JavaExpression;
import org.checkerframework.framework.flow.*;
import org.checkerframework.javacutil.AnnotationBuilder;
import org.checkerframework.javacutil.AnnotationUtils;

public class LinearTransfer extends CFTransfer {

    private final LinearAnnotatedTypeFactory atypeFactory;

    /** The @{@link Unique} annotation. */
    public LinearTransfer(CFAnalysis analysis) {
        super(analysis);
        this.atypeFactory = (LinearAnnotatedTypeFactory) analysis.getTypeFactory();
    }

    @Override
    public TransferResult<CFValue, CFStore> visitMethodInvocation(
            MethodInvocationNode n, TransferInput<CFValue, CFStore> in) {
        TransferResult<CFValue, CFStore> superResult = super.visitMethodInvocation(n, in);
        List<Node> arguments = n.getArguments();
        // Here we use this hard code, will change it later
        // 1. loop arguments, find one with unique
        // 2. get annotation mirror of this argument
        // 3. add new value into its value of annotation
        CFStore store = superResult.getRegularStore();
        if (arguments.size() > 0) {
            System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxx super result");
            Node argu0 = arguments.get(0);
            JavaExpression arg = JavaExpression.fromNode(argu0);
            System.out.println(superResult.getRegularStore().toString());
            CFValue flowValue = superResult.getRegularStore().getValue(arg);
            if (flowValue != null) {
                Set<AnnotationMirror> flowAnnos = flowValue.getAnnotations();
                List<String> valuesAsList = Arrays.asList("s1");
                for (AnnotationMirror anno : flowAnnos) {
                    List<String> oldFlowValues =
                            AnnotationUtils.getElementValueArray(
                                    anno, this.atypeFactory.usedUpValueElement, String.class);
                    // valuesAsList cannot have its length changed -- it is backed by an
                    // array.  getElementValueArray returns a new, modifiable list.
                    oldFlowValues.addAll(valuesAsList);
                    valuesAsList = oldFlowValues;
                    // create new anno and put back to
                    AnnotationBuilder builder =
                            new AnnotationBuilder(
                                    this.atypeFactory.env,
                                    (Class<? extends Annotation>)
                                            AnnotationUtils.annotationMirrorToClass(anno));
                    builder.setValue("value", valuesAsList);
                    // update failed.
                    AnnotationMirror newAnno = builder.build();
                    store.insertValue(arg, newAnno);
                }
            }
        }
        System.out.println(store.toString());
        return superResult;
    }

    //    @Override
    //    public TransferResult<CFValue, CFStore> visitAssignment(
    //            AssignmentNode n, TransferInput<CFValue, CFStore> in) {
    //        System.out.println("============================= Transfer function!!!");
    //        Node rhs = n.getExpression();
    //        CFValue rhsValue = (CFValue) in.getValueOfSubNode(rhs);
    //        System.out.println(rhsValue.toString());
    //        System.out.println("============================= TransferInput function!!!");
    //        System.out.println(in.toString());
    //        // create a new cfvalue and put it into the store.
    //        AnnotationMirror newAddedAnno = this.atypeFactory.USEDUP;
    //        Set<AnnotationMirror> newSet = AnnotationUtils.createAnnotationSet();
    //        newSet.add(newAddedAnno);
    //        CFValue newRhsValue = analysis.createAbstractValue(newSet,
    // rhsValue.getUnderlyingType());
    //        CFAbstractStore store = (CFAbstractStore) in.getRegularStore();
    //        // use store insert value instead. just like nullnesstransfer.
    //        store.updateForAssignment(rhs, newRhsValue);
    //        return new RegularTransferResult<CFValue, CFStore>(
    //                super.finishValue(newRhsValue, (CFStore) store), (CFStore) store);
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
