package org.cateyes.core.flv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.jayway.jsonpath.internal.IOUtils;

public class FlvInfo {
	final FMetadata metadata;
	final FLVTag vedeohead;
	final FLVTag audeohead;
	long tagsize;
	double duration;// m
	private Map<FlvInputStream, Double> list = new LinkedHashMap<FlvInputStream, Double>();

	public FlvInfo(File file) throws IOException {
		FlvInputStream fis = new FlvInputStream(file);
		list.put(fis, duration);
		this.metadata = fis.readMetadata2();
		long offset = fis.getCursor();
		vedeohead = fis.readTag();
		audeohead = fis.readTag();
		metadata.resetPos(offset);// fix remove 0
		this.duration = metadata.getduration();
		tagsize = file.length() - offset;
	}

	public void append(File file) throws Exception {
		FlvInputStream fis = new FlvInputStream(file);
		list.put(fis, duration);
		FMetadata metadata = fis.readMetadata2();
		fis.readTag();
		fis.readTag();
		metadata.removeHeader();
		long offset = fis.getCursor();
		long tagsize = file.length() - offset;
		offset -= tagsize;
		metadata.resetPos(offset);
		metadata.resetTimes(this.duration);

		this.duration += metadata.getduration();
		this.tagsize += tagsize;
		this.metadata.append(metadata);
	}

	public void write(File target) throws Exception {
		target.getParentFile().mkdirs();

		FlvOutputStream fos = new FlvOutputStream(target);
		double offset1 = metadata.getFrameCount()*18+9+289;
		metadata.resetPos(-offset1);//bug
		offset1 -= (9+4+11);
		
		FLVTag metatag = metadata.toTag();
		fos.writeTag(metatag);
		fos.writeTag(vedeohead);
		fos.writeTag(audeohead);

		for (FlvInputStream fis : list.keySet()) {
			double time = list.get(fis) * 1000;// m
			fos.writeTags(fis, (long) time);
		}
		fos.flush();
		fos.close();
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

}
