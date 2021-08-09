package com.wcpdoc.exam.core.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)// 表示该注解是加载参数上的; @Target： 定义放在什么位置，这个是放在参数中
@Retention(RetentionPolicy.RUNTIME)// 注解保留到运行阶段; @Retention： 定义了该Annotation被保留的时间长短，有些只在源码中保留，有时需要编译成的class中保留，有些需要程序运行时候保留。即描述注解的生命周期
public @interface Param {
 
}
