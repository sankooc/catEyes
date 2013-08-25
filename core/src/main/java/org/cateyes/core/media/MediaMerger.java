package org.cateyes.core.media;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;

import org.apache.commons.io.IOUtils;
import org.cateyes.core.VideoConstants.VideoType;
import org.cateyes.core.flv.FlvBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MediaMerger {

	private final static Logger logger = LoggerFactory.getLogger(MediaMerger.class);

	VideoType getType(File source) {
		InputStream stream = null;
		byte[] tmp = new byte[3];
		try {
			stream = new FileInputStream(source);
			stream.read(tmp);
			if (new String(tmp).equalsIgnoreCase("flv")) {
				return VideoType.FLV;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(stream);
		}

		return null;
	}

	public void merge(Collection<File> files ,File folder,String title){
		File[] sources = files.toArray(new File[]{});
		merge(sources, folder,title);
	}
	
	public void merge(File[] sources, File folder,String title) {
		if (null == sources || sources.length < 2) {
			return;
		}
		folder.mkdirs();
		File file = sources[0];
		VideoType type = getType(file);
		switch (type) {
		case FLV:
			mergeFlv(sources, folder,title);
			return;
		case MP4:
			mergeMp4(sources, folder,title);
			return;
		}

	}

	public void mergeFlv(File[] sources, File folder,String title) {
		FlvBuilder builder = null;
		try {
			for (int i = 0; i < sources.length; i++) {
				if (builder == null) {
					builder = new FlvBuilder(sources[i]);
				} else {
					builder.append(sources[i]);
				}
			}
			File target = new File(folder,title+".flv");
			builder.write(target);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(builder);
		}
	}

	public void mergeMp4(File[] sources, File folder,String title) {
		// TODO
	}

}
