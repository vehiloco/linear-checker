package org.checkerframework.checker.linear;

import com.sun.source.tree.ExpressionTree;
import java.util.Iterator;
import java.util.Set;
import javax.lang.model.element.AnnotationMirror;
import org.checkerframework.checker.linear.qual.Disappear;
import org.checkerframework.checker.linear.qual.Unique;
import org.checkerframework.dataflow.analysis.TransferInput;
import org.checkerframework.dataflow.analysis.TransferResult;
import org.checkerframework.dataflow.cfg.node.AssignmentNode;
import org.checkerframework.dataflow.cfg.node.LocalVariableNode;
import org.checkerframework.dataflow.cfg.node.Node;
import org.checkerframework.framework.flow.*;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.javacutil.AnnotationUtils;

public class LinearTransfer extends CFAbstractTransfer<CFValue, CFStore, LinearTransfer> {

    private final LinearAnnotatedTypeFactory atypeFactory;

    /** The @{@link Disappear} annotation. */
    public LinearTransfer(LinearAnalysis analysis) {
        super(analysis, true);
        this.atypeFactory = (LinearAnnotatedTypeFactory) analysis.getTypeFactory();
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
        CFAbstractStore store = in.getRegularStore();
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
