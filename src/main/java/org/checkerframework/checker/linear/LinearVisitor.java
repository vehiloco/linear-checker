package org.checkerframework.checker.linear;

import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodInvocationTree;
import java.util.List;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import org.checkerframework.checker.linear.qual.Disappear;
import org.checkerframework.checker.linear.qual.MayAliased;
import org.checkerframework.checker.linear.qual.Unique;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.common.basetype.BaseTypeVisitor;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.javacutil.AnnotationBuilder;
import org.checkerframework.javacutil.AnnotationUtils;

public class LinearVisitor extends BaseTypeVisitor<LinearAnnotatedTypeFactory> {

    final boolean STRONG_BOX_BACKED_ENABLE = checker.getLintOption("strongboxbacked", false);

    final ProcessingEnvironment env;

    /** The @{@link Disappear} annotation. */
    protected final AnnotationMirror DISAPPEAR =
            AnnotationBuilder.fromClass(elements, Disappear.class);
    /** The @{@link Unique} annotation. */
    protected final AnnotationMirror UNIQUUE = AnnotationBuilder.fromClass(elements, Unique.class);
    /** The @{@link MayAliased} annotation. */
    protected final AnnotationMirror MAYALIASED =
            AnnotationBuilder.fromClass(elements, MayAliased.class);

    public LinearVisitor(final BaseTypeChecker checker) {
        super(checker);
        env = checker.getProcessingEnvironment();
    }

    @Override
    public Void visitMethodInvocation(MethodInvocationTree node, Void p) {
        List<? extends ExpressionTree> args = node.getArguments();
        // Prevent @Disappear being used as a parameter
        for (int i = 0; i < args.size(); i++) {
            AnnotatedTypeMirror argTypeMirror = atypeFactory.getAnnotatedType(args.get(i));
            AnnotationMirror argAnnotationMirror = argTypeMirror.getAnnotation(Disappear.class);
            if (argAnnotationMirror != null
                    && AnnotationUtils.areSameByName(argAnnotationMirror, DISAPPEAR)) {
                checker.reportError(args.get(i), "unique.parameter.not.allowed");
            }
        }
        return super.visitMethodInvocation(node, p);
    }

    @Override
    public Void visitAssignment(AssignmentTree node, Void p) {
        System.out.println("-----------Visitor----------------");
        ExpressionTree lhs = node.getVariable();
        ExpressionTree rhs = node.getExpression();
        AnnotatedTypeMirror rhsValueType = atypeFactory.getAnnotatedType(rhs);
        AnnotatedTypeMirror lhsValueType = atypeFactory.getAnnotatedType(lhs);
        AnnotationMirror valueTypeMirror = rhsValueType.getAnnotation(Disappear.class);
        AnnotationMirror lhsAnnotationMirror = lhsValueType.getAnnotation(Unique.class);
        List<String> oldValues =
                AnnotationUtils.getElementValueArray(
                        lhsAnnotationMirror, this.atypeFactory.uniqueElements, String.class);
        System.out.println("lhs is: " + lhs.toString());
        System.out.println("rhs is: " + rhs.toString());
        System.out.println("The LHS type is now: " + lhsValueType.toString());
        System.out.println("The RHS type is now: " + rhsValueType.toString());
        System.out.println("The LHS anno elements are now: " + oldValues.toString());
        if (valueTypeMirror != null && AnnotationUtils.areSameByName(valueTypeMirror, DISAPPEAR)) {
            checker.reportError(rhs, "unique.assignment.not.allowed");
        }
        return super.visitAssignment(node, p);
    }
}
