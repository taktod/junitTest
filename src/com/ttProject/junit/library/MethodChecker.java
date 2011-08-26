package com.ttProject.junit.library;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.ttProject.junit.annotation.Init;
import com.ttProject.junit.annotation.Junit;
import com.ttProject.junit.annotation.Test;

import static org.junit.Assert.*;

public class MethodChecker {
	/** チェック対象クラス */
	private Set<Class<?>> classSet = null;
	/**
	 * コンストラクタ
	 * @param classSet
	 */
	public MethodChecker(Set<Class<?>> classSet) {
		this.classSet = classSet;
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
					ret = method.invoke(getClassInstance(cls), dataList.toArray());
				}
				method.setAccessible(access);
				System.out.println("assume : " + testEntry.assume() + " result : " + ret);
				if(ret == null) {
					ret = "null";
				}
				if(testEntry.assume().equals(ret.toString())) {
					System.out.println("...passed...");
				}
				else {
					System.out.println("value is corrupted...");
					fail("value is corrupted...");
					return;
				}
			}
			catch (Exception e) {
				if(testEntry.assume().indexOf("Exception") != -1) {
					try {
						if((e.getClass().getName().indexOf(testEntry.assume()) != -1)
							|| (e.getCause().getClass().getName().indexOf(testEntry.assume()) != -1)
							|| (e.getCause().getCause().getClass().getName().indexOf(testEntry.assume()) != -1)) {
							// 指定されている例外が存在する場合はOK
							System.out.println("find the exception : " + testEntry.assume());
							System.out.println("...passed...");
							continue;
						}
					}
					catch (Exception ex) {
					}
				}
				e.printStackTrace(System.out);
				System.out.println("Failed....");
				fail("interruptted by " + e.getClass().getName());
				return;
			}
		}
	}
	private Object getTestParam(Class<?> type, String obj) {
		if(obj == null || "null".equals(obj)) {
			return null;
		}
		if(type == String.class) {
			return obj;
		}
		if(type == int.class || type == Integer.class) {
			return new Integer(obj);
		}
		if(type == long.class || type == Long.class) {
			return new Long(obj);
		}
		if(type == float.class || type == Float.class) {
			return new Float(obj);
		}
		if(type == double.class || type == Double.class) {
			return new Double(obj);
		}
		// それ以外のクラスの場合、そこにInitのアノーテーション指定があるなら、その初期化方法で、それ以外の場合はデフォルトコンストラクタを利用して。おく。
		return getClassInstance(type);
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
		// まずコンストラクタにアノーテーションがついているかチェック。
		for(Constructor<?> construct : cls.getDeclaredConstructors()) {
			constructor = construct;
			init = construct.getAnnotation(Init.class);
			if(init != null) {
				break;
			}
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
}
