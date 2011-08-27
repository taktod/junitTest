package com.ttProject.junit.library;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import com.ttProject.junit.annotation.Junit;

/**
 * 前準備としてクラス情報を取得する。
 * @author taktod
 */
public class ClassFinder {
	/** 分割文字(ディレクトリ) */
	static String DIR_SEPARATOR = File.separator;
	/** 分割文字(パス) */
	static String PATH_SEPARATOR = System.getProperty("path.separator");
	/** 分割文字(パッケージ) */
	static String PACKAGE_SEPARATOR = "\\.";

	/**
	 * targetPathの文字列と対応しているクラスを探す。
	 * @param targetPath
	 * @return
	 */
	public Set<Class<?>> getAppClass(String targetPath) {
		Set<Class<?>> classSet = new HashSet<Class<?>>();
		String pathes = System.getProperty("java.class.path");
		String[] pathList = pathes.split(PATH_SEPARATOR);
		for(String path : pathList) {
			if(path.endsWith(".jar") || path.endsWith(".zip")) {
				// とりあえずjarになっているデータは無視することにする。
//				JarInputStream jis = null;
			}
			else {
				// packageのpathをFile pathに変更する。
//				String dir = path + DIR_SEPARATOR + targetPath.replaceAll(PACKAGE_SEPARATOR, DIR_SEPARATOR);
				String dir = path;
				for(String str : targetPath.split(PACKAGE_SEPARATOR)) {
					dir += DIR_SEPARATOR + str;
				}
				try {
					File directory = new File(dir);
					findClass(classSet, directory, targetPath);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return classSet;
	}
	/**
	 * クラスを見つける。
	 * @param findedClassSet
	 * @param dir
	 * @param p
	 */
	private void findClass(Set<Class<?>> findedClassSet, File dir, String p) {
		String path = p + ".";
		File[] files = dir.listFiles();
		if(files != null) {
			for(File file : files) {
				if(file.isDirectory()) {
					// リカーシブに検索する。
					findClass(findedClassSet, file, path + file.getName());
				}
				else if(file.isFile() && file.getName().endsWith(".class")) {
					try {
						// このクラスにJunitTargetのアノーテーションがついているものがあればテスト候補にする。
						checkClass(findedClassSet, Class.forName(path + file.getName().replaceFirst(".class", "")));
					}
					catch (Exception e) {
					}
				}
			}
		}
	}
	/**
	 * クラスを確認する。
	 * @param findedClassSet
	 * @param targetClass
	 */
	private void checkClass(Set<Class<?>> findedClassSet, Class<?> targetClass) {
		// targetClassがJunitTargetのアノーテーションをもっているか確認する。
		// 登録されているMethodをすべて列挙する。
		for(Method method : targetClass.getDeclaredMethods()) {
			// すべてのmethodを取得する
			if(method.getAnnotation(Junit.class) != null) {
				findedClassSet.add(targetClass);
			}
		}
	}
}
