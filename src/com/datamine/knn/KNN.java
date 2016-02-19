package com.datamine.knn;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class KNN {

	/**
	 * 设置优先队列的比较函数，距离越大，优先级越高
	 */
	private Comparator<KNNNode> comparator = new Comparator<KNNNode>() {

		public int compare(KNNNode o1, KNNNode o2) {
			if(o1.getDistance() >= o2.getDistance())
				return -1;
			else
				return 1;
		}
	};
	
	/**
	 *  获取K个不同的随机数
	 * @param k 随机数
	 * @param max 随机数的最大范围
	 * @return 随机数组
	 */
	public List<Integer> getRandKnum(int k, int max){
		
		List<Integer> rand = new ArrayList<Integer>(k);
		for(int i = 0; i<k;i++ ){
			int temp = (int) (Math.random()*max);
			if(!rand.contains(temp))
				rand.add(temp);
			else
				i--;
		}
		
		return rand;
	}
	
	/**
	 * 计算测试元组和训练元组之间的距离
	 * @param d1 测试元组
	 * @param d2 训练元组
	 * @return 距离
	 */
	public double calDistance(List<Double> d1, List<Double> d2){
		
		double distance = 0.0;
		for(int i =0; i<d1.size();i++){
			distance +=(d1.get(i)-d2.get(i))*(d1.get(i)-d2.get(i));
		}
		return distance;
	}
	
	
	/**
	 * 执行KNN算法，获取测试元组的类别
	 * @param datas 训练数据集
	 * @param testData 测试元组
	 * @param k 设定的k值
	 * @return 测试元组的类别
	 */
	public String knn(List<List<Double>> datas,List<Double> testData,int k){
		
		//维护一个大小为k的按距离由大到小的优先队列，用户存储最近邻训练元组
		PriorityQueue<KNNNode> pq = new PriorityQueue<KNNNode>(k, comparator);
		
		//随机从训练集中获取k个元组
		List<Integer> randNum = getRandKnum(k, datas.size());
		for(int i = 0; i<k ;i++){
			int index = randNum.get(i); //随机   获取训练数据集 数据的下标
			List<Double> currData = datas.get(index); //随机得到相应的训练元组
			String c = currData.get(currData.size()-1).toString(); //最后一个数为类别
			KNNNode node = new KNNNode(index, calDistance(testData,currData),c);
			pq.add(node);
		}
		
		/*
		 * 遍历训练元组集，计算训练元组和测试元组的距离
		 * 将所得距离distance和优先队列中的最大距离top比较
		 * 若top>distance，删除优先队列中最大距离top元组
		 * 将当前训练元组存入优先队列中
		*/
		for(int i = 0; i< datas.size();i++){
			List<Double> t = datas.get(i);
			double distance = calDistance(testData,t);
			KNNNode top = pq.peek();
			if(top.getDistance() > distance){
				pq.remove();
				pq.add(new KNNNode(i,distance,t.get(t.size()-1).toString()));
			}
		}
		
		return getMostClass(pq);
	}

	/**
	 * 获取所得到的k个最近邻元组的多数类
	 * @param pq存储k个最近邻元组的优先级队列
	 * @return 多数类的名称
	 */
	private String getMostClass(PriorityQueue<KNNNode> pq) {
		
		//classCount用来存储列别名和对应的个数
		Map<String,Integer> classCount = new HashMap<String, Integer>();
		int pqsize = pq.size();
		for(int i = 0;i <pqsize;i++){
			KNNNode node = pq.remove();
			String c = node.getC();
			if(classCount.containsKey(c))
				classCount.put(c, classCount.get(c)+1);
			else
				classCount.put(c, 1);
		}
		
		int maxIndex = -1; //类别keySet的下标
		int maxCount = 0; //最大类别的数目
		Object[] classes = classCount.keySet().toArray();
		for(int i = 0;i<classes.length;i++){
			if(classCount.get(classes[i]) > maxCount){
				maxIndex = i;
				maxCount = classCount.get(classes[i]);
			}
		}
		return classes[maxIndex].toString();
	}
	
}
