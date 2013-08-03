package org.cateyes.core.iqiyi;

import java.io.InputStream;


import org.junit.Assert;
import org.junit.Test;

public class IqiyiTest {
	IqiyiResolver resolver = new IqiyiResolver();
	@Test
	public void resolve(){
		InputStream stream = getClass().getClassLoader().getResourceAsStream("iqiyiweb.html");
		
		String id= resolver.getVideoId(stream);
		Assert.assertEquals("6d82da910a3a419ca249b0bf95716157", id);
		
		resolver.getRealURI("6d82da910a3a419ca249b0bf95716157");
		
	}
}
