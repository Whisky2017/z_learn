package com.datamine.kmeans;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

/**
 * 计算文档的属性向量，将所有文档向量化
 * @author Administrator
 */
public class ComputeWordsVector {

	/**
	 * 计算文档的TF-IDF属性向量，返回Map<文件名，<特征词，TF-IDF值>>
	 * @param testSampleDir 处理好的聚类样本测试样例集
	 * @return 所有测试样例的属性向量构成的map
	 * @throws IOException
	 */
	public Map<String,Map<String,Double>> computeTFMultiIDF(String testSampleDir) throws IOException{
		
		String word;
		Map<String,Map<String,Double>> allTestSampleMap = new TreeMap<String, Map<String,Double>>();
		Map<String,Double> idfPerWordMap = computeIDF(testSampleDir);
		Map<String,Double> tfPerDocMap = new TreeMap<String, Double>();
		
		File[] samples = new File(testSampleDir).listFiles();
		System.out.println("the total number of test files is " + samples.length);
		for(int i = 0;i<samples.length;i++){
			
			tfPerDocMap.clear();
			FileReader samReader = new FileReader(samples[i]);
			BufferedReader samBR = new BufferedReader(samReader);
			Double wordSumPerDoc = 0.0; //计算每篇文档的总词数
			while((word = samBR.readLine()) != null){
				if(!word.isEmpty()){
					wordSumPerDoc++;
					if(tfPerDocMap.containsKey(word))
						tfPerDocMap.put(word, tfPerDocMap.get(word)+1.0);
					else
						tfPerDocMap.put(word, 1.0);
				}
			}
			
			Double maxCount = 0.0,wordWeight; //记录出现次数最多的词的次数，用作归一化  ？？？
			Set<Map.Entry<String, Double>> tempTF = tfPerDocMap.entrySet();
			for(Iterator<Map.Entry<String, Double>> mt = tempTF.iterator();mt.hasNext();){
				Map.Entry<String, Double> me = mt.next();
				if(me.getValue() > maxCount)
					maxCount = me.getValue();
			}
			
			for(Iterator<Map.Entry<String, Double>> mt = tempTF.iterator();mt.hasNext();){
				Map.Entry<String, Double> me = mt.next();
				Double IDF = Math.log(samples.length / idfPerWordMap.get(me.getKey()));
				wordWeight = (me.getValue() / wordSumPerDoc) * IDF;
				tfPerDocMap.put(me.getKey(), wordWeight);
			}
			TreeMap<String,Double> tempMap = new TreeMap<String, Double>();
			tempMap.putAll(tfPerDocMap);
			allTestSampleMap.put(samples[i].getName(), tempMap);
		}
		printTestSampleMap(allTestSampleMap);
		return allTestSampleMap;
	}
	
	/**
	 * 输出测试样例map内容，用于测试
	 * @param allTestSampleMap
	 * @throws IOException 
	 */
	private void printTestSampleMap(
			Map<String, Map<String, Double>> allTestSampleMap) throws IOException {
		// TODO Auto-generated method stub
		File outPutFile = new File("E:/DataMiningSample/KmeansClusterResult/allTestSampleMap.txt");
		FileWriter outPutFileWriter = new FileWriter(outPutFile);
		Set<Map.Entry<String, Map<String,Double>>> allWords = allTestSampleMap.entrySet();
		
		for(Iterator<Entry<String, Map<String, Double>>> it = allWords.iterator();it.hasNext();){
			
			Map.Entry<String, Map<String,Double>> me = it.next();
			outPutFileWriter.append(me.getKey()+" ");
			
			Set<Map.Entry<String, Double>> vectorSet = me.getValue().entrySet();
			for(Iterator<Map.Entry<String, Double>> vt = vectorSet.iterator();vt.hasNext();){
				Map.Entry<String, Double> vme = vt.next();
				outPutFileWriter.append(vme.getKey()+" "+vme.getValue()+" ");
			}
			outPutFileWriter.append("\n");
			outPutFileWriter.flush();
		}
		outPutFileWriter.close();
		
	}

