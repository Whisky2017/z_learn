package com.reflect;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

interface fruit{
	public abstract void eat();
}

class Apple implements fruit{
	public void eat(){
		System.out.println("Apple");
	}
}

class Orange implements fruit{
	public void eat(){
		System.out.println("Orange");
	}
}


class Factory{
	public static fruit getInstance(Class<?> Clazz) throws Exception{
		fruit f = null;
//		if("Apple".equals(fruitName))
//			f = new Apple();
//		if("Orange".equals(fruitName))
//			f = new Orange();
		
		//f= (fruit) Class.forName(ClassName).newInstance();
		f = (fruit) Clazz.newInstance();
		return f;
	}
}

class hello {
	
	public static void main(String[] args) throws Exception {
		
//		fruit f = Factory.getInstance("Orange");
//		f.eat();
		fruit f = Factory.getInstance(Apple.class);
		if(f!=null)
			f.eat();
		
	}

}


