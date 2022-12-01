package org.checkerframework.checker.linear.qual;

import java.lang.annotation.*;
import org.checkerframework.framework.qual.SubtypeOf;

@Documented
@SubtypeOf({Shared.class})
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
public @interface Unique {
    String[] value() default {};
}
