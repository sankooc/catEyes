package org.cateyes.core;

public interface Resolver {
	
	String[] getResource(String uri);

	boolean isPrefer(String uri);
}
