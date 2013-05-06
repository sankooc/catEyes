package org.cateyes.core.entity;

import java.io.File;
import java.io.OutputStream;

import org.cateyes.core.VideoConstants.Provider;
import org.cateyes.core.VideoConstants.VideoType;
import org.cateyes.core.youku.YoukuResolver;

public class YoukuVolumn implements Volumn {

	final String yid;
	final YoukuResolver resolver;

	YoukuVolumn(String yid, YoukuResolver resolver) {
		this.yid = yid;
		this.resolver = resolver;
	}

	public String getVideoTitle() {
		return null;
	}

	protected int getFragmentCount() {
		return 0;
	}

	public long[] getFragmentLength(){
		return null;
	}
	
	public void selfCheck(File file){
		
	}
	
	public VideoType[] getVideoType() {
		return null;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public Provider getProvider() {
		return Provider.YOUKU;
	}

	public void write(OutputStream out) {
		// TODO Auto-generated method stub
	}

	public void write(OutputStream out, VideoType type) {
		// TODO Auto-generated method stub
	}

}
