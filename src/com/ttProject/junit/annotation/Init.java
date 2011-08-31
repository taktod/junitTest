package com.ttProject.junit.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * コンストラクタの初期化用パラメーター設定
 * @author taktod
 */
@Retention(RetentionPolicy.RUNTIME)
//@Retention(RetentionPolicy.SOURCE)
@Target({java.lang.annotation.ElementType.CONSTRUCTOR})
public @interface Init {
	String[] value() default {};
}
