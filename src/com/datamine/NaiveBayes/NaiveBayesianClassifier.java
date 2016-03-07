package com.datamine.NaiveBayes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;


/**
 * 利用朴素贝叶斯算法对newsgroup文档集做分类，采用十组交叉测试取平均值
 * 采用多项式模型
 * 类条件概率 P(tk|c)=(类c下 单词tk 在各个文档中出现过的次数之和 + 1)/(类c下单词的总数 + 训练集总单词数)
 * @author Administrator
 */
public class NaiveBayesianClassifier {

	/**
	 * 用朴素贝叶斯算法对测试文档集分类
	 * @param trainDir 训练文档集目录
	 * @param testDir  测试文档集目录
	 * @param classifyResultFileNew 分类结果文件路径
	 * @throws Exception 
	 */
	private void doProcess(String trainDir,String testDir,
			String classifyResultFileNew) throws Exception{
		
		//保存训练集中每个类别的总词数      <目录名，单词总数> category
		Map<String,Double> cateWordsNum = new TreeMap<String, Double>();
		//保存训练样本中每个类别中每个属性词的出现次数  <类目_单词,数目> 
		Map<String,Double> cateWordsProb = new TreeMap<String, Double>();
		
		cateWordsNum = getCateWordsNum(trainDir);
		cateWordsProb = getCateWordsProb(trainDir);
		
		double totalWordsNum = 0.0;//记录所有训练集的总词数
		Set<Map.Entry<String, Double>> cateWordsNumSet = cateWordsNum.entrySet();
		for(Iterator<Map.Entry<String, Double>> it = cateWordsNumSet.iterator();it.hasNext();){
			Map.Entry<String, Double> me = it.next();
			totalWordsNum += me.getValue();
		}
		
		//下面开始读取测试样例做分类
		Vector<String> testFileWords = new Vector<String>(); //测试样本所有词的容器
		String word;
		File[] testDirFiles = new File(testDir).listFiles();
		FileWriter crWriter = new FileWriter(classifyResultFileNew);
		for(int i =0;i<testDirFiles.length;i++){
			
			File[] testSample = testDirFiles[i].listFiles();
			
			for(int j =0;j<testSample.length;j++){
				
				testFileWords.clear();
				FileReader spReader = new FileReader(testSample[j]);
				BufferedReader spBR = new BufferedReader(spReader);
				while((word = spBR.readLine()) != null){
					testFileWords.add(word);
				}
				spBR.close();
				//下面分别计算该测试样例属于二十个类别的概率
				File[] trainDirFiles = new File(trainDir).listFiles();
				BigDecimal maxP = new BigDecimal(0);
				String bestCate = null;
				
				for(int k =0; k < trainDirFiles.length; k++){
					
					BigDecimal p = computeCateProb(trainDirFiles[k],testFileWords,cateWordsNum,totalWordsNum,cateWordsProb);
					
					if( k == 0){
						maxP = p;
						bestCate = trainDirFiles[k].getName();
						continue;
					}
					if(p.compareTo(maxP) == 1){
						maxP = p;
						bestCate = trainDirFiles[k].getName();
					}
				}
				crWriter.append(testSample[j].getName() + " " + bestCate + "\n");
				crWriter.flush();
			}
		}
		crWriter.close();
		
	}

	/**
	 * 类条件概率 P(tk|c)=(类c下 单词tk 在各个文档中出现过的次数之和 + 1)/(类c下单词的总数 + 训练集中总单词数)
	 * 计算某一个测试样本数据某个类别的概率 使用多项式模型
	 * @param trainFile 该类别所有的训练样本所在的目录
	 * @param testFileWords  该测试样本中的所有词构成的容器
	 * @param cateWordsNum  记录每个目录下单词的总数
	 * @param totalWordsNum  所有训练样本的单词的总数
	 * @param cateWordsProb  记录每个目录中出现单词和次数
	 * @return 返回该测试样本在该类别中的概率
	 */
	private BigDecimal computeCateProb(File trainFile, Vector<String> testFileWords,
			Map<String, Double> cateWordsNum, double totalWordsNum, Map<String, Double> cateWordsProb) {
		
		BigDecimal probability = new BigDecimal(1);
		double wordNumInCate = cateWordsNum.get(trainFile.getName());
		BigDecimal wordNumInCateBD = new BigDecimal(wordNumInCate);
		BigDecimal totalWordsNumBD = new BigDecimal(totalWordsNum);
		
		for(Iterator<String> it = testFileWords.iterator();it.hasNext();){
			
			String me = it.next();
			String key = trainFile.getName()+"_"+me;
			double testFileWordNumInCate;
			if(cateWordsProb.containsKey(key))
				testFileWordNumInCate = cateWordsProb.get(key);
			else
				testFileWordNumInCate = 0.0;
			BigDecimal testFileWordNumInCateBD = new BigDecimal(testFileWordNumInCate);
			
			BigDecimal xcProb = (testFileWordNumInCateBD.add(new BigDecimal(0.0001)))
					.divide(wordNumInCateBD.add(totalWordsNumBD), 10, BigDecimal.ROUND_CEILING);
			probability = probability.multiply(xcProb);
		}
		// P =  P(tk|c)*P(C)
		BigDecimal result = probability.multiply(wordNumInCateBD.divide(totalWordsNumBD,10, BigDecimal.ROUND_CEILING));
		
		return result;
	}

