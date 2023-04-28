package com.xl.shield;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @Author : wyl
 * @Date : 2023/4/23
 * Desc :
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ShieldAnnotation {
    String key();
    int time() default 2000;
}
