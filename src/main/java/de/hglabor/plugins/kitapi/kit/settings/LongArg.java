package de.hglabor.plugins.kitapi.kit.settings;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface LongArg {
    long min() default Long.MIN_VALUE;

    long max() default Long.MAX_VALUE;
}
