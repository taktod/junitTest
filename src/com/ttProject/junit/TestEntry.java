package com.ttProject.junit;

import static org.junit.Assert.*;

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
	/** メソッドごと一時停止 */
	private boolean stepBy = false;
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
	 * 特殊データを取得する。
	 * @return
	 */
	public Map<String, Object> getExtraData() {
		return extraData;
	}
	/**
	 * 呼び出すと、メソッドごとに処理を中断する
	 */
	public void stepByMode() {
		stepBy = true;
	}
	/**
	 * メソッドごとに一時中止
	 * @return
	 */
	public boolean isStepByMode() {
		return stepBy;
	}
	/**
	 * 動作前準備
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		try {
			ClassFinder cf = new ClassFinder();
			checker = new MethodChecker(cf.getAppClass(packagePath), this);
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
		checker.close();
	}
	/**
	 * 実行実体
	 * @throws Throwable 
	 */
	@Test
	public void doTest() throws Throwable {
		if(checker == null) {
			fail("Checker object is null...");
		}
		checker.checkStart();
	}
	/**
	 * Dump時に特別なデータを表示したいときにはこれを使う。
	 * @param obj
	 * @return true:標準Dumpも追加する。
	 */
	public boolean dump(Object obj) {
		return true;
	}
	/**
	 * 動作の返答に問題があるかどうかを特別な判定を行う場合はここで処理する。
	 * assume=@custom(適当な判定式)を指定する必要あり。
	 * @param ret
	 * @return
	 */
	public boolean customCheck(String assume, Object ret) {
		System.out.println("no check function");
		return false;
	}
}
