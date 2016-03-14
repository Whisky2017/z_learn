package com.datamine.kmeans;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * kmeans聚类算法的实现类，将newsgroup文档集聚成10类、20类、30类
 * 算法结束条件：当每个点最近的聚类中心点就是它所属的聚类中心点时，算法结束
 * @author Administrator
 *
 */
public class KmeansCluster {

	/**
	 * kmeans算法主过程
	 * @param allTestSampleMap 聚类算法测试样本map(已经向量化) <文件名，<特征词，TF-IDF值>>
	 * @param k 聚类的数量
	 * @return 聚类结果 <文件名，聚类完成后所属的类别号>
	 */
	private Map<String, Integer> doProcess(
			Map<String, Map<String, Double>> allTestSampleMap, int k) {
		
		//0、首先获取allTestSampleMap所有文件名顺序组成的数组
		String[] testSampleNames = new String[allTestSampleMap.size()];
		int count =0,tsLength = allTestSampleMap.size();
		Set<Map.Entry<String, Map<String,Double>>> allTestSampleMapSet = allTestSampleMap.entrySet();
		for(Iterator<Map.Entry<String, Map<String,Double>>> it = allTestSampleMapSet.iterator();it.hasNext();){
			Map.Entry<String, Map<String,Double>> me = it.next();
			testSampleNames[count++] = me.getKey();
		}
		
		//1、初始点的选择算法是随机选择或者是均匀分开选择，这里采用后者
		Map<Integer,Map<String,Double>> meansMap = getInitPoint(allTestSampleMap,k);
		double [][] distance = new double[tsLength][k]; //distance[i][k]记录点i到聚类中心k的距离
		
		//2、初始化k个聚类
		int[] assignMeans = new int[tsLength]; //记录所有点属于的聚类序号，初始化全部为0
		Map<Integer,Vector<Integer>> clusterMember = new TreeMap<Integer, Vector<Integer>>();//记录每个聚类的成员点序号
		Vector<Integer> mem = new Vector<Integer>();
		int iterNum = 0; //迭代次数
		
		while(true){
			System.out.println("Iteration No." + (iterNum++) + "-------------------------");
			//3、计算每个点和每个聚类中心的距离
			for(int i = 0;i < tsLength;i++){
				for(int j = 0;j<k;j++)
					distance[i][j] = getDistance(allTestSampleMap.get(testSampleNames[i]),meansMap.get(j));
			}
			
			//4、找出每个点最近的聚类中心
			int [] nearestMeans = new int[tsLength];
			for(int i = 0;i < tsLength;i++){
				nearestMeans[i] = findNearestMeans(distance,i);
			}
			
			//5、判断当前所有点属于的聚类序号是否已经全部是其离的最近的聚类，如果是或者达到最大的迭代次数，那么结束算法
			int okCount = 0;
			for(int i= 0;i<tsLength;i++){
				if(nearestMeans[i] == assignMeans[i])
					okCount ++;
			}
			System.out.println("okCount = " + okCount);
			if(okCount == tsLength || iterNum >= 10)
				break;
			
			//6、如果前面条件不满足，那么需要重新聚类再次进行一次迭代，需要修改每个聚类的成员和每个点属于的聚类信息
			clusterMember.clear();
			for(int i = 0;i < tsLength;i++){
				assignMeans[i] = nearestMeans[i];
				if(clusterMember.containsKey(nearestMeans[i])){
					clusterMember.get(nearestMeans[i]).add(i);
				}
				else{
					mem.clear();
					mem.add(i);
					Vector<Integer> tempMem = new Vector<Integer>();
					tempMem.addAll(mem);
					clusterMember.put(nearestMeans[i], tempMem);
				}
			}
			
			//7、重新计算每个聚类的中心点
			for(int i = 0;i<k;i++){
				
				if(!clusterMember.containsKey(i)) //注意kmeans可能产生空聚类
					continue;
				
				Map<String,Double> newMean = computeNewMean(clusterMember.get(i),allTestSampleMap,testSampleNames);
				Map<String,Double> tempMean = new TreeMap<String,Double>();
				tempMean.putAll(newMean);
				meansMap.put(i, tempMean);
			}
		
		}
		
		//8、形成聚类结果并且返回
 		Map<String,Integer> resMap = new TreeMap<String,Integer>();
		for(int i = 0;i<tsLength;i++){
			resMap.put(testSampleNames[i], assignMeans[i]);
		}
		
		return resMap;
	}
	
