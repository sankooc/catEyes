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
package org.cateyes.core.resolver.feng;

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

/**
 * @author sankooc
 */
public class FengResolver extends AbstractResolver implements Resolver {

	static Pattern pattern = Pattern.compile("\"id\": \"([^\"]+)\"");
	static String format = "http://v.ifeng.com/video_info_new/%s/%s/%s.xml";

	static XPathExpression expression_url;
	static XPathExpression expression_title;
	static {
		XPathFactory xfactory = javax.xml.xpath.XPathFactory.newInstance();
		XPath xpath = xfactory.newXPath();
		try {
			expression_url = xpath.compile("/PlayList/item/@VideoPlayUrl");
			expression_title = xpath.compile("/PlayList/item/@Name");
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
		return null != vid ? createVolumnFromVid(vid) : null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cateyes.core.Resolver#createVolumnFromVid(java.lang.String)
	 */
	public Volumn createVolumnFromVid(String vid) throws Exception {
		int length = vid.length();
		char ch1 = vid.charAt(length - 2);
		char ch2 = vid.charAt(length - 1);
		String desc = String.format(format, ch1, new String(new char[] { ch1,
				ch2 }), vid);
		Document doc = connector.getPageAsDocument(desc);
		String title = expression_title.evaluate(doc, XPathConstants.STRING)
				.toString();
		String uri = expression_url.evaluate(doc, XPathConstants.STRING)
				.toString();
		Volumn volumn = new VolumnImpl(title, vid, Provider.FENG);
		String suffix ="flv";
		if (uri.endsWith(".mp4")) {
			suffix = "mp4";
		}
		volumn.addFragment(0, suffix, uri);
		return volumn;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cateyes.core.AbstractResolver#getRegexStrings()
	 */
	@Override
	protected String[] getRegexStrings() {
		return new String[] { "v.ifeng.com" };
	}

}
