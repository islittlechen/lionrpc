package com.my.lionrpc.client.spring;

import java.lang.reflect.Proxy;

import org.springframework.beans.factory.FactoryBean;

public class LionServiceFactoryBean<T> implements FactoryBean<T> {
	
	private Class<T> serviceInterface;

	@SuppressWarnings("unchecked")
	public T getObject() throws Exception {
		return (T) Proxy.newProxyInstance(
				serviceInterface.getClassLoader(),
                new Class<?>[]{serviceInterface},
                new LionServiceProxy<T>()
        );
	}

	@Override
	public Class<?> getObjectType() {
		return this.serviceInterface;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public Class<T> getServiceInterface() {
		return serviceInterface;
	}

	public void setServiceInterface(Class<T> serviceInterface) {
		this.serviceInterface = serviceInterface;
	}
	
	

}