	/**
	 * 计算当前聚类的新中心，采用向量平均
	 * @param clusterM 该点到所有聚类中心的距离
	 * @param allTestSampleMap 所有测试样例 <文件名，向量>
	 * @param testSampleNames 所有测试样例名构成的数组
	 * @return 新的聚类中心向量
	 */
	private Map<String, Double> computeNewMean(Vector<Integer> clusterM,
			Map<String, Map<String, Double>> allTestSampleMap,
			String[] testSampleNames) {
		
		double memberNum = (double)clusterM.size();
		Map<String,Double> newMeanMap = new TreeMap<String,Double>();
		Map<String,Double> currentMemMap = new TreeMap<String, Double>();
		
		for(Iterator<Integer> it = clusterM.iterator();it.hasNext();){
			int me = it.next();
			currentMemMap = allTestSampleMap.get(testSampleNames[me]);
			Set<Map.Entry<String, Double>> currentMemMapSet = currentMemMap.entrySet();
			for(Iterator<Map.Entry<String, Double>> jt = currentMemMapSet.iterator();jt.hasNext();){
				Map.Entry<String, Double> ne = jt.next();
				if(newMeanMap.containsKey(ne.getKey()))
					newMeanMap.put(ne.getKey(), newMeanMap.get(ne.getKey())+ne.getValue());
				else
					newMeanMap.put(ne.getKey(), ne.getValue());
			}
		}
		
		Set<Map.Entry<String, Double>> newMeanMapSet = newMeanMap.entrySet();
		for(Iterator<Map.Entry<String, Double>> it = newMeanMapSet.iterator();it.hasNext();){
			Map.Entry<String, Double> me = it.next();
			newMeanMap.put(me.getKey(), newMeanMap.get(me.getKey()) / memberNum);
		}
		
		return newMeanMap;
	}

	/**
	 * 找出距离当前点最近的聚类中心
	 * @param distance 点到所有聚类中心的距离
	 * @param m 点（文本号）
	 * @return 最近聚类中心的序号j
	 */
	private int findNearestMeans(double[][] distance, int m) {
		
		double minDist = 10;
		int j = 0;
		for(int i = 0;i<distance[m].length;i++){
			if(distance[m][i] < minDist){
				minDist = distance[m][i];
				j = i;
			}
		}
		return j;
	}

	/**
	 * 计算两个点的距离
	 * @param map1 点1的向量map
	 * @param map2 点2的向量map
	 * @return 两个点的欧式距离
	 */
	private double getDistance(Map<String, Double> map1, Map<String, Double> map2) {

		return 1 - computeSim(map1,map2);
	}

	/**计算两个文本的相似度
	 * @param testWordTFMap 文本1的<单词,词频>向量
	 * @param trainWordTFMap 文本2<单词,词频>向量
	 * @return Double 向量之间的相似度 以向量夹角余弦计算（加上注释部分代码即可）或者向量内积计算（不加注释部分，效果相当而速度更快）
	 * @throws IOException 
	 */
	private double computeSim(Map<String, Double> testWordTFMap,
			Map<String, Double> trainWordTFMap) {
		// TODO Auto-generated method stub
		double mul = 0;//, testAbs = 0, trainAbs = 0;
		Set<Map.Entry<String, Double>> testWordTFMapSet = testWordTFMap.entrySet();
		for(Iterator<Map.Entry<String, Double>> it = testWordTFMapSet.iterator(); it.hasNext();){
			Map.Entry<String, Double> me = it.next();
			if(trainWordTFMap.containsKey(me.getKey())){
				mul += me.getValue()*trainWordTFMap.get(me.getKey());
			}
			//testAbs += me.getValue() * me.getValue();
		}
		//testAbs = Math.sqrt(testAbs);
		
		/*Set<Map.Entry<String, Double>> trainWordTFMapSet = trainWordTFMap.entrySet();
		for(Iterator<Map.Entry<String, Double>> it = trainWordTFMapSet.iterator(); it.hasNext();){
			Map.Entry<String, Double> me = it.next();
			trainAbs += me.getValue()*me.getValue();
		}
		trainAbs = Math.sqrt(trainAbs);*/
		return mul ;/// (testAbs * trainAbs);
	}

