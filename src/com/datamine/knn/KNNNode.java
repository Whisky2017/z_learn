package com.datamine.knn;

//KNN节点类，用来存储最近邻的K个元组的相关信息
public class KNNNode {

	private int index; //元组标号
	private double distance; //与测试元组之间的距离
	private String c; //所属类别
	
	public KNNNode(int index,double distance,String c){
		this.index = index;
		this.distance = distance;
		this.c = c;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public String getC() {
		return c;
	}

	public void setC(String c) {
		this.c = c;
	}
	
	
}
