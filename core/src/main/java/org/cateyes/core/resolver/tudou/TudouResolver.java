package org.cateyes.core.resolver.tudou;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.cateyes.core.IHeader;
import org.cateyes.core.VideoConstants.Provider;
import org.cateyes.core.resolver.AbstractResolver;
import org.cateyes.core.resolver.Resolver;
import org.cateyes.core.volumn.Volumn;
import org.cateyes.core.volumn.VolumnImpl;
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
	static Pattern pattern = Pattern.compile("iid:[ ]?([\\d]+)");

	static Logger logger = LoggerFactory.getLogger(TudouResolver.class);

	public Volumn createVolumn(String uri) throws Exception {
		String iid = connector.getPageRegix(uri, pattern);
		logger.info("tudou iid {}", iid);
		if (null != iid) {
			return createVolumnFromVid(iid);
		}
		return null;
	}

	public Volumn createVolumnFromVid(String vid) throws Exception {
		String desc = String.format(xmlformat, vid);
		logger.info(desc);
		Document doc = connector.getPageAsDoc(desc);
		String source = (String) expression_src.evaluate(doc,
				XPathConstants.STRING);
		String title = (String) expression_title.evaluate(doc,
				XPathConstants.STRING);
		VolumnImpl volumn = new VolumnImpl(title, vid, Provider.TUDOU);
		logger.info("title is {}", title);
		Long size = Long.parseLong((String) expression_size.evaluate(doc,
				XPathConstants.STRING));
		volumn.addUrl(source, size);
		return volumn;
	}

	@Override
	public Volumn createVolumn(String uri, IHeader headers) throws Exception {
		if(null == headers){
			headers = new IHeader(){
				public Map<String, String> getParams() {
					Map<String,String> map = new HashMap<String,String>();
					map.put("User-Agent",
							"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.94 Safari/537.36");
					return map;
				}};
		}
		return super.createVolumn(uri, headers);
	}

	@Override
	protected String[] getRegexStrings() {
		return new String[] { "www.tudou.com/" };
	}
}
