package com.algorithms.StringArrayMatrix;

import java.util.Stack;


public class ReversePolish {

	public static void main(String[] args) {
		
		String[] tokens = new String[]{"2","1","+","3","*"};
		
		System.out.println(evalRPN(tokens));
	}
	
	private static int evalRPN(String[] tokens) {

		int resultValue = 0;
		String operators = "+-*/";
		
		Stack<String> stack = new Stack<String>();
		
		for(String t : tokens){
			
			if(!operators.contains(t))
				stack.push(t);
			else{
				int a = Integer.valueOf(stack.pop());
				int b = Integer.valueOf(stack.pop());
				switch(t){
				case "+":
					stack.push(String.valueOf(a+b));
					break;
				case "-":
					stack.push(String.valueOf(b-a));
					break;
				case "*":
					stack.push(String.valueOf(b*a));
					break;
				case "/":
					stack.push(String.valueOf(b/a));
					break;
				}
			}
		}
		resultValue = Integer.valueOf(stack.pop());
		
		return resultValue;
	}
	
}
