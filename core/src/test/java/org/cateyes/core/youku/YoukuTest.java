package org.cateyes.core.youku;


import org.junit.Assert;
import org.junit.Test;

public class YoukuTest {
	YoukuResolver resolver = new YoukuResolver();
	
	
	@Test
	public void test(){
		try {
			resolver.resolvSid("XNTQ2OTc0OTAw");
//			String str = "24*28*24*24*24*3*24*20*24*24*65*52*57*17*65*13*55*45*65*3*39*52*24*65*24*52*57*24*65*57*28*36*24*28*40*39*57*50*63*65*50*28*17*63*40*13*36*55*63*65*36*50*45*63*39*57*39*24*65*28*39*65*40*40*57*24*";
//		    str.split("\\*");
//			String a = "2";
//			System.out.println(String.format("%02x", a));
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
//	@Test
	public void testMixString(){
		char[] chs = resolver.getMixString(4263);
		Assert.assertEquals(new String(chs), "mk/rCvw6lV7tJ\\E8:G4fdhpDZTI91NBK-RsgUy.L_OjYoWeQzX5qnaixFHc3PASMu02b");
	}
//	@Test
	public void testSeriaId(){
		System.out.println(resolver.getSerialId());
	}
}
