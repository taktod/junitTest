package com.ttProject.junit.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * テスト用の定義アノーテーション
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Test {
	String[] init() default {}; // デフォルトデータはnull配列にしておく。
	String[] value() default {};
	String assume() default "@dump"; // デフォルトはデータDumpにしておく。
}
