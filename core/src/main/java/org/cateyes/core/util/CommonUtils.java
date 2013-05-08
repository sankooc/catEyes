/**
 * 
 */
package org.cateyes.core.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.cateyes.core.MResource;
import org.cateyes.core.VideoConstants.VideoType;

/**
 * @author sankooc
 * 
 */
public class CommonUtils {
	static int cacheByte = 1024;

	public static void mergeMedia(File parent, String title, int size,
			VideoType type) {
		if (size < 2) {
			return;
		}
		switch (type) {
		case FLV:
			File[] files = new File[size];
			for (int i = 0; i < size; i++) {
				File file = new File(parent, String.format("%s-%02d.%s", title,
						(i + 1), "flv"));
				if (!file.exists()) {
					throw new RuntimeException("fregment " + i + " missing");
				}
			}
			File file = new File(parent, String.format("%s.%s", title, "flv"));
			mergeFlv(files, file);
		}
	}

	public static void mergeFlv(File[] files, File file) {
		
		for (File f : files) {

		}

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
