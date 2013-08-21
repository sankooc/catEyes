package org.cateyes.core.media;

import java.io.File;

import org.cateyes.core.VideoConstants.VideoType;

public class MediaMerger {

	VideoType getType(File source) {
		return null;// TODO
	}

	public void merge(File[] sources, File target) {
		if (null == sources || sources.length < 2) {
			return;
		}
		File file = sources[0];
		VideoType type = getType(file);
		switch (type) {
		case FLV:
			mergeFlv(sources, target);
			return;
		case MP4:
			mergeMp4(sources, target);
			return;
		}

	}

	public void mergeFlv(File[] sources, File target) {
		// TODO

	}

	public void mergeMp4(File[] sources, File target) {
		// TODO
	}

}
