package com.my.lionrpc.lionrpc.server;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:server-spring.xml"})
public class ServerTest {

	@Test
	public void test(){
		try {
			Object o = new Object();
			synchronized (o) {
				o.wait();
			}
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
