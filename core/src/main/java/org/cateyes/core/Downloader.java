package org.cateyes.core;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.cateyes.core.volumn.Volumn;
import org.cateyes.core.volumn.VolumnFactory;

public class Downloader {

	void checkDirectory(String path){
		File target = new File(path);
		if(target.exists() && target.isFile() ){
			throw new IllegalArgumentException("target path is not a directory.  path:"+path);
		}
		target.mkdirs();
	}
	ThreadPoolExecutor infoExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(50);
	public void add(String url, String title, String tmpFile, String targetFolder) throws Exception {
		checkDirectory(tmpFile);
		checkDirectory(targetFolder);
		Volumn volunm = VolumnFactory.createVolumn(url);
		
		
	}

}
