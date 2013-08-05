package org.cateyes.core.youku;

import java.io.IOException;
import java.net.MalformedURLException;

import org.cateyes.core.entity.Volumn;
import org.junit.Test;

public class YoukuTest {
	YoukuResolver resolver =new YoukuResolver();
	@Test
	public void test() throws MalformedURLException, IOException {
		String yid = "XNTQ2OTc0OTAw";
		try {
			Volumn volumn = resolver.createVolumnFromVid(yid);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
