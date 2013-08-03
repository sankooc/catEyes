package org.cateyes.core.iqiyi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.cateyes.core.ApacheConnector;
import org.cateyes.core.Resolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class IqiyiResolver implements Resolver {

	static XPathExpression expression;
	private ApacheConnector connector = new ApacheConnector();
	static {
		XPathFactory xfactory = javax.xml.xpath.XPathFactory.newInstance();
		XPath xpath = xfactory.newXPath();
		try {
			expression = xpath.compile("/root/video/fileUrl/file");
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
	}

	public String[] getResource(String uri) {
		try {
			String videoId = connector.doGet(URI.create(uri),
					new ResponseHandler<String>() {
						public String handleResponse(HttpResponse response)
								throws ClientProtocolException, IOException {
							InputStream stream = response.getEntity()
									.getContent();
							return getVideoId(stream);
						}
					});

			return getRealURI(videoId);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static final String xmlformat = "http://cache.video.qiyi.com/v/%s";

	protected String[] getRealURI(String videoId) {
		String desc = String.format(xmlformat, videoId);
		try {
			connector.doGet(URI.create(desc), new ResponseHandler<String>() {
				public String handleResponse(HttpResponse response)
						throws ClientProtocolException, IOException {
					InputStream stream = response.getEntity().getContent();
					try {
						NodeList list =  (NodeList) expression.evaluate(new InputSource(
								stream), XPathConstants.NODESET);
						
						for(int i = 0 ;i < list.getLength();i++){
							Node node = list.item(i);
							String uri = node.getTextContent();
							uri = uri.substring(0,uri.length()-3);
							uri += "hml?v=";
							int time = (int) (System.currentTimeMillis()/1000);
							uri += (time+1921658928);
							System.out.println(uri);
							
						}
					} catch (XPathExpressionException e) {
						e.printStackTrace();
					}
					return null;
				}
			});

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	final static Logger logger = LoggerFactory.getLogger(IqiyiResolver.class);
	final static Pattern pattern = Pattern
			.compile("data-player-videoid=\"([\\w]+)\"");

	protected String getVideoId(InputStream stream) {
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(stream));
		while (true) {
			try {
				String line = reader.readLine();
				if (null == line) {
					break;
				}
				Matcher matcher = pattern.matcher(line);
				if (matcher.find()) {
					return matcher.group(1);
				}
			} catch (IOException e) {
				logger.error(e.getMessage());
				break;
			}
		}
		return null;
	}

	public boolean isPrefer(String uri) {
		return true;
	}

}
