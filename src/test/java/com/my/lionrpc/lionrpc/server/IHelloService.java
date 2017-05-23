package com.my.lionrpc.lionrpc.server;

import com.my.lionrpc.lionrpc.common.User;

public interface IHelloService {
	
	public String echoHello(String msg);
	
	public User echoStudent(User user);

}
