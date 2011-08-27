package com.ttProject.junit;

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
		setPackagePath("com.ttProject.junit");
		super.setUp();
	}
	/**
	 * テスト実行
	 */
	@Override
	public void doTest() {
		super.doTest();
	}
}
