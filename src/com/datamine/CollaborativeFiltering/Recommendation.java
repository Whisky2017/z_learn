package com.datamine.CollaborativeFiltering;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 基于物品的推荐算法（基于用户的类似）
 * @author Administrator
 *
 */
public class Recommendation {

	public static final int KNEIGHBOUR = 3; // K近邻K值
	public static final int COLUMNCOUNT = 8; // 物品数8 即下面bookName的数量
	public static final int PREFROWCOUNT = 20; // 20个用户的偏好
	public static final int TESTROWCOUNT = 5; // 为5个测试用户推荐其偏好产品

	private String[] bookName = { "数据挖掘：概念与技术", "金融工程", "投资银行学", "算法导论",
			"machine learning", "经济学原理", "金融的逻辑", "Thinking in Java" };

	/**
	 * 推荐算法执行入口
	 */
	public void generateRecommendations() {

		int[][] preference = readFile(PREFROWCOUNT, "data/preference.data");
		int[][] test = readFile(TESTROWCOUNT, "data/test.data");

		// 根据用户偏好中的数据，计算物品(items)之间的相似度
		double[][] similarityMatrix = produceSimilarityMatrix(preference);

		//K个最近邻偏好
		List<Integer> neighborSerial = new ArrayList<Integer>();
		for (int i = 0; i < TESTROWCOUNT; i++) { //给5个新用户(test用户)推荐
			neighborSerial.clear(); 
			double max = 0;  		//只推荐一个最大的偏好item
			int itemSerial = 0;		//物品item的下标
			for (int j = 0; j < COLUMNCOUNT; j++) {
				
				if (test[i][j] == 0) { //当test用户没有对物品评分，理解为没有浏览过此物品，即为推荐对象
					
					double similaritySum = 0;
					double sum = 0;
					double score = 0;
					//在相似矩阵中找与test[i][j]最相近的k个item下标
					neighborSerial = findKNeighbors(test[i], j,similarityMatrix);
					
					for (int m = 0; m < neighborSerial.size(); m++) {
						sum += similarityMatrix[j][neighborSerial.get(m)] * test[i][neighborSerial.get(m)];
						similaritySum += similarityMatrix[j][neighborSerial.get(m)];
					}
					score = sum / similaritySum;
					if (score > max) {
						max = score;
						itemSerial = j;
					}
				}
			}
			
			System.out.println("The book recommended for user " + i + " is: "
					+ bookName[itemSerial] + " score: " + max);
		}
	}

	/**
	 * 在相似矩阵中找与score[][i]=0 最相近的k个item下标
	 * @param score 测试用户评分记录
	 * @param i 没有评分的item 即score[][i]=0
	 * @param similarityMatrix 相似矩阵
	 * @return 最相近的k个item下标
	 */
	private List<Integer> findKNeighbors(int[] score, int i,
			double[][] similarityMatrix) {
		
		List<Integer> neighborSerial = new ArrayList<Integer>();
		//记录已经评分item与i的相似度
		double[] similarity = new double[similarityMatrix.length];
		for (int j = 0; j < similarityMatrix.length; j++) {
			if (score[j] != 0) {
				similarity[j] = similarityMatrix[j][i]; 
			} else {
				similarity[j] = 0;
			}
		}
		double[] temp = new double[similarity.length];
		for (int j = 0; j < temp.length; j++) {
			temp[j] = similarity[j];
		}
		Arrays.sort(temp);
		
		//这是一种技巧 取相识度最高前K个 的下标
		for (int j = 0; j < similarity.length; j++) {
			for (int m = temp.length - 1; m >= temp.length - KNEIGHBOUR; m--) {
				if (similarity[j] == temp[m] && similarity[j] != 0.0)
					neighborSerial.add(new Integer(j));
			}
		}
		
		return neighborSerial;
	}

