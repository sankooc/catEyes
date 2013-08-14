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
package org.cateyes.core.resolver.cntv;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
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
public class CntvResolver extends AbstractResolver implements Resolver {

	public final static Pattern pattern = Pattern
			.compile("\"videoCenterId\",\"([^\"]+)\"");
	public final static String format = "http://vdn.apps.cntv.cn/api/getHttpVideoInfo.do?pid=%s";

	protected static final JsonPath jpath_title = JsonPath.compile("$.title");
	protected static final JsonPath jpath_url = JsonPath
			.compile("$.video.chapters[]");

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cateyes.core.Resolver#createVolumn(java.lang.String)
	 */
	public Volumn createVolumn(String uri) throws Exception {
		String vid = connector.getPageRegix(uri, pattern);
		if (null != vid) {
			return createVolumnFromVid(vid);
		}
		return null;
	}

	void addFragment(VolumnImpl volumn, AtomicInteger quality,
			JSONArray chapters) {
		if (null == chapters) {
			return;
		}
		for (int i = 0; i < chapters.size(); i++) {
			JSONObject chapter = chapters.getJSONObject(i);
			String url = chapter.getString("url");
			String suffix = url.substring(url.lastIndexOf('.') + 1);
			volumn.addFragment(quality.get(), suffix, url, -1);
		}
		quality.incrementAndGet();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cateyes.core.Resolver#createVolumnFromVid(java.lang.String)
	 */
	public Volumn createVolumnFromVid(String vid) throws Exception {
		String desc = String.format(format, vid);
		JSONObject data = connector.getPageAsJson(desc);
		String title = jpath_title.read(data);
		VolumnImpl volumn = new VolumnImpl(title, vid, Provider.CNTV);
		JSONObject video = data.getJSONObject("video");
		AtomicInteger quality = new AtomicInteger(0);
		JSONArray chapter = video.getJSONArray("lowChapters");
		addFragment(volumn,quality,chapter);
		SortedSet<String> keys =  getKeySet(video);
		for(String key : keys){
			chapter = video.getJSONArray(key);
			addFragment(volumn,quality,chapter);
		}
		return volumn;
	}
	
	@SuppressWarnings("unchecked")
	public SortedSet<String> getKeySet(JSONObject video) {
		SortedSet<String> keys =new TreeSet<String>();
		Iterator<String> ite = video.keySet().iterator();
		while (ite.hasNext()) {
			String ky = ite.next();
			if (ky.startsWith("chapters")) {
				keys.add(ky);
			}
		}
		return keys;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cateyes.core.AbstractResolver#getRegexStrings()
	 */
	@Override
	protected String[] getRegexStrings() {
		return new String[] { "\\.cntv\\.cn" };
	}

}
