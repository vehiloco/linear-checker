package org.checkerframework.checker.linear.qual;

import java.lang.annotation.*;
import org.checkerframework.framework.qual.DefaultFor;
import org.checkerframework.framework.qual.SubtypeOf;

@Documented
@SubtypeOf({MayAliased.class})
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@DefaultFor(types = {String.class, byte[].class})
public @interface Unique {
    String[] value() default {};
}
