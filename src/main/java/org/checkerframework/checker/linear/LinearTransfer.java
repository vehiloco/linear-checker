package org.checkerframework.checker.linear;

import java.util.Set;
import javax.lang.model.element.AnnotationMirror;
import org.checkerframework.checker.linear.qual.Unique;
import org.checkerframework.dataflow.analysis.TransferInput;
import org.checkerframework.dataflow.analysis.TransferResult;
import org.checkerframework.dataflow.cfg.node.AssignmentNode;
import org.checkerframework.dataflow.cfg.node.Node;
import org.checkerframework.framework.flow.*;
import org.checkerframework.javacutil.AnnotationUtils;

public class LinearTransfer extends CFTransfer {

    private final LinearAnnotatedTypeFactory atypeFactory;

    private boolean isRhs = false;

    /** The @{@link Unique} annotation. */
    public LinearTransfer(CFAnalysis analysis) {
        super(analysis);
        this.atypeFactory = (LinearAnnotatedTypeFactory) analysis.getTypeFactory();
    }

    @Override
    public TransferResult<CFValue, CFStore> visitAssignment(
            AssignmentNode n, TransferInput<CFValue, CFStore> in) {
        TransferResult<CFValue, CFStore> superResult = super.visitAssignment(n, in);
        Node rhs = n.getExpression();

        // TODO
        boolean prevIsRhs = isRhs;
        isRhs = true;
        CFValue rhsValue = (CFValue) in.getValueOfSubNode(rhs);
        isRhs = prevIsRhs;
        // create a new cfvalue and put it into the store.
        AnnotationMirror newAddedAnno = this.atypeFactory.USEDUP;
        Set<AnnotationMirror> newSet = AnnotationUtils.createAnnotationSet();
        newSet.add(newAddedAnno);
        CFValue newRhsValue = analysis.createAbstractValue(newSet, rhsValue.getUnderlyingType());
        CFAbstractStore store = (CFAbstractStore) in.getRegularStore();
        // use store insert value instead. just like nullnesstransfer.
        store.updateForAssignment(rhs, newRhsValue);
        superResult.setResultValue(newRhsValue);
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
