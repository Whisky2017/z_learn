package com.datamine.DecisionTree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;



public class MainC45 {

    private static final List<ArrayList<String>> dataList = new ArrayList<ArrayList<String>>(); //数据
    private static final List<String> attributeList = new ArrayList<String>(); //标签属性
    
    public static void main(String args[]) throws IOException{
        
        DecisionTree dt = new DecisionTree();
        
        String filename = "data/DecisionTree.data";
        
        //将数据读入
        File file = new File(filename);
        FileInputStream fs = new FileInputStream(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(fs));
        String line = "";
        boolean flag = true;
        while((line=br.readLine())!=null){
        	
        	if(flag == true){ //第一行读取标签属性
        		String[] text = line.split("\t");
        		for(int i = 0;i<text.length;i++){
        			attributeList.add(text[i]);
        		}
        		flag = false;
        	}else{ //读取数据
        		String[] text = line.split("\t");
        		ArrayList<String> temp = new ArrayList<String>();
        		for(int i =0 ;i<text.length;i++){
        			temp.add(text[i]);
        		}
        		dataList.add(temp);
        	}
        }
        
        dt.createDT(dataList,attributeList);
    }

	
}
