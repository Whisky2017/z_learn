package com.datamine.kmeans;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClusterMain {

	/**
	 * Kmeans 聚类主程序入口
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		//数据预处理 在分类算法中已经实现 这里（略）
		
		ComputeWordsVector computeV = new ComputeWordsVector();
		
		KmeansCluster kmeansCluster = new KmeansCluster();
		
		String srcDir = "E:\\DataMiningSample\\processedSample\\";
		String desDir = "E:\\DataMiningSample\\clusterTestSample\\";
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String beginTime = sdf.format(new Date());
		System.out.println("程序开始执行时间："+beginTime);
		
		String[] terms = computeV.createTestSamples(srcDir,desDir);
		kmeansCluster.KmeansClusterMain(desDir);
		
		String endTime = sdf.format(new Date());
		System.out.println("程序结束执行时间："+endTime);
		
	}
	
	
}
