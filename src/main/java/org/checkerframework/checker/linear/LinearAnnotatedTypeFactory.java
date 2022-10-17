package org.checkerframework.checker.linear;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.util.Elements;
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

        //     {"used"} <: {"initialized"} <: {}, hard code now
        @Override
        public boolean isSubtype(AnnotationMirror subtype, AnnotationMirror supertype) {
            if (AnnotationUtils.areSameByName(subtype, DISAPPEAR)) {
                return true;
            }
            if (!AnnotationUtils.areSameByName(subtype, supertype)
                    && AnnotationUtils.areSameByName(supertype, SHARED)) {
                return true;
            }
            if (AnnotationUtils.areSameByName(subtype, supertype)) {
                if (AnnotationUtils.areSameByName(subtype, UNIQUE)) {
                    List<String> supertypeElementList =
                            AnnotationUtils.getElementValueArray(
                                    supertype, "value", String.class, true);
                    List<String> subtypeElementList =
                            AnnotationUtils.getElementValueArray(
                                    subtype, "value", String.class, true);
                    // max size is 1
                    if (supertypeElementList.size() > 1 || subtypeElementList.size() > 1) {
                        return false;
                    }
                    if (supertypeElementList.size() == 0) {
                        return true;
                    }
                    if (supertypeElementList.size() != subtypeElementList.size()) {
                        return false;
                    }
                    if (supertypeElementList.size() == subtypeElementList.size()) {
                        if (supertypeElementList.get(0).equals(subtypeElementList.get(0))) {
                            return true;
                        }
                        if (supertypeElementList.get(0) == "initialized"
                                && subtypeElementList.get(0) == "used") {
                            return true;
                        }
                    }
                } else if (AnnotationUtils.areSameByName(subtype, SHARED)) {
                    return true;
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
