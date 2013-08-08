package org.cateyes.core.resolver.wuliu;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.cateyes.core.VideoConstants.Provider;
import org.cateyes.core.resolver.AbstractResolver;
import org.cateyes.core.resolver.Resolver;
import org.cateyes.core.volumn.Volumn;
import org.cateyes.core.volumn.VolumnImpl;

/**
 * @author sankooc
 */
public class WulResolver extends AbstractResolver implements Resolver {

	Pattern pattern = Pattern.compile("http://www.56.com/u\\d+/v_(\\w+).html");
	String format = "http://vxml.56.com/json/%s/?src=site";

	public Volumn createVolumn(String uri) throws Exception {
		Matcher macther = pattern.matcher(uri);
		if (macther.find()) {
			String vid = macther.group(1);
			return createVolumnFromVid(vid);
		}
		return null;
	}

	static String[] types = { "normal", "clear", "super" };

	public Volumn createVolumnFromVid(String vid) throws Exception {
		String desc = String.format(format, vid);
		JSONObject data = connector.getPageAsJson(desc);

		String title = data.getJSONObject("info").getString("Subject");

		Volumn volumn = new VolumnImpl(title, vid,Provider.WULIU);

		String type = types[data.getJSONObject("info").getInt("hd")];

		JSONArray arrays = data.getJSONObject("info").getJSONArray("rfiles");

		for (int i = 0; i < arrays.size(); i++) {
			JSONObject profile = arrays.getJSONObject(i);
			if (type.equals(profile.getString("type"))) {
				volumn.addUrl(profile.getString("url"),
						profile.getInt("filesize"));
				break;
			}
		}

		return volumn;
	}

	@Override
	protected String[] getRegexStrings() {
		return new String[] { "www\\.56\\.com\\/" };
	}

}
