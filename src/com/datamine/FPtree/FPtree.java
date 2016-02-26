package com.datamine.FPtree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class FPtree {

	private static float SUPPORT = 0.6f;
	private static long absSupport;
	
	/*public static void main(String[] args) throws Exception {
		
		List<String[]> matrix = Reader.readAsMatrix("data\\FPtree.txt", "	", "UTF-8");
		absSupport = (long) (SUPPORT * matrix.size());
		System.out.println("绝对支持度："+absSupport);
		System.out.println("频繁项集：");
		
		//频繁1项集 并按降序排序
		Map<String,Integer> frequentMap = new LinkedHashMap<String, Integer>();
		
		//挖掘过程中使用到的表头 + 线索
		Map<String,FPNode> header = getHeader(matrix,frequentMap);
		
		//生成FP树
		FPNode root = getFPTree(matrix,header,frequentMap);
		
		//printHeader(header);
		
		//FP树挖掘频繁项集
		Map<Set<FPNode>,Long> frequents = fpGrowth(root,header,null);
		
		for (Map.Entry<Set<FPNode>, Long> fre : frequents.entrySet()) {
			for (FPNode node : fre.getKey())
				System.out.print(node.idName + " ");
			System.out.println("\t" + fre.getValue());
		}
	}*/

	public static void run(float support,List<String[]> matrix) throws Exception {
	
		//List<String[]> matrix = Reader.readAsMatrix("data\\FPtree.txt", "	", "UTF-8");
		absSupport = (long) (SUPPORT * matrix.size());
		System.out.println("绝对支持度："+absSupport);
		System.out.println("频繁项集：");
		
		//频繁1项集 并按降序排序
		Map<String,Integer> frequentMap = new LinkedHashMap<String, Integer>();
		
		//挖掘过程中使用到的表头 + 线索
		Map<String,FPNode> header = getHeader(matrix,frequentMap);
		
		//生成FP树
		FPNode root = getFPTree(matrix,header,frequentMap);
		
		//printHeader(header);
		
		//FP树挖掘频繁项集
		Map<Set<FPNode>,Long> frequents = fpGrowth(root,header,null);
		
		for (Map.Entry<Set<FPNode>, Long> fre : frequents.entrySet()) {
			for (FPNode node : fre.getKey())
				System.out.print(node.idName + " ");
			System.out.println("\t" + fre.getValue());
		}
	}
	

	/**
	 * 挖掘频繁项集
	 * @param root 树的根节点
	 * @param header 用于挖掘的表头
	 * @param idName 用来做判断
	 * @return conditionFres 条件频繁项集
	 */
	public static Map<Set<FPNode>, Long> fpGrowth(FPNode root,
			Map<String, FPNode> header, String idName) {
		
		Map<Set<FPNode>, Long> conditionFres = new HashMap<Set<FPNode>, Long>();
		Set<String> keys = header.keySet();
		String[] keysArray = keys.toArray(new String[0]);
		String firstIdName = keysArray[keysArray.length - 1];
		
		if (isSinglePath(header, firstIdName)) {// 只有一条路径时，求路径上的所有组合即可得到调节频繁集
			
			if (idName == null)
				return conditionFres;
			
			FPNode leaf = header.get(firstIdName);
			List<FPNode> paths = new ArrayList<FPNode>();// 自顶向上保存路径结点
			paths.add(leaf);
			FPNode node = leaf;
			while (node.parent.idName != null) {
				paths.add(node.parent);
				node = node.parent;
			}
			//单路径组合构成频繁项
			conditionFres = getCombinationPattern(paths, idName);
			
			FPNode tempNode = new FPNode(idName, -1L);
			conditionFres = addLeafToFrequent(tempNode, conditionFres);

		} else {
			
			for (int i = keysArray.length - 1; i >= 0; i--) {// 递归求条件树的频繁集
				String key = keysArray[i];
				
				List<FPNode> leafs = new ArrayList<FPNode>();
				FPNode link = header.get(key);
				while (link != null) {
					leafs.add(link);
					link = link.next;
				}
				
				Map<List<String>, Long> paths = new HashMap<List<String>, Long>(); //所有的前缀路径
				Long leafCount = 0L;
				FPNode noParentNode = null;
				for (FPNode leaf : leafs) {
					List<String> path = new ArrayList<String>();
					FPNode node = leaf;
					while (node.parent.idName != null) {
						path.add(node.parent.idName);
						node = node.parent;
					}
					leafCount += leaf.count;
					if (path.size() > 0)
						paths.put(path, leaf.count);
					else {// 没有父结点
						noParentNode = leaf;
					}
				}
				
				if (noParentNode != null) { 
					Set<FPNode> oneItem = new HashSet<FPNode>();
					oneItem.add(noParentNode);
					if (idName != null)
						oneItem.add(new FPNode(idName, -2));
					conditionFres.put(oneItem, leafCount);
				}
				
				//生成条件树
				Holder holder = getConditionFpTree(paths);
				
				if (holder.header.size() != 0) {
					
					Map<Set<FPNode>, Long> preFres = fpGrowth(holder.root,holder.header, key);
					
					if (idName != null) {
						FPNode tempNode = new FPNode(idName, leafCount);
						preFres = addLeafToFrequent(tempNode, preFres);
					}
					
					conditionFres.putAll(preFres);
				}
			}
		}
		return conditionFres;

	}

	/**
	 * 将叶子结点添加到频繁集中
	 * 
	 * @param leaf
	 * @param conditionFres
	 */
	private static Map<Set<FPNode>, Long> addLeafToFrequent(FPNode leaf,
			Map<Set<FPNode>, Long> conditionFres) {
		
		if (conditionFres.size() == 0) {
			Set<FPNode> set = new HashSet<FPNode>();
			set.add(leaf);
			conditionFres.put(set, leaf.count);
		} else {
			Set<Set<FPNode>> keys = new HashSet<Set<FPNode>>(
					conditionFres.keySet());
			for (Set<FPNode> set : keys) {
				Long count = conditionFres.get(set);
				conditionFres.remove(set);
				set.add(leaf);
				conditionFres.put(set, count);
			}
		}
		return conditionFres;
	}
	
	/**
	 * 判断一颗fptree是否为单一路径
	 * @param header
	 * @param tableLink
	 * @return
	 */
	private static boolean isSinglePath(Map<String, FPNode> header,
			String tableLink) {
		if(header.size() == 1 && header.get(tableLink).next ==null)
			return true;
		return false;
	}
	
	
	/**
	 * 生成条件树
	 * @param paths 所有路径
	 * @return
	 */
	private static Holder getConditionFpTree(Map<List<String>, Long> paths) {
		
		List<String[]> matrix = new ArrayList<String[]>();
		for (Map.Entry<List<String>, Long> entry : paths.entrySet()) {
			for (long i = 0; i < entry.getValue(); i++) {
				matrix.add(entry.getKey().toArray(new String[0]));
			}
		}
		Map<String, Integer> frequentMap = new LinkedHashMap<String, Integer>();// 一级频繁项
		Map<String, FPNode> cHeader = getHeader(matrix, frequentMap);
		FPNode cRoot = getFPTree(matrix, cHeader, frequentMap);
		return new Holder(cRoot, cHeader);
	}

	/**
	 * 求单一路径上的所有组合加上idName构成的频繁项
	 * @param paths 前缀路径
	 * @param idName 后缀项
	 * @return
	 */
	private static Map<Set<FPNode>, Long> getCombinationPattern(
			List<FPNode> paths, String idName) {
		
		Map<Set<FPNode>, Long> conditionFres = new HashMap<Set<FPNode>, Long>();
		int size = paths.size();
		for (int mask = 1; mask < (1 << size); mask++) {// 求所有组合，从1开始表示忽略空集
			Set<FPNode> set = new HashSet<FPNode>();
			// 找出每次可能的选择
			for (int i = 0; i < paths.size(); i++) {
				if ((mask & (1 << i)) > 0) {
					set.add(paths.get(i));
				}
			}
			long minValue = Long.MAX_VALUE;
			for (FPNode node : set) {
				if (node.count < minValue)
					minValue = node.count;
			}
			conditionFres.put(set, minValue);
		}
		return conditionFres;
	}

	/**
	 * 打印header表
	 * @param header
	 */
	private static void printHeader(Map<String, FPNode> header) {
		
		for(Map.Entry<String, FPNode> entry : header.entrySet()){
			
			FPNode link = header.get(entry.getKey());
			while (link != null) {
				System.out.println(link);
				link = link.next;
			}
		}
		
	}

	/**
	 * 打印root树
	 * @param root
	 */
	private static void printTree(FPNode root) {

		System.out.println(root);
		FPNode node = root.getChild(0);
		System.out.println(node);
		for(FPNode child : node.children){
			System.out.println(child);
		}
		
		System.out.println("*******");
		
		node = root.getChild(1);
		System.out.println(node);
		for(FPNode child : node.children){
			System.out.println(child);
		}
	}

	/**
	 * 构造FP树，同时利用方法的副作用更新表头
	 * @param matrix 数据矩阵
	 * @param header 挖掘中使用的表头
	 * @param frequentMap 频繁一项集 
	 * @return 返回树的根节点
	 */
	public static FPNode getFPTree(List<String[]> matrix,
			Map<String, FPNode> header, Map<String, Integer> frequentMap) {

		FPNode root = new FPNode();
		
		for(String[] line : matrix){
			
			String[] orderLine = getOrderLine(line,frequentMap); //排序
			
			FPNode parent = root;
			
			for(String idName : orderLine){
				
				int index = parent.hasChild(idName);
				if(index != -1){ //已经包含该id，不需要新建节点
					parent = parent.getChild(index);
					parent.addCount();
				}else{
					//不包含,则新建一个节点
					FPNode node = new FPNode(idName);
					parent.addChild(node);
					node.setParent(parent);
					
					FPNode nextNode = header.get(idName);
					if(nextNode == null){ //表头还是为空个，添加到表头
						header.put(idName, node);
					}else{
						//添加的表头节点线索
						while(nextNode.next != null){
							nextNode = nextNode.next;
						}
						nextNode.next = node;
					}
					
					parent = node; //以后的节点挂在当前节点下面
				}
			}
		}
		
		return root;
	}

	/**
	 * 将line数组里id按照frequentMap的值 降序排序
	 * @param line
	 * @param frequentMap
	 * @return 排序好的line
	 */
	private static String[] getOrderLine(String[] line,
			Map<String, Integer> frequentMap) {

		Map<String,Integer> countMap = new HashMap<String, Integer>();
		
		for(String idName : line){
			if(frequentMap.containsKey(idName)) //过滤掉非频繁一项集
				countMap.put(idName, frequentMap.get(idName));
		}
		
		List<Map.Entry<String, Integer>> mapList = new ArrayList<Map.Entry<String,Integer>>(
				countMap.entrySet());
		
		Collections.sort(mapList, new Comparator<Map.Entry<String, Integer>>(){

			@Override
			public int compare(Entry<String, Integer> v1,
					Entry<String, Integer> v2) {
				return v2.getValue()-v1.getValue();
			}
		});
		
		String[] orderLine = new String[countMap.size()];
		int i = 0;
		for(Map.Entry<String, Integer> entry : mapList){
			orderLine[i] = entry.getKey();
			i++;
		}
		
		return orderLine;
	}

	/**
	 * 生成表头
	 * @param matrix 整个数据
	 * @param frequentMap 频繁项集
	 * @return 表头的键为id号，并且按照出现次数降序排列
	 */
	public static Map<String, FPNode> getHeader(List<String[]> matrix,
			Map<String, Integer> frequentMap) {
		
		Map<String,Integer> countMap = new HashMap<String, Integer>();
		
		for(String[] line : matrix){
			for(String idName : line){
				
				if(countMap.containsKey(idName))
					countMap.put(idName, countMap.get(idName)+1);
				else
					countMap.put(idName, 1);
			}
		}
		
		for(Map.Entry<String, Integer> entry : countMap.entrySet()){
			if(entry.getValue() >= absSupport) //过滤掉不满足支持度的项
				frequentMap.put(entry.getKey(), entry.getValue());
		}
		
		//降序排序
		List<Map.Entry<String, Integer>> mapList = new ArrayList<Map.Entry<String,Integer>>(
				frequentMap.entrySet());
		Collections.sort(mapList, new Comparator<Map.Entry<String, Integer>>() {

			@Override
			public int compare(Entry<String, Integer> o1,
					Entry<String, Integer> o2) {
				return o2.getValue() - o1.getValue();
			}

		});
		
		frequentMap.clear(); //清空，以便保持有序的键值对
		
		Map<String, FPNode> header = new LinkedHashMap<String, FPNode>();
		for(Map.Entry<String, Integer> entry : mapList){
			header.put(entry.getKey(), null);
			frequentMap.put(entry.getKey(), entry.getValue());
		}
		
		return header;
		
	}
	
	
}


/**
* 
* 生成条件树用到的包装器
* 
*/
class Holder {
	public final FPNode root;
	public final Map<String, FPNode> header;

	public Holder(FPNode root, Map<String, FPNode> header) {
		this.root = root;
		this.header = header;
	}
}
