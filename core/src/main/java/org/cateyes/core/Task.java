package org.cateyes.core;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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

	static Map<Volumn, VolumnInfo> volumnMap = new LinkedHashMap<Volumn, VolumnInfo>();
	static Map<VolumnDownloadResult, VolumnInfo> results = new LinkedHashMap<VolumnDownloadResult, VolumnInfo>();

	static Thread downThread;
	static Thread mergeThread;
	static Logger logger = LoggerFactory.getLogger(Task.class);

	static final Runnable merger = new Runnable() {
		public void run() {
			while (threadSwitch || !results.isEmpty()) {
				if (results.isEmpty()) {
					try {
						Thread.sleep(1500);
					} catch (InterruptedException e) {
					}
				} else {
					final VolumnDownloadResult result = results.keySet().iterator().next();
					results.remove(result);
					mergeExecutor.execute(new Runnable() {
						public void run() {
							if (result.isComplete()) {
								MediaMerger.merge(result.getSource(), result.getFolder(), result.getTitle());
							}
						}
					});

				}
			}

		}

	};
	static final Runnable downloader = new Runnable() {
		public void run() {
			while (threadSwitch || !volumnMap.isEmpty()) {
				if (volumnMap.isEmpty()) {
					try {
						Thread.sleep(1500);
					} catch (InterruptedException e) {
					}
				} else {
					final Volumn volumn = volumnMap.keySet().iterator().next();
					final VolumnInfo info = volumnMap.remove(volumn);
					downExecutor.execute(new Runnable() {
						public void run() {
							try {
								VolumnDownloadResult result = volumn.writeLowQuality(info.dir, info.title);
								results.put(result, info);
							} catch (Exception e) {
								logger.error(e.getMessage(), e);
							}
						}
					});
				}
			}
		}
	};

	public static void addVolumn(String url, String title, File root) {
		final VolumnInfo info = new VolumnInfo(url, title, root);
		infoExecutor.execute(new Runnable() {
			public void run() {
				try {
					Volumn volunm = VolumnFactory.createVolumn(info.url);
					volumnMap.put(volunm, info);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		if (null == downThread) {
			downThread = new Thread(downloader, "downloader");
			downThread.start();
		}
		if (null == mergeThread) {
			mergeThread = new Thread(merger, "merge");
			mergeThread.start();
		}
	}

	static void await(ThreadPoolExecutor executor) {
		try {
			executor.shutdown();
			while (!executor.isTerminated()) {
				executor.awaitTermination(1, TimeUnit.SECONDS);
			}
		} catch (InterruptedException e) {
		}
	}

	public static synchronized void waitToFinish() {
		threadSwitch = false;
		try {
			await(infoExecutor);
			if (null != downThread) {
				downThread.join();
			}
			await(downExecutor);
			if (null != mergeThread) {
				mergeThread.join();
			}
			await(mergeExecutor);
		} catch (InterruptedException e) {
		}

	}

	public void main(String[] args) {
		Task.addVolumn("", "LIad", new File("target/task"));
		Task.waitToFinish();
	}

	static ThreadPoolExecutor infoExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
	static ThreadPoolExecutor downExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
	static ThreadPoolExecutor mergeExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
}
