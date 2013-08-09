package org.cateyes.core;

import java.io.File;

import org.cateyes.core.volumn.Volumn;
import org.cateyes.core.volumn.VolumnFactory;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (null == args || args.length < 2) {
			useage();
		} else {
			String uri = args[0];
			String folder = args[1];
			File target = new File(folder);
			if (target.isFile()) {
				System.err.println("target is a file");
				useage();
				return;
			}
			try {
				Volumn volum = VolumnFactory.createVolumn(uri);
				volum.write(target);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	static void useage() {
		System.out.println("command error! /r downflv  [video url]  [store folder]");
	}

}
