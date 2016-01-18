package com.test;

import java.util.Scanner;

public class test {

	public static void main(String[] args) {
		
		while(true){
			
			Scanner cin = new Scanner(System.in);
			int X1=cin.nextInt(); //面积
			int X2=cin.nextInt();	//室
			int X3=cin.nextInt();	//片区
			//int X4=cin.nextInt();	//小区
			int X4=cin.nextInt();	//楼层
			int X5=cin.nextInt();	//装修情况
			int X6=cin.nextInt();	//城区
			int X7=cin.nextInt();	//厅
			double Y = 0;
			
			Y=-1767.644+170.009*X1-2465.434*X2-40.755*X3+69.508*X4+561.089*X5-344.594*X6-539.347*X7;
			
			System.out.println(Y);
		}
		
	}
	
	
	
	
}
