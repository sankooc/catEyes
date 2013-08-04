package org.cateyes.core;

import org.cateyes.core.entity.Volumn;

public interface Resolver {

	Volumn createVolumn(String uri) throws Exception;
	
	boolean isPrefer(String uri);
}
