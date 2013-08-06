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
package org.cateyes.core.pps;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cateyes.core.AbstractResolver;
import org.cateyes.core.Resolver;
import org.cateyes.core.VideoConstants.Provider;
import org.cateyes.core.entity.Volumn;
import org.cateyes.core.entity.VolumnImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author sankooc
 */
public class PPSResolver extends AbstractResolver implements Resolver {

	static Pattern pattern = Pattern.compile("url_key: \"(\\w+)\",");
	public final static Pattern pattern_title = Pattern
			.compile("&title=([^&]+)&");
	static String format = "http://dp.ppstream.com/get_play_url_cdn.php?sid=%s&flash_type=1";
	static Logger logger = LoggerFactory.getLogger(PPSResolver.class);

	public Volumn createVolumn(String uri) throws Exception {

		String vid = connector.getPageRegix(uri, pattern);
		if (null == vid) {
			logger.error("no vid for this video");
			return null;
		}

		return createVolumnFromVid(vid);
	}

	public Volumn createVolumnFromVid(String vid) throws Exception {
		String desc = String.format(format, vid);
		byte[] data = connector.doGet(desc);
		String uri = new String(data);
		Matcher matcher = pattern.matcher(uri);
		if (matcher.find()) {
			String title = matcher.group(1);
			Volumn volumn = new VolumnImpl(title, vid, Provider.PPS);
			volumn.addUrl(uri, -1);
			return volumn;
		} else {
			logger.error("no title be found");
		}

		return null;
	}

	@Override
	protected String[] getRegexStrings() {
		return new String[] { "\\.pps\\.tv" };
	}

}
