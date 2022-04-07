package org.checkerframework.checker.crypto.qual;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import org.checkerframework.framework.qual.PreconditionAnnotation;
import org.checkerframework.framework.qual.QualifierArgument;

@PreconditionAnnotation(qualifier = Unique.class)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface RequireUnique {
    String[] value();

    @QualifierArgument("value")
    String[] whatever();
}
