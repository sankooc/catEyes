/**
 * 
 */
package org.cateyes.core.flv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.LinkedList;
import java.util.List;

/**
 * @author sankooc
 * 
 */
public class FLVBuilder {
	final LinkedList<File> sources = new LinkedList<File>();

	public void addURI(File url) {
		sources.add(url);
	}

	FLVParser parser = new FLVParser();

	public void resolve(File target) throws Exception {
		FlvMetadata metadata = new FlvMetadata(null);
		double time = 0;
		double pos = 0;
		if (!target.exists()) {
			target.getParentFile().mkdirs();
		}
		FileOutputStream fos = new FileOutputStream(target);
		for (File file : sources) {
			InputStream is = null;
			FlvMetadata data = null;
			try {
				is = new FileInputStream(file);
				data = parser.parseMetaData(is);
				parser.copyTags(is, fos,(long)time);
				List<Double> list = data.getPosFrame();
				System.out.println(list.size());
				for (double value : list) {
					metadata.getPosFrame().add(value + pos);
				}
				metadata.getPosFrame().remove(0);
				metadata.getPosFrame().remove(0);
				list = data.getTimeFrame();
				for (double value : list) {
					metadata.getTimeFrame().add(value + time);
				}
				metadata.getTimeFrame().remove(0);
				metadata.getTimeFrame().remove(0);
				time += data.getDuration();
				metadata.setDuration(time);
				pos += file.length();
				//f1 + N * 0X12 + LENGTH
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					is.close();
				} catch (Exception e) {
				}
			}
		}
		fos.flush();
		fos.close();
		InputStream metadataStream = FLVParser.createMetaData(metadata);
		offset(target,metadataStream);
	}

	public void offset(File file, InputStream is) throws IOException {
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
		raf.read(buffer,0,(int) old);
		raf.seek(capacity);
		raf.write(buffer,0,(int) old);
		is.read(buffer);
		raf.seek(0);
		raf.write(buffer);
	}

	byte[] streamId = new byte[] { 0, 0, 0 };

	public static void writeTime(OutputStream os, int period)
			throws IOException {
		os.write((period >>> 16) & 0xff);
		os.write((period >>> 8) & 0xff);
		os.write((period) & 0xff);
		os.write((period >>> 24) & 0xff);
	}

	public static int readTimeUIB(RandomAccessFile file) throws IOException {
		int value = 0;
		value = (file.read() << 16) + (file.read() << 8) + file.read();
		int high = file.read();
		if (0 != high) {
			value += (high << 24);
		}
		return value;
	}

	public static void copy(RandomAccessFile file, OutputStream os, int length)
			throws IOException {
		byte[] tmp = new byte[length];
		int inx = file.read(tmp);
		os.write(tmp);
	}

	public static int read24UIB(RandomAccessFile file, OutputStream os)
			throws IOException {
		int value = 0;
		int tmp;
		os.write(tmp = file.read());
		value += (tmp << 16);
		os.write(tmp = file.read());
		value += (tmp << 8);
		os.write(tmp = file.read());
		value += tmp;
		return value;
	}

}
