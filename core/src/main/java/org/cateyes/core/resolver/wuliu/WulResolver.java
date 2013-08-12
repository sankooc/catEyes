package org.cateyes.core.resolver.wuliu;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cateyes.core.VideoConstants.Provider;
import org.cateyes.core.resolver.AbstractResolver;
import org.cateyes.core.resolver.Resolver;
import org.cateyes.core.volumn.Volumn;
import org.cateyes.core.volumn.VolumnImpl;

import com.jayway.jsonpath.JsonPath;

/**
 * @author sankooc
 */
public class WulResolver extends AbstractResolver implements Resolver {

	Pattern pattern = Pattern.compile("http://www.56.com/u\\d+/v_(\\w+).html");
	String format = "http://vxml.56.com/json/%s/?src=site";

	protected static final JsonPath jpath_title = JsonPath
			.compile("$.info.Subject");
	protected static final JsonPath jpath_type = JsonPath.compile("$.info.hd");

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
		Map<?,?> data = connector.getPageAsJson(desc);

		String title = jpath_title.read(data);
		Volumn volumn = new VolumnImpl(title, vid, Provider.WULIU);

		String type = types[jpath_type.read(data)];
		
		JsonPath japth_fs = JsonPath.compile("$.info.rfiles[?(@.type == '"
				+ type + "')].url");

		List<String> array = japth_fs.read(data);
		for (String url : array) {
			volumn.addUrl(url);
		}
		return volumn;
	}

	@Override
	protected String[] getRegexStrings() {
		return new String[] { "www\\.56\\.com\\/" };
	}

}
