package com.my.lionrpc.lionrpc.common;

import java.util.List;
import java.util.Map;

public class User {
	
	private String name;
	
	private int age;
	
	private boolean isAdmin;
	
	private double money;
	
	private short sex;
	
	private byte valide;
	
	private long currentTime;
	
	private float sore;
	
	private char level;
	
	private Teacher teacher;
	
	private List<Teacher> list;
	
	private Map<String,Teacher> map;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public short getSex() {
		return sex;
	}

	public void setSex(short sex) {
		this.sex = sex;
	}

	public byte getValide() {
		return valide;
	}

	public void setValide(byte valide) {
		this.valide = valide;
	}

	public long getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(long currentTime) {
		this.currentTime = currentTime;
	}

	public float getSore() {
		return sore;
	}

	public void setSore(float sore) {
		this.sore = sore;
	}

	public char getLevel() {
		return level;
	}

	public void setLevel(char level) {
		this.level = level;
	}

	public Teacher getTeacher() {
		return teacher;
	}

	public void setTeacher(Teacher teacher) {
		this.teacher = teacher;
	}

	public List<Teacher> getList() {
		return list;
	}

	public void setList(List<Teacher> list) {
		this.list = list;
	}

	public Map<String, Teacher> getMap() {
		return map;
	}

	public void setMap(Map<String, Teacher> map) {
		this.map = map;
	}
	
	
	

}
