package com.datamine.FPtree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Reader {

	public static String readFile(String fileName, String encoding) throws Exception{
		
		File file = new File(fileName);
		
		FileInputStream inStream = new FileInputStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inStream,encoding));
		String line = new String();
		String text = new String();
		
		while((line = reader.readLine()) != null){
			text += line;
		}
		reader.close();
		
		return text;
	}
	
	/**
	 * 以非整齐的二维表的形式读取文件
	 * @param filName 文件名
	 * @param regex 文件行内的分隔符
	 * @param encoding 编码方式
	 * @return matrix 二维表
	 * @throws Exception 
	 */
	public static List<String[] > readAsMatrix(String fileName,
			String regex, String encoding) throws Exception{
	
		List<String[]> matrix = new ArrayList<String[]>();
		File file = new File(fileName);
		
		FileInputStream inStream = new FileInputStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inStream,encoding));
		String line = new String();
		while((line = reader.readLine()) != null){
			
				matrix.add(line.split(regex));
			
		}
		
		reader.close();
		return matrix;
	}
	
    public static String stringToAscii(String value)  
    {  
        StringBuffer sbu = new StringBuffer();  
        char[] chars = value.toCharArray();   
        for (int i = 0; i < chars.length; i++) {  
            if(i != chars.length - 1)  
            {  
                sbu.append((int)chars[i]).append(",");  
            }  
            else {  
                sbu.append((int)chars[i]);  
            }  
        }  
        return sbu.toString();  
    }  
    
	public static void main(String[] args) throws Exception {
		
		List<String[]> matrix = readAsMatrix("data\\FPtree.txt", "	", "UTF-8");
		
		Map<String,Integer> countMap = new HashMap<String, Integer>();
		
		for(String[] line : matrix){
			for(String idName : line){
				
				if(countMap.containsKey(idName))
					countMap.put(idName, countMap.get(idName)+1);
				else
					countMap.put(idName, 1);
			}
		}
		
		//utf-8头字符65279 
		for(Map.Entry<String,Integer> entry :countMap.entrySet())
			System.out.println(stringToAscii(entry.getKey())+" " + entry.getValue());
		
	}
	
}
