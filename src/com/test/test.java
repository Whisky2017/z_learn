package com.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

public class test {
	
	
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

	public static void main(String[] args) throws Exception {
		
		String fileName = "c:\\test.txt";
		String encoding = "UTF-8";
		String result = readFile(fileName,encoding);
		
		String[] s = result.split(",");
		int [] r = new int[s.length];
		for(int i=0;i<s.length;i++){
			
			if(s[i].contains("."))
				System.out.println("这是ip"+s[i]);
			else
				System.out.println("这是数字"+s[i]);
		}
		
		
		
	}
	
	
	
	
}
