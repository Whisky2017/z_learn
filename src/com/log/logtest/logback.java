package com.log.logtest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class logback {

	
	//定义一个全局的记录器，通过LoggerFactory获取
	private final static Logger logger = LoggerFactory.getLogger(logback.class);
	
	public static void main(String[] args) {
		//logger.info("logback 成功了");  
        //logger.error("logback 成功了");  
		
		
		//logger.debug("Test the MessageFormat for {} to {} endTo {}", 1,2,3);
		//logger.info("Test the MessageFormat for {} to {} endTo {}", 1,2,3);
		//logger.error("Test the MessageFormat for {} to {} endTo {}", 1,2,3);
        /*for(int i=0;i<200;i++)
        {
        	try{
                //throw new IllegalStateException("try to throw an Exception");
            	int a= 1/0;
            }catch(Exception e){
            	logger.error(e.getMessage(),e);
            }
        }*/
		
		logger.info("package com.log.logtest;");
        
	
	}
}
