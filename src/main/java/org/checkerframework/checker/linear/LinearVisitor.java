package org.checkerframework.checker.linear;

import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodInvocationTree;
import java.util.List;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import org.checkerframework.checker.linear.qual.NonLinear;
import org.checkerframework.checker.linear.qual.Unique;
import org.checkerframework.checker.linear.qual.UsedUp;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.common.basetype.BaseTypeVisitor;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.javacutil.AnnotationBuilder;

public class LinearVisitor extends BaseTypeVisitor<LinearAnnotatedTypeFactory> {

    final boolean STRONG_BOX_BACKED_ENABLE = checker.getLintOption("strongboxbacked", false);

    final ProcessingEnvironment env;

    /** The @{@link Unique} annotation. */
    protected final AnnotationMirror UNIQUE = AnnotationBuilder.fromClass(elements, Unique.class);
    /** The @{@link NonLinear} annotation. */
    protected final AnnotationMirror ANY = AnnotationBuilder.fromClass(elements, NonLinear.class);
    /** The @{@link UsedUp} annotation. */
    protected final AnnotationMirror TOP = AnnotationBuilder.fromClass(elements, UsedUp.class);

    public LinearVisitor(final BaseTypeChecker checker) {
        super(checker);
        env = checker.getProcessingEnvironment();
    }

    @Override
    public Void visitMethodInvocation(MethodInvocationTree node, Void p) {
        List<? extends ExpressionTree> valueExp = node.getArguments();
        return super.visitMethodInvocation(node, p);
    }

    @Override
    public Void visitAssignment(AssignmentTree node, Void p) {
        // Forbid usage or give it a new anno;

        ExpressionTree lhs = node.getVariable();
        ExpressionTree rhs = node.getExpression();
        AnnotatedTypeMirror valueType = atypeFactory.getAnnotatedType(rhs);
        AnnotatedTypeMirror lhsValueType = atypeFactory.getAnnotatedType(lhs);
        System.out.println(valueType.toString());
        AnnotationMirror valueTypeMirror = valueType.getAnnotation(Unique.class);
        //        if (valueTypeMirror != null && AnnotationUtils.areSameByName(valueTypeMirror,
        // UNIQUE)) {
        //            checker.reportError(rhs, "unique.alias.not.allowed");
        //        }
        return super.visitAssignment(node, p);
    }
}
