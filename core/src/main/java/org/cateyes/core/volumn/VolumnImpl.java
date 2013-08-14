package org.cateyes.core.volumn;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
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

	class VolumnFragment implements Comparable<VolumnFragment> {

		public VolumnFragment(int quality, String suffix) {
			super();
			this.quality = quality;
			this.suffix = suffix;
		}

		int quality;
		String suffix="flv";

		String[] getTitleName() {
			if (resources.size() == 0) {
				return null;
			} else if (resources.size() == 1) {
				return new String[] { title };
			} else {
				String[] names = new String[resources.size()];
				for (int i = 0; i < resources.size(); i++) {
					names[i] = String.format(MULTIFIX, title, i + 1);
				}
				return names;
			}
		}

		Map<String, Long> resources = new LinkedHashMap<String, Long>();

		public int compareTo(VolumnFragment o) {
			return o.quality - quality;
		}
	}

	// video title
	String title;

	// video unique id
	String orginal;

	SortedSet<VolumnFragment> fragments = new TreeSet<VolumnFragment>();

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

	VolumnFragment getFragment(int quality) {
		for (VolumnFragment fragment : fragments) {
			if (fragment.quality == quality) {
				return fragment;
			}
		}
		return null;
	}

	VolumnFragment getFragment(int quality, String suffix) {
		VolumnFragment fragment = getFragment(quality);
		if (null == fragment) {
			fragment = new VolumnFragment(quality, suffix);
			fragments.add(fragment);
		}
		return fragment;
	}

	public void addFragment(int quality, String suffix, String url) {
		addFragment(quality, "flv", url, -1);
	}
	
	public void addFragment(int quality, String suffix, String url, long size) {
		VolumnFragment fragment = getFragment(quality, suffix);
		fragment.resources.put(url, size);
	}

	public void addFragment(int quality, String url, long size) {
		addFragment(quality, "flv", url, size);
	}

	public void addFragment(int quality, String url) {
		addFragment(quality, url, -1);
	}
	public void writeLowQuality(File dir) throws Exception {
		download(dir, fragments.first());
	}

	static Executor service = Executors.newFixedThreadPool(10);

	protected synchronized void download(final File dir, VolumnFragment fragment) throws Exception {
		if (dir.isFile()) {
			throw new Exception("file is not a directory");
		}
		if (!dir.exists()) {
			dir.mkdirs();
		}
		if (fragment.resources.isEmpty()) {
			throw new Exception("no source address");
		}
		final AtomicInteger counter = new AtomicInteger(fragment.resources.size());
		Iterator<String> ite = fragment.resources.keySet().iterator();
		final String suffix = fragment.suffix;
		String[] names = fragment.getTitleName();
		for (int i = 0; ite.hasNext(); i++) {
			final String fileName = names[i];
			final String uri = ite.next();
			final long size = fragment.resources.get(uri);
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

//	public Map<String, Long> getUrlSet() {
//		return urlSet;
//	}

	public Map<String, String> getParams() {
		return params;
	}

	public void write(File dir, int quality) throws Exception {
		VolumnFragment fragment = getFragment(quality);
		if(null == fragment){
			throw new Exception("no such this quality");
		}
		download(dir, fragment);
	}

	public int getQualityCount() {
		return fragments.size();
	}

	public Collection<String> getFragmentURL(int quality){
		VolumnFragment vf =getFragment(quality);
		return vf.resources.keySet();
	}
	
	public void writeHighQuality(File dir) throws Exception {
		download(dir, fragments.last());
	}

}
