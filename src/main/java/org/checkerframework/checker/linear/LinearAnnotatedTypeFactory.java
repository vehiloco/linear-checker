package org.checkerframework.checker.linear;

import java.lang.annotation.Annotation;
import java.util.Collection;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import org.checkerframework.checker.linear.qual.Disappear;
import org.checkerframework.checker.linear.qual.Shared;
import org.checkerframework.checker.linear.qual.Unique;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.framework.flow.CFStore;
import org.checkerframework.framework.flow.CFValue;
import org.checkerframework.framework.type.GenericAnnotatedTypeFactory;
import org.checkerframework.framework.type.QualifierHierarchy;
import org.checkerframework.framework.type.SubtypeIsSupersetQualifierHierarchy;
import org.checkerframework.javacutil.AnnotationBuilder;
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

    @Override
    protected QualifierHierarchy createQualifierHierarchy() {
        return new LinearQualifierHierarchy(getSupportedTypeQualifiers());
    }

    // SubtypeIsSubsetQualifierHierarchy, TODO: use my own qualifier hierarchy
    private final class LinearQualifierHierarchy extends SubtypeIsSupersetQualifierHierarchy {

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
