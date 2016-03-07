package com.datamine.NaiveBayes;

import java.io.*;
import java.util.*;


public class CreateTrainAndTestSample {

	
	void filterSpecialWords() throws IOException{
		
		String word;
		ComputeWordsVector cwv = new ComputeWordsVector();
		String fileDir = "E:\\DataMiningSample\\processedSample";
		SortedMap<String, Double> wordMap = new TreeMap<String, Double>();
		
		wordMap = cwv.countWords(fileDir, wordMap);
		cwv.printWordMap(wordMap); //把wordMap输出到文件
		
		File[] sampleDir = new File(fileDir).listFiles();
		for(int i = 0;i<sampleDir.length;i++){
			
			File[] sample = sampleDir[i].listFiles();
			String targetDir = "E:/DataMiningSample/processedSampleOnlySpecial/"+sampleDir[i].getName();
			File targetDirFile = new File(targetDir);
			if(!targetDirFile.exists()){
				targetDirFile.mkdir();
			}
			
			for(int j = 0; j<sample.length;j++){
				
				String fileShortName = sample[j].getName();
				targetDir = "E:/DataMiningSample/processedSampleOnlySpecial/"+sampleDir[i].getName()+"/"+fileShortName;
				FileWriter tgWriter = new FileWriter(targetDir);
				FileReader samReader = new FileReader(sample[j]);
				BufferedReader samBR = new BufferedReader(samReader);
				while((word = samBR.readLine()) != null){
					if(wordMap.containsKey(word))
						tgWriter.append(word+"\n");
				}
				tgWriter.flush();
				tgWriter.close();
				samBR.close();
			}
		}
	}
	
	/**
	 * 创建训练集和测试集
	 * @param fileDir 预处理好的文件路径 E:\DataMiningSample\processedSampleOnlySpecial\
	 * @param trainSamplePercent 训练集占的百分比0.8
	 * @param indexOfSample 一个测试集计算规则  1
	 * @param classifyResultFile 测试样例正确类目记录文件
	 * @throws IOException
	 */
	void createTestSample(String fileDir,double trainSamplePercent,int indexOfSample,String classifyResultFile) throws IOException{
		
		String word,targetDir;
		FileWriter crWriter = new FileWriter(classifyResultFile);//测试样例正确类目记录文件
		File[] sampleDir = new File(fileDir).listFiles();
		
		for(int i =0;i<sampleDir.length;i++){
			
			File[] sample = sampleDir[i].listFiles();
			double testBeginIndex = indexOfSample*(sample.length*(1-trainSamplePercent));
			double testEndIndex = (indexOfSample + 1)*(sample.length*(1-trainSamplePercent));
			
			for(int j = 0;j<sample.length;j++){
				
				FileReader samReader = new FileReader(sample[j]);
				BufferedReader samBR = new BufferedReader(samReader);
				String fileShortName = sample[j].getName();
				String subFileName = fileShortName;
				
				if(j > testBeginIndex && j < testEndIndex){
					targetDir = "E:/DataMiningSample/TestSample"+indexOfSample+"/"+sampleDir[i].getName(); 
					crWriter.append(subFileName + " "+sampleDir[i].getName()+"\n");
				}else{
					targetDir = "E:/DataMiningSample/TrainSample"+indexOfSample+"/"+sampleDir[i].getName();
				}
					
				targetDir = targetDir.replace("\\", "/");
				File trainSamFile = new File(targetDir);
				if(!trainSamFile.exists()){
					trainSamFile.mkdir();
				}
				
				targetDir += "/" + subFileName;
				FileWriter tsWriter = new FileWriter(new File(targetDir));
				while((word = samBR.readLine()) != null)
					tsWriter.append(word+"\n");
				tsWriter.flush();
				
				tsWriter.close();
				samBR.close();
			}
			
		}
		crWriter.close();
	}
	
	
	public static void main(String[] args) throws IOException {
		
		CreateTrainAndTestSample test = new CreateTrainAndTestSample();
		
		String fileDir = "E:/DataMiningSample/processedSampleOnlySpecial";
		double trainSamplePercent=0.8;
		int indexOfSample=1;
		String classifyResultFile="E:/DataMiningSample/classifyResult";
		
		test.createTestSample(fileDir, trainSamplePercent, indexOfSample, classifyResultFile);
		//test.filterSpecialWords();
	}
	
	
}
