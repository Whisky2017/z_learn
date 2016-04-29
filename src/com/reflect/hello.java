package com.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;


public class hello {
	
	public static void main(String[] args) throws Exception {
		
		Class<?> demo = null;
		Class<?> demo1 = null;
		Class<?> demo2 = null;
		demo = Class.forName("com.reflect.Person");
		demo1 = Person.class;
		demo2 = new Person().getClass();
		
		Method method = demo.getMethod("sayChina");
		method.invoke(demo.newInstance());
		
		method = demo.getMethod("sayHello", String.class,int.class);
		method.invoke(demo.newInstance(), "xiaojiang",27);
	}

}


