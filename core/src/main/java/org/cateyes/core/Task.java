package org.cateyes.core;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Executor;

import org.cateyes.core.media.MediaMerger;
import org.cateyes.core.volumn.Volumn;
import org.cateyes.core.volumn.Volumn.VolumnDownloadResult;
import org.cateyes.core.volumn.VolumnFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Task {
	static class VolumnInfo {
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

	static boolean threadSwitch = true;
	static Queue<VolumnInfo> infoQueue = new LinkedList<VolumnInfo>();
	static Map<VolumnInfo, Exception> errMap = new LinkedHashMap<VolumnInfo, Exception>();
	static Map<Volumn, VolumnInfo> volumnMap = new LinkedHashMap<Volumn, VolumnInfo>();
	static Map<VolumnDownloadResult, VolumnInfo> results = new LinkedHashMap<VolumnDownloadResult, VolumnInfo>();
	static Thread infoThread;
	static Thread downThread;
	static Thread mergeThread;
	static Logger logger = LoggerFactory.getLogger(Task.class);

	static final Runnable resolver = new Runnable() {
		public void run() {
			while (threadSwitch) {
				if (infoQueue.isEmpty()) {
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
					}
				} else {
					VolumnInfo info = infoQueue.poll();
					try {
						Volumn volum = VolumnFactory.createVolumn(info.url);
						volumnMap.put(volum, info);
					} catch (Exception e) {
						errMap.put(info, e);
					}
				}
			}
		}
	};
	
	static final Runnable merger = new Runnable() {
		public void run() {
			while (threadSwitch) {
				if (results.isEmpty()) {
					try {
						Thread.sleep(1500);
					} catch (InterruptedException e) {
					}
				} else {
					VolumnDownloadResult result =results.keySet().iterator().next();
					results.remove(results);
					if(result.isComplete()){
						MediaMerger.merge(result.getSource(), result.getFolder(), result.getTitle());
					}
				}
			}

		}

	};
	static final Runnable downloader = new Runnable() {
		public void run() {
			while (threadSwitch) {
				if (volumnMap.isEmpty()) {
					try {
						Thread.sleep(1500);
					} catch (InterruptedException e) {
					}
				} else {
					Volumn volumn = volumnMap.keySet().iterator().next();
					VolumnInfo info = volumnMap.remove(volumn);
					try {
						VolumnDownloadResult result = volumn.writeLowQuality(info.dir, info.title);
						results.put(result, info);
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
				}
			}
		}
	};

	public static void addVolumn(String url, String title, File root) {
		VolumnInfo info = new VolumnInfo(url, title, root);
		infoQueue.add(info);
		if (null == infoThread) {
			infoThread = new Thread(resolver, "resolver");
			infoThread.start();
		}
		if (null == downThread) {
			downThread = new Thread(resolver, "downloader");
			downThread.start();
		}
		if (null == mergeThread) {
			mergeThread = new Thread(resolver, "merge");
			mergeThread.start();
		}
	}

	public static synchronized void waitToFinish() {
		threadSwitch = false;

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
