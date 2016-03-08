package com.datamine.NaiveBayes;

import java.io.*;
import java.util.*;

/**
 * 计算文档的属性向量，将所有文档向量化
 * @author Administrator
 */
public class ComputeWordsVector {

	/**
	 * 计算文档的TF属性向量，TFPerDocMap
	 * 计算TF*IDF
	 * @param strDir 处理好的newsgroup文件目录的绝对路径
	 * @param trainSamplePercent 训练样本集占每个类目的比例
	 * @param indexOfSample 测试样例集的起始的测试样例编号      注释：通过这个参数可以将文本分成训练和测试两部分
	 * @param iDFPerWordMap  每个词的IDF权值属性向量
	 * @param wordMap 属性词典map
	 * @throws IOException 
	 */
	public void computeTFMultiIDF(String strDir,double trainSamplePercent,int indexOfSample,
			Map<String, Double> iDFPerWordMap,Map<String,Double> wordMap) throws IOException{
		
		File fileDir = new File(strDir);
		String word;
		SortedMap<String,Double> TFPerDocMap = new TreeMap<String, Double>();
		//注意可以用两个写文件，一个专门写测试样例，一个专门写训练样例，用sampleType的值来表示
		String trainFileDir = "E:/DataMiningSample/docVector/wordTFIDFMapTrainSample"+indexOfSample;
		String testFileDir = "E:/DataMiningSample/docVector/wordTFIDFMapTestSample"+indexOfSample;
		
		FileWriter tsTrainWriter = new FileWriter(new File(trainFileDir)); //往训练文件中写
		FileWriter tsTestWriter = new FileWriter(new File(testFileDir)); //往测试文件中写
		
		FileWriter tsWriter = null;
		File[] sampleDir = fileDir.listFiles();
		
		for(int i = 0;i<sampleDir.length;i++){
			
			String cateShortName = sampleDir[i].getName();
			System.out.println("开始计算: " + cateShortName);
			
			File[] sample = sampleDir[i].listFiles();
			//测试样例集起始文件序号
			double testBeginIndex = indexOfSample*(sample.length*(1-trainSamplePercent));
			//测试样例集的结束文件序号
			double testEndIndex = (indexOfSample+1)*(sample.length*(1-trainSamplePercent));
			System.out.println("文件名_文件数 :" + sampleDir[i].getCanonicalPath()+"_"+sample.length);
			System.out.println("训练数:"+sample.length*trainSamplePercent
					+ " 测试文本开始下标:"+ testBeginIndex+" 测试文本结束下标:"+testEndIndex);
			
			for(int j =0;j<sample.length; j++){
				
				//计算TF，即每个词在该文件中出现的频率
				TFPerDocMap.clear();
				FileReader samReader = new FileReader(sample[j]);
				BufferedReader samBR = new BufferedReader(samReader);
				String fileShortName = sample[j].getName();
				Double wordSumPerDoc = 0.0;//计算每篇文档的总字数
				while((word = samBR.readLine()) != null){
					
					if(!word.isEmpty() && wordMap.containsKey(word)){
						wordSumPerDoc++;
						if(TFPerDocMap.containsKey(word))
							TFPerDocMap.put(word, TFPerDocMap.get(word)+1);
						else
							TFPerDocMap.put(word, 1.0);
					}
				}
				samBR.close();
				
				/*
				 * 遍历 TFPerDocMap，除以文档的总词数wordSumPerDoc 则得到TF
				 * TF*IDF得到最终的特征权值，并输出到文件
				 * 注意：测试样例和训练样例写入的文件不同
				 */
				if(j >= testBeginIndex && j <= testEndIndex)
					tsWriter = tsTestWriter;
				else
					tsWriter = tsTrainWriter;
				
				Double wordWeight;
				Set<Map.Entry<String, Double>> tempTF = TFPerDocMap.entrySet();
				for(Iterator<Map.Entry<String, Double>> mt = tempTF.iterator();mt.hasNext();){
					
					Map.Entry<String, Double> me = mt.next();
					
					//由于计算IDF非常耗时，3万多个词的属性词典初步估计需要25个小时，先尝试认为所有词的IDF都是1的情况
					wordWeight = (me.getValue() / wordSumPerDoc) * iDFPerWordMap.get(me.getKey());
					//wordWeight = (me.getValue() / wordSumPerDoc) * 1.0;
					TFPerDocMap.put(me.getKey(), wordWeight);
				}
				
				tsWriter.append(cateShortName + " ");
				tsWriter.append(fileShortName + " ");
				Set<Map.Entry<String, Double>> tempTF2 = TFPerDocMap.entrySet();
				for(Iterator<Map.Entry<String, Double>> mt = tempTF2.iterator();mt.hasNext();){
					Map.Entry<String, Double> me = mt.next();
					tsWriter.append(me.getKey() + " " + me.getValue()+" ");
				}
				tsWriter.append("\n");
				tsWriter.flush();
				
			}
		}
		tsTrainWriter.close();
		tsTestWriter.close();
		tsWriter.close();
	}
	
