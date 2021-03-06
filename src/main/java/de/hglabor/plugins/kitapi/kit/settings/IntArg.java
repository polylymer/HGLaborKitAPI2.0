package de.hglabor.plugins.kitapi.kit.settings;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface IntArg {
    int min() default Integer.MIN_VALUE;

    int max() default Integer.MAX_VALUE;
}
