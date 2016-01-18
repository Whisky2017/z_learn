package com.model;

public class SingletonTest {

	public static void main(String[] args) {
		
		Singleton singletonOne =  Singleton.getSingleton();
		
		Singleton singletonTwo =  Singleton.getSingleton();
		
		if(singletonOne.equals(singletonTwo))
		{
			System.out.println("singletonOne 和 singletonTwo 代表的是同一个实例");
		}else
		{
			System.out.println("singletonOne 和 singletonTwo 代表的是不同实例");
		}
		
	}
	
}
