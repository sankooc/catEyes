package org.cateyes.core.youku;

import java.net.URI;
import java.net.URL;

import org.cateyes.core.Resolver;

public class YoukuResolver implements Resolver {
	public void resolv(URI url) {

	}

	
	public String getYoukuSid(URI url){
		String str = url.toString();
		
		return str;
	} 
	
	public void resolvSid(String sid) {
		String flcJsonURI = "http://v.youku.com/player/getPlayList/VideoIDS/"+sid;
		
		
		
	}
	
	
}
