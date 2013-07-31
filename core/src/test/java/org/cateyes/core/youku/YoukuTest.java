package org.cateyes.core.youku;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URLConnection;

import net.sf.json.JSONObject;

import org.cateyes.core.VideoConstants.VideoType;
import org.junit.Assert;
import org.junit.Test;

public class YoukuTest {

	@Test
	public void test() throws MalformedURLException, IOException {
		String yid = "XNTQ2OTc0OTAw";
		JSONObject data = YoukuResolver.getData(yid);
		String title = data.getString("title");
		Assert.assertNotNull(title);
		System.out.println("title:" + title);
		String[] uris = YoukuResolver.getRealUri(data, VideoType.FLV);
		for (String uri : uris) {
			URLConnection connection = URI.create(uri).toURL().openConnection();
			connection.connect();
			InputStream stream = connection.getInputStream();
			Assert.assertNotNull(stream);
		}
		
		// try {
		// YoukuResolver.getReadUriFromYID("XNTQ2OTc0OTAw", VideoType.FLV);
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	@Test
	public void testMixString() {
		char[] chs = YoukuResolver.getMixString(4263);
		Assert.assertEquals(new String(chs),
				"mk/rCvw6lV7tJ\\E8:G4fdhpDZTI91NBK-RsgUy.L_OjYoWeQzX5qnaixFHc3PASMu02b");
	}
}
