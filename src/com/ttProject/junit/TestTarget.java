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
	public int hoge(Integer a, int b) {
		return a+b;
	}
}
