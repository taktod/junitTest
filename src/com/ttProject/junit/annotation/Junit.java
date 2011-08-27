package com.ttProject.junit.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * JunitTestによるテストを実行することを宣言するアノーテーション
 * @author taktod
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Junit {
	Test[] value();
}
