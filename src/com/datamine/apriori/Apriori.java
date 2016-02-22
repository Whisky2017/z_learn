package com.datamine.apriori;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 本程序用于频繁集的挖掘
 * 首先用List<List<String>>类型的record将矩阵形式的数据读入内存
 * 
 * 程序先求出k-1候选集，由后选集和数据库记录record求得满足支持度的k-1级集合，
 * 在满足支持度集合中求出满足置信度的集合
 * 若满足置信度的集合为空，程序停止
 * 否则输出满足自信度的集合，以及对应的支持度和置信度
 * 并由满足支持度的k-1级集合求出候选k级集合，进入下一轮循环
 * 
 * 直至程序结束，输出全部频繁集
 * @author Administrator
 *
 */
public class Apriori {

	static boolean endTag = false;
	static Map<Integer,Integer> dCountMap = new HashMap<Integer, Integer>(); //k-1频繁集的记数表
	static Map<Integer,Integer> dkCountMap = new HashMap<Integer, Integer>(); //k频繁集的记数表
	static List<List<String>> record = new ArrayList<List<String>>(); //数据记录表
//	final static double MIN_SUPPORT = 0.2; //最小支持度
//	final static double MIN_CONF = 0.8;  //最小置信度
	static double MIN_SUPPORT = 0.2; //最小支持度
	static double MIN_CONF = 0.8;  //最小置信度
	static int lable = 1; //用于输出时的一个标记，记录当前打印第几级关联集
	static List<Double> confCount = new ArrayList<Double>(); //置信度记录表
	static List<List<String>> confItemset = new ArrayList<List<String>>(); //满足支持度的集合
	
	/*public static void main(String[] args) throws Exception {
		
		record = new TxtReader().getRecord(); //获取数据
		//System.out.println(record);
		List<List<String>> cItemset = findFirstCandidate(); //获取第一次的候选集
		//System.out.println(cItemset);
		List<List<String>> lItemset = getSupportedItemset(cItemset); //获取候选集cItemset满足支持的集合
		
		while(endTag != true){ //只要能继续挖掘
			
			List<List<String>> ckItemset = getNextCandidate(lItemset); //获取下一次的候选集

			List<List<String>> lkItemset = getSupportedItemset(ckItemset); //获取候选集ckItemset满足支持的集合
			
			System.out.println(lkItemset);
			
			getConfidencedItemset(lkItemset,lItemset,dCountMap,dkCountMap);
			
			if(confItemset.size() != 0) //满足置信度的集合不为空
				printConfItemset(confItemset); //打印满足置信度的集合
			confItemset.clear(); //清空置信度集合
			
			cItemset = ckItemset;
			lItemset = lkItemset;
			
			dCountMap.clear();
			dCountMap.putAll(dkCountMap);
			
		}
		
	}*/
	
	
	public void run(List<List<String>> record2,double support,double conf){
		
		record = record2;
		MIN_SUPPORT = support;
		MIN_CONF = conf;
		
		List<List<String>> cItemset = findFirstCandidate(); //获取第一次的候选集
		
		List<List<String>> lItemset = getSupportedItemset(cItemset); //获取候选集cItemset满足支持的集合
		
		while(endTag != true){ //只要能继续挖掘
			
			List<List<String>> ckItemset = getNextCandidate(lItemset); //获取下一次的候选集

			List<List<String>> lkItemset = getSupportedItemset(ckItemset); //获取候选集ckItemset满足支持的集合
			
			System.out.println(lkItemset);
			
			getConfidencedItemset(lkItemset,lItemset,dCountMap,dkCountMap);
			
			if(confItemset.size() != 0) //满足置信度的集合不为空
				printConfItemset(confItemset); //打印满足置信度的集合
			confItemset.clear(); //清空置信度集合
			
			cItemset = ckItemset;
			lItemset = lkItemset;
			
			dCountMap.clear();
			dCountMap.putAll(dkCountMap);
			
		}
	}

	
	/**
	 * 打印结果
	 * @param confItemset2
	 */
	private static void printConfItemset(List<List<String>> confItemset2) {
		
		System.out.println("*******************频繁模式挖掘结果***********************");
		for(int i = 0; i < confItemset2.size();i++){
			int j =0;
			for(j = 0; j < confItemset2.get(i).size()-3;j++)
				System.out.print(confItemset2.get(i).get(j)+" ");
			System.out.print("-->");
			System.out.print(confItemset2.get(i).get(j++));
			System.out.print(" 相对支持度："+confItemset2.get(i).get(j++));
			System.out.print(" 自信度："+confItemset2.get(i).get(j++)+"\n");
		}
		
	}

