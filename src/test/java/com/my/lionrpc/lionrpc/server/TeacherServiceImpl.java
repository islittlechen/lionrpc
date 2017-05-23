package com.my.lionrpc.lionrpc.server;

import com.my.lionrpc.annotation.LionService;
import com.my.lionrpc.lionrpc.common.Teacher;

@LionService(ITeacherService.class)
public class TeacherServiceImpl implements ITeacherService {

	@Override
	public Teacher echoTeacher(Teacher t) {
		t.setName("echo:"+t.getName());
		return t;
	}

}