	/**
	 * 获取kmeans算法迭代的初始点
	 * @param allTestSampleMap <文件名，<特征词，TF-IDF值>>
	 * @param k 聚类的数量
	 * @return  meansMap k个聚类的中心点向量
	 */
	private Map<Integer, Map<String, Double>> getInitPoint(
			Map<String, Map<String, Double>> allTestSampleMap, int k) {
		
		int count = 0, i = 0;
		//保存k个聚类的中心向量
		Map<Integer,Map<String,Double>> meansMap = new TreeMap<Integer, Map<String,Double>>();
		System.out.println("本次聚类的初始点对应的文件为：");
		Set<Map.Entry<String, Map<String,Double>>> allTestSampleMapSet = allTestSampleMap.entrySet();
		for(Iterator<Map.Entry<String, Map<String,Double>>> it = allTestSampleMapSet.iterator();it.hasNext();){
			Map.Entry<String, Map<String,Double>> me = it.next();
			if(count == i*allTestSampleMapSet.size() / k){
				meansMap.put(i, me.getValue());
				System.out.println(me.getKey());
				i++;
			}
			count++ ;
		}
		
		return meansMap;
	}

	/**
	 * 输出聚类结果到文件中
	 * @param kmeansClusterResult 聚类结果
	 * @param kmeansClusterResultFile 输出聚类结果到文件中
	 * @throws IOException 
	 */
	private void printClusterResult(Map<String, Integer> kmeansClusterResult,
			String kmeansClusterResultFile) throws IOException {

		FileWriter resultWriter = new FileWriter(kmeansClusterResultFile);
		Set<Map.Entry<String, Integer>> kmeansClusterResultSet = kmeansClusterResult.entrySet();
		for(Iterator<Map.Entry<String, Integer>> it = kmeansClusterResultSet.iterator();it.hasNext();){
			Map.Entry<String, Integer> me = it.next();
			resultWriter.append(me.getKey()+" "+me.getValue()+"\n");
		}
		resultWriter.flush();
		resultWriter.close();
	}
	
	/**
	 * 评估函数根据聚类结果文件统计熵 和 混淆矩阵
	 * @param kmeansClusterResultFile 聚类结果文件
	 * @param k 聚类数目
	 * @return 聚类结果的熵值
	 * @throws IOException 
	 */
	private double evaluateClusterResult(String kmeansClusterResultFile, int k) throws IOException {

		Map<String,String> rightCate = new TreeMap<String, String>();
		Map<String,String> resultCate = new TreeMap<String, String>();
		FileReader crReader = new FileReader(kmeansClusterResultFile);
		BufferedReader crBR  = new BufferedReader(crReader);
		String[] s;
		String line;
		while((line = crBR.readLine()) != null){
			s = line.split(" ");
			resultCate.put(s[0], s[1]);
			rightCate.put(s[0], s[0].split("_")[0]);
		}
		crBR.close();
		return computeEntropyAndConfuMatrix(rightCate,resultCate,k);//返回熵
	}
	