	/**
	 * 根据4个参数求出满足自信度的集合
	 * @param lkItemset k项候选集
	 * @param lItemset  k-1项集
	 * @param dCountMap2 k-1项频繁项纪录数
	 * @param dkCountMap2 k项频繁项纪录数
	 */
	private static void getConfidencedItemset(List<List<String>> lkItemset,
			List<List<String>> lItemset, Map<Integer, Integer> dCountMap2,
			Map<Integer, Integer> dkCountMap2) {
		
		for(int i = 0;i<lkItemset.size();i++){
			
			getConfItem(lkItemset.get(i),lItemset,dkCountMap2.get(i),dCountMap2);
		}
		
	}

	/**
	 * 检验集合list是否满足最低自信度要求
	 * 若满足则在全局变量confiItemset添加list
	 * 不满足返回null
	 * @param list k频繁项集中第i个元组
	 * @param lItemset k-1项集
	 * @param count  k项集第i个元组计数
	 * @param dCountMap2 k-1项频繁项纪录map
	 */
	private static void getConfItem(List<String> list,
			List<List<String>> lItemset, Integer count,
			Map<Integer, Integer> dCountMap2) {
		
		for(int i = 0;i < list.size() ;i++){
			
			List<String> testList = new ArrayList<String>();
			
			for(int j = 0; j <list.size(); j++)
				if( i !=j )
					testList.add(list.get(j));
			//查找testList中的内容在lItemset的位置
			int index = findConf(testList,lItemset);
			Double conf = count*1.0/dCountMap2.get(index);
			
			if(conf > MIN_CONF){
				testList.add(list.get(i));
				Double relativeSupport = count*1.0 /(record.size() - 1);
				testList.add(relativeSupport.toString());
				testList.add(conf.toString());
				confItemset.add(testList);
			}
			
		}
		
	}

	/**
	 * 查找testList中的内容在lItemset的位置
	 * @param testList
	 * @param lItemset
	 * @return
	 */
	private static int findConf(List<String> testList,
			List<List<String>> lItemset) {
		
		for(int i = 0; i < lItemset.size(); i++){
			
			boolean notHaveTag = false;
			for(int j = 0;j < testList.size();j++){
				if(haveThisItem(testList.get(j),lItemset.get(i))==false){
					notHaveTag = true;
					break;
				}
			}
			if(notHaveTag == false)
				return i;
		}
		
		return -1;
	}

	/**
	 * 检验list中是否包含s
	 * @param s
	 * @param list
	 * @return boolean
	 */
	private static boolean haveThisItem(String s, List<String> list) {
		
		for(int i = 0; i<list.size() ;i++)
			if(s.equals(list.get(i)))
				return true;
		return false;
		
	}

	/**
	 * 根据cItemset求得下一次的候选集合组，求出候选集合组中每一个集合的元素的个数比cItemset中的集合元素大1
	 * @param cItemset 
	 * [[I1], [I2], [I5], [I4], [I3]]
	 * @return nextItemset 
	 * [[I1, I2], [I1, I5], [I1, I4], [I1, I3], [I2, I5], [I2, I4], [I2, I3], [I5, I4], [I5, I3], [I4, I3]]
	 */
	private static List<List<String>> getNextCandidate(
			List<List<String>> cItemset) {
		
		List<List<String>> nextItemset = new ArrayList<List<String>>();
		
		for(int i =0;i<cItemset.size();i++){
			
			List<String> tempList = new ArrayList<String>(); 
			
			//tempList先存入一项
			for(int k = 0;k<cItemset.get(i).size();k++)
				tempList.add(cItemset.get(i).get(k));   
			
			//接下来每次添加下一项的一个元素
			for(int h=i+1;h<cItemset.size();h++){
				for(int j = 0;j<cItemset.get(h).size();j++){
					
					tempList.add(cItemset.get(h).get(j));
					
					//tempList的子集全部在cItemset中
					if(isSubsetInC(tempList,cItemset)){
						
						List<String> copyValueHelpList = new ArrayList<String>();
						for(int p =0;p<tempList.size();p++)
							copyValueHelpList.add(tempList.get(p));
						if(isHave(copyValueHelpList,nextItemset))
							nextItemset.add(copyValueHelpList);
					}
					tempList.remove(tempList.size()-1);
				}
			}
			
		}
		
		return nextItemset;
	}

