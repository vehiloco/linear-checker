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

public class LinearTransfer extends CFTransfer {

    /** The @{@link Unique} annotation. */
    public LinearTransfer(CFAnalysis analysis) {
        super(analysis);
    }

    @Override
    public void processCommonAssignment(
            TransferInput in, Node lhs, Node rhs, CFStore store, CFValue rhsValue) {
        // update information in the store
        Set<AnnotationMirror> annos = rhsValue.getAnnotations();
        super.processCommonAssignment(in, lhs, rhs, store, rhsValue);
    }
}
