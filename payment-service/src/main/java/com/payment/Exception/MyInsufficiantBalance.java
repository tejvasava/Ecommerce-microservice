package com.payment.Exception;

public class MyInsufficiantBalance extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MyInsufficiantBalance(String statement){
        super(statement);
		
    }
}
