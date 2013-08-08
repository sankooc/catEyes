package org.cateyes.core.iqiyi.regex;

import java.util.regex.Matcher;

import junit.framework.Assert;

import org.cateyes.core.resolver.pps.PPSResolver;
import org.junit.Test;

public class RegexTest {
	@Test
	public void ppsTitle() {
//		String content = "http://vurl.ppstv.com/ugc/6/d3/62282c0e37b3644c68de48b0daacd14271b13814/62282c0e37b3644c68de48b0daacd14271b13814.pfv?hd=0&all=01&title=十万个冷笑话-11&vtypeid=13&fd=1&ct=604&sha=da084e8a94f29b0131c184b37ce18de586cc45a8&fid=IJDOGB6NLTWE6XHGBGEFTDX4TH3VHCBT&bip=http://bip.ppstream.com/I/IJ/IJDOGB6NLTWE6XHGBGEFTDX4TH3VHCBT/IJDOGB6NLTWE6XHGBGEFTDX4TH3VHCBT.bip&sbest=vurl.pps.tv&tip=0&tracker=118.194.167.14,118.194.167.12";
//		Matcher matcher = PPSResolver.pattern_title.matcher(content);
//		Assert.assertTrue(matcher.find());
//		Assert.assertEquals(matcher.group(1), "十万个冷笑话-11");
		String s0 = "";
		String s1 = "chapter";
		String s2 = "chapter1";
		String s3 = "chapter2";
		String s4 = "chapter12";
		System.out.println(s3.compareTo(s4));
	}
}
