package org.checkerframework.checker.linear;

import com.sun.source.tree.*;

import org.checkerframework.checker.linear.qual.Disappear;
import org.checkerframework.checker.linear.qual.Shared;
import org.checkerframework.checker.linear.qual.Unique;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.common.basetype.BaseTypeVisitor;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.javacutil.AnnotationBuilder;
import org.checkerframework.javacutil.AnnotationUtils;

import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.type.TypeKind;

@SuppressWarnings("deprecation")
public class LinearVisitor extends BaseTypeVisitor<LinearAnnotatedTypeFactory> {

    final boolean STRONG_BOX_BACKED_ENABLE = checker.getLintOption("strongboxbacked", false);

    final ProcessingEnvironment env;

    /** The @{@link Disappear} annotation. */
    protected final AnnotationMirror DISAPPEAR =
            AnnotationBuilder.fromClass(elements, Disappear.class);

    /** The @{@link Unique} annotation. */
    protected final AnnotationMirror UNIQUUE = AnnotationBuilder.fromClass(elements, Unique.class);

    /** The @{@link Shared} annotation. */
    protected final AnnotationMirror MAYALIASED =
            AnnotationBuilder.fromClass(elements, Shared.class);

    public LinearVisitor(BaseTypeChecker checker) {
        super(checker);
        env = checker.getProcessingEnvironment();
    }

    // field can only be shared
    @Override
    public Void visitVariable(VariableTree node, Void p) {
        //        ElementKind varKind = TreeUtils.elementFromDeclaration(node).getKind();
        //        if (varKind == ElementKind.FIELD) {
        //
        //            if (atypeFactory.getAnnotationMirror(node, Shared.class) == null) {
        //                checker.reportError(node, "field.type.incompatible");
        //            }
        //        }
        return super.visitVariable(node, p);
    }

    // Parameter can not be btm, return type can not be btm
    @Override
    public Void visitMethod(MethodTree node, Void p) {
        AnnotatedTypeMirror.AnnotatedExecutableType methodType =
                atypeFactory.getAnnotatedType(node).deepCopy();
        // Type check receiver
        VariableTree receiverTree = node.getReceiverParameter();
        if (receiverTree != null
                && atypeFactory.getAnnotationMirror(receiverTree, Disappear.class) != null) {
            checker.reportError(node, "disappear.receiver.not.allowed");
        }

        // type check parameters
        List<AnnotatedTypeMirror> parameterTypes = methodType.getParameterTypes();
        for (int i = 0; i < parameterTypes.size(); i++) {
            AnnotationMirror ant = parameterTypes.get(i).getAnnotation(Disappear.class);
            if (ant != null && AnnotationUtils.areSameByName(ant, DISAPPEAR)) {
                checker.reportError(node, "disappear.parameter.not.allowed");
            }
        }
        // type check return type
        AnnotatedTypeMirror returnType = methodType.getReturnType();
        if (returnType != null) {
            AnnotationMirror ant = returnType.getAnnotation(Disappear.class);
            if (ant != null) {
                checker.reportError(node, "disappear.return.not.allowed");
            }
        }
        return super.visitMethod(node, p);
    }

    @Override
    public Void visitMethodInvocation(MethodInvocationTree node, Void p) {
        List<? extends ExpressionTree> args = node.getArguments();
        for (int i = 0; i < args.size(); i++) {
            if (atypeFactory.getAnnotationMirror(args.get(i), Disappear.class) != null) {
                checker.reportError(args.get(i), "disappear.arg.not.allowed");
            }
        }
        return super.visitMethodInvocation(node, p);
    }

    @Override
    public Void visitAssignment(AssignmentTree node, Void p) {
        ExpressionTree lhs = node.getVariable();
        ExpressionTree rhs = node.getExpression();
        AnnotatedTypeMirror rhsValueType = atypeFactory.getAnnotatedType(rhs);
        AnnotatedTypeMirror lhsValueType = atypeFactory.getAnnotatedType(lhs);
        //        System.out.println(
        //                "----------------------lhs is: "
        //                        + lhs.toString()
        //                        + "  the type is: "
        //                        + lhsValueType.toString());
        //        System.out.println(
        //                "----------------------rhs is: "
        //                        + rhs.toString()
        //                        + "  the type is: "
        //                        + rhsValueType.toString());
        // skip if rhs is null
        if (rhsValueType.getKind() == TypeKind.NULL) {
            return super.visitAssignment(node, p);
        }
        // forbid assignment to a disappear variable
        AnnotationMirror rhsAnnotationMirror = rhsValueType.getAnnotation(Disappear.class);
        AnnotationMirror lhsAnnotationMirror = lhsValueType.getAnnotation(Disappear.class);
        if (lhsAnnotationMirror != null
                && AnnotationUtils.areSameByName(lhsAnnotationMirror, DISAPPEAR)) {
            checker.reportError(lhs, "disappear.assignment.not.allowed");
        }
        if (rhsAnnotationMirror != null
                        && AnnotationUtils.areSameByName(rhsAnnotationMirror, DISAPPEAR)
                || lhsAnnotationMirror != null
                        && AnnotationUtils.areSameByName(lhsAnnotationMirror, DISAPPEAR)) {
            checker.reportError(rhs, "disappear.assignment.not.allowed");
        }
        return super.visitAssignment(node, p);
    }

    @Override
    protected boolean validateType(Tree tree, AnnotatedTypeMirror type) {
        // Check typestate here
        AnnotationMirror annotationMirror =
                type.getAnnotation(Unique.class) != null
                        ? type.getAnnotation(Unique.class)
                        : type.getAnnotation(Shared.class);
        if (atypeFactory.automaton != null && annotationMirror != null) {
            @SuppressWarnings("unchecked")
            List<String> states = (List<String>) atypeFactory.automaton.get("states");
            List<String> presentStates =
                    AnnotationUtils.getElementValueArray(
                            annotationMirror, "value", String.class, true);
            for (String state : presentStates) {
                if (!states.contains(state)) {
                    checker.reportError(tree, "typestate.invalid", state);
                }
            }
        }
        return super.validateType(tree, type);
    }
}
