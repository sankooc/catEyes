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
package org.cateyes.core.resolver.gametrailers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.cateyes.core.VideoConstants.Provider;
import org.cateyes.core.resolver.AbstractResolver;
import org.cateyes.core.resolver.Resolver;
import org.cateyes.core.volumn.Volumn;
import org.cateyes.core.volumn.VolumnImpl;

import com.jayway.jsonpath.JsonPath;

/**
 * @author sankooc
 */
public class GTResolver extends AbstractResolver<String> implements Resolver {

	Pattern pattern_data_token = Pattern.compile("data-token=\"([^\"]+)\"");
	Pattern pattern_video_data = Pattern.compile("data-video=\"([^\"]+)\"");
	Pattern pattern_title = Pattern
			.compile("/([^/]+)$");
	String format = "http://www.gametrailers.com/feeds/video_download/%s";
	protected static final JsonPath japth_url = JsonPath.compile("$.url");
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cateyes.core.Resolver#createVolumn(java.lang.String)
	 */
	public Volumn createVolumn(String uri) throws Exception {
		Matcher matcher = pattern_title.matcher(uri);
		if(!matcher.find()){
			return null;
		}
		String title = matcher.group(1);
		threadlocal.set(title);
		String token = connector.getPageRegix(uri, pattern_data_token);
		String vdata = connector.getPageRegix(uri, pattern_video_data);
		String vid = vdata+"/"+token;
		
		return createVolumnFromVid(vid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cateyes.core.Resolver#createVolumnFromVid(java.lang.String)
	 */
	public Volumn createVolumnFromVid(String vid) throws Exception {
		String title = threadlocal.get();
		Volumn volumn = new VolumnImpl(title,vid,Provider.GT);
		String desc = String.format(format, vid);
		JSONObject obj = connector.getPageAsJson(desc);
		String url = japth_url.read(obj);
		volumn.addFragment(0, url, -1);
		return volumn;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cateyes.core.AbstractResolver#getRegexStrings()
	 */
	@Override
	protected String[] getRegexStrings() {
		return new String[]{"www.gametrailers.com"};
	}

}
