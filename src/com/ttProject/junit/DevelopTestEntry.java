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
	 * @throws Throwable 
	 */
	@Override
	public void doTest() throws Throwable {
		super.doTest();
	}

/*	@Junit({
		@Test(value={"hello", "true"}, assume="hello:true"),
		@Test({"hey", "true"}),
		@Test(value={"hi", "false"}, assume="@RuntimeException"),
		@Test({"wow", "false"}),
	})//*/
	public String doSomething(String str, boolean flg) {
		if(!flg) {
			throw new RuntimeException("hogehoge");
		}
		return str + ":" + flg;
	}
}
