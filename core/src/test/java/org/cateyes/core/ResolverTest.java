/**
 * 
 */
package org.cateyes.core;

import java.io.FileNotFoundException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Assert;

import org.junit.Test;

/**
 * @author sankooc
 *
 */
public class ResolverTest {
	
	@Test
	public void youkutest() throws FileNotFoundException{
		
		String str = "var vid=\"736167\";";
		Pattern pattern = Pattern.compile("var vid=\"(\\d+)\";");
		
		
		Matcher mathcer = pattern.matcher(str);
//		System.out.println(mathcer.find());
		Assert.assertTrue(mathcer.find());
//		File file = new File("target/youku");
//		Volumn volumn = VolumnFactory.createVolumn("http://v.youku.com/v_show/id_XNTQ2OTc0OTAw.html", file);
//		Assert.assertNotNull(volumn);
//		File tm = new File("target/tmp.flv");
//		FileOutputStream out = new FileOutputStream(tm);
//		volumn.write();
//		String[] s =  YoukuResolver.getReadUriFromYID("XNTQ2OTc0OTAw", VideoType.FLV);
//		String uriStr = s[0];
//		URI uri = URI.create(uriStr);
		
//		YoukuResolver resolver = new YoukuResolver();
//		String url1 = "http://v.youku.com/v_show/id_XNTQ2OTc0OTAw.html";
//		String sid = resolver.getYoukuSid(URI.create(url1));
//		resolver.resolvSid("XNTQ2OTc0OTAw");
//		1198
//		27078
//		System.out.println(resolver.getFileIDMixString(4263));
//		4263
//		42063
//		mk/rCvw6lV7tJ\E8:G4fdhpDZTI91NBK-RsgUy.L_OjYoWeQzX5qnaixFHc3PASMu02b
	}
	
}
