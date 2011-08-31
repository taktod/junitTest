package com.ttProject.junit.test.dump;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ttProject.junit.annotation.Init;
import com.ttProject.junit.annotation.Junit;
import com.ttProject.junit.annotation.Test;

public class DumpTarget {
	/** テスト用文字列 */
	String strParam;
	/**
	 * デフォルトコンストラクタ
	 */
	public DumpTarget() {
	}
	/**
	 * 特殊コンストラクタ
	 * @param strParam
	 */
	@Init({"a"})
	public DumpTarget(String strParam) {
		this.strParam = strParam;
	}
	/**
	 * パラメータにクラスがあるときの応答用
	 * @return
	 */
	public String getStrParam() {
		return strParam;
	}
	@Junit(
		@Test(value="test", assume="@dumppause")
	)
	public String stringTest(String str) {
		return str;
	}
	@Junit(
		@Test(value="13", assume="@dumppause")
	)
	public int intTest(int val) {
		return val;
	}
	@Junit(
		@Test(value="int[1][2][3][-15]", assume="@dumppause")
	)
	public int[] intArrayTest(int[] vals) {
		return vals;
	}
/*	@Junit(
		@Test(value="char[a][b][c]", assume="@dumppause")
	)// */
	public char[] charArrayTest(char[] vals) {
		return vals;
	}
	@Junit(
		@Test(value="boolean[true][false][false]", assume="@dumppause")
	)
	public boolean[] booleanArrayTest(boolean[] vals) {
		return vals;
	}
	@Junit(
		@Test(value="short[3][2][1][-10]", assume="@dumppause")
	)
	public short[] shortArrayTest(short[] vals) {
		return vals;
	}
	@Junit(
		@Test(value="long[1234567890123][51][-1350]", assume="@dumppause")
	)
	public long[] longArrayTest(long[] vals) {
		return vals;
	}
	@Junit(
		@Test(value="float[15363264262476274][0][-132453.0][10E5]", assume="@dumppause")
	)
	public float[] floatArrayTest(float[] vals) {
		return vals;
	}
	public double[] doubleArrayTest(double[] vals) {
		return vals;
	}
	public String[] stringArrayTest(String[] vals) {
		return vals;
	}
	public Map<String, Integer> mapTest(Map<String, Integer> map) {
		return map;
	}
	public Set<Long> setTest(Set<Long> set) {
		return set;
	}
	public List<Double> listTest(List<Double> list) {
		return list;
	}
	public Object nullTest() {
		return null;
	}
}
