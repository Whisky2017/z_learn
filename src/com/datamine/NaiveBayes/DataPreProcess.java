package com.datamine.NaiveBayes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

/**
 * Newsgroup文档预处理
 * step1：英文词法分析，取出数字、连字符、标点符号、特殊字符，所有大写字母转换成小写，可用正则表达式：String res[] = line.split("[^a-zA-Z]");
 * step2：去停用词，过滤对别无价值的词
 * step3：词根还原stemmer，基于Porter算法
 * @author Administrator
 *
 */
public class DataPreProcess {

	private static ArrayList<String> stopWordsArray = new ArrayList<String>();
	
	/**
	 * 输入文件的路径，处理数据
	 * @param srcDir 文件目录的绝对路径
	 * @param desDir 清洗后的文件路径
	 * @throws Exception
	 */
	public void doProcess(String srcDir) throws Exception{
		
		File fileDir = new File(srcDir);
		if(!fileDir.exists()){
			System.out.println("文件不存在!");
			return ;
		}
		
		String subStrDir = srcDir.substring(srcDir.lastIndexOf('/'));
		String dirTarget = srcDir+"/../../processedSample"+subStrDir;
		File fileTarget = new File(dirTarget);
		
		if(!fileTarget.exists()){
			//注意processedSample需要先建立目录建出来，否则会报错，因为母目录不存在
			boolean mkdir = fileTarget.mkdir();
		}
		
		File[] srcFiles = fileDir.listFiles();
		
		for(int i =0 ;i <srcFiles.length;i++){
			
			String fileFullName = srcFiles[i].getCanonicalPath(); //CanonicalPath不但是全路径，而且把..或者.这样的符号解析出来。
			String fileShortName = srcFiles[i].getName(); //文件名
			
			if(!new File(fileFullName).isDirectory()){ //确认子文件名不是目录，如果是可以再次递归调用
				System.out.println("开始预处理："+fileFullName);
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append(dirTarget+"/"+fileShortName);
				
				createProcessFile(fileFullName,stringBuilder.toString());
				
			}else{
				fileFullName = fileFullName.replace("\\", "/");
				doProcess(fileFullName);
			}
		}
	}
	
	/**
	 * 进行文本预处理生成目标文件
	 * @param srcDir 源文件文件目录的绝对路径
	 * @param targetDir 生成目标文件的绝对路径
	 * @throws Exception 
	 */
	private void createProcessFile(String srcDir, String targetDir) throws Exception {
		
		FileReader srcFileReader = new FileReader(srcDir);
		FileWriter targetFileWriter = new FileWriter(targetDir);
		BufferedReader srcFileBR = new BufferedReader(srcFileReader);
		String line,resLine;
		
		while((line = srcFileBR.readLine()) != null){
			resLine = lineProcess(line);
			if(!resLine.isEmpty()){
				//按行写，一行写一个单词
				String[] tempStr = resLine.split(" ");
				for(int i =0; i<tempStr.length ;i++){
					if(!tempStr[i].isEmpty())
						targetFileWriter.append(tempStr[i]+"\n");
				}
			}
		}
		
		targetFileWriter.flush();
		targetFileWriter.close();
		srcFileReader.close();
		srcFileBR.close();
		
	}

	/**
	 * 对每行字符串进行处理，主要是词法分析、去停用词和stemming(去除时态)
	 * @param line 待处理的一行字符串
	 * @param stopWordsArray 停用词数组
	 * @return String 处理好的一行字符串，是由处理好的单词重新生成，以空格为分隔符
	 */
	private String lineProcess(String line) {
		
		/*
		 * step1 
		 * 英文词法分析，去除数字、连字符、标点符号、特殊字符，
		 * 所有大写字符转换成小写，可以考虑使用正则表达式
		 */
		String res[] = line.split("[^a-zA-Z]");
		
		//step2 去停用词，大写转换成小写 
		//step3 Stemmer.run()
		String resString = new String();
		
		for(int i=0;i<res.length;i++){
			if(!res[i].isEmpty() && !stopWordsArray.contains(res[i].toLowerCase()))
				resString += " " + Stemmer.run(res[i].toLowerCase()) + " ";
		}
		
		return resString;
	}

	/**
	 * 用stopWordsArray构造停用词的ArrayList容器
	 * @param stopwordsPath
	 * @throws Exception 
	 */
	private static void stopWordsToArray(String stopwordsPath) throws Exception {
		
		FileReader stopWordsReader = new FileReader(stopwordsPath);
		BufferedReader stopWordsBR = new BufferedReader(stopWordsReader);
		String stopWordsLine = null;
		
		//用stopWordsArray构造停用词的ArrayList容器
		while((stopWordsLine = stopWordsBR.readLine()) != null){
			if(!stopWordsLine.isEmpty())
				stopWordsArray.add(stopWordsLine);
		}
		
		stopWordsReader.close();
		stopWordsBR.close();
	}
	
	public static void main(String[] args) throws Exception{
		
		DataPreProcess dataPrePro = new DataPreProcess();
		String srcDir = "E:/DataMiningSample/orginSample";
		
		String stopwordsPath = "E:/DataMiningSample/stopwords.txt";
		
		stopWordsToArray(stopwordsPath);
		
		dataPrePro.doProcess(srcDir);
	}

	
}
