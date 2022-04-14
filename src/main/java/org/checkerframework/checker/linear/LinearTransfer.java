package org.checkerframework.checker.linear;

import com.sun.source.tree.Tree;
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

    /** The @{@link Unique} annotation. */
    public LinearTransfer(CFAnalysis analysis) {
        super(analysis);
        this.atypeFactory = (LinearAnnotatedTypeFactory) analysis.getTypeFactory();
    }

    @Override
    public TransferResult<CFValue, CFStore> visitAssignment(
            AssignmentNode n, TransferInput<CFValue, CFStore> in) {
        Node lhs = n.getTarget();
        Node rhs = n.getExpression();
        Tree tree = n.getTree();
        CFValue rhsValue = (CFValue) in.getValueOfSubNode(rhs);

        // re-construct an assignment node here.
        AnnotationMirror newAddedAnno = this.atypeFactory.USEDUP;
        Set<AnnotationMirror> newSet = AnnotationUtils.createAnnotationSet();
        newSet.add(newAddedAnno);
        CFValue newRhsValue = analysis.createAbstractValue(newSet, rhsValue.getUnderlyingType());

        CFAbstractStore store = (CFAbstractStore) in.getRegularStore();
        store.updateForAssignment(rhs, newRhsValue);
        store = (CFAbstractStore) in.getRegularStore();
        return super.visitAssignment(n, in);
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
