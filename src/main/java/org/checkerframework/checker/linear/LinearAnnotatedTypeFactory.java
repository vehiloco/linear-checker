package org.checkerframework.checker.linear;

import java.lang.annotation.Annotation;
import java.util.Collection;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
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
import org.checkerframework.javacutil.TreeUtils;

public class LinearAnnotatedTypeFactory extends BaseAnnotatedTypeFactory {

    protected final ProcessingEnvironment env;

    /** The @{@link Unique} annotation. */
    protected final AnnotationMirror UNIQUE = AnnotationBuilder.fromClass(elements, Unique.class);
    /** The @{@link NonLinear} annotation. */
    protected final AnnotationMirror NONLINEAR =
            AnnotationBuilder.fromClass(elements, NonLinear.class);
    /** The @{@link UsedUp} annotation. */
    protected final AnnotationMirror USEDUP = AnnotationBuilder.fromClass(elements, UsedUp.class);

    /** The {@link Unique#value} element/argument. */
    protected final ExecutableElement uniqueValueElement;

    /** The {@link UsedUp#value} element/argument. */
    protected final ExecutableElement usedUpValueElement;

    public LinearAnnotatedTypeFactory(BaseTypeChecker checker) {
        super(checker);
        this.postInit();
        env = checker.getProcessingEnvironment();
        uniqueValueElement = TreeUtils.getMethod(Unique.class, "value", 0, env);
        usedUpValueElement = TreeUtils.getMethod(UsedUp.class, "value", 0, env);
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
                return true;
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
