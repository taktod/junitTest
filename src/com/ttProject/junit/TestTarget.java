package com.ttProject.junit;

import com.ttProject.junit.annotation.Init;
import com.ttProject.junit.annotation.Junit;
import com.ttProject.junit.annotation.Test;

public class TestTarget {
	private String data;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	}
	public TestTarget() {
		data = "null";
	}
	@Init("shine")
	public TestTarget(String hogehoge) {
		data = hogehoge;
	}
	// nullやほかの型をいれるのは不可能みたい。
	@Junit({
		@Test(params={"15"}, assume="16"),
		@Test(params={"20"}, assume="21")
	})
	public String test(Long l) {
		System.out.println(data);
		return Long.toString(l + 1L);
	}
	@Junit({
		@Test(params={"test", "15"}, assume="test15"),
		@Test(params={"135", "135"}, assume="270")
	})
	public String testTarget(String a, int b) {
		return a + b;
	}
}
