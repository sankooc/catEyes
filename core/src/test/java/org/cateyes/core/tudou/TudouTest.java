package org.cateyes.core.tudou;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URLConnection;

import org.junit.Assert;
import org.junit.Test;

public class TudouTest {
	TudouResolver resolver = new TudouResolver();

	@Test
	public void truch() throws MalformedURLException, IOException {
		String url = "http://www.tudou.com/listplay/8Jr659zJxA4/Dyhg3Ucl1mQ.html";
		Assert.assertTrue(resolver.isPrefer(url));
		String[] uris = resolver.getResource(url);
		Assert.assertNotNull(uris);
		byte[] data =resolver.getConnector().doGet(URI.create(uris[0]));
		System.out.println(new String(data));
	}

	// @Test
	public void testIID() {
		InputStream stream = this.getClass().getClassLoader()
				.getResourceAsStream("tudouweb.html");
		String iid = resolver.getIIdFrom(stream);
		Assert.assertEquals(iid, "173613613");
	}

	// @Test
	public void testXml() throws Exception {
		String uri = resolver.getRealURI("173613613");
		Assert.assertNotNull(uri);
		URLConnection connection = URI.create(uri).toURL().openConnection();
		connection.connect();
		InputStream stream = connection.getInputStream();
		Assert.assertNotNull(stream);
	}

}
