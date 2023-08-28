package org.checkerframework.checker.linear.qual;

import org.checkerframework.framework.qual.PreconditionAnnotation;
import org.checkerframework.framework.qual.QualifierArgument;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@PreconditionAnnotation(qualifier = Unique.class)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface RequireUnique {
    String[] value();

    @QualifierArgument("value")
    String[] states();
}
