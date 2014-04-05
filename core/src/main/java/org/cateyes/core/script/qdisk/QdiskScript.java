package org.cateyes.core.script.qdisk;


import org.cateyes.core.script.SiteScript;

public class QdiskScript implements SiteScript {
	final QdiskResolver resolver = new QdiskResolver();
	
	public org.cateyes.core.script.SiteScript.Program getResource(String url) throws Exception{
		org.cateyes.core.script.SiteScript.Program program = resolver.getProgram2(url);
		return program;
	}
}
