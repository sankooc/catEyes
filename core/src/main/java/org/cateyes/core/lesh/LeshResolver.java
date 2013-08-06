package org.cateyes.core.lesh;

import java.io.ByteArrayInputStream;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.cateyes.core.AbstractResolver;
import org.cateyes.core.Resolver;
import org.cateyes.core.VideoConstants.Provider;
import org.cateyes.core.entity.Volumn;
import org.cateyes.core.entity.VolumnImpl;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
/**
 * @author sankooc
 */
public class LeshResolver extends AbstractResolver implements Resolver {

	Pattern pattern = Pattern.compile("  vid:(\\d+),");
	String format = "http://app.letv.com/v.php?id=%s";

	static XPathExpression expression_json;
	static XPathExpression expression_title;
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
		JSONObject json = JSONObject.fromObject(jsonContent);
		JSONArray jsonarry = json.getJSONObject("bean").getJSONArray("video");
		for (int i = 0; i < jsonarry.size(); i++) {
			JSONObject obj = jsonarry.getJSONObject(i);
			String url = obj.getString("url");
			obj = connector.getPageAsJson(url);
			volumn.addUrl(obj.getString("location"), -1);
		}
		return volumn;
	}

	@Override
	protected String[] getRegexStrings() {
		return new String[]{"http://www.letv.com"};
	}

}
