package org.checkerframework.checker.linear;

import java.lang.annotation.Annotation;
import java.util.Collection;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.util.Elements;
import org.checkerframework.checker.linear.qual.NonLinear;
import org.checkerframework.checker.linear.qual.Unique;
import org.checkerframework.checker.linear.qual.UsedUp;
import org.checkerframework.common.basetype.BaseAnnotatedTypeFactory;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.framework.type.ElementQualifierHierarchy;
import org.checkerframework.framework.type.QualifierHierarchy;
import org.checkerframework.javacutil.AnnotationBuilder;
import org.checkerframework.javacutil.AnnotationUtils;

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

    @Override
    protected QualifierHierarchy createQualifierHierarchy() {
        return new LinearQualifierHierarchy(this.getSupportedTypeQualifiers(), elements);
    }

    private final class LinearQualifierHierarchy extends ElementQualifierHierarchy {

        /**
         * Creates a LinearQualifierHierarchy from the given classes.
         *
         * @param qualifierClasses classes of annotations that are the qualifiers for this hierarchy
         * @param elements element utils
         */
        public LinearQualifierHierarchy(
                Collection<Class<? extends Annotation>> qualifierClasses, Elements elements) {
            super(qualifierClasses, elements);
        }

        @Override
        public boolean isSubtype(final AnnotationMirror subtype, final AnnotationMirror supertype) {
            if (AnnotationUtils.areSameByName(supertype, USEDUP)
                    || AnnotationUtils.areSameByName(subtype, UNIQUE)) {
                return true;
            } else if (AnnotationUtils.areSameByName(subtype, USEDUP)
                    || AnnotationUtils.areSameByName(supertype, UNIQUE)) {
                return false;
            } else if (AnnotationUtils.areSameByName(subtype, UNIQUE)
                    && AnnotationUtils.areSameByName(supertype, UNIQUE)) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        public AnnotationMirror greatestLowerBound(AnnotationMirror a1, AnnotationMirror a2) {
            return LinearAnnotatedTypeFactory.this.USEDUP;
        }

        @Override
        public AnnotationMirror leastUpperBound(AnnotationMirror a1, AnnotationMirror a2) {
            return LinearAnnotatedTypeFactory.this.USEDUP;
        }
    }
}
