package org.cateyes.core.resolver.lesh;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import net.sf.json.JSONObject;

import org.cateyes.core.VideoConstants.Provider;
import org.cateyes.core.resolver.AbstractResolver;
import org.cateyes.core.resolver.Resolver;
import org.cateyes.core.volumn.Volumn;
import org.cateyes.core.volumn.VolumnImpl;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.jayway.jsonpath.JsonPath;
/**
 * @author sankooc
 */
public class LeshResolver extends AbstractResolver<String> implements Resolver {

	Pattern pattern = Pattern.compile("  vid:(\\d+),");
	String format = "http://app.letv.com/v.php?id=%s";
	
	protected static final JsonPath japth_url1 = JsonPath.compile("$.bean.video[*].url");
	protected static final JsonPath japth_url2 = JsonPath.compile("$.location");
	
	protected static XPathExpression expression_json;
	protected static XPathExpression expression_title;
	static {
		XPathFactory xfactory = javax.xml.xpath.XPathFactory.newInstance();
		XPath xpath = xfactory.newXPath();
		try {
			expression_json = xpath.compile("/root/mmsJson/text()");
			expression_title = xpath.compile("/root/tal/text()");
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
	}

	public Volumn createVolumn(String uri) throws Exception {

		String vid = connector.getPageRegix(uri, pattern);
		return createVolumnFromVid(vid);
	}

	public Volumn createVolumnFromVid(String vid) throws Exception {
		String desc = String.format(format, vid);
		byte[] data = connector.doGet(desc);
		byte[] suf = "</root>".getBytes();
		byte[] ret = new byte[data.length + suf.length];
		System.arraycopy(data, 0, ret, 0, data.length);
		System.arraycopy(suf, 0, ret, data.length, suf.length);

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setNamespaceAware(false);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new InputSource(new ByteArrayInputStream(ret)));
		String title = expression_title.evaluate(doc, XPathConstants.STRING).toString();
		Volumn volumn = new VolumnImpl(title, vid, Provider.LESH);
		String jsonContent = expression_json.evaluate(doc, XPathConstants.STRING).toString();
		List<String> urls1 = japth_url1.read(jsonContent);
		for(String u1 : urls1){
			JSONObject obj = connector.getPageAsJson(u1);
			String url = japth_url2.read(obj);
			volumn.addFragment(0, url, -1);
		}
		return volumn;
	}

	@Override
	protected String[] getRegexStrings() {
		return new String[]{"http://www.letv.com"};
	}

}
