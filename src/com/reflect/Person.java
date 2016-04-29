package com.reflect;

public class Person implements China{

	private String sex;
	public Person(){
		
	}
	
	public Person(String sex){
		this.sex = sex;
	}
	
	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}
	
	@Override
	public void sayChina() {
		
		System.out.println("hello,china");
	}

	@Override
	public void sayHello(String name, int age) {
		
		System.out.println(name+" "+age);
	}

	
}
