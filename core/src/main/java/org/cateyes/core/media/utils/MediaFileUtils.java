package org.cateyes.core.media.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.cateyes.core.flv.FMetadata;

import com.jayway.jsonpath.internal.IOUtils;

public class MediaFileUtils {
	
	public static String getSuffixByContentType(String contentType ,String defaultSuffix){
		if(StringUtils.isEmpty(contentType)){
			return defaultSuffix;
		}
		if (contentType.contains("video/x-flv")) {
			return "flv";
		} else if (contentType.contains("video/f4v")) {
			return "flv";
		} else if (contentType.contains("video/mp4")) {
			return "mp4";
		} else if (contentType
				.contains("application/octet-stream")) {
			return defaultSuffix;
		} else {
			return defaultSuffix;
		}
	}
	
	
	public static boolean checkFile(File file, FMetadata metadata) {
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(file, "r");
			List<Double> list2 = metadata.getPosition();
			int inx = 1;
			for (double d : list2) {
				long address = (long) d;
				raf.seek(address);
				int type = raf.read();
				if (9 != type) {
					System.err.println("error occur at :" + String.format("%02x", address)+" index :"+inx);
					return false;
				}
				inx++;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			IOUtils.closeQuietly(raf);
		}
	}
	
	public static void offset(File file, InputStream is) throws IOException {
		int capacity = is.available();
		byte[] buffer = new byte[capacity];
		long old = file.length();
		RandomAccessFile raf = new RandomAccessFile(file, "rw");
		long mod = (old - 1) / capacity;
		for (int i = 0; i < mod; i++) {
			old -= capacity;
			raf.seek(old);
			raf.read(buffer);
			raf.write(buffer);
		}
		raf.seek(0);
		raf.read(buffer, 0, (int) old);
		raf.seek(capacity);
		raf.write(buffer, 0, (int) old);
		is.read(buffer);
		raf.seek(0);
		raf.write(buffer);
	}
	
	static char[] tokens = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	public static String toHexString(byte[] contents) {
		if (contents == null) {
			return null;
		}
		StringBuilder builder = new StringBuilder();
		for (int j = 0; j < contents.length; j++) {
			int val = 0xff & contents[j];
			builder.append(tokens[val >>> 4]);
			builder.append(tokens[val & 0x0f]);
		}
		return builder.toString();
	}
	
	public static String getMessageDigest(File file) throws NoSuchAlgorithmException, IOException{
		MessageDigest digest = MessageDigest.getInstance("MD5");
		
		byte[] tmp =new byte[2048];
		
		FileInputStream fis = new FileInputStream(file);
		int inx;
		while(true){
			inx = fis.read(tmp);
			if(inx < 0){
				fis.close();
				break;
			}
			digest.update(tmp, 0, inx);
		}
		byte[] data = digest.digest();
		return toHexString(data);
	}
}
