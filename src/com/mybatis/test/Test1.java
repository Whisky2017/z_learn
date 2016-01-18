package com.mybatis.test;

import java.io.InputStream;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.mybatis.domain.User;

public class Test1 {

	public static void main(String[] args) {
		
		//mybatis的配置文件
		String resource = "conf.xml";
		
		//使用类加载器加载mybatis的配置文件（它也加载关联的映射文件）
		InputStream is = Test1.class.getClassLoader().getResourceAsStream(resource);
		
		//构建sqlSession工厂
		SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(is);
		
		//创建能执行映射文件中的sql的sqlSession
		SqlSession session = sessionFactory.openSession();
		
		/**
		 * 映射sql的标识字符串，
		 * com.mybatis.userMapper是userMapper.xml文件中mapper标签的namespace属性值，
		 * getUser是select标签的id属性值，通过select标签的id属性值就可以找到要执行的sql
		 */
		
		String statement = "com.mybatis.mapping.userMapper.getUser";//映射sql的标识字符串
		
		//执行查询返回一个唯一user对象的sql
		User user = session.selectOne(statement,1);
		
		System.out.println(user);
		
		
	}
	
}
