package org.cateyes.core;

import java.io.File;
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
	static Logger logger = LoggerFactory.getLogger(Task.class);

	class Worker implements Runnable {

		String url;
		File dir;
		String title;

		public Worker(String url, File dir, String title) {
			super();
			this.url = url;
			this.dir = dir;
			this.title = title;
		}

		public void run() {
			try {
				Volumn volunm = VolumnFactory.createVolumn(url);
				if (null != volunm) {
					downExecutor.execute(new DownloadWorker(url, dir, title, volunm));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	class MergerWorker extends Worker {

		VolumnDownloadResult result;

		public MergerWorker(String url, File dir, String title, VolumnDownloadResult result) {
			super(url, dir, title);
			this.result = result;
		}

		@Override
		public void run() {
			MediaMerger.merge(result.getSource(), result.getFolder(), result.getTitle());
		}
	}

	class DownloadWorker extends Worker {

		Volumn volumn;

		public DownloadWorker(String url, File dir, String title, Volumn volumn) {
			super(url, dir, title);
			this.volumn = volumn;
		}

		public void run() {
			try {
				VolumnDownloadResult result = volumn.writeLowQuality(dir, title);
				if (result.isComplete()) {
					mergeExecutor.execute(new MergerWorker(url, dir, title, result));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
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

	static Task INSTANCE = new Task();

	public static Task getTask() {
		return INSTANCE;
	}

	private Task() {
	}

	public void addTask(String title, String url, File dir) {
		infoExecutor.execute(new Worker(url, dir, title));
	}

	ThreadPoolExecutor infoExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
	ThreadPoolExecutor downExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
	ThreadPoolExecutor mergeExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
}
