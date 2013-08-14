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
package org.cateyes.core.resolver.sohu;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.cateyes.core.VideoConstants.Provider;
import org.cateyes.core.resolver.AbstractResolver;
import org.cateyes.core.resolver.Resolver;
import org.cateyes.core.volumn.Volumn;
import org.cateyes.core.volumn.VolumnImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * sohu video uri resolver
 * 
 * @author sankooc
 * 
 */
public class SohuResolver extends AbstractResolver implements Resolver {

	static Logger logger = LoggerFactory.getLogger(SohuResolver.class);
	static Pattern pattern = Pattern.compile("var vid=\"(\\d+)\";");

	private static String format = "http://hot.vrs.sohu.com/vrs_flash.action?vid=%s";

	private static String urlFormat = "http://%s/?prot=%s&file=%s&new=%s";

	private static String urlFormat2 = "%s%s?key=%s";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cateyes.core.Resolver#createVolumn(java.lang.String)
	 */
	public Volumn createVolumn(String uri) throws Exception {
		String vid = connector.getPageRegix(uri, pattern);// getVid
		logger.info("sohu vid {}" + vid);

		return createVolumnFromVid(vid);
	}

	public Volumn createVolumnFromVid(String vid) throws Exception {
		String desc = String.format(format, vid);

		JSONObject data = connector.getPageAsJson(desc);
		String host = data.getString("allot");
		String rot = data.getString("prot");
		data = data.getJSONObject("data");
		String title = data.getString("tvName");
//		JSONArray clipsSize = data.getJSONArray("clipsBytes");
		JSONArray clipsURLS = data.getJSONArray("clipsURL");
		JSONArray suffixs = data.getJSONArray("su");
		VolumnImpl volumn = new VolumnImpl(title, vid, Provider.SOHO);
		for (int i = 0; i < clipsURLS.size(); i++) {
			String clips = clipsURLS.getString(i);
			String suffix = clips.substring(clips.lastIndexOf('.') + 1);
			String uri = String.format(urlFormat, host, rot, clips,
					suffixs.getString(i));
			byte[] content = connector.doGet(uri);
			String ss = new String(content);
			String[] tokens = ss.split("\\|");
			String url = String.format(urlFormat2,
					tokens[0].substring(0, tokens[0].length() - 1),
					suffixs.getString(i), tokens[3]);
			volumn.addFragment(0, suffix, url);
//			int size = clipsSize.getInt(i);
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
		return new String[] { "sohu\\.com\\/" };
	}

}
