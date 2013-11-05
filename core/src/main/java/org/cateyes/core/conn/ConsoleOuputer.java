package org.cateyes.core.conn;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsoleOuputer implements Runnable {

	private final static ConsoleOuputer INSTANCE = new ConsoleOuputer();

	public static ConsoleOuputer getInstance() {
		return INSTANCE;
	}

	private int duration = 3000;

	private static Logger logger = LoggerFactory.getLogger(ConsoleOuputer.class);

	final Thread thread = new Thread(this);

	ExecutorService service = Executors.newCachedThreadPool();

	// int count;

	// void show(String task, long current, long content, int time, long
	// totalSize) {
	// content = content * 1000 / time;
	// String md = getStr(content)+ "/s";
	// int percent = (int) (current * 100 / totalSize);
	// logger.info("task:[{}] total:[{}] percent:[{}%] speed [{}] \r", task,
	// getStr(totalSize), percent, md + "/s");
	// }

	final Collection<ConsolerTask> taskList = new LinkedList<ConsolerTask>();

	public synchronized MResource createConsoler(final String task) {

		ConsolerTask dtask = new ConsolerTask(task);
//		taskList.add(dtask);
		if (!this.thread.isAlive()) {
			thread.start();
		}
		return dtask;
	}

	String getStr(long content) {
		String bt = (content & 0x02ffl) + "b";
		String kb = "";
		String mb = "";
		String gb = "";
		content = content >> 10;
		if (content > 0) {
			kb = (content & 0x02ffl) + "k ";
			content = content >> 10;
			if (content > 0) {
				mb = (content & 0x02ffl) + "m ";
				content = content >> 10;
				if (content > 0) {
					gb = (content & 0x02ffl) + "g ";
					content = content >> 10;
				}
			}
		}
		return gb + mb + kb + bt;
	}

	class ConsolerTask implements MResource, Runnable {

		public ConsolerTask(String task) {
			this.task = task;
		}

		int percent;

		String currentSpeed;

		public void run() {
			while (flag) {
				long s = content;
				try {
					Thread.sleep(duration);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				s = content - s;
				long data = s * 1000 / duration;
				currentSpeed = getStr(data) + "/s";
				percent = (int) (content * 100 / totalSize);
			}
		}

		private String task;
		private boolean flag;
		private long start;
		// private long initSize = 0;
		private long totalSize;
		private long content;

		public void init() {
			logger.info("init task:{}", task);
		}

		public void start() {
			logger.info("start task:{}", task);
			start = System.currentTimeMillis();
			flag = true;
			addTask(this);
			service.execute(this);
		}

		public void error(String msg) {
			logger.error(msg);
		}

		public void finish() {
			flag = false;
//			long time = System.currentTimeMillis() - start;
			removeTask(this);
//			logger.info("task:{} is finish cost time {}ms", task, time);
		}

		public boolean isError() {
			return !flag;
		}

		public void setLength(long size) {
			totalSize = size;

		}

		public void setContent(long content) {
			// initSize = content;

		}

		public void addContent(long increase) {
			content += increase;
		}

		public long getTime() {
			return System.currentTimeMillis() - start;
		}

	}

	synchronized void addTask(ConsolerTask task) {
		taskList.add(task);
	}

	synchronized void removeTask(ConsolerTask task) {
		taskList.remove(task);
	}

	synchronized void show() {
		int taskCount = taskList.size();
		if (taskCount < 1) {
			return;
		}
		StringBuilder builder = new StringBuilder();
		for (ConsolerTask task : taskList) {
			show(task, builder);
			builder.append(" | ");
		}
//		if(null == System.console()){
			System.out.print(builder.toString() + '\r');
//		}else{
//			System.console().writer().write(builder.toString() + "\r");
//		}
	}

	void show(ConsolerTask task, StringBuilder builder) {
//		builder.append(" t: " + task.task);
//		builder.append(" s: " + task.currentSpeed);
		builder.append(" p: " + task.percent+"%");
	}

	public void run() {
		while (true) {
			// long s = content;
			try {
				Thread.sleep(duration);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (taskList.isEmpty()) {
				continue;
			}
			show();
		}
	}
}
