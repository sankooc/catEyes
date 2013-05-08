/**
 * 
 */
package org.cateyes.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author sankooc
 * 
 */
public class CommonAdaptor extends Adaptor implements MResource {

	Logger logger = LoggerFactory.getLogger(CommonAdaptor.class);
	long total;
	long current;
	long counter;
	long limit;
	final String taskName;

	public CommonAdaptor(String name) {
		this.taskName = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cateyes.core.MResource#init()
	 */
	public void init() {
		if (logger.isDebugEnabled()) {
			logger.debug("{} is init", taskName);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cateyes.core.MResource#start()
	 */
	public void start() {
		if (logger.isDebugEnabled()) {
			logger.debug("{} is started", taskName);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cateyes.core.MResource#error(java.lang.String)
	 */
	public void error(String msg) {
		this.flag = true;
		if (logger.isDebugEnabled()) {
			logger.debug("{} occur error", taskName);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cateyes.core.MResource#finish()
	 */
	public void finish() {
		if (logger.isDebugEnabled()) {
			logger.debug("{} is finish total length {}", taskName, current);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cateyes.core.MResource#setLength(long)
	 */
	public void setLength(long size) {
		if (size > 0) {
			total = size;
			limit = total / 100;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("{} resource length is {} byte", taskName, size);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cateyes.core.MResource#setContent(long)
	 */
	public void setContent(long content) {
		if (content > 0) {
			current = content;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("{} resource current length is {} byte", taskName,
					current);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cateyes.core.MResource#addContent(long)
	 */
	public void addContent(long increase) {
		current += increase;
		if (limit > 0) {
			counter += increase;
			if (counter > limit) {
				logger.info("has transfer {} data", current);
				counter = 0;
			}
		}
	}
	boolean flag = false;
	public boolean isError() {
		return flag;
	}

}
