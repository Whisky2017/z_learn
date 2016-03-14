package com.datamine.NaiveBayes;

import java.io.*;
import java.util.*;

/**
 * KNN算法的实现类，本程序用向量夹角余弦计算相识度
 * @author Administrator
 */
public class KNNClassifier {

	/**
	 * 用knn算法对测试文档集分类，读取测试样例和训练样例集
	 * @param trainFiles 训练样例的所有向量构成的文件
	 * @param testFiles  测试样例的所有向量构成的文件
	 * @param knnResultFile KNN分类结果文件路径
	 * @throws Exception 
	 */
	private void doProcess(String trainFiles, String testFiles,
			String knnResultFile) throws Exception {
		/*
		 * 首先读取训练样本和测试样本，用map<String,map<word,TF>>保存测试集和训练集，注意训练样本的类目信息也得保存
		 * 然后遍历测试样本，对于每一个测试样本去计算它与所有训练样本的相识度，相识度保存到map<String,double>有序map中
		 * 然后取钱K个样本，针对这k个样本来给它们所属的类目计算权重得分，对属于同一个类目的权重求和进而得到最大得分的类目
		 * 就可以判断测试样例属于该类目下，K值可以反复测试，找到分类准确率最高的那个值
		 * 注意：
		 *  1、要以"类目_文件名"作为每个文件的key，才能避免同名不同内容的文件出现
		 *  2、注意设置JM参数，否则会出现JAVA Heap溢出错误
		 *  3、本程序用向量夹角余弦计算相识度
		 */
		File trainSample = new File(trainFiles);
		BufferedReader trainSampleBR = new BufferedReader(new FileReader(trainSample));
		String line;
		String[] lineSplitBlock;
		//trainFileNameWordTFMap<类名_文件名,map<特征词,特征权重>>
		Map<String,TreeMap<String,Double>> trainFileNameWordTFMap = new TreeMap<String, TreeMap<String,Double>>();
		//trainWordTFMap<特征词,特征权重>
		TreeMap<String,Double> trainWordTFMap = new TreeMap<String, Double>();
		while((line = trainSampleBR.readLine()) != null){
			lineSplitBlock = line.split(" ");
			trainWordTFMap.clear();
			for(int i =2 ;i<lineSplitBlock.length;i = i+2){
				trainWordTFMap.put(lineSplitBlock[i], Double.valueOf(lineSplitBlock[i+1]));
			}
			TreeMap<String,Double> tempMap = new TreeMap<String, Double>();
			tempMap.putAll(trainWordTFMap);
			trainFileNameWordTFMap.put(lineSplitBlock[0]+"_"+lineSplitBlock[1], tempMap);
		}
		trainSampleBR.close();
		
		File testSample = new File(testFiles);
		BufferedReader testSampleBR = new BufferedReader(new FileReader(testSample));
		Map<String,Map<String,Double>>  testFileNameWordTFMap = new TreeMap<String, Map<String,Double>>();
		Map<String,Double> testWordTFMap = new TreeMap<String, Double>();
		while((line = testSampleBR.readLine()) != null){
			lineSplitBlock = line.split(" ");
			testWordTFMap.clear();
			for(int i =2;i<lineSplitBlock.length;i = i+2){
				testWordTFMap.put(lineSplitBlock[i], Double.valueOf(lineSplitBlock[i+1]));
			}
			TreeMap<String,Double> tempMap = new TreeMap<String, Double>();
			tempMap.putAll(testWordTFMap);
			testFileNameWordTFMap.put(lineSplitBlock[0]+"_"+lineSplitBlock[1], tempMap);
		}
		testSampleBR.close();
		
		//下面遍历每一个测试样例计算所有训练样本的距离，做分类
		String classifyResult;
		FileWriter knnClassifyResultWriter = new FileWriter(knnResultFile);
		Set<Map.Entry<String, Map<String,Double>>> testFileNameWordTFMapSet = testFileNameWordTFMap.entrySet();
		
		for(Iterator<Map.Entry<String, Map<String,Double>>> it = testFileNameWordTFMapSet.iterator();it.hasNext();){
			
			Map.Entry<String, Map<String,Double>> me = it.next();
			
			classifyResult = knnComputeCate(me.getKey(),me.getValue(),trainFileNameWordTFMap);
			
			knnClassifyResultWriter.append(me.getKey()+" "+classifyResult+"\n");
			knnClassifyResultWriter.flush();
		}
		knnClassifyResultWriter.close();
	}
	
	
	/**
	 * 对于每一个测试样本去计算它与所有训练样本的向量夹角余弦相识度
	 * 相识度保存入map<String,double>有序map中，然后取前k个样本
	 * 针对这k个样本来给他们所属的类目计算权重得分，对属于同一个类目的权重求和进而得到最大得分类目
	 * k值可以反复测试，找到分类准确率最高的那个值
	 * @param testFileName 测试文件名 "类别名_文件名"
	 * @param testWordTFMap 测试文件向量  map<特征词,特征权重>
	 * @param trainFileNameWordTFMap 训练样本<类目_文件名,向量>
	 * @return K个邻居权重得分最大的类目
	 */
	private String knnComputeCate(String testFileName, Map<String, Double> testWordTFMap, 
			Map<String, TreeMap<String, Double>> trainFileNameWordTFMap) {

		//<类目_文件名,距离> 后面需要将该HashMap按照value排序
		HashMap<String,Double> simMap = new HashMap<String, Double>();
		double similarity;
		Set<Map.Entry<String, TreeMap<String,Double>>> trainFileNameTFMapSet = trainFileNameWordTFMap.entrySet();
		for(Iterator<Map.Entry<String, TreeMap<String,Double>>> it = trainFileNameTFMapSet.iterator();it.hasNext();){
			
			Map.Entry<String, TreeMap<String,Double>> me = it.next();
			similarity = computeSim(testWordTFMap,me.getValue());
			simMap.put(me.getKey(), similarity);
		}
		
		//下面对simMap按照value降序排序
		ByValueComparator bvc = new ByValueComparator(simMap);
		TreeMap<String,Double> sortedSimMap = new TreeMap<String, Double>(bvc);
		sortedSimMap.putAll(simMap);
		
		//在disMap中取前K个最近的训练样本对其类别计算距离之和，K的值通过反复试验而得
		Map<String,Double> cateSimMap = new TreeMap<String, Double>(); //k个最近训练样本所属类目的距离之和
		double K = 20;
		double count = 0;
		double tempSim ;
		
		Set<Map.Entry<String, Double>> simMapSet = sortedSimMap.entrySet();
		for(Iterator<Map.Entry<String, Double>> it = simMapSet.iterator();it.hasNext();){
			
			Map.Entry<String, Double> me = it.next();
			count++;
			String categoryName = me.getKey().split("_")[0];
			if(cateSimMap.containsKey(categoryName)){
				tempSim = cateSimMap.get(categoryName);
				cateSimMap.put(categoryName, tempSim+me.getValue());
			}else
				cateSimMap.put(categoryName, me.getValue());
			
			if(count>K)
				break;
		}
		//下面到cateSimMap里面吧sim最大的那个类目名称找出来
		double maxSim = 0;
		String bestCate = null;
		Set<Map.Entry<String, Double>> cateSimMapSet = cateSimMap.entrySet();
		for(Iterator<Map.Entry<String, Double>> it = cateSimMapSet.iterator();it.hasNext();){
			
			Map.Entry<String, Double> me = it.next();
			if(me.getValue() > maxSim){
				bestCate = me.getKey();
				maxSim = me.getValue();
			}
		}
		return bestCate;
	}

