package org.cateyes.core.entity;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.ArrayUtils;
import org.cateyes.core.ApacheConnector.ContentComsumer;
import org.cateyes.core.VideoConstants.Provider;
import org.cateyes.core.VideoConstants.VideoType;
import org.cateyes.core.youku.YoukuResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YoukuVolumn implements Volumn {

	final String yid;
	final YoukuResolver resolver;
	String name;
	String title;
	File tmpFile;
	Logger logger = LoggerFactory.getLogger(YoukuVolumn.class);

	// final static Map<String, VideoType> typeMapper = new HashMap<String,
	// VideoType>();
	// final static Map<VideoType, String> segMapper = new HashMap<VideoType,
	// String>();
	// static {
	// typeMapper.put("flv", VideoType.FLV);
	// typeMapper.put("mp4", VideoType.MP4);
	// typeMapper.put("hd2", VideoType.HD2);
	// segMapper.put(VideoType.FLV, "flv");
	// segMapper.put(VideoType.MP4, "mp4");
	// segMapper.put(VideoType.HD2, "flv");
	//
	// }

	YoukuVolumn(String yid, YoukuResolver resolver, String name) {
		if (null == yid || null == resolver) {
			throw new IllegalArgumentException();
		}
		this.yid = yid;
		this.resolver = resolver;
		this.name = name;

	}

	public void selfCheck(File file) {

	}

	// public VideoType[] getVideoType() {
	// JSONObject data = YoukuResolver.getData(yid);
	// return YoukuResolver.getVideoType(data);
	// }

	public String getName() {
		if (null == name) {
		}
		return name;
	}

	public Provider getProvider() {
		return Provider.YOUKU;
	}

	public void write(OutputStream out) {
		write(out, VideoType.FLV);
	}

	public void write(final OutputStream out, VideoType type) {
		String[] uris = YoukuResolver.getReadUriFromYID(yid, type);
		if (ArrayUtils.isEmpty(uris)) {
			logger.error("cannote download {}", yid);
			return;
		}
		if (uris.length == 1) {
			String uri = uris[0];
			YoukuResolver.getConnector().doGet(URI.create(uri),
					new ContentComsumer() {
						public void consume(InputStream content)
								throws Exception {
							byte[] tmp = new byte[1024];
							while (true) {
								try {
									int num = content.read(tmp);
									if (num < 1) {
										out.flush();
										break;
									}
									out.write(tmp, 0, num);
								} catch (Exception e) {
									out.flush();
									break;
								}
							}
						}
					});
		} else {

		}

	}

	Executor service = Executors.newCachedThreadPool();

	public void download(String[] uris) {

	}
}
