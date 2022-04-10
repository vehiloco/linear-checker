package org.checkerframework.checker.linear;

import java.util.Set;
import javax.lang.model.element.AnnotationMirror;
import org.checkerframework.checker.linear.qual.Unique;
import org.checkerframework.dataflow.analysis.TransferInput;
import org.checkerframework.dataflow.cfg.node.Node;
import org.checkerframework.framework.flow.CFAnalysis;
import org.checkerframework.framework.flow.CFStore;
import org.checkerframework.framework.flow.CFTransfer;
import org.checkerframework.framework.flow.CFValue;
import org.checkerframework.javacutil.AnnotationUtils;

public class LinearTransfer extends CFTransfer {

    private final LinearAnnotatedTypeFactory atypeFactory;

    /** The @{@link Unique} annotation. */
    public LinearTransfer(CFAnalysis analysis) {
        super(analysis);
        this.atypeFactory = (LinearAnnotatedTypeFactory) analysis.getTypeFactory();
    }

    @Override
    public void processCommonAssignment(
            TransferInput in, Node lhs, Node rhs, CFStore store, CFValue rhsValue) {
        // update information in the store
        Set<AnnotationMirror> annos = rhsValue.getAnnotations();
        AnnotationMirror newAddedAnno = this.atypeFactory.USEDUP;
        //        AnnotatedTypeMirror valueType =
        // valueAnnotatedTypeFactory.getAnnotatedType(valueExp);
        //        TypeMirror underlying =
        //
        // TypeAnnotationUtils.unannotatedType(varType.getErased().getUnderlyingType());
        //        CFValue c = new CFValue(analysis, Collections.singleton(this.atypeFactory.USEDUP),
        // underlying);
        Set<AnnotationMirror> newSet = AnnotationUtils.createAnnotationSet();
        newSet.add(newAddedAnno);
        CFValue c = analysis.createAbstractValue(newSet, rhsValue.getUnderlyingType());
        store.updateForAssignment(lhs, c);
        super.processCommonAssignment(in, lhs, rhs, store, c);
    }
}
