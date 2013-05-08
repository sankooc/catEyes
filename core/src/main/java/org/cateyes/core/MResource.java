package org.cateyes.core;

public interface MResource {
	void init();
	void start();
	void error(String msg);
	void finish();
	boolean isError();
	void setLength(long size);
	void setContent(long content);
	void addContent(long increase);
	
}
