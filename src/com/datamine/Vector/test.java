package com.datamine.Vector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.NamedVector;
import org.apache.mahout.math.VectorWritable;


public class test {

	public static void main(String[] args) throws Exception {
		
		List<NamedVector> apples = new ArrayList<NamedVector>();
		//将名字和向量关联
		NamedVector apple;
		apple = new NamedVector(new DenseVector(new double[] {0.11,510,1}),"samll round green apple");
		apples.add(apple);
		apple = new NamedVector(new DenseVector(new double[] {0.23,650,3}),"large oval red apple");
		apples.add(apple);
		apple = new NamedVector(new DenseVector(new double[] {0.09,630,1}),"samll elongated red apple");
		apples.add(apple);
		apple = new NamedVector(new DenseVector(new double[] {0.25,590,3}),"large round yellow apple");
		apples.add(apple);
		apple = new NamedVector(new DenseVector(new double[] {0.18,520,2}),"medium oval green apple");
		
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);
		Path path = new Path("appledata/apples");
		SequenceFile.Writer writer = new SequenceFile.Writer(fs, conf, path, Text.class, VectorWritable.class);
		//序列化向量数据
		VectorWritable vec = new VectorWritable();
		for(NamedVector vector : apples){
			vec.set(vector);
			writer.append(new Text(vector.getName()), vec);
		}
		writer.close();
		
		//反序列化向量数据
		SequenceFile.Reader reader = new SequenceFile.Reader(fs, new Path("appledata/apples"), conf);
		Text key = new Text();
		VectorWritable value = new VectorWritable();
		while(reader.next(key,value)){
			System.out.println(key.toString()+" "+value.get().asFormatString());
		}
		reader.close();
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