	/**
	 * 计算测试样本向量和训练样本向量的相识度
	 * sim(D1,D2)=(D1*D2)/(|D1|*|D2|)
	 * 例：D1(a 30;b 20;c 20;d 10) D2(a 40;c 30;d 20; e 10)
	 * D1*D2 = 30*40 + 20*0 + 20*30 + 10*20 + 0*10 = 2000
	 * |D1| = sqrt(30*30+20*20+20*20+10*10) = sqrt(1800)
	 * |D2| = sqrt(40*40+30*30+20*20+10*10) = sqrt(3000)
	 * sim = 0.86;
	 * @param testWordTFMap  当前测试文件的<单词，权重>向量
	 * @param trainWordTFMap 当前训练样本<单词，权重>向量
	 * @return 向量之间的相识度，以向量夹角余弦计算
	 */
	private double computeSim(Map<String, Double> testWordTFMap,
			TreeMap<String, Double> trainWordTFMap) {
		
		// mul = test*train  testAbs = |test|  trainAbs = |train|
		double mul = 0,testAbs = 0, trainAbs = 0;
		Set<Map.Entry<String, Double>> testWordTFMapSet = testWordTFMap.entrySet();
		for(Iterator<Map.Entry<String, Double>> it = testWordTFMapSet.iterator();it.hasNext();){
			
			Map.Entry<String, Double> me = it.next();
			if(trainWordTFMap.containsKey(me.getKey())){
				mul += me.getValue()*trainWordTFMap.get(me.getKey());
			}
			testAbs += me.getValue()*me.getValue();
		}
		testAbs = Math.sqrt(testAbs);
		
		Set<Map.Entry<String, Double>> trainWordTFMapSet = trainWordTFMap.entrySet();
		for(Iterator<Map.Entry<String, Double>> it = trainWordTFMapSet.iterator();it.hasNext();){
			
			Map.Entry<String, Double> me = it.next();
			trainAbs += me.getValue()*me.getValue();
		}
		trainAbs = Math.sqrt(trainAbs);
		
		return mul / (testAbs * trainAbs);
	}


