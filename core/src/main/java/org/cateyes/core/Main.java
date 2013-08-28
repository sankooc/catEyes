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

	static void downloadByStream(InputStream stream,File dir) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(stream));
			while (true) {
				String path = reader.readLine();
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

	static void download(String uri,File file){
		try {
			Volumn volum = VolumnFactory.createVolumn(uri);
			volum.writeLowQuality(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (null == args || args.length < 2) {
			useage();
			File cf = new File("downlist.ini");
			File dir = new File("../downloads");
			dir.mkdirs();
			try {
				downloadByStream(new FileInputStream(cf),dir);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

		} else {
			String uri = args[0];
			String folder = args[1];
			File target = new File(folder);
			if (target.isFile()) {
				System.err.println("target is a file");
				useage();
				return;
			}
			download(uri,target);
		}
	}

	static void useage() {
		System.out
				.println("command error! /r downflv  [video url]  [store folder]");
	}

}
