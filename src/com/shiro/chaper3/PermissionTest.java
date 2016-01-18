package com.shiro.chaper3;

import java.util.Arrays;

import org.apache.shiro.authz.UnauthorizedException;
import org.junit.Assert;
import org.junit.Test;

public class PermissionTest extends BaseTest{

	@Test
	public void testIsPermitted(){
		
		login("classpath:shiro/shiro-permission.ini","zhang","123");
		
		//判断拥有权限：user:create  
	    Assert.assertTrue(subject().isPermitted("user:create"));  
	    //判断拥有权限：user:update and user:delete  
	    Assert.assertTrue(subject().isPermittedAll("user:update", "user:delete"));  
	    //判断没有权限：user:view  
	    Assert.assertFalse(subject().isPermitted("user:view"));
	}
	
	
	
	
}
