package com.datamine.apriori;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 本程序用于读取矩阵型的记录数据，并转换为List<List<String>> 格式数据
 * @author Administrator
 *
 */
public class TxtReader {

	public List<List<String>> getRecord() throws Exception {
		
		List<List<String>> record = new ArrayList<List<String>>();
		
		String encoding = "GBK"; //字符编码 解决中文乱码问题
		String simple = new File("").getAbsolutePath() + File.separator + "data\\Apriori.txt";
		File file = new File(simple);
		
		if(file.isFile() && file.exists()){
			InputStreamReader read = new InputStreamReader(new FileInputStream(file),encoding);
			BufferedReader bufferedReader = new BufferedReader(read);
			String lineTxt = null;
			while((lineTxt = bufferedReader.readLine()) != null){ //读一行文件
				String[] lineString = lineTxt.split(" ");
				List<String> lineList = new ArrayList<String>();
				for(int i = 0; i < lineString.length ; i++){  //处理矩阵中的T、F、YES、NO
					if(lineString[i].endsWith("T") || lineString[i].endsWith("YES"))
						lineList.add(record.get(0).get(i));  //保存 T或者YES对应的头(也就是商品)
					else if (lineString[i].endsWith("F") || lineString[i].endsWith("NO"))
						; //F、NO记录不保存   ：当为F、NO时，说明没有购买响应的商品
					else
						lineList.add(lineString[i]);
				}
				record.add(lineList);
			}
			read.close();
		}else{
			System.out.println("找不到 指定的文件！");
		}
		return record;
	}

}
