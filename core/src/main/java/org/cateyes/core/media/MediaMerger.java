package org.cateyes.core.media;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.cateyes.core.VideoConstants.VideoType;
import org.cateyes.core.flv.FLVBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coremedia.iso.IsoFile;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;

public class MediaMerger {

	private final static Logger logger = LoggerFactory.getLogger(MediaMerger.class);

	static VideoType getType(File source) {
		InputStream stream = null;
		byte[] tmp = new byte[3];
		try {
			stream = new FileInputStream(source);
			stream.read(tmp);
			if ("flv".equalsIgnoreCase(new String(tmp))) {
				return VideoType.FLV;
			}
			stream.skip(1);
			tmp = new byte[4];
			stream.read(tmp);
			if ("ftyp".equalsIgnoreCase(new String(tmp))) {
				return VideoType.MP4;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(stream);
		}

		return null;
	}

	public static File merge(Collection<File> files, File folder, String title) {
		File[] sources = files.toArray(new File[] {});
		Arrays.sort(sources);
		return merge(sources, folder, title);
	}

	public static File merge(File[] sources, File folder, String title) {
		if (null == sources || sources.length == 0) {
			return null;
		}
		if (sources.length == 1) {
			return sources[0];
		}
		folder.mkdirs();
		File file = sources[0];
		VideoType type = getType(file);
		if (null == type) {
			return null;
		}
		switch (type) {
		case FLV:
			return mergeFlv(sources, folder, title);
		case MP4:
			return mergeMp4(sources, folder, title);
		}
		return null;
	}

	public static File mergeFlv(File[] sources, File folder, String title) {
		FLVBuilder builder = null;
		try {
			for (int i = 0; i < sources.length; i++) {
				if (builder == null) {
					builder = new FLVBuilder(sources[i]);
				} else {
					builder.append(sources[i]);
				}
			}
			File target = new File(folder, title + ".flv");
			builder.write(target);
			return target;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(builder);
		}
		return null;
	}

	public static File mergeMp4(File[] sources, File folder, String title) {
		try {
			File target = new File(folder, title + ".mp4");
			List<Track> videoTracks = new LinkedList<Track>();
			List<Track> audioTracks = new LinkedList<Track>();
			for (int i = 0; i < sources.length; i++) {
				Movie movie = MovieCreator.build(Channels.newChannel(new FileInputStream(sources[i])));
				for (Track t : movie.getTracks()) {
					if (t.getHandler().equals("soun")) {
						audioTracks.add(t);
					} else if (t.getHandler().equals("vide")) {
						videoTracks.add(t);
					}
				}
			}

			Movie result = new Movie();

			if (audioTracks.size() > 0) {
				result.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
			}
			if (videoTracks.size() > 0) {
				result.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
			}

			IsoFile out = new DefaultMp4Builder().build(result);
			FileOutputStream fos = new FileOutputStream(target);
			out.getBox(fos.getChannel());
			fos.close();
			return target;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