	/**
	 * 根据knn算法分类结果文件生成正确类目文件，而正确率和混淆矩阵的计算可以复用贝叶斯算法中的方法
	 * @param knnResultFile 分类结果文件   <"目录名_文件名",分类结果>
	 * @param knnRightFile 分类正确类目文件  <"目录名_文件名",正确结果>
	 * @throws IOException 
	 */
	private void createRightFile(String knnResultFile, String knnRightFile) throws IOException {
		
		String rightCate;
		FileReader fileR = new FileReader(knnResultFile);
		FileWriter knnRightWriter = new FileWriter(new File(knnRightFile));
		BufferedReader fileBR = new BufferedReader(fileR);
		String line;
		String lineBlock[];
		while((line = fileBR.readLine()) != null){
			
			lineBlock = line.split(" ");
			rightCate = lineBlock[0].split("_")[0];
			knnRightWriter.append(lineBlock[0]+" "+rightCate+"\n");
		}
		knnRightWriter.flush();
		fileBR.close();
		knnRightWriter.close();
	}
	
	
	public static void main(String[] args) throws Exception {
	
		//wordMap是所有属性词的词典<单词，在所有文档中出现的次数>
		double[] accuracyOfEveryExp = new double[10];
		double accuracyAvg,sum=0;
		KNNClassifier knnClassifier = new KNNClassifier();
		NaiveBayesianClassifier nbClassifier = new NaiveBayesianClassifier();
		Map<String,Double> wordMap = new TreeMap<String, Double>();
		Map<String,Double> IDFPerWordMap = new TreeMap<String, Double>();
		ComputeWordsVector computeWV = new ComputeWordsVector();
		
		wordMap = computeWV.countWords("E:\\DataMiningSample\\processedSample", wordMap);
		IDFPerWordMap = computeWV.computeIDF("E:\\DataMiningSample\\processedSampleOnlySpecial", wordMap);
		//IDFPerWordMap=null;
		computeWV.printWordMap(wordMap);
		
		// 首先生成KNN算法10次试验需要的文档TF矩阵文件
		for (int i = 0; i < 1; i++) {
			
			computeWV.computeTFMultiIDF("E:/DataMiningSample/processedSampleOnlySpecial", 0.9, i, IDFPerWordMap, wordMap);
			
			String trainFiles = "E:\\DataMiningSample\\docVector\\wordTFIDFMapTrainSample"+i;
			String testFiles = "E:/DataMiningSample/docVector/wordTFIDFMapTestSample"+i;
			
			String knnResultFile = "E:/DataMiningSample/docVector/KNNClassifyResult"+i;
			String knnRightFile = "E:/DataMiningSample/docVector/KNNClassifyRight"+i;
			
			knnClassifier.doProcess(trainFiles,testFiles,knnResultFile);
			knnClassifier.createRightFile(knnResultFile,knnRightFile);
			
			//计算准确率和混淆矩阵使用朴素贝叶斯中的方法
			accuracyOfEveryExp[i] = nbClassifier.computeAccuracy(knnRightFile, knnResultFile);
			sum += accuracyOfEveryExp[i];
			System.out.println("The accuracy for KNN Classifier in "+i+"th Exp is :" + accuracyOfEveryExp[i]);
		}
		//accuracyAvg = sum / 10;
		//System.out.println("The average accuracy for KNN Classifier in all Exps is :" + accuracyAvg);
	}
	
	//对hashMap按照value做排序 降序
	static class ByValueComparator implements Comparator<Object>{

		HashMap<String,Double> base_map;
		
		public ByValueComparator(HashMap<String,Double> disMap) {
			this.base_map = disMap;
		}
		
		@Override
		public int compare(Object o1, Object o2) {
			
			String arg0 = o1.toString();
			String arg1 = o2.toString();
			if(!base_map.containsKey(arg0) || !base_map.containsKey(arg1)){
				return 0;
			}
			if(base_map.get(arg0) < base_map.get(arg1))
				return 1;
			else if(base_map.get(arg0) == base_map.get(arg1))
				return 0;
			else
				return -1;
		}
		
	}
	
}
