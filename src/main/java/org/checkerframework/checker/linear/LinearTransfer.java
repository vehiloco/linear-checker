package org.checkerframework.checker.linear;

import java.util.Set;
import javax.lang.model.element.AnnotationMirror;
import org.checkerframework.checker.linear.qual.Unique;
import org.checkerframework.dataflow.analysis.TransferInput;
import org.checkerframework.dataflow.analysis.TransferResult;
import org.checkerframework.dataflow.cfg.node.AssignmentNode;
import org.checkerframework.dataflow.cfg.node.LocalVariableNode;
import org.checkerframework.dataflow.cfg.node.Node;
import org.checkerframework.framework.flow.*;
import org.checkerframework.javacutil.AnnotationUtils;

public class LinearTransfer extends CFTransfer {

    private final LinearAnnotatedTypeFactory atypeFactory;

    /** The @{@link Unique} annotation. */
    public LinearTransfer(CFAnalysis analysis) {
        super(analysis);
        this.atypeFactory = (LinearAnnotatedTypeFactory) analysis.getTypeFactory();
    }

    @Override
    public TransferResult<CFValue, CFStore> visitAssignment(
            AssignmentNode n, TransferInput<CFValue, CFStore> in) {
        TransferResult<CFValue, CFStore> superResult = super.visitAssignment(n, in);
        System.out.println("============================= Transfer function!!!");
        Node rhs = n.getExpression();
        CFValue rhsValue = (CFValue) in.getValueOfSubNode(rhs);
        System.out.println(rhsValue.toString());
        System.out.println("============================= TransferInput function!!!");
        System.out.println(in.toString());
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

    @Override
    public TransferResult<CFValue, CFStore> visitLocalVariable(
            LocalVariableNode n, TransferInput<CFValue, CFStore> in) {
        TransferResult<CFValue, CFStore> superResult = super.visitLocalVariable(n, in);
        CFAbstractStore store = (CFAbstractStore) in.getRegularStore();
        CFValue oldValue = (CFValue) store.getValue(n);
        AnnotationMirror newAddedAnno = this.atypeFactory.NONLINEAR;
        Set<AnnotationMirror> newSet = AnnotationUtils.createAnnotationSet();
        newSet.add(newAddedAnno);
        CFValue newValue = analysis.createAbstractValue(newSet, oldValue.getUnderlyingType());
        store.updateForAssignment(n, newValue);
        superResult.setResultValue(newValue);
        return superResult;
    }

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
