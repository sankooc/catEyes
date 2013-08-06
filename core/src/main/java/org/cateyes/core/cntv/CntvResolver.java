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
package org.cateyes.core.cntv;

import java.awt.RenderingHints.Key;
import java.util.Iterator;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.cateyes.core.AbstractResolver;
import org.cateyes.core.Resolver;
import org.cateyes.core.VideoConstants.Provider;
import org.cateyes.core.entity.Volumn;
import org.cateyes.core.entity.VolumnImpl;

/**
 * @author sankooc
 */
public class CntvResolver extends AbstractResolver implements Resolver {

	public final static Pattern pattern = Pattern
			.compile("\"videoCenterId\",\"([^\"]+)\"");
	public final static String format = "http://vdn.apps.cntv.cn/api/getHttpVideoInfo.do?pid=%s";

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cateyes.core.Resolver#createVolumnFromVid(java.lang.String)
	 */
	public Volumn createVolumnFromVid(String vid) throws Exception {
		String desc = String.format(format, vid);
		JSONObject data = connector.getPageAsJson(desc);
		String title = data.getString("title");
		VolumnImpl volumn = new VolumnImpl(title, vid, Provider.CNTV);
		JSONObject video = data.getJSONObject("video");
		JSONArray chapters = select(video);
		for (int i = 0; i < chapters.size(); i++) {
			JSONObject chapter = chapters.getJSONObject(i);
			String uri = chapter.getString("url");
			String suffix = uri.substring(uri.lastIndexOf('.') + 1);
			volumn.addUrl(uri, -1);
			volumn.setSuffix(suffix);
		}
		return volumn;
	}

	@SuppressWarnings("unchecked")
	public JSONArray select(JSONObject video) {
		String key = null;
		JSONArray chapters = null;
		Iterator<String> ite = video.keySet().iterator();
		while (ite.hasNext()) {
			String ky = ite.next();
			if (ky.startsWith("chapter")) {
				if (null == chapters) {
					key = ky;
					chapters = video.getJSONArray(ky);
					continue;
				}
				// TODO 根据 quality 返回不同画质
				 if ((ky.compareTo(key) * quality) > 0) {
				 key = ky;
				 chapters = video.getJSONArray(ky);
				 continue;
				 }
			}
		}
		return chapters;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cateyes.core.AbstractResolver#getRegexStrings()
	 */
	@Override
	protected String[] getRegexStrings() {
		return  new String[] { "\\.cntv\\.cn" };
	}

}
