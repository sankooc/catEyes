package org.cateyes.core.resolver;

import org.cateyes.core.volumn.Volumn;

public interface Resolver {

	Volumn createVolumn(String uri) throws Exception;
	
	Volumn createVolumnFromVid(String vid) throws Exception;
	
	boolean isPrefer(String uri);
}
