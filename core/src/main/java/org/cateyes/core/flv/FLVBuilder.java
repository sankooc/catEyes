package org.cateyes.core.flv;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.cateyes.core.media.io.FlvInputStream;
import org.cateyes.core.media.io.FlvOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlvBuilder implements Closeable {
	static Logger logger = LoggerFactory.getLogger(FlvBuilder.class);
	final FMetadata metadata;
	final FLVTag vedeohead;
	final FLVTag audeohead;
	long tagsize;
	double duration;// m
	private Map<FlvInputStream, Double> list = new LinkedHashMap<FlvInputStream, Double>();

	public FlvBuilder(File file) throws IOException {
		FlvInputStream fis = new FlvInputStream(file);
		list.put(fis, duration);
		this.metadata = fis.readMetadata();
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
		FMetadata metadata = fis.readMetadata();
		fis.readTag();
		fis.readTag();
		metadata.removeHeader();
		long offset = fis.getCursor();
		long _tagsize = file.length() - offset;
		offset -= tagsize;
		metadata.resetPos(offset);
		metadata.resetTimes(this.duration);

		this.duration += metadata.getduration();
		this.tagsize += _tagsize;
		this.metadata.append(metadata);
	}

	public void write(File target) {
		
		FlvOutputStream fos = null;
		try {
			fos = new FlvOutputStream(target);
			double offset1 = metadata.getFrameCount() * 18 + 9 + 289;
			metadata.resetPos(-offset1);// bug
			offset1 -= (FlvConstants.FLV_HEADLENGH + FlvConstants.TAG_SPLIT + FlvConstants.TAG_INCREASE);

			metadata.setDuration(duration);

			FLVTag metatag = metadata.toTag();
			fos.writeTag(metatag);
			fos.writeTag(vedeohead);
			fos.writeTag(audeohead);

			for (FlvInputStream fis : list.keySet()) {
				double time = list.get(fis) * 1000;// m
				fos.writeTags(fis, (long) time);
			}
			fos.flush();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(fos);
		}
	}

	public void close() {
		for (FlvInputStream fis : list.keySet()) {
			try {
				fis.close();
			} catch (IOException e) {
			}
		}
	}

}
