package org.cateyes.core.entity;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.cateyes.core.ApacheConnector;
import org.cateyes.core.VideoConstants.Provider;
import org.cateyes.core.VideoConstants.VideoType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VolumnImpl implements Volumn {

	String title;

	String orginal;

	String[] uris;

	VideoType type = VideoType.FLV;

	static Logger logger = LoggerFactory.getLogger(VolumnImpl.class);

	ApacheConnector connector = ApacheConnector.getInstance();
	public final static String MULTIFIX = "%s-%02d.%s";

	public Provider getProvider() {
		// TODO Auto-generated method stub
		return null;
	}

	public VolumnImpl() {
	}
	
	
	public VolumnImpl(String title, String orginal, String[] uris) {
		super();
		this.title = title;
		this.orginal = orginal;
		this.uris = uris;
	}


	public void write(File dir) throws Exception {
		if (dir.isFile()) {
			throw new Exception("file is not a directory");
		}
		if (!dir.exists()) {
			dir.mkdirs();
		}
		if (null == uris || uris.length == 0) {
			throw new Exception("no source address");
		}

		if (uris.length == 1) {
			String fileName = title + ".flv";
			download(dir, new String[] { fileName });
		} else {
			File root = new File(dir, title);
			if (!root.exists()) {
				root.mkdirs();
			}
			String[] names = new String[uris.length];
			for (int i = 0; i < uris.length; i++) {
				String fileName = String.format(MULTIFIX, title, i + 1, "flv");
				names[i] = fileName;
			}
			download(root, names);
		}

	}

	static Executor service = Executors.newFixedThreadPool(10);

	protected synchronized void download(File dir, String[] names) {
		final AtomicInteger counter = new AtomicInteger(names.length);
		for (int i = 0; i < uris.length; i++) {
			final String uri = uris[i];
			final String fileName = names[i];
			final File target = new File(dir, fileName);
			service.execute(new Runnable() {
				public void run() {
					try {
						connector.download(uri, target, null);
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

	public void write() throws Exception {
		// TODO Auto-generated method stub

	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String[] getUris() {
		return uris;
	}

	public void setUris(String[] uris) {
		this.uris = uris;
	}

}
