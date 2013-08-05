package org.cateyes.core.entity;

import java.io.File;
import java.util.Map;

import org.cateyes.core.VideoConstants.Provider;

public interface Volumn {

//	Provider getProvider();

	void write(File dir) throws Exception;
	void addUrl(String url, long size);
	Map<String,Long> getUrlSet();
//	void setTitle(String title);
//
//	void setUris(String[] uris);
//	
//	String getTitle();
//	
//	String[] getUris();

}
