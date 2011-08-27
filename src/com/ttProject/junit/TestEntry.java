package com.ttProject.junit;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ttProject.junit.library.ClassFinder;
import com.ttProject.junit.library.MethodChecker;

/**
 * テスト開始用のエントリークラス。
 * @author taktod
 */
public class TestEntry {
	/** 設定パッケージ */
	private String packagePath = "com.ttProject";
	/** [#key]で呼び出される特殊変数 */
	private Map<String, Object> extraData = new HashMap<String, Object>();
	/** 動作確認用プログラム */
	private MethodChecker checker = null;

	/**
	 * 設定パッケージを変更します。
	 * @param path 変更後のパッケージ名
	 */
	public void setPackagePath(String path) {
		packagePath = path;
	}
	/**
	 * [#key]で呼び出しする特殊変数を指定します。
	 * @param key #をのぞいたkeyの文字列
	 * @param value 設定したいJavaオブジェクト
	 */
	public void setData(String key, Object value) {
		extraData.put(key, value);
	}
	/**
	 * 動作前準備
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		try {
			ClassFinder cf = new ClassFinder();
			checker = new MethodChecker(cf.getAppClass(packagePath), extraData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 動作後処理
	 * @throws Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	/**
	 * 実行実体
	 */
	@Test
	public void doTest() {
		if(checker == null) {
			org.junit.Assert.fail("Checker object is null...");
		}
		checker.checkStart();
	}
}
