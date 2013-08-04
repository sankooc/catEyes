package org.cateyes.core.entity;

import java.io.File;

import org.cateyes.core.VideoConstants.Provider;

public interface Volumn {

	Provider getProvider();

//	void write(/* OutputStream out */) throws Exception;

	void write(File dir) throws Exception;
	
	void setTitle(String title);

	void setUris(String[] uris);
	
	String getTitle();
	
	String[] getUris();

}
