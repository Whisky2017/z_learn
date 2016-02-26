package com.datamine.FPtree;

import java.util.ArrayList;
import java.util.List;

public class FPNode {

	String idName; //id号
	List<FPNode> children; // 孩子节点
	FPNode parent; // 父节点
	FPNode next; //下一个节点
	long count; // 出现的次数
	
	//无参构造函数 ，初始化根节点
	public FPNode(){
		this.idName = null;
		this.count = -1;
		children = new ArrayList<FPNode>();
		next = null;
		parent = null;
	}
	
	//有参构造函数，初始化叶子节点
	public FPNode(String idName){
		this.idName = idName;
		this.count = 1;
		children = new ArrayList<FPNode>();
		next = null;
		parent = null;
	}
	
	//有参构造函数，初始化非根节点
	public FPNode(String idName,long count){
		this.idName = idName;
		this.count = count;
		children = new ArrayList<FPNode>();
		next = null;
		parent = null;
	}
	
	/**
	 * 添加一个孩子
	 * @param child
	 */
	public void addChild(FPNode child){
		children.add(child);
	}
	
	/**
	 * 取指定的孩子
	 * @param index
	 * @return
	 */
	public FPNode getChild(int index){
		return children.get(index);
	}
	
	/**
	 * 计数器加1
	 */
	public void addCount(){
		this.count += 1;
	}
	
	public void addCount(int count){
		this.count += count;
	}
	
	/**
	 * 设置下一个节点
	 * @param next
	 */
	public void setNextNode(FPNode next){
		this.next = next;
	}
	
	/**
	 * 设置父节点
	 * @param parent
	 */
	public void setParent(FPNode parent){
		this.parent = parent;
	}
	
	/**
	 * 查找是否包含id号为idName的孩子
	 * @param idName
	 * @return 孩子下标号
	 */
	public int hasChild(String idName){
		
		for(int i = 0;i<children.size();i++){
			if(children.get(i).idName.equals(idName))
				return i ;
		}
		return -1;
	}
	
	public String toString(){
		return "id:"+idName+" Count:"+count+" 孩子个数："+children.size();
	}
	
}