	/**
	 * 计算混淆矩阵并输出，返回熵
	 * @param rightCate 正确的类目对应map
	 * @param resultCate 聚类结果对应map
	 * @param k 聚类的数目
	 * @return 返回聚类熵
	 */
	private double computeEntropyAndConfuMatrix(Map<String, String> rightCate,
			Map<String, String> resultCate, int k) {
		
		//k行20列，[i,j]表示聚类i中属于类目j的文件数
		int[][] confusionMatrix = new int[k][20];
		
		//首先求出类目对应的数组索引
		SortedSet<String> cateNames = new TreeSet<String>();
		Set<Map.Entry<String, String>> rightCateSet = rightCate.entrySet();
		for(Iterator<Map.Entry<String, String>> it = rightCateSet.iterator();it.hasNext();){
			Map.Entry<String, String> me = it.next();
			cateNames.add(me.getValue());
		}
		
		String[] cateNamesArray = cateNames.toArray(new String[0]);
		Map<String,Integer> cateNamesToIndex = new TreeMap<String, Integer>();
		for(int i =0;i < cateNamesArray.length ;i++){
			cateNamesToIndex.put(cateNamesArray[i], i);
		}
		
		for(Iterator<Map.Entry<String, String>> it = rightCateSet.iterator();it.hasNext();){
			Map.Entry<String, String> me = it.next();
			confusionMatrix[Integer.parseInt(resultCate.get(me.getKey()))][cateNamesToIndex.get(me.getValue())]++;
		}
		
		//输出混淆矩阵
		double [] clusterSum = new double[k]; //记录每个聚类的文件数
		double [] everyClusterEntropy = new double[k]; //记录每个聚类的熵
		double clusterEntropy = 0;
		
		System.out.print("      ");
		
		for(int i=0;i<20;i++){
			System.out.printf("%-6d",i);
		}
		
		System.out.println();
		
		for(int i =0;i<k;i++){
			System.out.printf("%-6d",i);
			for(int j = 0;j<20;j++){
				clusterSum[i] += confusionMatrix[i][j];
				System.out.printf("%-6d",confusionMatrix[i][j]);
			}
			System.out.println();
		}
		System.out.println();
		
		//计算熵值
		for(int i = 0;i<k;i++){
			if(clusterSum[i] != 0){
				for(int j = 0;j< 20 ;j++){
					double p = (double)confusionMatrix[i][j]/clusterSum[i];
					if(p!=0)
						everyClusterEntropy[i] += -p * Math.log(p); 
				}
				clusterEntropy += clusterSum[i]/(double)rightCate.size() * everyClusterEntropy[i];  
			}
		}
		return clusterEntropy;
	}

	public void KmeansClusterMain(String testSampleDir) throws IOException {
		
		//首先计算文档TF-IDF向量，保存为Map<String,Map<String,Double>> 即为Map<文件名,Map<特征词，TF-IDF值>>
		ComputeWordsVector computV = new ComputeWordsVector();
		
		//int k[] = {10,20,30}; 三组分类
		int k[] = {20};
		
		Map<String,Map<String,Double>> allTestSampleMap = computV.computeTFMultiIDF(testSampleDir);
		
		for(int i =0;i<k.length;i++){
			System.out.println("开始聚类，聚成"+k[i]+"类");
			String KmeansClusterResultFile = "E:\\DataMiningSample\\KmeansClusterResult\\";
			Map<String,Integer> KmeansClusterResult = new TreeMap<String, Integer>();
			KmeansClusterResult = doProcess(allTestSampleMap,k[i]);
			KmeansClusterResultFile += k[i];
			printClusterResult(KmeansClusterResult,KmeansClusterResultFile);
			System.out.println("The Entropy for this Cluster is " + evaluateClusterResult(KmeansClusterResultFile,k[i]));
		}
		
	}
	
	
	public static void main(String[] args) throws IOException {
		
		KmeansCluster test = new KmeansCluster();
		
		String KmeansClusterResultFile = "E:\\DataMiningSample\\KmeansClusterResult\\20";
		System.out.println("The Entropy for this Cluster is " + test.evaluateClusterResult(KmeansClusterResultFile,20));
	}


	
}
