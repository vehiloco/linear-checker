package org.checkerframework.checker.linear.qual;

import java.lang.annotation.*;
import org.checkerframework.framework.qual.DefaultFor;
import org.checkerframework.framework.qual.SubtypeOf;
import org.checkerframework.framework.qual.TypeKind;

@Documented
@SubtypeOf({Shared.class})
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@DefaultFor(typeKinds = {TypeKind.ARRAY})
public @interface Unique {
    String[] value() default {};
}
