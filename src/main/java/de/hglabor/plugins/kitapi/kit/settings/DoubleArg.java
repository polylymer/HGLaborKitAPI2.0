package de.hglabor.plugins.kitapi.kit.settings;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface DoubleArg {
    double min() default Double.MIN_VALUE;

    double max() default Double.MAX_VALUE;
}