	/**
	 * 根据用户偏好之间的物品信息，计算8*8的物品相似矩阵
	 * @param preference 用于偏好信息
	 * @return similarityMatrix 相似矩阵
	 */
	private double[][] produceSimilarityMatrix(int[][] preference) {

		// 对preference进行转置  因为要算物品之间的相似度   如果要算用户之间的相似度则应该为20*20的矩阵
		preference = TransformArray(preference);
		
		double[][] similarityMatrix = new double[COLUMNCOUNT][COLUMNCOUNT];
		for (int i = 0; i < COLUMNCOUNT; i++) {
			for (int j = 0; j < COLUMNCOUNT; j++) {
				if (i == j) {
					// 物品和物品之间相识度为0
					similarityMatrix[i][j] = 0;
				} else {
					// 计算物品两两之间的相似度
					similarityMatrix[i][j] = computeSimilarity(preference[i],preference[j]);
				}
			}
		}
		/*for (int i = 0; i < similarityMatrix.length; i++) {
			for (int j = 0; j < similarityMatrix[0].length; j++) {
				System.out.printf("%10f", similarityMatrix[i][j]);
			}
			System.out.println();
		}*/
		return similarityMatrix;
	}

	/**
	 * 对二维数组 int[][] preference 进行转置
	 * @param preference
	 */
	private int[][] TransformArray(int[][] preference) {
		
		int result[][] = new int[preference[0].length][preference.length];
		
		for(int i =0;i<preference[0].length;i++)
			for(int j = 0;j<preference.length;j++)
				result[i][j]=preference[j][i];
		
		return result;
	}

	/**
	 * 计算物品item1和物品item2之间的相似度
	 * @param item1 物品1
	 * @param item2 物品2
	 * @return 相识度
	 */
	private double computeSimilarity(int[] item1, int[] item2) {
		
		List<Integer> list1 = new ArrayList<Integer>();
		List<Integer> list2 = new ArrayList<Integer>();
		for (int i = 0; i < item1.length; i++) {
			if (item1[i] != 0 && item2[i] != 0) {
				list1.add(new Integer(item1[i]));
				list2.add(new Integer(item2[i]));
			}
		}
		
		return pearsonCorrelation(list1, list2);
	}

	/**
	 * 读取文本文件中用户偏好数据
	 * @param rowCount 文本的行数
	 * @param fileName  文本地址+名称
	 * @return
	 */
	private int[][] readFile(int rowCount, String fileName) {
		int[][] preference = new int[rowCount][COLUMNCOUNT];
		try {
			File file = new File(fileName);
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line = "";
			int i = 0;
			while (br.ready()) {
				line = br.readLine();
				String[] data = line.split(",");
				for (int j = 0; j < data.length; j++) {
					preference[i][j] = Integer.parseInt(data[j]);
				}
				i++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return preference;
	}

	/**
	 * 计算两个物品(item)之间的pearson相似度  
	 * 皮尔森相关系数等于两个变量的协方差除于两个变量的标准差。
	 * pearson = (E(X*Y)-E(X)*E(Y)) / sqrt(E(X*X)-E(X)*E(X))*sqrt(E(Y*Y)-E(Y)*E(Y))
	 * @param a 物品a
	 * @param b 物品b
	 * @return a和b的相似度
	 */
	private double pearsonCorrelation(List<Integer> a, List<Integer> b) {
		int num = a.size();
		int sum_prefOne = 0;
		int sum_prefTwo = 0;
		int sum_squareOne = 0;
		int sum_squareTwo = 0;
		int sum_product = 0;
		for (int i = 0; i < num; i++) {
			sum_prefOne += a.get(i);
			sum_prefTwo += b.get(i);
			sum_squareOne += Math.pow(a.get(i), 2);
			sum_squareTwo += Math.pow(b.get(i), 2);
			sum_product += a.get(i) * b.get(i);
		}
		double sum = num * sum_product - sum_prefOne * sum_prefTwo;
		double den = Math.sqrt((num * sum_squareOne - Math.pow(sum_squareOne, 2))
				* (num * sum_squareTwo - Math.pow(sum_squareTwo, 2)));
		double result = sum / den;
		return result;
	}

	/**
	 * 主函数入口
	 * @param args
	 */
	public static void main(String[] args) {
		Recommendation application = new Recommendation();
		application.generateRecommendations();
	}
}
