package com.my.lionrpc.common.exception;

public class InvokeTimeOutException extends RuntimeException{

	private static final long serialVersionUID = 5226498736989677828L;

	public InvokeTimeOutException(String msg) {
		super(msg);
	}
}
