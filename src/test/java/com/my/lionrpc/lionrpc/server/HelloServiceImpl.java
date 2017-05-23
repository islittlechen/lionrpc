package com.my.lionrpc.lionrpc.server;

import com.my.lionrpc.annotation.LionService;
import com.my.lionrpc.lionrpc.common.User;

@LionService(IHelloService.class)
public class HelloServiceImpl implements IHelloService{
	
	public String echoHello(String msg){
		return "echo:"+ msg;
	}

	 
	public User echoStudent(User user) {
		user.setName("echo:"+user.getName());
		return user;
	}
}
