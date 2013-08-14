package org.cateyes.core.resolver.yyt;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cateyes.core.VideoConstants.Provider;
import org.cateyes.core.resolver.AbstractResolver;
import org.cateyes.core.resolver.Resolver;
import org.cateyes.core.volumn.Volumn;
import org.cateyes.core.volumn.VolumnImpl;

public class YinyuetaiResolver extends AbstractResolver implements Resolver {

	static Pattern pattern_vid = Pattern.compile("/video/(\\d+)$");
	static Pattern pattern_titile = Pattern.compile("title : \"([^\"]+)\"");
	static Pattern pattern_url = Pattern.compile("videoUrl : \'([^\"]+)\'");
	static String format = "http://www.yinyuetai.com/video/%s";

	public Volumn createVolumn(String uri) throws Exception {
		Matcher matcher = pattern_vid.matcher(uri);
		return matcher.find() ? createVolumnFromVid(matcher.group(1)) : null;
	}

	public Volumn createVolumnFromVid(String vid) throws Exception {
		String desc = String.format(format, vid);
		String title = connector.getPageRegix(desc, pattern_titile);
		VolumnImpl volumn = new VolumnImpl(title, vid, Provider.YYT);
		String url = connector.getPageRegix(desc, pattern_url);
		String suffix = "flv";
		if (url.contains(".mp4")) {
			suffix = "mp4";
		}
		volumn.addFragment(0, suffix, url);
		return volumn;
	}

	@Override
	protected String[] getRegexStrings() {
		return new String[] { "www\\.yinyuetai\\.com" };
	}

}
