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
package org.cateyes.core.resolver.ku6;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.cateyes.core.VideoConstants.Provider;
import org.cateyes.core.resolver.AbstractResolver;
import org.cateyes.core.resolver.Resolver;
import org.cateyes.core.volumn.Volumn;
import org.cateyes.core.volumn.VolumnImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jayway.jsonpath.JsonPath;

/**
 * @author sankooc
 */
public class Ku6Resolver extends AbstractResolver implements Resolver {

	protected static final Pattern pattern = Pattern.compile("/([^/]+).html");

	protected static final String format = "http://v.ku6.com/fetchVideo4Player/%s.html";
	protected static final Logger logger = LoggerFactory.getLogger(Ku6Resolver.class);
	
	protected static final JsonPath jpath_title = JsonPath.compile("$.data.t");
	protected static final JsonPath jpath_url = JsonPath.compile("$.data.f");
	protected static final JsonPath jpath_size = JsonPath.compile("$.data.videosize");

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cateyes.core.Resolver#createVolumn(java.lang.String)
	 */
	public Volumn createVolumn(String uri) throws Exception {
		Matcher matcher = pattern.matcher(uri);
		if (matcher.find()) {
			String vid = matcher.group(1);
			return createVolumnFromVid(vid);

		} else {
			logger.error("no match");
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cateyes.core.Resolver#createVolumnFromVid(java.lang.String)
	 */
	public Volumn createVolumnFromVid(String vid) throws Exception {
		String desc = String.format(format, vid);
		JSONObject data = connector.getPageAsJson(desc);
		String url = jpath_url.read(data);
		String title = jpath_title.read(data);
		String size =  jpath_size.read(data);
		
		Volumn volumn = new VolumnImpl(title,vid,Provider.KU6);
		volumn.addUrl(url, Long.parseLong(size));
		return volumn;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cateyes.core.AbstractResolver#getRegexStrings()
	 */
	@Override
	protected String[] getRegexStrings() {
		return new String[] { "v.ku6.com" };
	}

}
