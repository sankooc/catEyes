package org.cateyes.core.conn;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsoleOuputer {

	private final static ConsoleOuputer INSTANCE = new ConsoleOuputer();

	public static ConsoleOuputer getInstance() {
		return INSTANCE;
	}

	private int duration = 3000;

	private static Logger logger = LoggerFactory
			.getLogger(ConsoleOuputer.class);

	ExecutorService service = Executors.newCachedThreadPool();

	public MResource createConsoler(final String task) {
		return new ConsolerTask(task);
	}

	public String getStr(long content) {
		String bt = (content & 0x02ffl) + "b";
		String kb = "";
		String mb = "";
		String gb = "";
		content = content >> 10;
		if (content > 0) {
			kb = (content & 0x02ffl) + "kb ";
			content = content >> 10;
			if (content > 0) {
				mb = (content & 0x02ffl) + "mb ";
				content = content >> 10;
				if (content > 0) {
					gb = (content & 0x02ffl) + "gb ";
					content = content >> 10;
				}
			}
		}
		return gb + mb + kb + bt;
		// return String.format("%sMB %sKB %sB", m, k, b);
	}

	class ConsolerTask implements MResource, Runnable {

		public ConsolerTask(String task) {
			this.task = task;
		}

		void show(String task, long content, int time) {
			content = content * 1000 / time;
			String md = getStr(content);
			logger.info("task:[{}] total:[{}] current:[{}] speed [{}] ", task,
					totalSize, content, md + "/s");
		}

		public void run() {
			while (flag) {
				long s = content;
				try {
					Thread.sleep(duration);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				s = content - s;
				show(task, s, duration);
			}
		}

		private String task;
		private boolean flag;
		private long start;
		private long initSize = 0;
		private long totalSize;
		private long content;

		public void init() {
			logger.info("init task:{}", task);
		}

		public void start() {
			logger.info("start task:{}", task);
			start = System.currentTimeMillis();
			flag = true;
			service.execute(this);
		}

		public void error(String msg) {
			logger.error(msg);
		}

		public void finish() {
			flag = false;
			long time = System.currentTimeMillis() - start;
			logger.info("task:{} is finish cost time {}ms", task, time);
		}

		public boolean isError() {
			return !flag;
		}

		public void setLength(long size) {
			totalSize = size;

		}

		public void setContent(long content) {
			initSize = content;

		}

		public void addContent(long increase) {
			content += increase;
		}

	}
}