	/**
	 * 统计某个类训练样本中每个单词出现的次数
	 * @param trainDir 训练样本集目录
	 * @return cateWordsProb 用"类目_单词"来索引map，value就是该类目下该单词出现的次数
	 * @throws Exception 
	 */
	private Map<String, Double> getCateWordsProb(String trainDir) throws Exception {

		Map<String,Double> cateWordsProb = new TreeMap<String, Double>();
		File sampleFile = new File(trainDir);
		File[] sampleDir = sampleFile.listFiles();
		String word;
		
		for(int i =0;i < sampleDir.length;i++){
			
			File[] sample = sampleDir[i].listFiles();
			
			for(int j =0;j<sample.length;j++){
				
				FileReader samReader = new FileReader(sample[j]);
				BufferedReader samBR = new BufferedReader(samReader);
				while((word = samBR.readLine()) != null){
					String key = sampleDir[i].getName()+"_"+word;
					if(cateWordsProb.containsKey(key))
						cateWordsProb.put(key, cateWordsProb.get(key)+1);
					else
						cateWordsProb.put(key, 1.0);
				}
				samBR.close();
			}
		}
		
		return cateWordsProb;
	}

	/**
	 * 获得每个类目下的单词总数
	 * @param trainDir 训练文档集目录
	 * @return cateWordsNum <目录名，单词总数>的map
	 * @throws IOException 
	 */
	private Map<String, Double> getCateWordsNum(String trainDir) throws IOException {

		Map<String, Double> cateWordsNum = new TreeMap<String, Double>();
		File[] sampleDir = new File(trainDir).listFiles();
		
		for(int i =0;i<sampleDir.length;i++){
			
			double count = 0;
			File[] sample = sampleDir[i].listFiles();
			
			for(int j =0;j<sample.length;j++){
				
				FileReader spReader = new FileReader(sample[j]);
				BufferedReader spBR = new BufferedReader(spReader);
				while(spBR.readLine() != null){
					count++;
				}
				spBR.close();
			}
			cateWordsNum.put(sampleDir[i].getName(), count);
		}
		
		return cateWordsNum;
	}
	
	/**
	 * 根据正确类目文件和分类结果文件统计出准确率
	 * @param classifyRightCate 正确类目文件     <文件名，类别目录名>
	 * @param classifyResultFileNew 分类结果文件     <文件名，类别目录名>
	 * @return 分类的准确率
	 * @throws Exception 
	 */
	private double computeAccuracy(String classifyRightCate,
			String classifyResultFileNew) throws Exception {
		
		Map<String,String> rightCate = new TreeMap<String, String>();
		Map<String,String> resultCate = new TreeMap<String,String>();
		rightCate = getMapFromResultFile(classifyRightCate);
		resultCate = getMapFromResultFile(classifyResultFileNew);
		
		Set<Map.Entry<String, String>> resCateSet = resultCate.entrySet();
		double rightCount = 0.0;
		for(Iterator<Map.Entry<String, String>> it = resCateSet.iterator();it.hasNext();){
			
			Map.Entry<String, String> me = it.next();
			if(me.getValue().equals(rightCate.get(me.getKey())))
				rightCount++;
		}
		
		computerConfusionMatrix(rightCate,resultCate);
		
		return rightCount / resultCate.size();
	}
	
