package org.cateyes.core.volumn;

import java.io.File;
import java.util.Map;

/**
 * @author sankooc
 */
public interface Volumn {

	// Provider getProvider();

	void write(File dir) throws Exception;

	void addUrl(String url, long size);

	Map<String, Long> getUrlSet();
	
	void setSuffix(String suffix);

}
