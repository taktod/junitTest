package com.ttProject.junit.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/*
 * テスト用のアノーテーション
 * paramsは入力配列
 * assumeはあるべき結果
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Test {
	String[] params();
	String assume() default "@none";
}
