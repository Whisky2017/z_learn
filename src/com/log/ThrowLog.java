package com.log;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.log4j.Logger;

public class ThrowLog {

	static Logger logger = Logger.getLogger(ThrowLog.class);
	
	public static void main(String[] args) {
		
		try {
			//int[] a = new int[2];
			//a[3]=0;
			double a = 1/0;
			
		} catch (Exception e) {
			
			StringWriter sw = null;
	        PrintWriter pw = null;
	        try {
	            sw = new StringWriter();
	            pw =  new PrintWriter(sw);
	            //将出错的栈信息输出到printWriter中
	            e.printStackTrace(pw);
	            pw.flush();
	            sw.flush();
	        } finally {
	            if (sw != null) {
	                try {
						sw.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
	            }
	            if (pw != null) {
	                pw.close();
	            }
	        }
	        logger.error(sw.toString());
		}
	}
}