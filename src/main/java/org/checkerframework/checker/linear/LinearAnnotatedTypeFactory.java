package org.checkerframework.checker.linear;

import java.lang.annotation.Annotation;
import java.util.Collection;
import javax.lang.model.element.AnnotationMirror;
import org.checkerframework.checker.linear.qual.Disappear;
import org.checkerframework.checker.linear.qual.MayAliased;
import org.checkerframework.checker.linear.qual.Unique;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.framework.flow.CFStore;
import org.checkerframework.framework.flow.CFValue;
import org.checkerframework.framework.type.GenericAnnotatedTypeFactory;
import org.checkerframework.framework.type.SubtypeIsSubsetQualifierHierarchy;
import org.checkerframework.javacutil.AnnotationBuilder;

public class LinearAnnotatedTypeFactory
        extends GenericAnnotatedTypeFactory<CFValue, CFStore, LinearTransfer, LinearAnalysis> {

    /** The @{@link Disappear} annotation. */
    protected final AnnotationMirror DISAPPEAR =
            AnnotationBuilder.fromClass(elements, Disappear.class);
    /** The @{@link Unique} annotation. */
    protected final AnnotationMirror UNIQUE = AnnotationBuilder.fromClass(elements, Unique.class);
    /** The @{@link MayAliased} annotation. */
    protected final AnnotationMirror MAYALIASED =
            AnnotationBuilder.fromClass(elements, MayAliased.class);

    public LinearAnnotatedTypeFactory(BaseTypeChecker checker) {
        super(checker, true);
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
