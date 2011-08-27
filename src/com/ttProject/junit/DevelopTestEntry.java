package com.ttProject.junit;

import org.junit.Test;

public class DevelopTestEntry extends TestEntry {
	@Override
	public void setUp() throws Exception {
		setPackagePath("com.ttProject.junit");
		super.setUp();
	}
	@Override
	public void tearDown() throws Exception {
		super.tearDown();
	}
	@Test
	@Override
	public void test() {
		super.test();
	}
}
