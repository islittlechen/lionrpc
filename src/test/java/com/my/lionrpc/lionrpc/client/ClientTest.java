package com.my.lionrpc.lionrpc.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.my.lionrpc.lionrpc.common.Teacher;
import com.my.lionrpc.lionrpc.common.User;
import com.my.lionrpc.lionrpc.server.IHelloService;
import com.my.lionrpc.lionrpc.server.ITeacherService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:consumer-spring.xml"})
public class ClientTest {
	
	@Autowired
	private IHelloService service;
	
	@Autowired
	private ITeacherService tservie;

	long count = 0l;
	
	@Test
	public void test(){
		
		for(int i = 0; i< 10 ;i++){
			User user = new User();
			Teacher teacher = new Teacher();
			teacher.setName("teacher");
			teacher.setAge(23);
			user.setTeacher(teacher);
			user.setAdmin(true);
			user.setAge(12);
			user.setCurrentTime(System.currentTimeMillis());
			user.setLevel('A');
			user.setMoney(125d);
			user.setName("xiaoming");
			user.setSex((short)1);
			user.setSore(82.3f);
			user.setValide((byte)1);
			List<Teacher> teachers = new ArrayList<Teacher>(); 
			teacher = new Teacher();
			teacher.setName("teacher1");
			teacher.setAge(23);
			teachers.add(teacher);
			teacher = new Teacher();
			teacher.setName("teacher2");
			teacher.setAge(23);
			teachers.add(teacher);
			teacher = new Teacher();
			teacher.setName("teacher3");
			teacher.setAge(23);
			teachers.add(teacher);
			
			Map<String,Teacher> tm = new HashMap<String,Teacher>();
			teacher = new Teacher();
			teacher.setName("teacher12");
			teacher.setAge(23);
			tm.put(teacher.getName(), teacher);
			teacher = new Teacher();
			teacher.setName("teacher22");
			teacher.setAge(23);
			tm.put(teacher.getName(), teacher);
			user.setList(teachers);
			user.setMap(tm);
			User result = service.echoStudent(user);
			System.out.println(result);
			Teacher tr = tservie.echoTeacher(teacher);
			System.out.println(tr);
		}
		
	}
}
