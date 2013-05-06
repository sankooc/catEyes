package org.cateyes.core.entity;

import java.io.OutputStream;

import org.cateyes.core.VideoConstants.Provider;

public interface Volumn {
	String getName();

	Provider getProvider();
	
	void write(OutputStream out);
	
}
