package org.cateyes.core.volumn;

import java.io.File;
import java.util.Map;

import org.cateyes.core.VideoConstants.Provider;

/**
 * @author sankooc
 */
public interface Volumn {

	 Provider getProvider();

	void write(File dir) throws Exception;

	void addUrl(String url, long size);
	
	void addUrl(String url);

	String getTitle();
	
	Map<String, Long> getUrlSet();
	
	void setSuffix(String suffix);

}
