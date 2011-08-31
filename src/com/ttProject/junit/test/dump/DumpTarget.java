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
	@Init("a")
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
		@Test("test")
	)
	public String stringTest(String str) {
		return str;
	}
	@Junit(
		@Test("13")
	)
	public int intTest(int val) {
		return val;
	}
	@Junit(
		@Test("int[1][2][3][-15]")
	)
	public int[] intArrayTest(int[] vals) {
		return vals;
	}
/*	@Junit(
		@Test("char[a][b][c]")
	)// */
	public char[] charArrayTest(char[] vals) {
		return vals;
	}
	@Junit(
		@Test("boolean[true][false][false]")
	)
	public boolean[] booleanArrayTest(boolean[] vals) {
		return vals;
	}
	@Junit(
		@Test("short[3][2][1][-10]")
	)
	public short[] shortArrayTest(short[] vals) {
		return vals;
	}
	@Junit(
		@Test("long[1234567890123][51][-1350]")
	)
	public long[] longArrayTest(long[] vals) {
		return vals;
	}
	@Junit(
		@Test("float[15363264262476274][0][-132453.0][10E5][0.000352516]")
	)
	public float[] floatArrayTest(float[] vals) {
		return vals;
	}
	@Junit(
		@Test("double[0.000000000000000000012][10E-5]")
	)
	public double[] doubleArrayTest(double[] vals) {
		return vals;
	}
	@Junit(
		@Test("string[ahaha][shine][hello][[what!]][shine]")
	)
	public String[] stringArrayTest(String[] vals) {
		return vals;
	}
	@Junit(
		@Test("string->int[hey->1.5][hoy->-10539][shy->15395]")
	)
	public Map<String, Integer> mapTest(Map<String, Integer> map) {
		return map;
	}
	@Junit(
		@Test("long[13509640428694186][31598][10][1]")
	)
	public Set<Long> setTest(Set<Long> set) {
		return set;
	}
	@Junit(
		@Test("double[0.01353269][1395823958469147][1964.246982490000135931895]")
	)
	public List<Double> listTest(List<Double> list) {
		return list;
	}
	@Junit(
		@Test()
	)
	public Object nullTest() {
		return null;
	}
}
