package org.cateyes.core.iqiyi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.cateyes.core.AbstractResolver;
import org.cateyes.core.Resolver;
import org.cateyes.core.VideoConstants.Provider;
import org.cateyes.core.entity.Volumn;
import org.cateyes.core.entity.VolumnImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
/**
 * 
 * application/octet-stream must set suffix
 * @author sankooc
 */
public class IqiyiResolver extends AbstractResolver implements Resolver {

	static XPathExpression expression1;
	static XPathExpression expression2;
	static XPathExpression expression_size;
	static {
		XPathFactory xfactory = javax.xml.xpath.XPathFactory.newInstance();
		XPath xpath = xfactory.newXPath();
		try {
			expression1 = xpath.compile("/root/video/fileUrl/file");
			expression_size = xpath.compile("/root/video/fileBytes/size");
			expression2 = xpath.compile("/root/video/title");
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
	}

	public String[] getResource(String uri) throws Exception {
		String videoId = connector.doGet(uri, new ResponseHandler<String>() {
			public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
				InputStream stream = response.getEntity().getContent();
				return getVideoId(stream);
			}
		});
		logger.debug("the {} videoId is {}", uri, videoId);
		return getRealURI(videoId);
	}

	private static final String xmlformat = "http://cache.video.qiyi.com/v/%s";

	private final static int mask = 0x96283bc0;

	public long suffix() {
		long time = System.currentTimeMillis() / 1000;
		time = time ^ mask;
		time += 0x100000000l;
		return time;
	}

	protected String[] getRealURI(String videoId) throws Exception {
		String desc = String.format(xmlformat, videoId);
		return connector.doGet(desc, new ResponseHandler<String[]>() {
			public String[] handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
				InputStream stream = response.getEntity().getContent();
				try {
					NodeList list = (NodeList) expression1.evaluate(new InputSource(stream), XPathConstants.NODESET);
					String[] uris = new String[list.getLength()];
					for (int i = 0; i < list.getLength(); i++) {
						Node node = list.item(i);
						String uri = node.getTextContent();
						uri = uri.substring(0, uri.length() - 3);
						uri += "hml?v=";
						uri += suffix();
						byte[] data = connector.doGet(uri);
						JSONObject obj = JSONObject.fromObject(new String(data));
						uris[i] = obj.getString("l");
					}
					return uris;
				} catch (XPathExpressionException e) {
					logger.error(e.getMessage(), e);
				}
				return null;
			}
		});
	}

	final static Logger logger = LoggerFactory.getLogger(IqiyiResolver.class);
	final static Pattern pattern = Pattern.compile("data-player-videoid=\"([\\w]+)\"");

	protected String getVideoId(InputStream stream) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
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

	public Volumn createVolumn(String uri) throws Exception {
		String videoId = connector.doGet(uri, new ResponseHandler<String>() {
			public String handleResponse(HttpResponse arg0) throws ClientProtocolException, IOException {
				HttpEntity entity = arg0.getEntity();
				InputStream stream = entity.getContent();
				return getVideoId(stream);
			}
		});
		logger.info("video id is {}", videoId);
		if (null == videoId) {
			return null;
		}
		return createVolumnFromVid(videoId);
	}

	@Override
	protected String[] getRegexStrings() {
		return new String[] { "iqiyi\\.com\\/" };
	}

	public Volumn createVolumnFromVid(String vid) throws Exception {
		String desc = String.format(xmlformat, vid);
		logger.info(desc);
		Document doc = connector.getPageAsDoc(desc);
		String title = (String) expression2.evaluate(doc, XPathConstants.STRING);
		logger.info("video title {}", title);
		NodeList list = (NodeList) expression1.evaluate(doc, XPathConstants.NODESET);
		NodeList slist = (NodeList) expression_size.evaluate(doc, XPathConstants.NODESET);
		VolumnImpl volumn = new VolumnImpl(title, vid, Provider.IQIYI);
		// String[] uris = new String[list.getLength()];
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			Node snode = slist.item(i);
			String newURL = node.getTextContent();
			long size = Long.parseLong(snode.getTextContent());
			newURL = newURL.substring(0, newURL.length() - 3);
			newURL += "hml?v=";
			newURL += suffix();
			byte[] data = connector.doGet(newURL);
			JSONObject obj = JSONObject.fromObject(new String(data));
			volumn.addUrl(obj.getString("l"), size);
		}
		return volumn;
	}
}
