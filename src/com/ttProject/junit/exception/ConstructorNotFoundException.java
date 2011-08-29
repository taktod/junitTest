package com.ttProject.junit.exception;

public class ConstructorNotFoundException extends ClassNotFoundException {
	/** シリアルID */
	private static final long serialVersionUID = 2972651788485501378L;
	public ConstructorNotFoundException() {
	}
	public ConstructorNotFoundException(String s) {
		super(s);
	}
	public ConstructorNotFoundException(String s, Throwable ex) {
		super(s, ex);
	}
}
