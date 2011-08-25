package com.ttProject.junit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ttProject.junit.library.ClassFinder;
import com.ttProject.junit.library.MethodChecker;

public class EntryTest {
	private String packagePath = "com.ttProject";
	private MethodChecker checker = null;

	@Before
	public void setUp() throws Exception {
		try {
			ClassFinder cf = new ClassFinder();
			checker = new MethodChecker(cf.getAppClass(packagePath));
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
