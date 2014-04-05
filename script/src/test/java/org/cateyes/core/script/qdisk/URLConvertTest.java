package org.cateyes.core.script.qdisk;

import java.net.URLDecoder;

import org.junit.Test;

public class URLConvertTest {

	@Test
	public void common() {
		String tudou1_url = "http%3A%2F%2Fwww.tudou.com%2Fv%2F40fAgzeKFAI%2Fv.swf";
		String tudou2_url = "http://www.tudou.com/v/40fAgzeKFAI/v.swf";
		
		String str = URLDecoder.decode(tudou1_url);
		System.out.println(str);
		
	}

}
