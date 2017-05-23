package com.my.lionrpc.client.spring;

import java.util.Iterator;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

public class LionServiceProxyFactory implements BeanDefinitionRegistryPostProcessor,PriorityOrdered{

	
	private Map<String,String> serviceMap;
	
	
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
		 if(null != serviceMap){
				Iterator<String> iterator = serviceMap.keySet().iterator();
				while(iterator.hasNext()){
					String beanId = iterator.next();
					String className = serviceMap.get(beanId);
					GenericBeanDefinition definition = new GenericBeanDefinition();
					 
					definition.getPropertyValues().add("serviceInterface", className);
				    definition.setBeanClass(LionServiceFactoryBean.class);
				    beanDefinitionRegistry.registerBeanDefinition(beanId, definition);
				}
			}
	}

	public Map<String, String> getServiceMap() {
		return serviceMap;
	}

	public void setServiceMap(Map<String, String> serviceMap) {
		this.serviceMap = serviceMap;
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}
}
