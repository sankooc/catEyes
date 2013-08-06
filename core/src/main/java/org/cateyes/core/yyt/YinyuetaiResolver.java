package org.cateyes.core.yyt;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cateyes.core.AbstractResolver;
import org.cateyes.core.Resolver;
import org.cateyes.core.VideoConstants.Provider;
import org.cateyes.core.entity.Volumn;
import org.cateyes.core.entity.VolumnImpl;

public class YinyuetaiResolver extends AbstractResolver implements Resolver {

	static Pattern pattern_vid = Pattern.compile("/video/(\\d+)$");
	static Pattern pattern_titile = Pattern.compile("title : \"([^\"]+)\"");
	static Pattern pattern_url = Pattern.compile("videoUrl : \'([^\"]+)\'");
	static String format = "http://www.yinyuetai.com/video/%s";

	public Volumn createVolumn(String uri) throws Exception {
		Matcher matcher =  pattern_vid.matcher(uri);
		return matcher.find() ? createVolumnFromVid(matcher.group(1)) :  null;
	}

	public Volumn createVolumnFromVid(String vid) throws Exception {
		String desc = String.format(format, vid);
		String title = connector.getPageRegix(desc, pattern_titile);
		VolumnImpl volumn = new VolumnImpl(title, vid, Provider.YYT);
		String url = connector.getPageRegix(desc, pattern_url);
		volumn.addUrl(url, -1);
		if (url.contains(".mp4")) {
			volumn.setSuffix("mp4");
		}
		return volumn;
	}

	@Override
	protected String[] getRegexStrings() {
		return new String[] { "www\\.yinyuetai\\.com" };
	}

}
