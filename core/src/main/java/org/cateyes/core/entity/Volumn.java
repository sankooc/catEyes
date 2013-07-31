package org.cateyes.core.entity;

import org.cateyes.core.VideoConstants.Provider;

public interface Volumn {
	// String getName();

	Provider getProvider();

	void write(/* OutputStream out */) throws Exception;

	void setTitle(String title);

	void setUris(String[] uris);

}
