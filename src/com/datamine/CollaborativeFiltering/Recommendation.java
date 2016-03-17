package com.datamine.CollaborativeFiltering;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Recommendation {
	
	public static final int KNEIGHBOUR = 3;
	public static final int COLUMNCOUNT = 8;	//number of items
	public static final int PREFROWCOUNT = 20;		
	public static final int TESTROWCOUNT = 5;
	
	private String[] bookName = {"数据挖掘：概念与技术","金融工程","投资银行学","算法导论","machine learning","经济学原理","金融的逻辑","Thinking in Java"};
	
	public void generateRecommendations() {
		int[][] preference = readFile(PREFROWCOUNT, "data/preference.data");
		int[][] test = readFile(TESTROWCOUNT, "data/test.data");
		double[][] similarityMatrix = produceSimilarityMatrix(preference);
//		for (int i = 0; i < similarityMatrix.length; i++) {
//			for (int j = 0; j < similarityMatrix[0].length; j++) {
//				System.out.print(similarityMatrix[i][j]+" ");
//			}
//			System.out.println();
//		}
		List<Integer> neighborSerial = new ArrayList<Integer>();
		for (int i = 0; i < TESTROWCOUNT; i++) {
			neighborSerial.clear();
			double max = 0;
			int itemSerial = 0;
			for (int j = 0; j < COLUMNCOUNT; j++) {
				if(test[i][j] == 0) {
					double similaritySum = 0;
					double sum = 0;
					double score = 0;
					neighborSerial = findKNeighbors(test[i], j, similarityMatrix);
					for (int m = 0; m < neighborSerial.size(); m++) {
						sum += similarityMatrix[j][neighborSerial.get(m)] * test[i][neighborSerial.get(m)];
						similaritySum += similarityMatrix[j][neighborSerial.get(m)];
					}
					score = sum / similaritySum;
					if(score > max) {
						max = score;
						itemSerial = j;
					}
				}
			}
			System.out.println("The book recommended for user "+i+" is: "+bookName[itemSerial]+" score: "+max);
		}
	}
	
	/**
	 * This method is used to find the nearest K neighbors to the un_scored item 
	 * @param score
	 * @param i
	 * @param similarityMatrix
	 * @return
	 */
	private List<Integer> findKNeighbors(int[] score,int i,double[][] similarityMatrix) {	//该方法有三个参数，score表示某一用户对所有项目的评分；i表示某个项目的序号
		List<Integer> neighborSerial = new ArrayList<Integer>();
		double[] similarity = new double[similarityMatrix.length];
		for (int j = 0; j < similarityMatrix.length; j++) {
			if(score[j] != 0) {
				similarity[j] = similarityMatrix[j][i];
			} 
			else {
				similarity[j] = 0;
			}
		}
		double[] temp = new double[similarity.length];
		for (int j = 0; j < temp.length; j++) {
			temp[j] = similarity[j];
		}
		Arrays.sort(temp);
		for(int j = 0; j < similarity.length; j++) {
			for (int m = temp.length - 1; m >= temp.length - KNEIGHBOUR; m--) {
				if (similarity[j] == temp[m] && similarity[j] != 0.0)
					neighborSerial.add(new Integer(j));
			}	
		}
		return neighborSerial;
	}
	
	/**
	 * This method is used to produce similarity matrix among items
	 * @param preference
	 * @return
	 */
	private double[][] produceSimilarityMatrix(int[][] preference) {
		double[][] similarityMatrix = new double[COLUMNCOUNT][COLUMNCOUNT];
		for (int i = 0; i < COLUMNCOUNT; i++) {
			for (int j = 0; j < COLUMNCOUNT; j++) {
				if (i == j) {
					similarityMatrix[i][j] = 0;
				}
				else {
					similarityMatrix[i][j] = computeSimilarity(preference[i], preference[j]);
				}			
			}
		}
//		for (int i = 0; i < similarityMatrix.length; i++) {
//			for (int j = 0; j < similarityMatrix[0].length; j++) {
//				System.out.print(similarityMatrix[i][j]);
//			}
//			System.out.println();
//		}
//		System.out.println("**********");
		return similarityMatrix;
	}
	
	/**
	 * This method is used to compute similarity between two items
	 * @param item1
	 * @param item2
	 * @return
	 */
	private double computeSimilarity(int[] item1,int[] item2) {
		List<Integer> list1 = new ArrayList<Integer>();
		List<Integer> list2 = new ArrayList<Integer>();
		int j = 0;
		for (int i = 0; i < item1.length; i++) {
			if(item1[i] != 0 && item2[i] !=0) {
				list1.add(new Integer(item1[i]));
				list2.add(new Integer(item2[i]));
			}
			j++;
		}
		return pearsonCorrelation(list1,list2);
	}
	
	/**
	 * This method is used to read file and store file data into arrays
	 * @param rowCount
	 * @param fileName 
	 * @return
	 */
	private int[][] readFile(int rowCount,String fileName) {	
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
	 * This method is used to compute Pearson Correlation between two items
	 * @param a
	 * @param b
	 * @return
	 */
	private double pearsonCorrelation(List<Integer> a,List<Integer> b) {
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
		double den = Math.sqrt((num * sum_squareOne - Math.pow(sum_squareOne, 2)) * (num * sum_squareTwo - Math.pow(sum_squareTwo, 2)));
		double result = sum / den;
		return result;
	}
	
	public static void main (String[] args) {
		Recommendation application = new Recommendation();
		application.generateRecommendations();
	}
}

