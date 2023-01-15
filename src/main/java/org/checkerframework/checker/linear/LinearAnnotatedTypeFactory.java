package org.checkerframework.checker.linear;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.util.Elements;
import org.checkerframework.checker.linear.qual.Bottom;
import org.checkerframework.checker.linear.qual.Disappear;
import org.checkerframework.checker.linear.qual.Shared;
import org.checkerframework.checker.linear.qual.Unique;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.framework.flow.CFStore;
import org.checkerframework.framework.flow.CFValue;
import org.checkerframework.framework.type.ElementQualifierHierarchy;
import org.checkerframework.framework.type.GenericAnnotatedTypeFactory;
import org.checkerframework.framework.type.QualifierHierarchy;
import org.checkerframework.javacutil.AnnotationBuilder;
import org.checkerframework.javacutil.AnnotationUtils;
import org.checkerframework.javacutil.TreeUtils;

public class LinearAnnotatedTypeFactory
        extends GenericAnnotatedTypeFactory<CFValue, CFStore, LinearTransfer, LinearAnalysis> {

    /** The @{@link Bottom} annotation. */
    protected final AnnotationMirror BOTTOM = AnnotationBuilder.fromClass(elements, Bottom.class);
    /** The @{@link Disappear} annotation. */
    protected final AnnotationMirror DISAPPEAR =
            AnnotationBuilder.fromClass(elements, Disappear.class);
    /** The @{@link Unique} annotation. */
    protected final AnnotationMirror UNIQUE = AnnotationBuilder.fromClass(elements, Unique.class);
    /** The @{@link Shared} annotation. */
    protected final AnnotationMirror SHARED = AnnotationBuilder.fromClass(elements, Shared.class);

    protected final ExecutableElement uniqueElements =
            TreeUtils.getMethod(Unique.class, "value", 0, processingEnv);;

    public LinearAnnotatedTypeFactory(BaseTypeChecker checker) {
        super(checker, true);
        this.postInit();
    }

    //    @Override
    //    protected QualifierHierarchy createQualifierHierarchy() {
    //        return new LinearQualifierHierarchy(getSupportedTypeQualifiers());
    //    }
    //
    //    private final class LinearQualifierHierarchy extends SubtypeIsSupersetQualifierHierarchy {
    //        public LinearQualifierHierarchy(Collection<Class<? extends Annotation>>
    // qualifierClasses) {
    //            super(qualifierClasses, processingEnv);
    //        }
    //    }

    @Override
    protected QualifierHierarchy createQualifierHierarchy() {
        return new LinearQualifierHierarchy(getSupportedTypeQualifiers(), elements);
    }

    // SubtypeIsSubsetQualifierHierarchy, TODO: use my own qualifier hierarchy
    private final class LinearQualifierHierarchy extends ElementQualifierHierarchy {

        /**
         * Creates a CryptoQualifierHierarchy from the given classes.
         *
         * @param qualifierClasses classes of annotations that are the qualifiers for this hierarchy
         */
        public LinearQualifierHierarchy(
                Collection<Class<? extends Annotation>> qualifierClasses, Elements elements) {
            super(qualifierClasses, elements);
        }

        // 1.@Shared is super type
        // 2.@Unique({}) is the super type of other @Unique with any elements
        @Override
        public boolean isSubtype(AnnotationMirror subtype, AnnotationMirror supertype) {
            // for top and bottom
            // TODO: shared may have elements in it.
            if (AnnotationUtils.areSameByName(supertype, SHARED)
                    || AnnotationUtils.areSameByName(subtype, BOTTOM)) {
                return true;
            }
            // for unique and disappear
            if (AnnotationUtils.areSameByName(supertype, UNIQUE)
                    && AnnotationUtils.areSameByName(subtype, DISAPPEAR)) {
                return true;
            }
            if (AnnotationUtils.areSameByName(subtype, supertype)) {
                // for both disappear
                if (AnnotationUtils.areSameByName(subtype, DISAPPEAR)) {
                    return true;
                }
                if (AnnotationUtils.areSameByName(subtype, UNIQUE)) {
                    List<String> supertypeElementList =
                            AnnotationUtils.getElementValueArray(
                                    supertype, "value", String.class, true);
                    List<String> subtypeElementList =
                            AnnotationUtils.getElementValueArray(
                                    subtype, "value", String.class, true);
                    // @Unique({}) is super
                    if (supertypeElementList.size() == 0) {
                        return true;
                    }
                    // State is the same
                    if (supertypeElementList.size() == 1 && subtypeElementList.size() == 1) {
                        if (supertypeElementList.get(0).equals(subtypeElementList.get(0))) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        @Override
        public AnnotationMirror greatestLowerBound(AnnotationMirror a1, AnnotationMirror a2) {
            return a2;
        }

        @Override
        public AnnotationMirror leastUpperBound(AnnotationMirror a1, AnnotationMirror a2) {
            return a2;
        }
    }
}
