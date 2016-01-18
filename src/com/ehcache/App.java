package com.ehcache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public class App {

	public static void main(String[] args) throws Exception {
		
		CacheManager manager = CacheManager.create();
		
		//取出所有的CacheName
		String names[] = manager.getCacheNames();
		System.out.println("-------all cache names----------");
		for(String name : names){
			System.out.println(name);
		}
		
		System.out.println("------------------------------");
		//得到一个cache对象
		Cache cache1 = manager.getCache(names[0]);
		
		//向cache1对象里添加缓存
		cache1.put(new Element("key1","value1"));
		Element element = cache1.get("key1");
		
		//读取缓存
		System.out.println("key1 \t = " + element.getObjectValue());
		
		//手动创建一个cache(ehcache里必须有defaultCache存在，"test"可以替换成任何值)
		Cache cache2 = new Cache("test", 1, true, false, 2, 3);
		manager.addCache(cache2);
		
		cache2.put(new Element("xiaojiang","小江"));
		
		//故意提顿1.5秒，以验证是否过期
		Thread.sleep(1500);
		
		Element e = cache2.get("xiaojiang");
		
		//1.5<2 不会过期
		if(e != null){
			System.out.println("xiaojiang \t = " + e.getObjectValue());
		}
		
		//再等0.5s，总时长1.5 +　0.5　＞= min(2,3),过期
		Thread.sleep(500);
		e = cache2.get("xiaojiang");
		if(e != null){
			System.out.println("xiaojiang \t = " + e.getObjectValue());
		}
		
		//取出一个不存在的缓存项
		System.out.println("fake \t= " + cache2.get("fake"));
		
		manager.shutdown();
		
	
		
	}
	
}
