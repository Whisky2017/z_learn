package com.lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
























import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;


public class Lucene {

	private static Lucene lucene;
	private static String content = "";
	private static String INDEX_DIR = "E:\\lucene\\luceneIndex";
	private static String DATA_DIR = "E:\\lucene\\luceneData";
	private static Analyzer analyzer = null;
	private static Directory directory = null;
	private static IndexWriter indexWriter = null;
	
	
	/**
	 * lucene 管理器
	 */
	public Lucene getLucene(){
		if(lucene == null){
			this.lucene = new Lucene();
		}
		return lucene;
	}
	
	/**
	 * 创建当前文件目录的索引
	 * @param path 当前目录
	 * @return 是否成功
	 */
	public static boolean createIndex(String path){
		
		Date date1 = new Date();
		
		List<File> fileList = getFileList(path);
		
		for(File file : fileList){
			
			content = "";
			//获取文件后缀
			String type = file.getName().substring(file.getName().lastIndexOf(".")+1);
			
			if("txt".equalsIgnoreCase(type)){
				content += txt2String(file);
			}else if("doc".equalsIgnoreCase(type)){
				content += doc2String(file);
			}else if("xls".equalsIgnoreCase(type)){
				content += xls2String(file);
			}
			
			System.out.println("name:"+file.getName());
			System.out.println("path:"+file.getPath());
			
			System.out.println();
			
			try{
				//首先，我们需要定义一个词法分析器。
				analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
				
				//第二步，确定索引文件存储的位置，Lucene提供给我们两种方式：
				directory = FSDirectory.open(new File(INDEX_DIR));
				
				File indexFile = new File(INDEX_DIR);
				if(!indexFile.exists()){
					indexFile.mkdir();
				}
				
				//第三步，创建IndexWriter，进行索引文件的写入。
				IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_CURRENT,analyzer);
				indexWriter = new IndexWriter(directory, config);
				
				//第四步，内容提取，进行索引的存储。
				Document document = new Document();
				document.add(new TextField("filename", file.getName(),Store.YES));
				document.add(new TextField("content", content, Store.YES));
				document.add(new TextField("path", file.getPath(), Store.YES));
				
				indexWriter.addDocument(document);
				indexWriter.commit();
				
			} catch(Exception e){
				e.printStackTrace();
				
			} finally{
				
				try {
					if(indexWriter != null)
						indexWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			content = "";
		}
		
		Date date2 = new Date();
		
		System.out.println("创建索引-----耗时：" + (date2.getTime() - date1.getTime()) + "ms\n");
		
		return true;
	}

	/**
	 * 查找索引，返回符合条件的文件
	 * @param text 查找的字符串
	 * @return 符合条件的文件list
	 */
	public static void searchIndex(String text) {
		Date date1 = new Date();
		try {
			
			directory = FSDirectory.open(new File(INDEX_DIR));
			analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
			
			//第一步，打开存储位置
			DirectoryReader ireader = DirectoryReader.open(directory);
			
			//第二步，创建搜索器
			IndexSearcher isearcher = new IndexSearcher(ireader);

			//第三步，类似SQL，进行关键字查询
			QueryParser parser = new QueryParser(Version.LUCENE_CURRENT,
					"content", analyzer);
			Query query = parser.parse(text);

			ScoreDoc[] hits = isearcher.search(query, null, 1000).scoreDocs;

			for (int i = 0; i < hits.length; i++) {
				Document hitDoc = isearcher.doc(hits[i].doc);
				System.out.println("____________________________");
				System.out.println(hitDoc.get("filename"));
				System.out.println(hitDoc.get("content"));
				System.out.println(hitDoc.get("path"));
				System.out.println("____________________________");
			}
			//第四步，关闭查询器等。
			ireader.close();
			directory.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Date date2 = new Date();
		System.out.println("查看索引-----耗时：" + (date2.getTime() - date1.getTime())
				+ "ms\n");
	}

	/**
	 * 读取xls文件内容
	 * @param file
	 * @return
	 */
	private static String xls2String(File file) {

		String result = "";
		try {
			FileInputStream fis = new FileInputStream(file);
			StringBuilder sb = new StringBuilder();
			jxl.Workbook rwb = Workbook.getWorkbook(fis);
			Sheet[] sheet = rwb.getSheets();
			for (int i = 0; i < sheet.length; i++) {
				Sheet rs = rwb.getSheet(i);
				for (int j = 0; j < rs.getRows(); j++) {
					Cell[] cells = rs.getRow(j);
					for (int k = 0; k < cells.length; k++)
						sb.append(cells[k].getContents()+" ");
				}
			}
			fis.close();
			result += sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 读取doc文件中的内容
	 * @param file
	 * @return
	 */
	private static String doc2String(File file) {

		String result = "";
		
		try {
			FileInputStream fis = new FileInputStream(file);
			HWPFDocument doc = new HWPFDocument(fis);
			Range rang = doc.getRange();
			result += rang.text();
			fis.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 读取TXT文件的内容
	 * @param file
	 * @return
	 */
	private static String txt2String(File file) {

		String result = "";
		
		try {
			//构造一个BufferedReader类来读取文件
			BufferedReader br = new BufferedReader(new FileReader(file));
			String s = null;
			while((s = br.readLine()) != null){
				result = result + "\n" + s;
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 过滤目录下的文件
	 * @param dirpath 想要获取文件的目录
	 * @return 返回文件list
	 */
	private static List<File> getFileList(String dirpath) {
		
		File[] files = new File(dirpath).listFiles();
		List<File> fileList = new ArrayList<File>();
		for(File file : files){
			if(isTxtFile(file.getName())){
				fileList.add(file);
			}
		}
		
		return fileList;
	}

	/**
	 * 判断是否为目标文件，目前支持txt xls doc 格式
	 * @param fileName 文件名称
	 * @return 返回布尔型
	 */
	private static boolean isTxtFile(String fileName) {

		if(fileName.lastIndexOf(".txt") > 0)
			return true;
		else if(fileName.lastIndexOf(".xls") > 0)
			return true;
		else if(fileName.lastIndexOf(".doc") > 0)
			return true;
		return false;
	}
	
	/**
	 * 删除文件目录下所有的文件
	 * @param fileIndex 要删除的文件目录
	 * @return 成功返回true
	 */
	private static boolean deleteDir(File file) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				deleteDir(files[i]);
			}
		}
		file.delete();
		return true;
	}

	public static void main(String[] args) {
		
		File fileIndex = new File(INDEX_DIR);
		if(deleteDir(fileIndex)){
			fileIndex.mkdir();
		}else{
			fileIndex.mkdir();
		}
		
		createIndex(DATA_DIR);
		searchIndex("main");
		
	}

	
	
	
}
