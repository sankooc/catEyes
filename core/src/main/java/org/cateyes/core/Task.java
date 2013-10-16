package org.cateyes.core;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Executor;

import org.cateyes.core.volumn.Volumn;
import org.cateyes.core.volumn.VolumnFactory;


public class Task {
	static class VolumnInfo{
		String url;
		String title;
		File dir;
		public VolumnInfo(String url, String title, File dir) {
			super();
			this.url = url;
			this.title = title;
			this.dir = dir;
		}
		
	}
	static boolean threadSwitch =true;
	static Queue<VolumnInfo> infoQueue = new LinkedList<VolumnInfo>();
	static Map<VolumnInfo,Exception> errMap = new LinkedHashMap<VolumnInfo,Exception>();
	static Map<Volumn,VolumnInfo> volumnMap = new LinkedHashMap<Volumn,VolumnInfo>();
	static Thread infoThread;
	public static void addVolumn(String url,String title,File root){
		VolumnInfo info  = new VolumnInfo(url,title,root);
		infoQueue.add(info);
		if(null == infoThread){
			infoThread = new Thread(new Runnable(){
				public void run() {
					while(threadSwitch){
						if(infoQueue.isEmpty()){
							try {
								Thread.sleep(1000);
							} catch (Exception e) {
							}
						}else{
							VolumnInfo info = infoQueue.poll();
							try {
								Volumn volum = VolumnFactory.createVolumn(info.url);
								volumnMap.put(volum, info);
							} catch (Exception e) {
								errMap.put(info, e);
							}
						}
					}
				}},"url-resolver");
			infoThread.start();
		}
//		Volumn volum = VolumnFactory.createVolumn(uri);
//	    volum.writeLowQuality(file,null);
		
	}
	
	public static Executor getResolverExecutor() {
		return null;
	}
	public static Executor getDownloadExecutor() {
		return null;
	}
	public static Executor getMergeExecutor() {
		return null;
	}
}
