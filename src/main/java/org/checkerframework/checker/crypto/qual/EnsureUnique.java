package org.checkerframework.checker.crypto.qual;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import org.checkerframework.framework.qual.PostconditionAnnotation;
import org.checkerframework.framework.qual.QualifierArgument;

@PostconditionAnnotation(qualifier = Unique.class)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface EnsureUnique {
    String[] value();

    @QualifierArgument("value")
    String[] whatever();
}
