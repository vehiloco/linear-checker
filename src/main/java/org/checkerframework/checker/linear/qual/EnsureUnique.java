package org.checkerframework.checker.linear.qual;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import org.checkerframework.framework.qual.PostconditionAnnotation;
import org.checkerframework.framework.qual.QualifierArgument;

@PostconditionAnnotation(qualifier = Unique.class)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
// Range of value could also work, for example, int value
public @interface EnsureUnique {
    String[] value();

    @QualifierArgument("value")
    String[] states() default {};
}
