package com.ttProject.junit.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/*
 * テスト用のアノーテーション
 * paramsは入力配列
 * "string[abc][test][gesg][trsy]" hashSetにする。型は自分で宣言する必要あり。(省略時はString強制)
 * "string->string[a->test][b->hello]" hashMapにする。型は自分で宣言する必要あり。(省略時はString強制 keyもvalも)
 * つかえる型はboolean char byte short int long double float string
 * あらかじめMapにデータをセットしておいて、そのデータを参照することも可
 * "#data"この場合はsetUpで所定のHashMapにデータを入れる必要あり。(できた)
 * 使える型は
 * *[は入力文字に指定不可
 * assumeはあるべき結果
 * @none 応答チェックなし。(できた)
 * @ok 応答チェックなし。例外はエラーにする。(できた)
 * @dump 応答データを標準出力にだす。チェックはしない。(できた)
 * @Exception 例外(できた)
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Test {
	String[] params();
	String assume() default "@none";
}
