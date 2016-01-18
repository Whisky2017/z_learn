package com.algorithms.StringArrayMatrix;

import java.util.LinkedList;

class ListNode{
	int val;
	ListNode next;
	ListNode(int x){
		val = x;
		next = null;
	}
}




public class AddTwoNumbers {

	public static ListNode addTwoNo(ListNode l1,ListNode l2){
		
		int carry = 0;
		ListNode newHead = new ListNode(0);
		ListNode p1 = l1,p2 = l2,p3= newHead;
		while(p1 !=null || p2 != null){
			
			if(p1 != null){
				carry += p1.val;
				p1=p1.next;
			}
			
			if(p2!=null){
				carry += p2.val;
				p2 = p2.next;
			}
			
			p3.next = new ListNode(carry%10);
			p3=p3.next;
			carry/=10;
		}
		
		if(carry == 1)
			p3.next = new ListNode(1);
		return newHead.next;
	}
	
	
	public static LinkedList<Integer> addTwoNo(LinkedList<Integer> l1,
			LinkedList<Integer> l2) {
		
		int carry = 0;

		LinkedList<Integer> l3 = new LinkedList<Integer>();

		int len1 = l1.size();
		int len2 = l2.size();
		//取最大的长度
		for(int i=0;i<len1;i++){
			
			carry += l1.get(i)+l2.get(i);
			l3.add(carry%10);
			carry /= 10;
		}
		
		return l3;
	}
	
	public static void main(String[] args) {
		
		/*ListNode l1 = new ListNode(2);
		l1.next = new ListNode(4);
		l1.next.next = new ListNode(3);
		ListNode l2 = new ListNode(5);
		l2.next = new ListNode(6);
		l2.next.next= new ListNode(4);
		ListNode result = addTwoNo(l1,l2);
		while(result != null){
			System.out.print(result.val);
			result = result.next;
			if(result != null)
				System.out.print("_>");
		}*/
		LinkedList<Integer> l1 = new LinkedList<Integer>();
		LinkedList<Integer> l2 = new LinkedList<Integer>();
		
		l1.add(2);
		l1.add(4);
		l1.add(3);
		
		l2.add(5);
		l2.add(6);
		l2.add(4);
		
		LinkedList result = addTwoNo(l1,l2);
		for(int i = 0; i<result.size();i++){
			System.out.print(result.get(i));
		}
		
	}
	
	
}
