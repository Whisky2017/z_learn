package com.algorithms.StringArrayMatrix;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class WorkBreakII {

	public static List<String> wordBreak(String s, Set<String> dict){
		
		List<String> dp[] = new ArrayList[s.length()+1];
		dp[0] = new ArrayList<String>();
		
		for(int i=0;i<s.length();i++){
			if(dp[i]==null)
				continue;
			
			for(String word :dict){
				int len = word.length();
				int end = i+ len;
				
				if(end > s.length())
					continue;
				
				if(s.substring(i, end).equals(word)){
					if(dp[end] == null){
						dp[end] = new ArrayList<String>();
					}
					dp[end].add(word);
				}
			}
		}
		
		List<String> result = new LinkedList<String>();
		if(dp[s.length()]== null){
			return result;
		}
		
		ArrayList<String> temp = new ArrayList<String>();
		dfs(dp,s.length(),result,temp);
		
		return result;
	}
	
	private static void dfs(List<String>[] dp, int end, List<String> result,
			ArrayList<String> temp) {

		if(end <= 0){
			String path = temp.get(temp.size()-1);
			for(int i= temp.size()-2 ; i>=0 ; i--){
				path += " "+temp.get(i);
			}
			result.add(path);
			return ;
		}
		
		for(String str : dp[end]){
			temp.add(str);
			dfs(dp,end-str.length(),result,temp);
			temp.remove(temp.size()-1);
		}
	}

	public static void main(String[] args) {
		
		String s= "catsanddog";
		HashSet<String> dict = new HashSet<String>();
		dict.add("cat");
		dict.add("cats");
		dict.add("and");
		dict.add("sand");
		dict.add("dog");
		
		System.out.println(wordBreak(s,dict));
		
	}
	
}
