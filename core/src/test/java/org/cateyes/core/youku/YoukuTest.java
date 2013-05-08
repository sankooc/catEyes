package org.cateyes.core.youku;

import org.cateyes.core.VideoConstants.VideoType;
import org.junit.Assert;
import org.junit.Test;

public class YoukuTest {

	@Test
	public void test() {
		try {
			YoukuResolver.getReadUriFromYID("XNTQ2OTc0OTAw", VideoType.FLV);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	 @Test
	public void testMixString() {
		char[] chs = YoukuResolver.getMixString(4263);
		Assert.assertEquals(new String(chs),
				"mk/rCvw6lV7tJ\\E8:G4fdhpDZTI91NBK-RsgUy.L_OjYoWeQzX5qnaixFHc3PASMu02b");
	}

	 @Test
	public void testSeriaId() {
		System.out.println(YoukuResolver.getSerialId());
	}
}