	/**
	 * 统计每个词的总出现次数，返回出现次数大于n次的词汇构成最终的属性词典
	 * @param strDir 处理好的newsgroup文件目录的绝对路径
	 * @param wordMap 记录出现的每个词构成的属性词典
	 * @return newWordMap 返回出现次数大于n次的词汇构成最终的属性词典
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
		 * 去除停顿词后，先用DF算法选取特征词，后面再加入特征词的选取算法
		 */
		SortedMap<String,Double> newWordMap = new TreeMap<String, Double>();
		Set<Map.Entry<String, Double>> allWords = wordMap.entrySet();
		for(Iterator<Map.Entry<String, Double>> it = allWords.iterator();it.hasNext();){
			Map.Entry<String, Double> me = it.next();
			if(me.getValue() > 100) //DF算法降维
				newWordMap.put(me.getKey(), me.getValue());
		}
		
		return newWordMap;
	}
	
	/**
	 * 计算IDF，即属性词典中每个词在多少个文档中出现过
	 * @param testSampleDir 聚类算法测试样本所在的目录
	 * @return 单词IDFmap <单词，包含该单词的文档数>
	 * @throws IOException
	 */
	public Map<String,Double> computeIDF(String testSampleDir) throws IOException{
		
		Map<String,Double> IDFPerWordMap = new TreeMap<String, Double>();
		//记下当前已经遇到过的该文档中的词
		Set<String> alreadyCountWord = new HashSet<String>();
		String word;
		File[] samples = new File(testSampleDir).listFiles();
		for(int i = 0;i<samples.length;i++){
			
			alreadyCountWord.clear();
			FileReader tsReader = new FileReader(samples[i]);
			BufferedReader tsBR = new BufferedReader(tsReader);
			while((word = tsBR.readLine()) != null){
				
				if(!alreadyCountWord.contains(word)){
					if(IDFPerWordMap.containsKey(word))
						IDFPerWordMap.put(word, IDFPerWordMap.get(word)+1.0);
					else
						IDFPerWordMap.put(word, 1.0);
					alreadyCountWord.add(word);
				}
			}
		}
		return IDFPerWordMap;
	}

	/**
	 * 创建聚类算法的测试样例集，主要是过滤出只含有特征词的文档写到一个目录下
	 * @param srcDir 源目录，已经预处理但是还没有过滤非特征词的文档目录
	 * @param desDir 目的目录，聚类算法的测试样例目录
	 * @return 创建测试样例集中特征词数组
	 * @throws IOException 
	 */
	public String[] createTestSamples(String srcDir, String desDir) throws IOException {
		
		SortedMap<String,Double> wordMap = new TreeMap<String, Double>();
		wordMap = countWords(srcDir,wordMap);
		System.out.println("special words map sizes:" + wordMap.size());
		String word,testSampleFile;
		
		File[] sampleDir = new File(srcDir).listFiles();
		for(int i =0;i<sampleDir.length;i++){
			
			File[] sample = sampleDir[i].listFiles();
			for(int j =0;j<sample.length;j++){
				
				testSampleFile = desDir + sampleDir[i].getName()+"_"+sample[j].getName();
				FileReader samReader = new FileReader(sample[j]);
				BufferedReader samBR = new BufferedReader(samReader);
				FileWriter tsWriter = new FileWriter(new File(testSampleFile));
				while((word = samBR.readLine()) != null){
					if(wordMap.containsKey(word))
						tsWriter.append(word + "\n");
				}
				tsWriter.flush();
				tsWriter.close();
			}
		}
	
		//返回属性词典
		String[] terms = new String[wordMap.size()];
		int i = 0;
		Set<Map.Entry<String, Double>> allWords = wordMap.entrySet();
		for(Iterator<Map.Entry<String, Double>> it = allWords.iterator();it.hasNext();){
			Map.Entry<String, Double> me = it.next();
			terms[i] = me.getKey();
			i++;
		}
		
		return terms;
		
	}
	
	
	

	
	
}
