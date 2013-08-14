/*
 *
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cateyes.core.resolver.pptv;

import java.security.MessageDigest;
import java.util.regex.Pattern;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.cateyes.core.VideoConstants.Provider;
import org.cateyes.core.resolver.AbstractResolver;
import org.cateyes.core.resolver.Resolver;
import org.cateyes.core.volumn.Volumn;
import org.cateyes.core.volumn.VolumnImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author sankooc
 */
public class PPTVResolver extends AbstractResolver implements Resolver {

	static String format = "http://web-play.pptv.com/webplay3-151-%s.xml";
	Pattern pattern = Pattern.compile("\"id\":(\\d+),");

	static XPathExpression expression_host;
	static XPathExpression expression_time;
	static XPathExpression expression_rid;
	static XPathExpression expression_title;
	static {
		XPathFactory xfactory = javax.xml.xpath.XPathFactory.newInstance();
		XPath xpath = xfactory.newXPath();
		try {
			expression_host = xpath.compile("/root/dt/sh/text()");
			expression_time = xpath.compile("/root/dt/st/text()");
			expression_rid = xpath.compile("/root/dragdata/sgm");
			expression_title = xpath.compile("/root/channel/@nm");
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cateyes.core.Resolver#createVolumn(java.lang.String)
	 */
	public Volumn createVolumn(String uri) throws Exception {
		String vid = connector.getPageRegix(uri, pattern);

		return createVolumnFromVid(vid);
	}

	String getToken(byte[] data) {
		StringBuilder builder = new StringBuilder();
		for (byte b : data) {
			builder.append(String.format("%02x", b));
		}
		return builder.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cateyes.core.Resolver#createVolumnFromVid(java.lang.String)
	 */
	public Volumn createVolumnFromVid(String vid) throws Exception {
		if (null == vid) {
			return null;
		}
		String desc = String.format(format, vid);
		Document doc = connector.getPageAsDoc(desc);
		String title = expression_title.evaluate(doc, XPathConstants.STRING).toString();
		Volumn volumn = new VolumnImpl(title, vid, Provider.PPTV);
		int port = 8080;
		MessageDigest degest = MessageDigest.getInstance("MD5");
		String host = expression_host.evaluate(doc, XPathConstants.STRING).toString();
		String st = expression_time.evaluate(doc, XPathConstants.STRING).toString();
		st = st.replace("Wed", "Sat");
		degest.update(st.getBytes());
//		degest.digest();
		NodeList rids = (NodeList) expression_rid.evaluate(doc, XPathConstants.NODESET);
//		String urlFormat = "http://%s:%s/%s/%s?key=%s";
		String urlFormat = "http://pptv.vod.lxdns.com/%s/%s?key=%s";
		String key = getToken(degest.digest());
		for (int i = 0; i < rids.getLength(); i++) {
			Node node = rids.item(i);
			String rid = node.getAttributes().getNamedItem("rid").getNodeValue();
			String no = node.getAttributes().getNamedItem("no").getNodeValue();
			String url  = String.format(urlFormat,no,rid+".mp4",key);
//			long size = Long.par//incorrect//TODO fix it
			volumn.addFragment(0, url, -1);
		}

		return volumn;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cateyes.core.AbstractResolver#getRegexStrings()
	 */
	@Override
	protected String[] getRegexStrings() {
		return new String[] { "v.pptv.com" };
	}

}
