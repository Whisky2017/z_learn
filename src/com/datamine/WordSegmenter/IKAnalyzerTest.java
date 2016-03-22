package com.datamine.WordSegmenter;


import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class IKAnalyzerTest {

	public static String str="基于java语言开发的轻量级的中文分词工具包";
	
	public static void main(String[] args) throws Exception {
		
		//基于Lucene实现
		Analyzer analyzer = new IKAnalyzer(true); //true智能切分,false为细粒度的分词
		StringReader reader = new StringReader(str);
		TokenStream ts = analyzer.tokenStream("", reader);
		CharTermAttribute term = ts.getAttribute(CharTermAttribute.class);
		
		while(ts.incrementToken()){
			System.out.print(term.toString()+ "|");
		}
		reader.close();
		
		System.out.println();
		
		//独立实现
		StringReader re = new StringReader(str);
		IKSegmenter ik = new IKSegmenter(re, false);
		Lexeme lex = null;
		while((lex = ik.next())!=null){
			System.out.print(lex.getLexemeText()+"|");
		}
		
	}
	
	
}
