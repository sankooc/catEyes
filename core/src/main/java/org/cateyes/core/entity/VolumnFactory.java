package org.cateyes.core.entity;

import java.io.File;
import java.net.URI;
import java.util.Collection;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VolumnFactory {
	static Collection<Pattern> youkuPattern =new LinkedList<Pattern>();
	static {
		youkuPattern.add(Pattern.compile("^http://v.youku.com/v_show/id_([\\w=]+).html"));
		youkuPattern.add(Pattern.compile("^http://player.youku.com/player.php/sid/([\\w=]+)/v.swf"));
	}
	
	public static Volumn createVolumn(URI uri){
		//TODO 解析uri
		return null;
	}
	public static Volumn createVolumn(String uri,File file){
		String yid = match(youkuPattern,uri);
		Volumn  volumn = null;
		if(null != yid){
			volumn = new YoukuVolumn(yid,file);
		}
		return volumn;
	}
	
	public static String match(Collection<Pattern> patterns,String uri){
		for(Pattern pattern : patterns){
			Matcher matcher = pattern.matcher(uri);
			if(matcher.find()){
				return matcher.group(1);
			}
		}
		return null;
	}
	
}
