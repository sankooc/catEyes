package org.cateyes.core.flv;

import org.cateyes.core.media.utils.CommonUtil;
import org.junit.Test;

public class UtilTest {
	@Test
	public void base64Test(){
		String code = CommonUtil.base64encode("30280885");
		System.out.println(code);
	}
}
