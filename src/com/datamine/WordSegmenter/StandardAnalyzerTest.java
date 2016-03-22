package com.datamine.WordSegmenter;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

public class StandardAnalyzerTest {

	public static String str="基于java语言开发的轻量级的中文分词工具包";
	
	public static void main(String[] args) throws Exception {
		
		//Lucene一元分词(逐字拆分)的方法
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT); 
		StringReader reader = new StringReader(str);
		TokenStream ts = analyzer.tokenStream("", reader);
		CharTermAttribute term = ts.getAttribute(CharTermAttribute.class);
		
		while(ts.incrementToken()){
			System.out.print(term.toString()+ "|");
		}
		reader.close();
		System.out.println();
		
	}
}