	/**
	 * 根据正确类目文件和分类结果文件计算混淆矩阵并输出
	 * @param rightCate 正确类目map 
	 * @param resultCate 分类结果对应map
	 */
	private void computerConfusionMatrix(Map<String, String> rightCate,
			Map<String, String> resultCate) {
		
		int[][] confusionMatrix = new int[20][20];
		
		//首先求出类目对应的数组索引
		SortedSet<String> cateNames = new TreeSet<String>();
		Set<Map.Entry<String, String>> rightCateSet = rightCate.entrySet();
		for(Iterator<Map.Entry<String, String>> it = rightCateSet.iterator();it.hasNext();){
			
			Map.Entry<String, String> me = it.next();
			cateNames.add(me.getValue());
		}
		cateNames.add("rec.sport.baseball");//防止数少一个类目
		String[] cateNamesArray = cateNames.toArray(new String[0]);
		Map<String,Integer> cateNamesToIndex = new TreeMap<String, Integer>();
		
		for(int i =0;i<cateNamesArray.length;i++){
			cateNamesToIndex.put(cateNamesArray[i], i);
		}
		
		for(Iterator<Map.Entry<String, String>> it = rightCateSet.iterator();it.hasNext();){
			
			Map.Entry<String, String> me = it.next();
			confusionMatrix[cateNamesToIndex.get(me.getValue())][cateNamesToIndex.get(resultCate.get(me.getKey()))]++;
		}
		
		//输出混淆矩阵
		double[] hangSum = new double[20];
		System.out.print("      ");
		
		for(int i=0;i<20;i++){
			System.out.printf("%-6d",i);
		}
		
		System.out.println("准确率");
		
		for(int i =0;i<20;i++){
			System.out.printf("%-6d",i);
			for(int j = 0;j<20;j++){
				System.out.printf("%-6d",confusionMatrix[i][j]);
				hangSum[i] += confusionMatrix[i][j];
			}
			System.out.printf("%-6f\n",confusionMatrix[i][i]/hangSum[i]);
		}
		System.out.println();
		
	}

	/**
	 * 从结果文件中读取Map
	 * @param file 类目文件
	 * @return Map<String,String> 由<文件名,类目名>保存的map
	 * @throws Exception 
	 */
	private Map<String, String> getMapFromResultFile(String file) throws Exception {

		File crFile = new File(file);
		FileReader crReader = new FileReader(crFile);
		BufferedReader crBR = new BufferedReader(crReader);
		Map<String,String> res = new TreeMap<String, String>();
		String[] s;
		String line;
		while((line = crBR.readLine()) != null){
			s = line.split(" ");
			res.put(s[0], s[1]);
		}
		return res;
	}

	public static void main(String[] args) throws Exception {
		
		CreateTrainAndTestSample ctt = new CreateTrainAndTestSample();
		NaiveBayesianClassifier nbClassifier = new NaiveBayesianClassifier();
		
		//根据包含非特征词的文档集生成只包含特征词的文档集到processedSampleOnlySpecial目录下
		ctt.filterSpecialWords();
		
		double[] accuracyOfEveryExp  = new double[10];
		double accuracyAvg,sum = 0;
		

		for(int i =0;i<10;i++){//用交叉验证法做十次分类实验，对准确率取平均值	
			
			String TrainDir = "E:/DataMiningSample/TrainSample"+i;
			String TestDir = "E:/DataMiningSample/TestSample"+i;
			String classifyRightCate = "E:/DataMiningSample/classifyRightCate"+i+".txt";
			String classifyResultFileNew = "E:/DataMiningSample/classifyResultNew"+i+".txt";
		
			ctt.createTestSample("E:/DataMiningSample/processedSampleOnlySpecial", 0.8, i, classifyRightCate);
			
			nbClassifier.doProcess(TrainDir, TestDir, classifyResultFileNew);
		
			accuracyOfEveryExp[i] = nbClassifier.computeAccuracy(classifyRightCate,classifyResultFileNew);
			
			System.out.println("The accuracy for Naive Bayesian Classifier in "+i+"th Exp is :" + accuracyOfEveryExp[i]);
			
		}
		
		for(int i =0;i<10;i++)
			sum += accuracyOfEveryExp[i];
		accuracyAvg = sum/10;
		
		System.out.println("The average accuracy for Naive Bayesian Classifier in all Exps is :" + accuracyAvg);
		
	}

	
	
}
