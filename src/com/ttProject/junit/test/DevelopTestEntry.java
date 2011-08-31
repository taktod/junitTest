package com.ttProject.junit.test;

import com.ttProject.junit.TestEntry;

/**
 * 作成中に利用したテスト動作
 * @author taktod
 */
public class DevelopTestEntry extends TestEntry {
	/**
	 * 初期準備
	 */
	@Override
	public void setUp() throws Exception {
//		setPackagePath("com.ttProject.junit"); // 全体の確認(エラーがでてとまらなければ結果はどうあれすべてパス)
		setPackagePath("com.ttProject.junit.test.dump"); // dump出力のみの確認(項目数を減らすため、別テストにしてある。出力内容を目で確認する必要あり。)
//		stepByMode();
		super.setUp();
	}
	/**
	 * テスト実行
	 * @throws Throwable 
	 */
	@Override
	public void doTest() throws Throwable {
		super.doTest();
	}
}
