package org.cateyes.core.resolver.blibli;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cateyes.core.resolver.AbstractResolver;
import org.cateyes.core.resolver.Resolver;
import org.cateyes.core.volumn.Volumn;


/**
 * 暂无意义 貌似都是转的
 * @author sankooc
 *
 */
public class BlibliResolver extends AbstractResolver<String> implements Resolver {

	
	static String format = "http://interface.bilibili.tv/playurl?cid=%s";
	
	public Volumn createVolumn(String uri) throws Exception {
		Pattern pattern = Pattern
				.compile("http://(www.bilibili.tv|bilibili.kankanews.com|bilibili.smgbb.cn)/video/av(\\d+)");
		Matcher match = pattern.matcher(uri);
		if (match.find()) {
			String vid = match.group(2);
			return createVolumnFromVid(vid);
			
		}

		return null;
	}

	public Volumn createVolumnFromVid(String vid) {
		
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String[] getRegexStrings() {
		// TODO Auto-generated method stub
		return null;
	}

}
