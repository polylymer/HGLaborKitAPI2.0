package de.hglabor.plugins.kitapi.kit.settings;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface FloatArg {
    float min() default Float.MIN_VALUE;

    float max() default Float.MAX_VALUE;
}
