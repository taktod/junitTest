package com.ttProject.junit.library;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ttProject.junit.annotation.Init;
import com.ttProject.junit.annotation.Junit;
import com.ttProject.junit.annotation.Test;
import com.ttProject.junit.exception.ConstructorNotFoundException;

import static org.junit.Assert.*;

/**
 * 関数定義をアノーテーションに従って調査して応答を返す。
 * @author taktod
 */
public class MethodChecker {
	/** チェック対象クラス */
	private Set<Class<?>> classSet = null;
	/** #keyで登録されているデータ */
	private Map<String, Object>dataMap = null;
	/**
	 * コンストラクタ
	 * @param classSet
	 */
	public MethodChecker(Set<Class<?>> classSet, Map<String, Object>dataMap) {
		this.classSet = classSet;
		this.dataMap = dataMap;
	}
	/**
	 * コンストラクタ
	 */
	@Init({})
	public MethodChecker() {
		classSet = null;
		dataMap = null;
	}
	/**
	 * 検査スタート
	 * @throws Throwable 
	 */
	public void checkStart() throws Throwable {
		for(Class<?> cls : classSet) {
			for(Method m : cls.getDeclaredMethods()) {
				if(m.getAnnotation(Junit.class) != null) {
					System.out.println("[check for class:(" + cls.getName() + ") method:(" + m.getName() + ")]");
					// 実行すべきMethod
					checkMethod(cls, m);
					System.out.println();
				}
			}
		}
	}
	/**
	 * 関数を確認する。
	 * @param cls
	 * @param method
	 * @throws Throwable 
	 */
	private void checkMethod(Class<?> cls, Method method) throws Throwable {
		// 入力データ
		Junit test = method.getAnnotation(Junit.class);
		for(Test testEntry : test.value()) {
			try {
				List<Object> dataList = new ArrayList<Object>();
				List<String> paramList = new ArrayList<String>();
				List<String> constructorParamList = new ArrayList<String>();
				// データリストの作成
				for(String str : testEntry.value()) {
					paramList.add(str);
				}
				for(String str : testEntry.init()) {
					constructorParamList.add(str);
				}
				// 文字列のデータリストを関数のパラメーター定義に合わせて変換
				System.out.print("[param : ");
				for(Class<?> type : method.getParameterTypes()) {
					String param = paramList.remove(0);
					System.out.print(param);
					System.out.print(" ");
					dataList.add(getTestParam(type, param));
				}
				System.out.println("]");
				Object ret = null;
				boolean access = method.isAccessible();
				method.setAccessible(true);

				System.out.println();
				if(Modifier.isStatic(method.getModifiers())) {
					// static関数
					ret = method.invoke(null, dataList.toArray());
				}
				else {
					// 一般関数 (new Instanceの部分初期か方法が指定されている場合はそっちにあわせる。)
					ret = method.invoke(getClassInstance(cls, constructorParamList), dataList.toArray());
				}
				method.setAccessible(access);
				String assume = testEntry.assume();
				System.out.println("[assume : " + assume + " result : " + ret + "]");
				if(assume.equals("@dump")) {
					dumpAll(ret);
					System.out.println("[...passed...]");
					System.out.println();
					continue;
				}
				if(ret == null) {
					ret = "null";
				}
				if(assume.equals("@none") || assume.equals("@ok") || assume.equals(ret.toString())) {
					System.out.println("[...passed...]");
					System.out.println();
				}
				else {
					System.out.println("[value is corrupted...]");
					fail("value is corrupted...");
					return;
				}
			}
			catch (InvocationTargetException e) {
				String assume = testEntry.assume();
				e.getCause().printStackTrace(System.out);
				if(assume.equals("@none")) {
					System.out.println("[assume : @none]");
					System.out.println("[...passed...]");
					System.out.println();
					continue;
				}
				if(assume.indexOf("Exception") != -1) {
					try {
						assume = assume.substring(1);
						if((e.getCause().getClass().getName().indexOf(assume) != -1)
							|| (e.getCause().getCause().getClass().getName().indexOf(assume) != -1)) {
							// 指定されている例外が存在する場合はOK
							System.out.println("[find the exception : " + testEntry.assume() + "]");
							System.out.println("[...passed...]");
							System.out.println();
							continue;
						}
					}
					catch (Exception ex) {
					}
				}
				System.out.println("[Failed...]");
				throw e.getCause();
			}
			catch (ConstructorNotFoundException e) {
				System.out.println("[Cannot find the constructor for " + e.getMessage() + "]");
				System.out.println("[Failed...]");
				fail("Cannot find the constactor for " + e.getMessage());
			}
			catch (Exception e) {
				e.printStackTrace(System.out);
				throw e;
			}
		}
	}
	/**
	 * テストに利用するパラメーターを取得する。
	 * @param type 欲しいパラメーター(関数定義より読み込む。)
	 * @param obj 文字列指定(Testアノーテーションから取得)
	 * @return データ
	 * @throws InvocationTargetException 
	 */
	private Object getTestParam(Class<?> type, String obj) throws InvocationTargetException {
		// nullチェック
		if(obj == null || "null".equals(obj)) {
			return null;
		}
		// dataMapに設定されているデータ
		if(obj.startsWith("#")) {
			return dataMap.get(obj.substring(1));
		}
		try {
			// Class型
			if(obj.startsWith("$")) {
				return Class.forName(obj.substring(1));
			}
		}
		catch (Exception e) {
		}
		// 文字列
		if(type == String.class) {
			return obj;
		}
		// int
		if(type == int.class || type == Integer.class) {
			return new Integer(obj);
		}
		// long
		if(type == long.class || type == Long.class) {
			return new Long(obj);
		}
		// float
		if(type == float.class || type == Float.class) {
			return new Float(obj);
		}
		// double
		if(type == double.class || type == Double.class) {
			return new Double(obj);
		}
		// short
		if(type == short.class || type == Short.class) {
			return new Short(obj);
		}
		// byte
		if(type == byte.class || type == Byte.class) {
			return new Byte(obj);
		}
		// char
		if(type == char.class || type == Character.class) {
			return obj.charAt(0);
		}
		// boolean
		if(type == boolean.class || type == Boolean.class) {
			return new Boolean(obj);
		}
		// 配列
		if(type.isArray()) {
			return makeArray(type, obj);
		}
		// リスト
		if(List.class.isAssignableFrom(type)) {
			return makeList(obj);
		}
		// セット	
		if(Set.class.isAssignableFrom(type)) {
			return makeSet(obj);
		}
		// マップ
		if(Map.class.isAssignableFrom(type)) {
			return makeMap(obj);
		}
		try {
			return getClassInstance(type);
		}
		catch (ConstructorNotFoundException e) {
			return null;
		}
	}
	/**
	 * 文字列定義からHashMapを作成して応答する。
	 * @param obj
	 * @return
	 */
/*	@Junit({
		@Test(params="string->string", assume="@dump"),
		@Test(params="string->string[a->b]", assume="@dump"),
		@Test(params="string->int[a->12]", assume="@dump"),
		@Test(params="long->string[153->test]", assume="@dump"),
	}) // */
	private Object makeMap(String obj) {
		if(!obj.contains("[")) {
			return null;
		}
		String[] type = obj.split("\\[", 2);
		// typeの部分はkey->value[a->b][c->d]...という形になっている。
		Constructor<?> constructKey, constructValue;
		try {
			String[] types = type[0].split("->");
			constructKey = getElementType(types[0]);
			constructValue = getElementType(types[1]);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		String[] array = getElementArray(obj);
		Map<Object, Object> map = new HashMap<Object, Object>();
		Object key, value;
		for(String str : array) {
			try {
				String[] data = str.split("->");
				try {
					key = constructKey.newInstance(data[0]);
				}
				catch (Exception e) {
					key = data[0];
				}
				try {
					value = constructValue.newInstance(data[1]);
				}
				catch (Exception e) {
					value = data[1];
				}
				map.put(key, value);
			}
			catch (Exception e) {
			}
		}
		return map;
	}
	/**
	 * 文字列定義からHashSetを作成して応答する。
	 * @param obj
	 * @return
	 */
/*	@Junit({
		@Test(params={"byte[12][23][35][21]"}, assume="@dump"),
	}) // */
	private Object makeSet(String obj) {
		if(!obj.contains("[")) {
			return null;
		}
		String[] type = obj.split("\\[", 2);
		Constructor<?> construct = getElementType(type[0]);
		String[] array = getElementArray(obj);
		Set<Object> set = new HashSet<Object>();
		for(String str : array) {
			try {
				set.add(construct.newInstance(str));
			}
			catch (Exception e) {
			}
		}
		return set;
	}
	/**
	 * 文字列定義からArrayListを作成して応答する。
	 */
/*	@Junit({
		@Test(params={"test"}, assume="@dump"),
		@Test(params={"test[a][b][c]"}, assume="@dump"),
		@Test(params={"double[1][2][3.5]"}, assume="@dump"),
		@Test(params={"int[1][2][3.5]"}, assume="@dump"),
	}) // */
	private Object makeList(String obj) {
		if(!obj.contains("[")) {
			return null;
		}
		String[] type = obj.split("\\[", 2);
		Constructor<?> construct = getElementType(type[0]);
		String[] array = getElementArray(obj);
		List<Object> list = new ArrayList<Object>();
		for(String str : array) {
			try {
				list.add(construct.newInstance(str));
			}
			catch (Exception e) {
			}
		}
		return list;
	}

	/**
	 * 指定した型に対応するピュア配列を作成する。
	 * @param resultType　対象型
	 * @param obj 配列の定義をおこなっている文字列(先頭の文字列定義の部分は無視します。(配列の場合は指定が取得できるので、クラスに定義されている型を優先させます。))
	 * @return
	 */
/*	@Junit({
		@Test(params={"$[I", "int[12][13][14]"}, assume="@dump"), // int
		@Test(params={"$[D", "int[12][13][14]"}, assume="@dump"), // double
		@Test(params={"$[J", "int[12][13][14]"}, assume="@dump"), // long
		@Test(params={"$java.lang.String", "int[12][13][14]"}, assume="@dump"), // string
		@Test(params={"$[J", "int[ab][cd][ef]"}, assume="@dump"), // long
	}) // */
	private Object makeArray(Class<?> resultType, String obj) {
		if(!obj.contains("[")) {
			return null;
		}
		String target = resultType.getName();
		String[] array = getElementArray(obj);
		try {
			if("[Z".equals(target)) {
				boolean[] result = new boolean[array.length];
				for(int i = 0;i < array.length;i ++) {
					result[i] = new Boolean(array[i]);
				}
				return result;
			}
			if("[C".equals(target)) {
				char[] result = new char[array.length];
				for(int i = 0;i < array.length;i ++) {
					result[i] = array[i].charAt(0);
				}
				return result;
			}
			if("[B".equals(target)) {
				byte[] result = new byte[array.length];
				for(int i = 0;i < array.length;i ++) {
					result[i] = new Byte(array[i]);
				}
				return result;
			}
			if("[S".equals(target)) {
				short[] result = new short[array.length];
				for(int i = 0;i < array.length;i ++) {
					result[i] = new Short(array[i]);
				}
				return result;
			}
			if("[I".equals(target)) {
				int[] result = new int[array.length];
				for(int i = 0;i < array.length;i ++) {
					result[i] = new Integer(array[i]);
				}
				return result;
			}
			if("[J".equals(target)) {
				long[] result = new long[array.length];
				for(int i = 0;i < array.length;i ++) {
					result[i] = new Long(array[i]);
				}
				return result;
			}
			if("[F".equals(target)) {
				float[] result = new float[array.length];
				for(int i = 0;i < array.length;i ++) {
					result[i] = new Float(array[i]);
				}
				return result;
			}
			if("[D".equals(target)) {
				double[] result = new double[array.length];
				for(int i = 0;i < array.length;i ++) {
					result[i] = new Double(array[i]);
				}
				return result;
			}
		}
		catch (Exception e) {
			// 例外が発生した場合はnullを返す。(空の配列でもいいかも・・・)
			return null;
		}
		/*
		 * とりあえず文字列であると仮定しておく。
		 */
		return array;
	}
	/**
	 * それぞれのエレメントに対するコンストラクタを取得しておく
	 * @param target
	 * @return
	 */
/*	@Junit({
		@Test(params="boolean", assume="@dump"),
		@Test(params="char", assume="@dump"),
		@Test(params="byte", assume="@dump"),
		@Test(params="short", assume="@dump"),
		@Test(params="int", assume="@dump"),
		@Test(params="long", assume="@dump"),
		@Test(params="float", assume="@dump"),
		@Test(params="double", assume="@dump"),
		@Test(params="String", assume="@dump"),
		@Test(params="hogehoge", assume="@dump"),
	}) // */
	private Constructor<?> getElementType(String target) {
		try {
			if("boolean".equals(target)) {
				return Boolean.class.getConstructor(String.class);
			}
			if("char".equals(target)) {
				return Character.class.getConstructor(char.class);
			}
			if("byte".equals(target)) {
				return Byte.class.getConstructor(String.class);
			}
			if("short".equals(target)) {
				return Short.class.getConstructor(String.class);
			}
			if("int".equals(target)) {
				return Integer.class.getConstructor(String.class);
			}
			if("long".equals(target)) {
				return Long.class.getConstructor(String.class);
			}
			if("float".equals(target)) {
				return Float.class.getConstructor(String.class);
			}
			if("double".equals(target)) {
				return Double.class.getConstructor(String.class);
			}
		}
		catch (Exception e) {
		}
		return null;
	}
	/**
	 * 入力文字列を[]でくぎってエレメントにわける。出力は文字列
	 * @param target
	 * @return
	 */
/*	@Junit({
		@Test(params={"string[hello][my][name][is][takahiko]"}, assume="@dump"),
		@Test(params={"int[3][1][4][1][5][9][2][6][5][3][5]"}, assume="@dump"),
		@Test(params={"string->string[a->1][b->2][c->3]"}, assume="@dump"),
	}) // */
	private String[] getElementArray(String target) {
		// 文字列を割っておく。
		String[] result;
		String last;
		int num;
		if(!target.contains("[")) { // セパレータが存在しないので、分割できない。
			return null;
		}
		result = target.split("\\[",2)[1].split("\\]\\[", 0);
		num = result.length - 1;
		last = result[num];
		if(last.contains("]")) {
			result[num] = last.split("\\]", 2)[0];
		}
		else {
			result[num] = null;
		}
		return result;
	}
	public Object getClassInstance(Class<?> cls) throws ConstructorNotFoundException, InvocationTargetException {
		return getClassInstance(cls, null);
	}
	/**
	 * 対象クラスのデフォルトインスタンスを生成する。(検証対象のコンストラクタで利用できるようにこのメソッドはpublicにしておく。)
	 * @param type
	 * @return
	 * @throws ConstructorNotFoundException 
	 * @throws InvocationTargetException 
	 */
/*	@Junit({
		@Test("$java.lang.String"),
		@Test(value={"$java.lang.Integer"}, assume="ok"),
	})// */
	private Object getClassInstance(Class<?> cls, List<String> constructorParamList) throws ConstructorNotFoundException, InvocationTargetException {
		Init init = null;
		Boolean access = null;
		Constructor<?> constructor = null;
		try {
			constructor = cls.getDeclaredConstructor();
		}
		catch (Exception e) {
		}
		// まずコンストラクタにアノーテーションがついているかチェック。
		for(Constructor<?> construct : cls.getDeclaredConstructors()) {
			init = construct.getAnnotation(Init.class);
			if(init != null) {
				constructor = construct;
				break;
			}
		}
		if(constructor == null) {
			throw new ConstructorNotFoundException(cls.getName());
		}
		access = constructor.isAccessible();
		constructor.setAccessible(true);
		try {
			if(init != null) {
				List<Object> dataList = new ArrayList<Object>();
				// 先にparamListが指定されている場合はそちらを優先。
				List<String> paramList;
				if(constructorParamList != null && constructorParamList.size() != 0) {
					paramList = constructorParamList;
				}
				else {
					paramList = new ArrayList<String>();
					// データリストの作成
					for(String str : init.value()) {
						paramList.add(str);
					}
				}
				// 文字列のデータリストをコンストラクタのパラメーターにあわせて変換
				for(Class<?>type : constructor.getParameterTypes()) {
					String param = paramList.remove(0);
					dataList.add(getTestParam(type, param));
				}
				return constructor.newInstance(dataList.toArray());
			}
			else {
				// アノーテーション指定がない。
				return constructor.newInstance();
			}
		}
		catch (InvocationTargetException e) {
			throw e;
		}
		catch (Exception e) {
			e.printStackTrace(System.out);
			fail(e.getClass().getName());
			return null;
		}
		finally {
			if(access != null) {
				constructor.setAccessible(access);
			}
		}
	}
	/**
	 * 入力データを標準出力に表示する。
	 * @param obj
	 */
	private void dumpAll(Object obj) {
		if(obj == null) {
			System.out.println("null");
			return;
		}
		System.out.println(obj.getClass().getSimpleName());
		if(obj instanceof Object[]) {
			for(Object ob : (Object[])obj) {
				System.out.println(ob);
			}
		}
		else {
			String type = obj.getClass().getName();
			if("[Z".equals(type)) {
				System.out.println(Arrays.toString((boolean[])obj));
				return;
			}
			if("[C".equals(type)) {
				System.out.println(Arrays.toString((char[])obj));
				return;
			}
			if("[B".equals(type)) {
				System.out.println(Arrays.toString((byte[])obj));
				return;
			}
			if("[S".equals(type)) {
				System.out.println(Arrays.toString((short[])obj));
				return;
			}
			if("[I".equals(type)) {
				System.out.println(Arrays.toString((int[])obj));
				return;
			}
			if("[J".equals(type)) {
				System.out.println(Arrays.toString((long[])obj));
				return;
			}
			if("[F".equals(type)) {
				System.out.println(Arrays.toString((float[])obj));
				return;
			}
			if("[D".equals(type)) {
				System.out.println(Arrays.toString((double[])obj));
				return;
			}
			System.out.println(obj);
		}
	}
}
