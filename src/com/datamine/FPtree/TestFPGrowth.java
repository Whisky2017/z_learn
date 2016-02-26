package com.datamine.FPtree;

import java.util.List;

public class TestFPGrowth {
	
	public static void main(String[] args) throws Exception {
		
		float support = 0.6f;
		List<String[]> matrix = Reader.readAsMatrix("data\\FPtree.txt", "	", "UTF-8");
		
		FPtree.run(support,matrix);
		
	}
	
}
