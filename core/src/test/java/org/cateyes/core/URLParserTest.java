package org.cateyes.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Assert;

import org.cateyes.core.resolver.AbstractResolver;
import org.cateyes.core.resolver.wuliu.WulResolver;
import org.junit.Test;

public class URLParserTest {
	
	@Test
	public void test56(){
				WulResolver resolver = new WulResolver();
		test("http://www.56.com/n_v139_/c37_/2_/12_/kimguoxing_/sc_mp4_120479247657_/3745000_/0_/30280885.swf", "MzAyODA4ODU=", resolver);
		test("http://www.56.com/u69/v_ODg5MTIzNTQ.html","ODg5MTIzNTQ",resolver);
		
	}
	
	void test(String url ,String value , AbstractResolver<String> resolver){
		Assert.assertEquals(value,resolver.getVid(url));
	}
	
}
