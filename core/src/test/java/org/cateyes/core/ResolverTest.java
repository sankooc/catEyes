/**
 * 
 */
package org.cateyes.core;

import java.net.URI;

import org.cateyes.core.youku.YoukuResolver;
import org.junit.Test;

/**
 * @author sankooc
 *
 */
public class ResolverTest {
	
	@Test
	public void youkutest(){
		YoukuResolver resolver = new YoukuResolver();
		String url1 = "http://v.youku.com/v_show/id_XNTQ2OTc0OTAw.html";
		String sid = resolver.getYoukuSid(URI.create(url1));
	}
	
}
