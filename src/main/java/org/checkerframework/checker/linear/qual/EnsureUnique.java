package org.checkerframework.checker.linear.qual;

import org.checkerframework.framework.qual.InheritedAnnotation;
import org.checkerframework.framework.qual.PostconditionAnnotation;
import org.checkerframework.framework.qual.QualifierArgument;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@PostconditionAnnotation(qualifier = Unique.class)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@InheritedAnnotation
// Range of value could also work, for example, int value
public @interface EnsureUnique {
    String[] value();

    // TODO: check and document
    @QualifierArgument("value")
    String[] states() default {};
}
