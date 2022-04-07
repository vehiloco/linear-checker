package org.checkerframework.checker.crypto;

import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.ExpressionTree;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.util.Elements;
import org.checkerframework.checker.crypto.qual.Any;
import org.checkerframework.checker.crypto.qual.Top;
import org.checkerframework.checker.crypto.qual.Unique;
import org.checkerframework.common.basetype.BaseAnnotatedTypeFactory;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.framework.type.AnnotatedTypeFactory;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.framework.type.ElementQualifierHierarchy;
import org.checkerframework.framework.type.QualifierHierarchy;
import org.checkerframework.framework.type.treeannotator.ListTreeAnnotator;
import org.checkerframework.framework.type.treeannotator.TreeAnnotator;
import org.checkerframework.javacutil.AnnotationBuilder;
import org.checkerframework.javacutil.AnnotationUtils;

public class CryptoAnnotatedTypeFactory extends BaseAnnotatedTypeFactory {

    /** The @{@link Unique} annotation. */
    protected final AnnotationMirror UNIQUE = AnnotationBuilder.fromClass(elements, Unique.class);
    /** The @{@link Any} annotation. */
    protected final AnnotationMirror ANY = AnnotationBuilder.fromClass(elements, Any.class);
    /** The @{@link Top} annotation. */
    protected final AnnotationMirror TOP = AnnotationBuilder.fromClass(elements, Top.class);

    public CryptoAnnotatedTypeFactory(BaseTypeChecker checker) {
        super(checker);
        this.postInit();
    }

    @Override
    protected QualifierHierarchy createQualifierHierarchy() {
        return new CryptoQualifierHierarchy(this.getSupportedTypeQualifiers(), elements);
    }

    private final class CryptoQualifierHierarchy extends ElementQualifierHierarchy {

        /**
         * Creates a CryptoQualifierHierarchy from the given classes.
         *
         * @param qualifierClasses classes of annotations that are the qualifiers for this hierarchy
         * @param elements element utils
         */
        public CryptoQualifierHierarchy(
                Collection<Class<? extends Annotation>> qualifierClasses, Elements elements) {
            super(qualifierClasses, elements);
        }

        @Override
        public boolean isSubtype(final AnnotationMirror subtype, final AnnotationMirror supertype) {
            if (AnnotationUtils.areSameByName(supertype, TOP)
                    || AnnotationUtils.areSameByName(subtype, ANY)) {
                return true;
            } else if (AnnotationUtils.areSameByName(subtype, TOP)
                    || AnnotationUtils.areSameByName(supertype, ANY)) {
                return false;
            } else if (AnnotationUtils.areSameByName(subtype, UNIQUE)
                    && AnnotationUtils.areSameByName(supertype, UNIQUE)) {
                return compareAllowedAlgorithmOrProviderTypes(subtype, supertype);
            } else {
                return false;
            }
        }

        @Override
        public AnnotationMirror greatestLowerBound(AnnotationMirror a1, AnnotationMirror a2) {
            return CryptoAnnotatedTypeFactory.this.ANY;
        }

        @Override
        public AnnotationMirror leastUpperBound(AnnotationMirror a1, AnnotationMirror a2) {
            return CryptoAnnotatedTypeFactory.this.TOP;
        }

        private boolean compareAllowedAlgorithmOrProviderTypes(
                final AnnotationMirror subtype, final AnnotationMirror supertype) {
            List<String> supertypeRegexList =
                    AnnotationUtils.getElementValueArray(supertype, "value", String.class, true);
            List<String> subtypeRegexList =
                    AnnotationUtils.getElementValueArray(subtype, "value", String.class, true);
            return supertypeRegexList.containsAll(subtypeRegexList);
        }
    }

    @Override
    protected TreeAnnotator createTreeAnnotator() {
        return new ListTreeAnnotator(
                super.createTreeAnnotator(),
                new CryptoAnnotatedTypeFactory.CryptoTreeAnnotator(this));
    }

    private class CryptoTreeAnnotator extends TreeAnnotator {
        public CryptoTreeAnnotator(AnnotatedTypeFactory atypeFactory) {
            super(atypeFactory);
        }

        @Override
        public Void visitAssignment(AssignmentTree node, AnnotatedTypeMirror type) {
            ExpressionTree rhs = node.getExpression();
            AnnotatedTypeMirror valueType = atypeFactory.getAnnotatedType(rhs);
            AnnotationMirror valueTypeMirror = valueType.getAnnotation(Unique.class);
            // replace rhs anno, order and why does not work?
//            if (valueTypeMirror != null && AnnotationUtils.areSameByName(valueTypeMirror, UNIQUE)) {
                valueType.replaceAnnotation(TOP);
//            }
            return super.visitAssignment(node, type);
        }
    }
}
