package com.ttProject.junit.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * コンストラクタの初期化用パラメーター設定
 * @author taktod
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Init {
	String[] value();
}
