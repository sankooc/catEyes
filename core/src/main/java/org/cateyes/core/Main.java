package org.cateyes.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.cateyes.core.volumn.Volumn;
import org.cateyes.core.volumn.VolumnFactory;

import com.jayway.jsonpath.internal.IOUtils;

public class Main {

	static void downloadByStream(InputStream stream, File dir) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(stream));
			while (true) {
				String path = reader.readLine().trim();
				if (null == path) {
					break;
				}
				if (path.startsWith("#")) {
					continue;
				}
				download(path, dir);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(reader);
		}
	}

	static void download(String uri, File file) {
		System.out.println("url : " + uri);
		System.out.println("folder:" + file.getPath());
		try {
			Volumn volum = VolumnFactory.createVolumn(uri);
			volum.writeLowQuality(file,null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length == 0) {
			useage();
			return;
		}
		File root = new File(args[0]);
		System.out.println("program root : " + root.getPath());
		if (args.length == 1) {
			useage();
			File cf = new File(root, "downlist.ini");
			File dir = new File(root, "downloads");
			dir.mkdirs();
			try {
				downloadByStream(new FileInputStream(cf), dir);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else if (args.length == 3) {
			String uri = args[1];
			String folder = args[2];
			File target = new File(folder);
			if (target.isFile()) {
				System.err.println("target is a file");
				useage();
				return;
			}
			target.mkdirs();
			download(uri, target);
		}
	}

	static void useage() {
		System.out
				.println("command error! /r downflv  [video url]  [store folder]");
	}

}
