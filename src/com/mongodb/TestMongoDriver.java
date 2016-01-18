package com.mongodb;

import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.google.gson.Gson;
import com.mongodb.util.JSON;

public class TestMongoDriver {

	
	@Test
	public void testCRUD() throws UnknownHostException, MongoException{
		
		//链接mongodb
		Mongo mongo = new Mongo("localhost",27017);
		
		//打开数据库test
		DB db = mongo.getDB("test");
		
		//遍历所有集合的名字
		Set<String> colls = db.getCollectionNames();
		for(String s : colls){
			System.out.println(s);
			// 先删除所有Collection(类似于关系数据库中的"表")
			if(!s.equals("system.indexes"))
				db.getCollection(s).drop();
		}
		
		//取得集合emp(若:emp不存在,mongodb将自动创建该集合)
		DBCollection coll = db.getCollection("emp");
		
		//delete all
		DBCursor dbCursor = coll.find();
		for(DBObject dbObject : dbCursor){
			coll.remove(dbObject);
		}
		
		//create
		BasicDBObject doc = new BasicDBObject("name","小江").
				append("sex", "男").
				append("address",new BasicDBObject(
						"postcode","201202").
						append("street", "深南大道").
						append("city", "深圳")
				);
		coll.insert(doc);
		
		//retrieve
		BasicDBObject docFind = new BasicDBObject("name","小江");
		DBObject findResult = coll.findOne(docFind);
		System.out.println(findResult);
		
		//update
		doc.put("sex","MALE");// 把sex属性从"男"，改成"MALE"
		coll.update(docFind, doc);
		findResult = coll.findOne(docFind);
		System.out.println(findResult);
		
		
		coll.dropIndexes();// 先删除所有索引
		//create index
		coll.createIndex(new BasicDBObject("name",1)); // 1代表升序
		
		//复杂对象
		UserData userData = new UserData("jimmy","123456");
		Set<String> pets = new HashSet<String>();
		pets.add("cat");
		pets.add("dog");
		Map<String, String> favoriteMovies = new HashMap<String, String>();
		favoriteMovies.put("dragons", "Dragons II");
		favoriteMovies.put("avator", "Avator I");
		userData.setFavoriteMovies(favoriteMovies);
		userData.setPets(pets);
		userData.setBirthday(getDate(1990,5,1));
		BasicDBObject objUser = new BasicDBObject("key","jimmy").
				append("value", toDBOject(userData));
		coll.insert(objUser);
		System.out.println(coll.findOne(objUser));
		
		
	}

	private DBObject toDBOject(Object obj) {
		System.out.println(obj);
		Gson gson = new Gson();
		String json = gson.toJson(obj);
		System.out.println(json);
		return (DBObject) JSON.parse(json);
	}

	private Date getDate(int year, int month, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(year, month-1, day);
		return calendar.getTime();
	}
	
	
}
