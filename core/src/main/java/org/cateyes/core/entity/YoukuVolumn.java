package org.cateyes.core.entity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
import java.util.concurrent.atomic.AtomicInteger;

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

	public String getName() {
		if (null == name) {
		}
		return name;
	}

	public Provider getProvider() {
		return Provider.YOUKU;
	}

	public void write() throws IOException {
		write(VideoType.FLV);
	}

	public void write(final VideoType type) throws IOException {
		JSONObject data = YoukuResolver.getData(yid);
		title = data.getString("title");
		String[] uris = YoukuResolver.getRealUri(data, type);
		if (ArrayUtils.isEmpty(uris)) {
			logger.error("cannot download {}", yid);
			return;
		}
		if (uris.length == 1) {
			String uri = uris[0];
			File file = new File(tmpFile, title + "-." + type.name());
			file.createNewFile();
			final OutputStream out = new FileOutputStream(file);
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
							out.close();
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

	Executor service = Executors.newFixedThreadPool(10);

	public synchronized void download(final String[] uris, VideoType type) {
		// final Collection<String> taskList = new TreeSet<String>();
		// final MutableBoolean flag = new MutableBoolean(true);
		final AtomicInteger total = new AtomicInteger(uris.length);
		for (int i = 0; i < uris.length; i++) {
			final String uri = uris[i];
			final String fileName = String.format(CommonUtils.FIX, title, i,
					"flv");
			final File freg = new File(tmpFile, fileName);
			service.execute(new Runnable() {
				public void run() {
					try {
						YoukuResolver.getConnector().download(URI.create(uri),
								freg, null);
						// taskList.add(fileName);
					} catch (Exception e) {
						// flag.setValue(false);
						logger.error(e.getMessage(), e);
					} finally {
						if (total.decrementAndGet() == 0) {
							YoukuVolumn clz = YoukuVolumn.this;
							synchronized (clz) {
								clz.notifyAll();
							}
						}
					}
				}

			});
		}

		try {
			wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// service.execute(new Runnable() {
		// public void run() {
		// int size = uris.length;
		// while (flag.booleanValue()) {
		// if (taskList.size() == size) {
		// } else {
		// try {
		// Thread.sleep(2000);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// }
		// }
		// }
		// });
	}
}
