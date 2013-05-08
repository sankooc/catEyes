package org.cateyes.core.entity;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.mutable.MutableBoolean;
import org.cateyes.core.ApacheConnector.ContentComsumer;
import org.cateyes.core.CommonAdaptor;
import org.cateyes.core.VideoConstants.Provider;
import org.cateyes.core.VideoConstants.VideoType;
import org.cateyes.core.util.CommonUtils;
import org.cateyes.core.youku.YoukuResolver;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YoukuVolumn implements Volumn {

	final String yid;
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

	YoukuVolumn(String yid, File file) {
		this(yid, null, file);
	}

	YoukuVolumn(String yid, String name, File file) {
		if (null == yid) {
			throw new IllegalArgumentException();
		}
		this.yid = yid;
		this.name = name;
		this.tmpFile = file;
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

	public void write(final OutputStream out, final VideoType type) {
		String[] uris = YoukuResolver.getReadUriFromYID(yid, type);
		if (ArrayUtils.isEmpty(uris)) {
			logger.error("cannot download {}", yid);
			return;
		}
		Document page = YoukuResolver.getConnector().getPage(
				"http://v.youku.com/v_show/id_" + yid + ".html");
		String title = page.title();
		title = title.replace(" - 视频 - 优酷视频 - 在线观看", "");
		title = title.replace(" - 专辑 - 优酷视频", "");
		title = title.replace("—优酷网，视频高清在线观看", "");
		this.title = title;
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
			download(uris, type);
		}

	}

	public void singleDownload(String uri, final OutputStream out) {
		YoukuResolver.getConnector().doGet(URI.create(uri),
				new ContentComsumer() {
					public void consume(InputStream content) throws Exception {
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
	}

	Executor service = Executors.newCachedThreadPool();

	public void download(final String[] uris, VideoType type) {
		final Collection<String> taskList = new TreeSet<String>();
		final MutableBoolean flag = new MutableBoolean(true);
		for (int i = 0; i < uris.length; i++) {

			final String uri = uris[i];
			final String fileName = String.format(CommonUtils.FIX, title, i,
					"flv");
			// taskList.add(fileName);
			final File freg = new File(tmpFile, fileName);
			service.execute(new Runnable() {
				public void run() {
					CommonAdaptor adaptor = new CommonAdaptor(fileName
							+ " download");
					try {
						YoukuResolver.getConnector().download(URI.create(uri),
								freg, adaptor);
						taskList.add(fileName);
					} catch (Exception e) {
						flag.setValue(false);
						logger.error(e.getMessage(), e);
					}
				}

			});
		}
		service.execute(new Runnable() {
			public void run() {
				int size = uris.length;
				while (flag.booleanValue()) {
					if (taskList.size() == size) {
					} else {
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
	}
}
