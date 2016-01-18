package com.shiro.chaper2;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.apache.shiro.util.ThreadContext;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

public class shiroTest {
	
	@Test
	public void testShiro() {
		
		//1.获取SecurityManager工厂，此处使用ini配置文件初始化SecurityManager
		Factory<SecurityManager> factory = 
				new IniSecurityManagerFactory("classpath:shiro/shiro.ini");
		
		//2.得到SecurityManager实例，并绑定给SecurityUtils
		SecurityManager securityManager = factory.getInstance();
		SecurityUtils.setSecurityManager(securityManager);
		
		//3.得到Subject及创建用户名/密码身份验证token（用户名/凭证）
		Subject subject = SecurityUtils.getSubject();
		UsernamePasswordToken token = 
				new UsernamePasswordToken("zhang", "123");
		
		try {
			//4.登录，即身份验证
			subject.login(token);
		} catch (AuthenticationException e) {
			//5.登录失败
			//e.printStackTrace();
		}
		
		System.out.println(subject.isAuthenticated());
		//6.退出
		subject.logout();
		
		System.out.println(subject.isAuthenticated());
	}
	
	@Test
	public void testCustomRealm(){
		//1、获取SecurityManager工厂，此处使用Ini配置文件初始化SecurityManager
        Factory<org.apache.shiro.mgt.SecurityManager> factory =
                new IniSecurityManagerFactory("classpath:shiro/shiro_realm.ini");

        //2、得到SecurityManager实例 并绑定给SecurityUtils
        org.apache.shiro.mgt.SecurityManager securityManager = factory.getInstance();
        SecurityUtils.setSecurityManager(securityManager);

        //3、得到Subject及创建用户名/密码身份验证Token（即用户身份/凭证）
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken("zhang", "123");

        try {
            //4、登录，即身份验证
            subject.login(token);
        } catch (AuthenticationException e) {
            //5、身份验证失败
            e.printStackTrace();
        }

      System.out.println(subject.isAuthenticated()); //断言用户已经登录

        //6、退出
        subject.logout();
	}
	
	@Test
	public void testJDBCRealm(){
		//1、获取SecurityManager工厂，此处使用Ini配置文件初始化SecurityManager
        Factory<org.apache.shiro.mgt.SecurityManager> factory =
                new IniSecurityManagerFactory("classpath:shiro/shiro_jdbc_realm.ini");

        //2、得到SecurityManager实例 并绑定给SecurityUtils
        org.apache.shiro.mgt.SecurityManager securityManager = factory.getInstance();
        SecurityUtils.setSecurityManager(securityManager);

        //3、得到Subject及创建用户名/密码身份验证Token（即用户身份/凭证）
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken("zhang", "123");

        try {
            //4、登录，即身份验证
            subject.login(token);
        } catch (AuthenticationException e) {
            //5、身份验证失败
            e.printStackTrace();
        }

        System.out.println(subject.isAuthenticated()); //断言用户已经登录

        //6、退出
        subject.logout();
	}
	
	@After
    public void tearDown() throws Exception {
        ThreadContext.unbindSubject();//退出时请解除绑定Subject到线程 否则对下次测试造成影响
    }
	
	
}
