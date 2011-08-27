package com.ttProject.junit.library;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ttProject.junit.annotation.Init;
import com.ttProject.junit.annotation.Junit;
import com.ttProject.junit.annotation.Test;

import static org.junit.Assert.*;

@SuppressWarnings("unused")
public class MethodChecker {
	/** チェック対象クラス */
	private Set<Class<?>> classSet = null;
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
	 * 検査スタート
	 */
	public void checkStart() {
		for(Class<?> cls : classSet) {
			for(Method m : cls.getDeclaredMethods()) {
				if(m.getAnnotation(Junit.class) != null) {
					System.out.println("check for class:(" + cls.getSimpleName() + ") method:(" + m.getName() + ")");
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
	 */
	private void checkMethod(Class<?> cls, Method method) {
		// 入力データ
		Junit test = method.getAnnotation(Junit.class);
		for(Test testEntry : test.value()) {
			try {
				List<Object> dataList = new ArrayList<Object>();
				List<String> paramList = new ArrayList<String>();
				// データリストの作成
				for(String str : testEntry.params()) {
					paramList.add(str);
				}
				// 文字列のデータリストを関数のパラメーター定義に合わせて変換
				System.out.print("param : ");
				for(Class<?> type : method.getParameterTypes()) {
					String param = paramList.remove(0);
					System.out.print(param);
					System.out.print(" ");
					dataList.add(getTestParam(type, param));
				}
				System.out.println();
				Object ret = null;
				boolean access = method.isAccessible();
				method.setAccessible(true);
				if(Modifier.isStatic(method.getModifiers())) {
					// static関数
					ret = method.invoke(null, dataList.toArray());
				}
				else {
					// 一般関数 (new Instanceの部分初期か方法が指定されている場合はそっちにあわせる。)
					for(Object obj : dataList) {
						System.out.println(obj);
					}
					ret = method.invoke(getClassInstance(cls), dataList.toArray());
				}
				method.setAccessible(access);
				String assume = testEntry.assume();
				System.out.println("assume : " + assume + " result : " + ret);
				if(ret == null) {
					ret = "null";
				}
				if(assume.equals("@dump")) {
					dumpAll(ret);
				}
				else if(assume.equals("@none") || assume.equals("@ok") || assume.equals(ret.toString())) {
					System.out.println("...passed...");
				}
				else {
					System.out.println("value is corrupted...");
					fail("value is corrupted...");
					return;
				}
			}
			catch (Exception e) {
				String assume = testEntry.assume();
				if(e instanceof InvocationTargetException) {
					e.getCause().printStackTrace(System.out);
				}
				else {
					e.printStackTrace(System.out);
				}
				if(assume.equals("@none")) {
					System.out.println("assume : @none");
					System.out.println("...passed...");
					continue;
				}
				if(assume.indexOf("Exception") != -1) {
					try {
						assume = assume.substring(1);
						if((e.getCause().getClass().getName().indexOf(assume) != -1)
							|| (e.getCause().getCause().getClass().getName().indexOf(assume) != -1)) {
							// 指定されている例外が存在する場合はOK
							System.out.println("find the exception : " + testEntry.assume());
							System.out.println("...passed...");
							continue;
						}
					}
					catch (Exception ex) {
					}
				}
				System.out.println("Failed....");
				fail("interruptted by " + e.getClass().getName());
				return;
			}
		}
	}
	private Object getTestParam(Class<?> type, String obj) {
		/*
		 * ArrayList<String> isArray:false
		 * String[] set :true
		 * String... set :true
		 * Set<String> set :false
		 * ...
		 * 配列の扱いもなんとかしないといけない。
		 */
		// nullチェック
		if(obj == null || "null".equals(obj)) {
			return null;
		}
		// dataMapに設定されているデータ
		if(obj.startsWith("#")) {
			return dataMap.get(obj.substring(1));
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
		}
		// セット
		if(Set.class.isAssignableFrom(type)) {
		}
		// マップ
		if(Map.class.isAssignableFrom(type)) {
		}
		// それ以外のクラスの場合、そこにInitのアノーテーション指定があるなら、その初期化方法で、それ以外の場合はデフォルトコンストラクタを利用して。おく。
		return getClassInstance(type);
	}
	private Object makeArray(Class<?> resultType, String obj) {
		if(!obj.contains("[")) {
			return null;
		}
		String target = resultType.getName();
		String[] type = obj.split("\\[", 2);
		Constructor<?> construct = getElementType(type[0]);
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
		}
		/*
		 * とりあえず文字列であると仮定しておく。
		 */
		return array;
	}
	private Constructor<?> getElementType(String target) {
		try {
			if("boolean".equals(target)) {
				return Boolean.class.getConstructor(String.class);
			}
			if("char".equals(target)) {
				return Character.class.getConstructor(String.class);
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
	/**
	 * 対象クラスのデフォルトインスタンスを生成する。
	 * @param type
	 * @return
	 */
	private Object getClassInstance(Class<?> cls) {
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
			return null;
		}
		access = constructor.isAccessible();
		constructor.setAccessible(true);
		try {
			if(init != null) {
				List<Object> dataList = new ArrayList<Object>();
				List<String> paramList = new ArrayList<String>();
				// データリストの作成
				for(String str : init.value()) {
					paramList.add(str);
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
	private void dumpAll(Object obj) {
		if(obj instanceof Object[]) {
			for(Object ob : (Object[])obj) {
				System.out.println(ob);
			}
		}
		else {
			System.out.println(obj);
		}
	}
}