	/**
	 * 统计每个词的总出现次数，返回出现次数大于3词的词汇构成最终的属性词典
	 * @param strDir 处理好的newsgroup文件目录的绝对路径
	 * @param wordMap 记录出现的每个词构成的属性词典
	 * @return newWordMap 返回出现次数大于3次的词汇构成最终的属性词典
	 * @throws IOException
	 */
	public SortedMap<String, Double> countWords(String strDir,
			Map<String, Double> wordMap) throws IOException {
		
		File sampleFile = new File(strDir);
		File[] sample = sampleFile.listFiles();
		String word;
		
		for(int i =0 ;i < sample.length;i++){
			
			if(!sample[i].isDirectory()){
				FileReader samReader = new FileReader(sample[i]);
				BufferedReader samBR = new BufferedReader(samReader);
				while((word = samBR.readLine()) != null){
					if(!word.isEmpty() && wordMap.containsKey(word))
						wordMap.put(word, wordMap.get(word)+1);
					else
						wordMap.put(word, 1.0);
				}
				samBR.close();
			}else{
				countWords(sample[i].getCanonicalPath(),wordMap);
			}
		}
		
		/*
		 * 只返回出现次数大于3的单词
		 * 这里为了简单，应该独立一个函数，避免多次运行
		 */
		SortedMap<String,Double> newWordMap = new TreeMap<String, Double>();
		Set<Map.Entry<String, Double>> allWords = wordMap.entrySet();
		for(Iterator<Map.Entry<String, Double>> it = allWords.iterator();it.hasNext();){
			Map.Entry<String, Double> me = it.next();
			if(me.getValue() > 2)
				newWordMap.put(me.getKey(), me.getValue());
		}
		
		System.out.println("newWordMap "+ newWordMap.size());
		
		return newWordMap;
	}
	
	/**
	 * 打印属性词典，到allDicWordCountMap.txt中
	 * @param wordMap 属性词典
	 * @throws IOException 
	 */
	public void printWordMap(Map<String, Double> wordMap) throws IOException{
		
		System.out.println("printWordMap:");
		int countLine = 0;
		File outPutFile = new File("E:/DataMiningSample/docVector/allDicWordCountMap.txt");
		FileWriter outPutFileWriter = new FileWriter(outPutFile);
		
		Set<Map.Entry<String, Double>> allWords = wordMap.entrySet();
		for(Iterator<Map.Entry<String, Double>> it = allWords.iterator();it.hasNext();){
			Map.Entry<String, Double> me = it.next();
			outPutFileWriter.write(me.getKey()+" "+me.getValue()+"\n");
			countLine++;
		}
		outPutFileWriter.close();
		System.out.println("WordMap size : " + countLine);
	}
	
	/**
	 * 词w在整个文档集合中的逆向文档频率idf (Inverse Document Frequency)，
	 * 即文档总数n与词w所出现文件数docs(w, D)比值的对数: idf = log(n / docs(w, D))
	 * 计算IDF，即属性词典中每个词在多少个文档中出现过
	 * @param strDir 处理好的newsgroup文件目录的绝对路径
	 * @param wordMap 属性词典
	 * @return 单词的IDFMap
	 * @throws IOException 
	 */
	public SortedMap<String,Double> computeIDF(String strDir,Map<String, Double> wordMap) throws IOException{
		
		File fileDir = new File(strDir);
		String word;
		SortedMap<String,Double> IDFPerWordMap = new TreeMap<String, Double>();
		Set<Map.Entry<String, Double>> wordMapSet = wordMap.entrySet();
		
		for(Iterator<Map.Entry<String, Double>> it = wordMapSet.iterator();it.hasNext();){
			Map.Entry<String, Double> pe = it.next();
			Double countDoc = 0.0; //出现字典词的文本数
			Double sumDoc = 0.0; //文本总数
			String dicWord = pe.getKey();
			File[] sampleDir = fileDir.listFiles();
			
			for(int i =0;i<sampleDir.length;i++){
				
				File[] sample = sampleDir[i].listFiles();
				for(int j = 0;j<sample.length;j++){
					
					sumDoc++; //统计文本数
					
					FileReader samReader = new FileReader(sample[j]);
					BufferedReader samBR = new BufferedReader(samReader);
					boolean isExist = false;
					while((word = samBR.readLine()) != null){
						if(!word.isEmpty() && word.equals(dicWord)){
							isExist = true;
							break;
						}
					}
					if(isExist)
						countDoc++;
					
					samBR.close();
				}
			}
			//计算单词的IDF
			//double IDF = Math.log(sumDoc / countDoc) / Math.log(10);
			double IDF = Math.log(sumDoc / countDoc);
			IDFPerWordMap.put(dicWord, IDF);
		}
		return IDFPerWordMap;
	}
	
	
	
	public static void main(String[] args) throws IOException {
		
		ComputeWordsVector wordsVector = new ComputeWordsVector();
		
		String strDir = "E:\\DataMiningSample\\processedSample";
		Map<String, Double> wordMap = new TreeMap<String, Double>();
		
		//属性词典
		Map<String, Double> newWordMap = new TreeMap<String, Double>();
		
		newWordMap = wordsVector.countWords(strDir,wordMap);
		
		//wordsVector.printWordMap(newWordMap);
		//wordsVector.computeIDF(strDir, newWordMap);
		
//		double trainSamplePercent = 0.8;
//		int indexOfSample = 1;
//		Map<String, Double> iDFPerWordMap = null;
//		
//		wordsVector.computeTFMultiIDF(strDir, trainSamplePercent, indexOfSample, iDFPerWordMap, newWordMap);
		
		//test();
	}
	
	public static void test(){
		
		double sumDoc  = 18828.0;
		double countDoc = 229.0;
		
		double IDF1 = Math.log(sumDoc / countDoc) / Math.log(10);
		double IDF2 = Math.log(sumDoc / countDoc) ;
		
		System.out.println(IDF1);
		System.out.println(IDF2);
		
		System.out.println(Math.log(10));
	}
	
}
