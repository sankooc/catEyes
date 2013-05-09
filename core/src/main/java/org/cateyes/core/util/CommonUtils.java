/**
 * 
 */
package org.cateyes.core.util;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


import org.cateyes.core.MResource;
import org.cateyes.core.VideoConstants.VideoType;
import org.cateyes.core.flv.FlvInputStream;
import org.cateyes.core.flv.FlvMetadata;
import org.junit.Assert;

/**
 * @author sankooc
 * 
 */
public class CommonUtils {
	static int cacheByte = 1024;
	public final static String FIX = "%s-%02d.%s";

	public static void mergeMedia(File parent, String title, int size,
			VideoType type) throws IOException {
		if (size < 2) {
			return;
		}
		switch (type) {
		case FLV:
			File[] files = new File[size];
			for (int i = 0; i < size; i++) {
				File file = new File(parent,
						String.format(FIX, title, i, "flv"));
				if (!file.exists()) {
					throw new RuntimeException("fragment " + i + " missing");
				}
			}
			File file = new File(parent, String.format("%s.%s", title, "flv"));
			mergeFlv(files, file);
		}
	}

	public static void mergeFlv(File[] files, OutputStream out) {
		for (File f : files) {
			
		}

	}

	public static void mergeFlv(File[] files, File file) throws IOException {
		long pos = 0;
		FlvMetadata metatdata = new FlvMetadata();
		HashMap<File, Double> map = new HashMap<File, Double>();
		List<FlvInputStream> ins = new LinkedList<FlvInputStream>();
		for (File f : files) {
			Assert.assertTrue(f.exists());
			map.put(f, metatdata.getDuration());
			FlvInputStream fis = new FlvInputStream(new FileInputStream(f));
			metatdata.update(fis);
			ins.add(fis);
		}
		metatdata.write(file);
		

		// file.getParentFile().mkdirs();
		// OutputStream out = new FileOutputStream(file);
		// mergeFlv(files, out);
		// out.flush();
		// out.close();

	}

	public static void copyStream(InputStream in, OutputStream out,
			MResource control) throws IOException {
		byte[] tmp = new byte[cacheByte];
		while (true) {
			try {
				int num = in.read(tmp);
				if (num < 1) {
					break;
				}
				out.write(tmp, 0, num);
				control.addContent(num);
			} catch (IOException e) {
				break;
			}
		}
		out.flush();
	}
}
