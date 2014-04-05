package org.cateyes.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

	static Logger logger = LoggerFactory.getLogger(Main.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("---plz input the url you want to download--");
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		Task task = Task.getTask();
		File defaultRoot = new File(System.getProperty("user.home"),"eyes");
		while (true) {
			try {
				String line = reader.readLine();
				if ("exit".equalsIgnoreCase(line) || "quit".equalsIgnoreCase(line)) {
					System.exit(0);
				}
				String[] tokens = line.split(" ");
				String url = tokens.length > 0 ? tokens[0] : null;
				if (StringUtils.isEmpty(url)) {
					logger.error("no url");
					help();
					continue;
				}
				logger.debug("url : {}", url);
				String title = tokens.length > 1 ? tokens[1] : null;
				logger.debug("title : {}", title);
				File dir = tokens.length > 2 ? new File(tokens[2]) : defaultRoot;
				logger.debug("download folder : {}", dir.getPath());
				task.addTask(title, url, dir);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	static void help() {
		System.out.println("************************************");
		System.out.println("command format:");
		System.out.println("     [URL] <title name> <target root>");
		System.out.println("eg:");
		System.out.println("http://v.youku.com/v_show/id_XNjE5NjI4Mjk2.html ljsw");
		System.out.println("************************************");
	}
}
