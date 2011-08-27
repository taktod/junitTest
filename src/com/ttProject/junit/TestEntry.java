package com.ttProject.junit;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ttProject.junit.library.ClassFinder;
import com.ttProject.junit.library.MethodChecker;

public class TestEntry {
	private String packagePath = "com.ttProject";
	private Map<String, Object> extraData = new HashMap<String, Object>();
	private MethodChecker checker = null;

	public void setPackagePath(String path) {
		packagePath = path;
	}
	public void setData(String key, Object value) {
		extraData.put(key, value);
	}
	@Before
	public void setUp() throws Exception {
		try {
			ClassFinder cf = new ClassFinder();
			checker = new MethodChecker(cf.getAppClass(packagePath), extraData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		if(checker == null) {
			org.junit.Assert.fail("Checker object is null...");
		}
		checker.checkStart();
	}
}
