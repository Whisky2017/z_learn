package com.log;

import org.apache.log4j.Logger;

public class log {
	
	static Logger logger = Logger.getLogger(log.class);
	
	public static void main(String[] args) {
		logger.fatal("这是一条从TestServlet产生的fatal信息");
		logger.error("这是一条从TestServlet产生的error信息！");
		logger.warn("这是一条从TestServlet产生的warn信息！");
		logger.info("这是一条从TestServlet产生的info信息！");
		logger.debug("这是一条从TestServlet产生的debug信息！");
	}
}
