package com.datamine.apriori;

import java.util.List;


public class TestApriori {

	public static void main(String[] args) throws Exception {
		
		//输入数据
		TxtReader reader = new TxtReader();
		List<List<String>> record = reader.getRecord();
		//最小支持度
		double support = 0.2;
		//最小置信度
		double conf = 0.8;
	
		Apriori apriori = new Apriori();

		apriori.run(record, support, conf);
	}

}
