package com.mybatis.mapping;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.mybatis.domain.User;

public interface UserMapperI {

	//使用@Insert注解指明add方法要执行的SQL
	@Insert("insert into users(name,age) values(#{name},#{age})")
	public int add(User user);
	
	@Delete("delete from users where id=#{id}")
	public int deleteById(int id);
	
	@Update("update users set name=#{name},age=#{age} where id=#{id}")
	public int update(User user);
	
	@Select("select * from users where id =#{id}")
	public User getUserById(int id);
	
	@Select("select * from users")
	public List<User> getAllUser();
	
}
