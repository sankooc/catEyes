package org.cateyes.core.deprecated;
//package org.cateyes.core.entity;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.net.URI;
//import java.util.concurrent.Executor;
//import java.util.concurrent.Executors;
//import java.util.concurrent.atomic.AtomicInteger;
//
//import net.sf.json.JSONObject;
//
//import org.apache.commons.lang.ArrayUtils;
//import org.cateyes.core.ApacheConnector;
//import org.cateyes.core.VideoConstants.Provider;
//import org.cateyes.core.VideoConstants.VideoType;
//import org.cateyes.core.util.CommonUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//public class YoukuVolumnImp implements Volumn {
//
//	private String yid;
//	private String title;
//	private File tmpFile;
//	static Logger logger = LoggerFactory.getLogger(YoukuVolumnImp.class);
//	String[] uris;
//	private ApacheConnector connector;
//	private String subfix = "flv";
//
//	YoukuVolumnImp(String yid, File file, ApacheConnector connector) {
//		if (null == yid) {
//			throw new IllegalArgumentException();
//		}
//		this.yid = yid;
//		this.tmpFile = file;
//		this.connector = connector;
//	}
//
//	public void selfCheck(File file) {
//
//	}
//
//	public Provider getProvider() {
//		return Provider.YOUKU;
//	}
//
//	public void write() throws IOException {
//		if (uris.length == 1) {
//			String uri = uris[0];
//			File file = new File(tmpFile, title + "-." + subfix);
//			file.createNewFile();
//			final OutputStream out = new FileOutputStream(file);
////			connector.doGet(URI.create(uri), new ContentComsumer() {
////				public void consume(InputStream content) throws Exception {
////					byte[] tmp = new byte[1024];
////					while (true) {
////						try {
////							int num = content.read(tmp);
////							if (num < 1) {
////								out.flush();
////								break;
////							}
////							out.write(tmp, 0, num);
////						} catch (Exception e) {
////							out.flush();
////							break;
////						}
////					}
////					out.close();
////				}
////			});
//		} else {
//			download(uris);
//		}
//
//	}
//
//
//	static Executor service = Executors.newFixedThreadPool(10);
//
//	public synchronized void download(final String[] uris) {
//		final AtomicInteger total = new AtomicInteger(uris.length);
//		for (int i = 0; i < uris.length; i++) {
//			final String uri = uris[i];
//			final String fileName = String.format(CommonUtils.FIX, title, i,
//					subfix);
//			final File freg = new File(tmpFile, fileName);
//			service.execute(new Runnable() {
//				public void run() {
//					try {
//						connector.download(uri, freg, null);
//					} catch (Exception e) {
//						logger.error(e.getMessage(), e);
//					} finally {
//						if (total.decrementAndGet() == 0) {
//							YoukuVolumnImp clz = YoukuVolumnImp.this;
//							synchronized (clz) {
//								clz.notifyAll();
//							}
//						}
//					}
//				}
//
//			});
//		}
//
//		try {
//			wait();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//	}
//
//	public String getYid() {
//		return yid;
//	}
//
//	public void setYid(String yid) {
//		this.yid = yid;
//	}
//
//	public String getTitle() {
//		return title;
//	}
//
//	public void setTitle(String title) {
//		this.title = title;
//	}
//
//	public File getTmpFile() {
//		return tmpFile;
//	}
//
//	public void setTmpFile(File tmpFile) {
//		this.tmpFile = tmpFile;
//	}
//
//	public String[] getUris() {
//		return uris;
//	}
//
//	public void setUris(String[] uris) {
//		this.uris = uris;
//	}
//
//	public ApacheConnector getConnector() {
//		return connector;
//	}
//
//	public void setConnector(ApacheConnector connector) {
//		this.connector = connector;
//	}
//
//	public void write(File dir) throws Exception {
//		// TODO Auto-generated method stub
//		
//	}
//
//}
