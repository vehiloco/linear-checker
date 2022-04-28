package org.checkerframework.checker.linear;

import java.lang.annotation.Annotation;
import java.util.Collection;
import javax.lang.model.element.AnnotationMirror;
import org.checkerframework.checker.linear.qual.NonLinear;
import org.checkerframework.checker.linear.qual.Unique;
import org.checkerframework.checker.linear.qual.UsedUp;
import org.checkerframework.common.basetype.BaseAnnotatedTypeFactory;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.framework.type.SubtypeIsSubsetQualifierHierarchy;
import org.checkerframework.javacutil.AnnotationBuilder;

public class LinearAnnotatedTypeFactory extends BaseAnnotatedTypeFactory {

    /** The @{@link Unique} annotation. */
    protected final AnnotationMirror UNIQUE = AnnotationBuilder.fromClass(elements, Unique.class);
    /** The @{@link NonLinear} annotation. */
    protected final AnnotationMirror NONLINEAR =
            AnnotationBuilder.fromClass(elements, NonLinear.class);
    /** The @{@link UsedUp} annotation. */
    protected final AnnotationMirror USEDUP = AnnotationBuilder.fromClass(elements, UsedUp.class);

    public LinearAnnotatedTypeFactory(BaseTypeChecker checker) {
        super(checker);
        this.postInit();
    }

    private final class LinearQualifierHierarchy extends SubtypeIsSubsetQualifierHierarchy {

        /**
         * Creates a CryptoQualifierHierarchy from the given classes.
         *
         * @param qualifierClasses classes of annotations that are the qualifiers for this hierarchy
         */
        public LinearQualifierHierarchy(Collection<Class<? extends Annotation>> qualifierClasses) {
            super(qualifierClasses, processingEnv);
        }
    }
}
