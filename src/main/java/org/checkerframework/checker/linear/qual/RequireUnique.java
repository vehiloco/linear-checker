package org.checkerframework.checker.linear.qual;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import org.checkerframework.framework.qual.PreconditionAnnotation;
import org.checkerframework.framework.qual.QualifierArgument;

@PreconditionAnnotation(qualifier = Disappear.class)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface RequireUnique {
    String[] value();

    @QualifierArgument("value")
    String[] whatever();
}
