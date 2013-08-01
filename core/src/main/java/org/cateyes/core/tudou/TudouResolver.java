package org.cateyes.core.tudou;

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

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.cateyes.core.ApacheConnector;
import org.cateyes.core.Resolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

public class TudouResolver implements Resolver {
	public static final String xmlformat = "http://v2.tudou.com/v?it=%s&st=1,2,3,4,99";
	static XPathExpression expression;
	private ApacheConnector connector = new ApacheConnector();
	static {
		XPathFactory xfactory = javax.xml.xpath.XPathFactory.newInstance();
		XPath xpath = xfactory.newXPath();
		try {
			expression = xpath.compile("/v/b/f[last()]/text()");
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
	}
	static Pattern pattern = Pattern.compile("iid:([\\w]+)");

	static Logger logger = LoggerFactory.getLogger(TudouResolver.class);

	protected String getIIdFrom(InputStream stream) {
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

	protected String getRealURI(String iid) {
		String desc = String.format(xmlformat, iid);
		try {
			return connector.doGet(URI.create(desc),
					new ResponseHandler<String>() {
						public String handleResponse(HttpResponse response)
								throws ClientProtocolException, IOException {
							InputStream stream = response.getEntity()
									.getContent();
							try {
								return (String) expression.evaluate(
										new InputSource(stream),
										XPathConstants.STRING);
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

		// expression.evaluate(source, returnType)
		return null;
	}

	public ApacheConnector getConnector() {
		return connector;
	}

	public void setConnector(ApacheConnector connector) {
		this.connector = connector;
	}

	public String[] getResource(String uri) {
		try {
			String iid = connector.doGet(URI.create(uri),
					new ResponseHandler<String>() {
						public String handleResponse(HttpResponse response)
								throws ClientProtocolException, IOException {
							InputStream stream = response.getEntity()
									.getContent();
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
		// return null;
	}

	// protected String getId(){
	//
	// }

	public boolean isPrefer(String uri) {
		String p1 = "tudou\\.com\\/";
		Pattern pattern = Pattern.compile(p1);
		Matcher matcher = pattern.matcher(uri);
		return matcher.find();
	}
}
