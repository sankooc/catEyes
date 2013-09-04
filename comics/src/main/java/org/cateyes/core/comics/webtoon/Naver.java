package org.cateyes.core.comics.webtoon;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import org.cateyes.core.comics.MangaProvider;
import org.cateyes.core.conn.ApacheConnector;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Naver {
	static Logger logger = LoggerFactory.getLogger(Naver.class);
	
	
	public void download(String id, File file, int start, int end)
			throws IOException, InterruptedException {
		String title = detail(id, file);
		int inx = start;
		for (; inx <= end; inx++) {
			File root = new File(file, title + "-" + inx);
			download(id, inx, root.getPath(), title + "-" + inx);
		}
		while (service.getActiveCount() != 0) {
			Thread.sleep(3000);
		}
	}

	public String detail(String id, File file) throws IOException {
		URI uri = URI.create(detail + id);
		Document page = connector.getPage(uri);
		Elements eles = page.getElementsByAttributeValue("class", "comicinfo");
		Element detail = eles.get(0).child(0).child(0).child(0);
		String title = detail.attr("title");
		logger.info("webtoon title {}", title);
		String icon = detail.attr("src");
		file.mkdirs();
		// connector.download(icon, new File(file, "icon.jpg"));
		return title;
	}

	final ApacheConnector connector = ApacheConnector.getInstance();
	String detail = "http://comic.naver.com/webtoon/list.nhn?titleId=";
	String prefix = "http://comic.naver.com/webtoon/detail.nhn";
	ThreadPoolExecutor service = (ThreadPoolExecutor) Executors
			.newCachedThreadPool();

	public void download(String id, int volumn, String path, final String prix)
			throws InterruptedException {
		try {
			URI uri = URI.create(prefix + "?titleId=" + id + "&no=" + volumn);
			Document page = connector.getPage(uri);
			Elements list = page.getElementsByAttributeValue("class",
					"view_area");
			Element ele = list.get(0);
			List<Node> nodes = ele.child(0).childNodes();
			final AtomicInteger i = new AtomicInteger(0);
			final File root = new File(path);
			root.mkdirs();
			for (final Node node : nodes) {
				if (node.nodeName().equals("img")) {
					service.execute(new Runnable() {
						public void run() {
							try {
								connector.download(node.attr("src"), new File(
										root, prix + "-" + i.incrementAndGet()
												+ ".jpg"));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
				}
			}

			System.out.println(nodes.size());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
