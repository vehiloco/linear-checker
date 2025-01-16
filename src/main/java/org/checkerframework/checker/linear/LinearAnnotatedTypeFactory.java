package org.checkerframework.checker.linear;

import org.checkerframework.checker.linear.qual.Bottom;
import org.checkerframework.checker.linear.qual.Disappear;
import org.checkerframework.checker.linear.qual.EnsureUnique;
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
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.util.Elements;

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

    protected final Map<String, Object> automaton = parseAutomaton();

    protected final ExecutableElement sharedValueElement =
            TreeUtils.getMethod(Shared.class, "value", 0, processingEnv);
    protected final ExecutableElement uniqueValueElement =
            TreeUtils.getMethod(Unique.class, "value", 0, processingEnv);
    protected final ExecutableElement ensureUniqueValueElement =
            TreeUtils.getMethod(EnsureUnique.class, "value", 0, processingEnv);

    @SuppressWarnings("this-escape")
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

    protected Map<String, Object> parseAutomaton() {
        String atomotansOption = checker.getOption("atomotans");
        if (atomotansOption != null) {
            Yaml yaml = new Yaml();
            // parse atomotan yaml file and store it into the field
            List<String> files = Arrays.asList(atomotansOption.split(File.pathSeparator));
            for (String file : files) {
                InputStream inputStream =
                        LinearAnnotatedTypeFactory.class.getResourceAsStream(file);
                if (inputStream != null) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> ret = (Map<String, Object>) yaml.load(inputStream);
                    return ret;
                }
            }
        }
        return null;
    }

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
            super(qualifierClasses, elements, LinearAnnotatedTypeFactory.this);
        }

        // 1.@Shared is super type
        // 2.@Unique({}) is the super type of other @Unique with any elements
        @Override
        public boolean isSubtypeQualifiers(AnnotationMirror subtype, AnnotationMirror supertype) {
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
                                    supertype,
                                    uniqueValueElement,
                                    String.class,
                                    Collections.emptyList());
                    List<String> subtypeElementList =
                            AnnotationUtils.getElementValueArray(
                                    subtype,
                                    uniqueValueElement,
                                    String.class,
                                    Collections.emptyList());
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
        public AnnotationMirror greatestLowerBoundQualifiers(
                AnnotationMirror a1, AnnotationMirror a2) {
            // 1. shared and shared 2.shared and unique 3. shared and disappear 4.shared and btm
            // 4. unique and unique 5. unique and disappear 6.unique and btm
            // 7. disappear and disappear 8. disappear and btm 9.btm and btm
            if (AnnotationUtils.areSameByName(a1, BOTTOM)
                    || AnnotationUtils.areSameByName(a2, BOTTOM)) {
                return BOTTOM;
            }
            if (AnnotationUtils.areSameByName(a1, SHARED)) {
                return a2;
            }
            if (AnnotationUtils.areSameByName(a2, SHARED)) {
                return a1;
            }
            // TODO: think about type states, this may cause a serious bug.
            if (AnnotationUtils.areSameByName(a1, UNIQUE)
                    && AnnotationUtils.areSameByName(a2, UNIQUE)) {
                List<String> a1ElementList =
                        AnnotationUtils.getElementValueArray(a1, uniqueValueElement, String.class);
                List<String> a2ElementList =
                        AnnotationUtils.getElementValueArray(a2, uniqueValueElement, String.class);
                if (a1ElementList.size() == 0) {
                    return a2;
                } else if (a2ElementList.size() == 0) {
                    return a1;
                } else {
                    // TODO: This does not make sense, or I can change the framework.
                    return a2;
                }
            }
            // TODO: also think about this
            if ((AnnotationUtils.areSameByName(a1, UNIQUE)
                            && AnnotationUtils.areSameByName(a2, DISAPPEAR))
                    || (AnnotationUtils.areSameByName(a1, DISAPPEAR)
                            && AnnotationUtils.areSameByName(a2, UNIQUE))) {
                return DISAPPEAR;
            }
            return BOTTOM;
        }

        @Override
        public AnnotationMirror leastUpperBoundQualifiers(
                AnnotationMirror a1, AnnotationMirror a2) {
            // 4. unique and unique 5. unique and disappear
            // 7. disappear and disappear
            if (AnnotationUtils.areSameByName(a1, SHARED)
                    || AnnotationUtils.areSameByName(a2, SHARED)) {
                return SHARED;
            }
            if (AnnotationUtils.areSameByName(a1, BOTTOM)) {
                return a2;
            }
            if (AnnotationUtils.areSameByName(a2, BOTTOM)) {
                return a1;
            }
            // TODO: think about type states
            if (AnnotationUtils.areSameByName(a1, UNIQUE)
                    && AnnotationUtils.areSameByName(a2, UNIQUE)) {
                return UNIQUE;
            }
            if (AnnotationUtils.areSameByName(a1, DISAPPEAR)
                    && AnnotationUtils.areSameByName(a2, DISAPPEAR)) {
                return DISAPPEAR;
            }
            if (AnnotationUtils.areSameByName(a1, UNIQUE)
                    && AnnotationUtils.areSameByName(a2, DISAPPEAR)) {
                return UNIQUE;
            }
            // TODO: think about this.
            if (AnnotationUtils.areSameByName(a1, DISAPPEAR)
                    && AnnotationUtils.areSameByName(a2, UNIQUE)) {
                return UNIQUE;
            }
            // TODO: check default return
            return SHARED;
        }
    }
}
