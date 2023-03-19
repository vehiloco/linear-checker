package org.checkerframework.checker.linear;

import java.util.Objects;
import java.util.Set;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.type.TypeMirror;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.dataflow.analysis.TransferResult;
import org.checkerframework.dataflow.cfg.node.AssignmentNode;
import org.checkerframework.dataflow.cfg.node.FieldAccessNode;
import org.checkerframework.dataflow.cfg.node.LocalVariableNode;
import org.checkerframework.dataflow.cfg.node.Node;
import org.checkerframework.framework.flow.CFAbstractAnalysis;
import org.checkerframework.framework.flow.CFStore;
import org.checkerframework.framework.flow.CFValue;
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
        // update lhs value
        // TODO: there is a bug, the lhs node should be updated later, think about a way to do it.
        if (node instanceof AssignmentNode) {
            // update lhs value
            Node lhsNode = ((AssignmentNode) node).getTarget();
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
                //                if (newVal != null) {
                nodeValues.put(
                        lhsNode,
                        transferResult.getRegularStore().getValue((FieldAccessNode) lhsNode));
                //                }
            }
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
    public CFValue createAbstractValue(
            Set<AnnotationMirror> annotations, TypeMirror underlyingType) {
        return defaultCreateAbstractValue(this, annotations, underlyingType);
    }

    protected boolean canUpdate(Node lhsNode) {
        CFValue lhsValue = nodeValues.get(lhsNode);
        //        if (lhsNode == null) {
        //            return false;
        //        }
        // don't update if both lhs and rhs are Unique
        if (lhsValue != null) {
            for (AnnotationMirror lhsAnno : lhsValue.getAnnotations()) {
                if (AnnotationUtils.areSameByName(atypeFactory.UNIQUE, lhsAnno)) {
                    System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!");
                    return false;
                    //                    for (AnnotationMirror rhsAnno : rhsValue.getAnnotations())
                    // {
                    //                        if (AnnotationUtils.areSameByName(atypeFactory.UNIQUE,
                    // rhsAnno)) {
                    //                            return false;
                    //                        }
                    //                    }
                }
            }
        }
        return true;
    }
}
