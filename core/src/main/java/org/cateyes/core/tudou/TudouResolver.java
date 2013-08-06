package org.cateyes.core.tudou;

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

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.cateyes.core.AbstractResolver;
import org.cateyes.core.ApacheConnector;
import org.cateyes.core.Resolver;
import org.cateyes.core.VideoConstants.Provider;
import org.cateyes.core.entity.Volumn;
import org.cateyes.core.entity.VolumnImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
/**
 * @author sankooc
 */
public class TudouResolver extends AbstractResolver implements Resolver {
	public static final String xmlformat = "http://v2.tudou.com/v?it=%s&st=1,2,3,4,99";
	static XPathExpression expression_src;
	static XPathExpression expression_title;
	static XPathExpression expression_size;
	static {
		XPathFactory xfactory = javax.xml.xpath.XPathFactory.newInstance();
		XPath xpath = xfactory.newXPath();
		try {
			expression_src = xpath.compile("/v/b/f[last()]/text()");
			expression_title = xpath.compile("/v/@title");
			expression_size = xpath.compile("/v/b/f[last()]/@size");
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
	}
	static Pattern pattern = Pattern.compile("iid:([\\w]+)");

	static Logger logger = LoggerFactory.getLogger(TudouResolver.class);

	protected String getIIdFrom(InputStream stream) {
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

	protected String getRealURI(String iid) throws Exception {
		String desc = String.format(xmlformat, iid);
		logger.info(desc);
		Document doc = connector.getPageAsDoc(desc);
		return (String) expression_src.evaluate(doc, XPathConstants.STRING);
	}

	public String[] getResource(String uri) throws Exception {
		try {
			String iid = connector.doGet(uri, new ResponseHandler<String>() {
				public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
					Header[] headers = response.getAllHeaders();
					for (Header header : headers) {
						System.out.println("key:" + header.getName() + " value:" + header.getValue());
					}
					InputStream stream = response.getEntity().getContent();
					return getIIdFrom(stream);
				}
			});

			return new String[] { getRealURI(iid) };
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public Volumn createVolumn(String uri) throws Exception {
		String iid = connector.doGet(uri, new ResponseHandler<String>() {
			public String handleResponse(HttpResponse arg0) throws ClientProtocolException, IOException {
				InputStream stream = arg0.getEntity().getContent();
				return getIIdFrom(stream);
			}
		});
		logger.info("tudou iid {}", iid);

		return createVolumnFromVid(iid);
	}

	public Volumn createVolumnFromVid(String vid) throws Exception {
		String desc = String.format(xmlformat, vid);
		logger.info(desc);
		Document doc = connector.getPageAsDoc(desc);
		String source = (String) expression_src.evaluate(doc, XPathConstants.STRING);
		String title = (String) expression_title.evaluate(doc, XPathConstants.STRING);
		VolumnImpl volumn = new VolumnImpl(title, vid, Provider.TUDOU);
		logger.info("title is {}", title);
		Long size = Long.parseLong((String) expression_size.evaluate(doc, XPathConstants.STRING));
		volumn.addUrl(source, size);
		return volumn;
	}

	@Override
	protected String[] getRegexStrings() {
		return new String[] { "tudou\\.com\\/" };
	}
}
