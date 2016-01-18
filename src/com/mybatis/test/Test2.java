package com.mybatis.test;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import com.mybatis.domain.User;
import com.mybatis.util.MyBatisUtil;

public class Test2 {

	@Test
	public void testAdd(){
		
		SqlSession sqlSession = MyBatisUtil.getSqlSession(false);
		
		/*
		 * 映射sql标识字符串
		 * com.mybatis.mapping.userMapper是userMapper.xm;文件中Mapper标签中namespace属性值
		 * addUser是insert标签的id属性值，通过insert标签的id属性值可以找到要执行的SQL
		 */
		
		String statement = "com.mybatis.mapping.userMapper.addUser";
		
		User user = new User();
		user.setName("用户孤傲苍狼");
		user.setAge(20);
		
		//执行插入操作
		int result = sqlSession.insert(statement, user);
		
		//手动提交事务
		sqlSession.commit();
		
		//使用Sqlsession执行完SQL之后需要关闭Sqlsession
		sqlSession.close();
		
		System.out.println(result);
	}
	
	@Test
	public void testDelete(){

		SqlSession sqlSession = MyBatisUtil.getSqlSession(true);
		/*
		 * 映射sql标识字符串
		 * com.mybatis.mapping.userMapper是userMapper.xm;文件中Mapper标签中namespace属性值
		 * deleteUser是insert标签的id属性值，通过insert标签的id属性值可以找到要执行的SQL
		 */
		String statement = "com.mybatis.mapping.userMapper.deleteUser";
		
		//执行删除操作
		int result = sqlSession.delete(statement, 5);
		
		//使用Sqlsession执行完SQL之后需要关闭Sqlsession
		sqlSession.close();
		
		System.out.println(result);
	}
	
	@Test
	public void testGetAll(){
		
		SqlSession sqlSession = MyBatisUtil.getSqlSession(true);
		/*
		 * 映射sql标识字符串
		 * com.mybatis.mapping.userMapper是userMapper.xm;文件中Mapper标签中namespace属性值
		 * deleteUser是insert标签的id属性值，通过insert标签的id属性值可以找到要执行的SQL
		 */
		String statement = "com.mybatis.mapping.userMapper.getAllUsers";
		
		List<User> listUsers = sqlSession.selectList(statement);
		
		sqlSession.close();
		
		System.out.println(listUsers);
	}
	
	
}
