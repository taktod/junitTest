package com.ttProject.junit.exception;

/**
 * コンストラクタがみつからなかった場合の例外
 * @author taktod
 */
public class ConstructorNotFoundException extends ClassNotFoundException {
	/** シリアルID */
	private static final long serialVersionUID = 2972651788485501378L;
	/**
	 * コンストラクタ
	 */
	public ConstructorNotFoundException() {
		super();
	}
	/**
	 * コンストラクタ
	 * @param s メッセージ
	 */
	public ConstructorNotFoundException(String s) {
		super(s);
	}
	/**
	 * コンストラクタ
	 * @param s メッセージ
	 * @param ex 例外
	 */
	public ConstructorNotFoundException(String s, Throwable ex) {
		super(s, ex);
	}
}
