package com.ttProject.junit.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/*
 * テスト用のアノーテーション
 * paramsは入力配列
 * "[abc][test][gesg][trsy]" hashSetにする。型は関数の引数の型に合わせる(Objectの場合はStringにマージ)
 * "[a->test][b->hello]" hashMapにする。型は関数の引数の型に合わせる(Objectの場合はStringにマージ)
 * *[は入力文字に指定不可
 * assumeはあるべき結果
 * @none 応答チェックなし。
 * @dump 応答データを標準出力にだす。チェックはしない。
 * @Exception 例外
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Test {
	String[] params();
	String assume() default "@none";
}
