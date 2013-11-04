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
package org.cateyes.core.resolver.tencent;

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
public class TencentResolver extends AbstractResolver implements Resolver {

	static Pattern pattern = Pattern.compile("vid:\"(\\w+)\"");
	static String descFormat = "http://vv.video.qq.com/geturl?otype=xml&platform=1&format=2&&vid=%s";
	static XPathExpression expression_src;
	static XPathExpression expression_size;
	static {
		XPathFactory xfactory = javax.xml.xpath.XPathFactory.newInstance();
		XPath xpath = xfactory.newXPath();
		try {
			expression_src = xpath.compile("/root/vd/vi/url/text()");
			expression_size = xpath.compile("/root/vd/vi/fs/text()");
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
	}
	
	public Volumn createVolumn(String uri) throws Exception {
		String vid = connector.getPageRegix(uri, pattern);
		if (null == vid) {
			return null;
		}
		Pattern pattern_title  =Pattern.compile("title=\"([^\"]*)\"  sv=\""+vid+"\"");
		String title = connector.getPageRegix(uri, pattern_title);
		threadlocal.set(title);
		return createVolumnFromVid(vid);
	}

	public Volumn createVolumnFromVid(String vid) throws Exception {
		String title = threadlocal.get();
		Volumn volumn = new VolumnImpl(title,vid,Provider.TENCENT);
		String desc = String.format(descFormat, vid);
		Document doc = connector.getPageAsDocument(desc);
		String url = expression_src.evaluate(doc, XPathConstants.STRING)
				.toString();
		long size = Long.parseLong(expression_size.evaluate(doc,
				XPathConstants.STRING).toString());
		volumn.addFragment(0, url, size);
		return volumn;
	}

	@Override
	protected String[] getRegexStrings() {
		return new String[]{"v.qq.com"};
	}

}
