package org.cateyes.core.volumn;

import java.io.File;
import java.util.Collection;

import org.cateyes.core.IHeader;
import org.cateyes.core.VideoConstants.Provider;

/**
 * @author sankooc
 */
public interface Volumn extends IHeader {

	Provider getProvider();

	void addFragment(int quality, String suffix, String url);
	
	void addFragment(int quality, String suffix, String url, long size);
	
	void addFragment(int quality, String url, long size);
	
	void writeLowQuality(File dir) throws Exception;

	void write(File dir,int quality) throws Exception;
	
	void writeHighQuality(File dir) throws Exception;

	int getQualityCount();
	
	Collection<String> getFragmentURL(int quality);
	
	String getTitle();

}
