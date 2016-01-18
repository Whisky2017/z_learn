package com.algorithms.StringArrayMatrix;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordBreak {

	/*
	 * 方法一：Naive Approach （一般方法）
	 * A discussion can always start from that though.
	 * 时间复杂度为O(n^2)
	 */
	public boolean wordBreak1 (String s, Set<String> dict){
		return wordBreakHelper(s,dict,0);
	}

	public boolean wordBreakHelper(String s, Set<String> dict, int start) {
		
		if(start == s.length())
			return true;
		
		for(String a : dict){
			
			int len = a.length();
			int end = start+len;
			
			//end index should be <= string length
			if(end > s.length())
				continue;
			
			if(s.substring(start, start+len).equals(a)){
				if(wordBreakHelper(s, dict, start+len))
					return true;
			}
		}
		
		return false;
	}
	
	/*
	 * 方法二：Dynamic Programming （动态规划）
	 * Define an array t[] such that t[i]==true => 0-(i-1) can be segmented using dictionary
	 * Initial state t[0] == true
	 * 时间复杂度为O(String length*dict size)
	 */
	public boolean wordBreak2(String s, Set<String> dict){
		
		boolean[] t = new boolean[s.length()+1];
		t[0]=true;
		
		for(int i=0;i<s.length();i++){
			
			if(!t[i])
				continue;
			
			for(String a : dict){
				
				int len = a.length();
				int end = i + len;
				
				if(end>s.length())
					continue;
				
				if(t[end]) continue;
				
				if(s.substring(i, end).equals(a))
					t[end] = true;
			}
		}
		return t[s.length()];
	}
	
	/*
	 * 方法三：Regular Expression （正则表达式）
	 * The problem is equivalent to matching the regular expression (leet|code)*
	 * which means that it can be solved by building a DFA in O(2^m) and executing it in O(n).
	 * DFA 有穷自动机
	 * 时间复杂度为O(n)
	 */
	private boolean wordBreak3(String s, Set<String> dict) {
		
		StringBuilder sb = new StringBuilder();
		
		for(String a : dict){
			sb.append(a+"|");
		}

		String pattern = sb.toString().substring(0, sb.length()-1);
		pattern = "("+pattern+")*";
		//System.out.println(pattern);
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(s);
		
		if(m.matches()){
			return true;
		}else{
			return false;
		}
	}
	
	public static void main(String[] args) {
		
		String s = "programcreek";
		Set<String> dict = new HashSet<String>();
		dict.add("programcree");
		dict.add("program");
		dict.add("creek");
		
		WordBreak wb = new WordBreak();
		System.out.println(wb.wordBreak3(s,dict));
	}

	
	
	
}
