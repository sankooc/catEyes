package org.cateyes.core.volumn;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.cateyes.core.VideoConstants.Provider;
import org.cateyes.core.conn.ApacheConnector;
import org.cateyes.core.conn.ConsoleOuputer;
import org.cateyes.core.conn.ApacheConnector.VideoInfo;
import org.cateyes.core.conn.MResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author sankooc
 */
public class VolumnImpl implements Volumn {

	// video title
	String title;

	// video unique id
	String orginal;

	// video fragments
	Map<String, Long> urlSet = new LinkedHashMap<String, Long>();

	// video suffix
	String suffix = "flv";

	static Logger logger = LoggerFactory.getLogger(VolumnImpl.class);
	Provider provider;
	Map<String, String> params;
	transient ApacheConnector connector = ApacheConnector.getInstance();

	public final static String MULTIFIX = "%s-%02d";

	public Provider getProvider() {
		return provider;
	}

	public VolumnImpl() {
	}

	public VolumnImpl(String title, String orginal, Provider provider) {
		super();
		this.title = title;
		this.orginal = orginal;
		this.provider = provider;
	}

	public void addUrl(String url) {
		addUrl(url, -1);
	}

	public void addUrl(String url, long size) {
		urlSet.put(url, size);
	}

	public void write(File dir) throws Exception {
		if (dir.isFile()) {
			throw new Exception("file is not a directory");
		}
		if (!dir.exists()) {
			dir.mkdirs();
		}
		if (urlSet.isEmpty()) {
			throw new Exception("no source address");
		}

		if (urlSet.size() == 1) {
			String fileName = title;
			download(dir, new String[] { fileName });
		} else {
			File root = new File(dir, title);
			if (!root.exists()) {
				root.mkdirs();
			}
			String[] names = new String[urlSet.size()];
			for (int i = 0; i < urlSet.size(); i++) {
				String fileName = String.format(MULTIFIX, title, i + 1);
				names[i] = fileName;
			}
			download(root, names);
		}

	}

	static Executor service = Executors.newFixedThreadPool(10);

	protected synchronized void download(final File dir, String[] names) {
		final AtomicInteger counter = new AtomicInteger(names.length);
		Iterator<String> ite = urlSet.keySet().iterator();
		for (int i = 0; ite.hasNext(); i++) {
			final String uri = ite.next();
			final long size = urlSet.get(uri);
			final String fileName = names[i];
			service.execute(new Runnable() {
				public void run() {
					try {
						Thread.sleep(500);// tudou need delay
						VideoInfo info = connector.getVideoInfo(uri);
						if (null == info) {
							connector.download(uri, size, new File(dir,
									fileName + "." + suffix), null);
						} else {
							String contentType = info.getType();
							String suf = ".";
							if (null != contentType) {
								if (contentType.contains("video/x-flv")) {
									suf += "flv";
								} else if (contentType.contains("video/f4v")) {
									suf += "flv";
								} else if (contentType.contains("video/mp4")) {
									suf += "mp4";
								} else if (contentType
										.contains("application/octet-stream")) {
									suf += suffix;
								} else {
									suf += suffix;
								}
							} else {
								suf += suffix;
							}
							MResource resource = ConsoleOuputer.getInstance()
									.createConsoler(fileName);
							connector.download(uri, info.getSize(), new File(
									dir, fileName + suf), resource);
						}
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					} finally {
						if (counter.decrementAndGet() == 0) {
							VolumnImpl clz = VolumnImpl.this;
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
			logger.error(e.getMessage());
		}

	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public Map<String, Long> getUrlSet() {
		return urlSet;
	}

	public Map<String, String> getParams() {
		return params;
	}

}