	/**
	 * 检验nextItemset中是否包含copyValueHelpList
	 * @param copyValueHelpList
	 * @param nextItemset
	 * @return boolean
	 */
	private static boolean isHave(List<String> copyValueHelpList,
			List<List<String>> nextItemset) {
		
		for(int i =0;i<nextItemset.size();i++)
			if(copyValueHelpList.equals(nextItemset.get(i)))
				return false;
		return true;
	}

	/**
	 * 检验tempList是不是cItemset的子集
	 * @param tempList
	 * @param cItemset
	 * @return boolean
	 */
	private static boolean isSubsetInC(List<String> tempList,
			List<List<String>> cItemset) {

		boolean haveTag = false;
		
		for(int i = 0;i<tempList.size();i++){
			
			List<String> testList = new ArrayList<String>();
			for(int j = 0;j<tempList.size();j++)
				if(i != j )
					testList.add(tempList.get(j));  //testList记录tempList中k-1级频繁集
			
			for(int k = 0;k < cItemset.size();k++){
				if(testList.equals(cItemset.get(k))){ //子集存在于k-1频繁集中
					haveTag = true;
					break;
				}
			}
			
			if(haveTag == false) //其中一个子集不再k-1频繁集中
				return false;
		}
		
		return haveTag;
	}

	/** 返回候选集中满足最低支持度的集合
	 * @param cItemset
	 * [[I1, I2], [I1, I5], [I1, I4], [I1, I3], [I2, I5], [I2, I4], [I2, I3], [I5, I4], [I5, I3], [I4, I3]]
	 * @return supportedItemset
	 * [[I1, I2], [I1, I5], [I1, I3], [I2, I5], [I2, I4], [I2, I3], [I4, I3]]
	 */
	private static List<List<String>> getSupportedItemset(
			List<List<String>> cItemset) {
		
		boolean end = true;
		List<List<String>> supportedItemset = new ArrayList<List<String>>();
		int k = 0;
		
		for(int i = 0; i < cItemset.size(); i++){
			
			int count = countFrequent(cItemset.get(i)); //统计记录数
			//System.out.println(cItemset.get(i)+" " +count);
			
			if(count >= MIN_SUPPORT*(record.size()-1)){ //count值大于支持度与记录数的乘积，即满足支持度要求
				if(cItemset.get(0).size() == 1)
					dCountMap.put(k++, count);
				else
					dkCountMap.put(k++, count);
				supportedItemset.add(cItemset.get(i));
				end = false;
			}
		}
		
		endTag = end;
		return supportedItemset;
	}

	/**
	 * 统计数据库记录record中出现list中的集合的个数
	 * @param list
	 * @return  频繁项集 个数
	 */
	private static int countFrequent(List<String> list) {
		
		int count = 0;
		for(int i = 1; i<record.size(); i++){
			
			boolean notHaveThisList = false;
			
			for(int k = 0;k<list.size();k++){
				
				boolean thisRecordHave = false;
				
				for(int j = 1;j<record.get(i).size();j++){
					if(list.get(k).equals(record.get(i).get(j)))
						thisRecordHave = true;
				}
				
				if(!thisRecordHave){ // 扫描一遍记录表的一行，发现list.get(i)不在记录表的第j行中，即list不可能在j行中
					notHaveThisList = true;
					break;
				}
			}
			
			if(notHaveThisList == false)
				count++;
		}
		
		return count;
	}

	/**
	 * 根据数据库记录求出第一次候选集
	 * @return 返回一级候选集
	 */
	private static List<List<String>> findFirstCandidate() {
		
		List<List<String>> tableList = new ArrayList<List<String>>();
		List<String> lineList = new ArrayList<String>();
		
		int size = 0;
		for(int i = 1; i < record.size(); i++){
			for(int j = 1; j<record.get(i).size();j++){
				if(lineList.isEmpty())
					lineList.add(record.get(i).get(j));
				else{
					boolean haveThisItem = false;
					size = lineList.size();
					for(int k = 0; k<size;k++){
						if(lineList.get(k).equals(record.get(i).get(j))){
							haveThisItem = true;
							break;
						}
					}
					if(haveThisItem == false)
						lineList.add(record.get(i).get(j));
				}
			}
		}
		// [I1, I2, I5, I4, I3]
		// System.out.println(lineList);
		
		for(int i = 0; i<lineList.size();i++){
			List<String> helpList = new ArrayList<String>();
			helpList.add(lineList.get(i));
			tableList.add(helpList);
		}
		// [[I1], [I2], [I5], [I4], [I3]]
		// System.out.println(tableList);
		
		return tableList;
	}
	
	
}
