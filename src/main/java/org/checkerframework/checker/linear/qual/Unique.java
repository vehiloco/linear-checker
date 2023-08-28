package org.checkerframework.checker.linear.qual;

import org.checkerframework.framework.qual.SubtypeOf;

import java.lang.annotation.*;

@Documented
@SubtypeOf({Shared.class})
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
public @interface Unique {
    String[] value() default {};
}
