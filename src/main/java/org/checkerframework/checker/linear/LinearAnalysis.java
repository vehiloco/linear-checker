package org.checkerframework.checker.linear;

import java.util.List;
import java.util.Objects;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.type.TypeMirror;
import org.checkerframework.checker.linear.qual.*;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.dataflow.analysis.TransferResult;
import org.checkerframework.dataflow.cfg.node.AssignmentNode;
import org.checkerframework.dataflow.cfg.node.FieldAccessNode;
import org.checkerframework.dataflow.cfg.node.LocalVariableNode;
import org.checkerframework.dataflow.cfg.node.Node;
import org.checkerframework.framework.flow.CFAbstractAnalysis;
import org.checkerframework.framework.flow.CFStore;
import org.checkerframework.framework.flow.CFValue;
import org.checkerframework.javacutil.AnnotationMirrorSet;
import org.checkerframework.javacutil.AnnotationUtils;

public class LinearAnalysis extends CFAbstractAnalysis<CFValue, CFStore, LinearTransfer> {

    private final LinearAnnotatedTypeFactory atypeFactory;

    public LinearAnalysis(BaseTypeChecker checker, LinearAnnotatedTypeFactory factory) {
        super(checker, factory);
        this.atypeFactory = factory;
    }

    @Override
    public boolean updateNodeValues(Node node, TransferResult<CFValue, CFStore> transferResult) {
        CFValue newVal = transferResult.getResultValue();
        boolean nodeValueChanged = false;
        // update lhs value, do not update lhs if both lhs and rhs are @unique

        // TODO: there is a bug, the lhs node should be updated later, think about a way to do it.
        if (node instanceof AssignmentNode) {
            Node rhsNode = ((AssignmentNode) node).getExpression();
            // update lhs value
            Node lhsNode = ((AssignmentNode) node).getTarget();

            //            CFValue lhsValue =
            //                    transferResult.getRegularStore().getValue((LocalVariableNode)
            // lhsNode);
            //            if (canUpdate(lhsNode, rhsNode, lhsValue)) {
            if (lhsNode instanceof LocalVariableNode) {

                if (transferResult.getRegularStore().getValue((LocalVariableNode) lhsNode)
                        != null) {
                    // search value from store
                    nodeValues.put(
                            lhsNode,
                            transferResult.getRegularStore().getValue((LocalVariableNode) lhsNode));
                }
            }
            if (((AssignmentNode) node).getTarget() instanceof FieldAccessNode) {
                nodeValues.put(
                        lhsNode,
                        transferResult.getRegularStore().getValue((FieldAccessNode) lhsNode));
            }
            //            }
        }

        if (newVal != null) {
            CFValue oldVal = nodeValues.get(node);
            if (node instanceof AssignmentNode) {
                Node rhsNode = ((AssignmentNode) node).getExpression();
                if (rhsNode instanceof LocalVariableNode) {
                    oldVal = nodeValues.get(rhsNode);
                    nodeValues.put(rhsNode, newVal);
                }
                if (rhsNode instanceof FieldAccessNode) {
                    oldVal = nodeValues.get(rhsNode);
                    nodeValues.put(
                            rhsNode,
                            transferResult.getRegularStore().getValue((FieldAccessNode) rhsNode));
                }
            } else {
                nodeValues.put(node, newVal);
            }
            nodeValueChanged = !Objects.equals(oldVal, newVal);
        }
        return nodeValueChanged || transferResult.storeChanged();
    }

    @Override
    public CFStore createEmptyStore(boolean sequentialSemantics) {
        return new CFStore(this, sequentialSemantics);
    }

    @Override
    public CFStore createCopiedStore(CFStore s) {
        return new CFStore(s);
    }

    @Override
    public CFValue createAbstractValue(AnnotationMirrorSet annotations, TypeMirror underlyingType) {
        return defaultCreateAbstractValue(this, annotations, underlyingType);
    }

    protected boolean canUpdate(Node lhsNode, Node rhsNode, CFValue lhsValue) {
        AnnotationMirror lhsAMUnique =
                atypeFactory.getAnnotationMirror(lhsNode.getTree(), Unique.class);
        AnnotationMirror rhsAMUnique =
                atypeFactory.getAnnotationMirror(rhsNode.getTree(), Unique.class);
        if (lhsAMUnique != null && rhsAMUnique != null && lhsValue != null) {
            for (AnnotationMirror lhsAnno : lhsValue.getAnnotations()) {
                if (AnnotationUtils.areSameByName(atypeFactory.UNIQUE, lhsAnno)) {
                    List<String> lhsStatesList =
                            AnnotationUtils.getElementValueArray(
                                    lhsAnno, "value", String.class, true);
                    // TODO: hard to check
                    if (lhsStatesList.size() > 0) {
                        return true;
                    }
                }
            }
        }
        return true;
    }
}
