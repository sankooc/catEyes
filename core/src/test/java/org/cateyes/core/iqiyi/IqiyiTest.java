package org.cateyes.core.iqiyi;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.cateyes.core.ApacheConnector;
import org.junit.Assert;
import org.junit.Test;

public class IqiyiTest {
	IqiyiResolver resolver = new IqiyiResolver();
	
	
//	@Test
	public void count() throws ClientProtocolException, IOException, XPathExpressionException {
		final long time1 = System.currentTimeMillis()/1000;
//		ApacheConnector connector = new ApacheConnector();
		
//		
//		connector.doGet(URI.create("http://data.video.qiyi.com/t.hml?tn=1"), new ResponseHandler<Void>(){
//
//			public Void handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
//				InputStream stream = response.getEntity().getContent();
//				byte[] data = IOUtils.toByteArray(stream);
//				JSONObject obj = JSONObject.fromObject(new String(data));
//				System.out.println(obj.getString("t"));
//				System.out.println(time1);
//				return null;
//			}
//			
//		});
	}

	@Test
	public void resolve() throws Exception {
		InputStream stream = getClass().getClassLoader().getResourceAsStream("iqiyiweb.html");
		String id = resolver.getVideoId(stream);
		Assert.assertEquals("6d82da910a3a419ca249b0bf95716157", id);

		resolver.createVolumn("http://www.iqiyi.com/dongman/20120416/77770ccdf98f2322.html");
		
		resolver.getRealURI("6d82da910a3a419ca249b0bf95716157");

	}
}
