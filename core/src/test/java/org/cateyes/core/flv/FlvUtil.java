/**
 * 
 */
package org.cateyes.core.flv;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.cateyes.core.VideoConstants.VideoType;
import org.cateyes.core.conn.MResource;
import org.cateyes.core.flv.EcmaArray;
import org.cateyes.core.flv.FLVTag;
import org.cateyes.core.flv.FMetadata;
import org.cateyes.core.flv.FlvInputStream;

/**
 * @author sankooc
 * 
 */
public class FlvUtil {
	private static int cacheByte = 1024;
	
	public final static String FIX = "%s-%02d.%s";

	public static void mergeMedia(File parent, String title, int size,
			VideoType type) throws Exception {
		if (size < 2) {
			return;
		}
		switch (type) {
		case FLV:
			File[] files = new File[size];
			for (int i = 0; i < size; i++) {
				File file = new File(parent,
						String.format(FIX, title, i, "flv"));
				if (!file.exists()) {
					throw new RuntimeException("fragment " + i + " missing");
				}
			}
			File file = new File(parent, String.format("%s.%s", title, "flv"));
			mergeFlv(files, file);
		}
	}

	public static void getInfo(File file) throws Exception{
		FlvInputStream fis = new FlvInputStream(new FileInputStream(file));
		EcmaArray<String, Object> ecma = fis.readMetadata();
	}
	
	public static void test(File file) throws FileNotFoundException,
			IOException {
		assert file.exists();
		FlvInputStream fis = new FlvInputStream(new FileInputStream(file));
		EcmaArray<String, Object> ecma = fis.readMetadata();
		FMetadata metatdata = new FMetadata(ecma);
		List<Double> list = metatdata.getTimes();
		List<Double> list2 = metatdata.getPosition();
		System.out.println(list.size());
		System.out.println(list2.size());
		for (Double d : list) {
			int ct = (int) (d * 300)/20;
			System.out.print(ct + ",");
		}
		System.out.println();
		for (Double d : list2) {
			System.out.print(d + ",");
		}
		System.out.println();
		long v = 0;
		long a = 0;
		int vcount = 0;
		int acount = 0;
		FLVTag atag = null;
		FLVTag vtag = null;
		while (true) {
			FLVTag tag = fis.readTag();
			if (null == tag) {
				break;
			}
			switch (tag.getType()) {
			case AUDIO:
				vcount += 1;
				a = tag.getTime();
				System.out.print(a+",");
				atag = tag;
				break;
			case VIDEO:
				acount += 1;
				v = tag.getTime();
				vtag = tag;
				break;
			}
		}
		System.out.println();
		System.out.println(vtag + "" + atag);
		System.out.println("video tag count" + vcount);
		System.out.println("audio tag count" + acount);
		System.out.println("video tag count" + vcount * 46);
		System.out.println("audio tag count" + acount * 66);
		System.out.println(v);
		// System.out.println(a);
		// Queue<FlvInputStream> queue = new LinkedList<FlvInputStream>();
		// queue.add(fis);
		// OutputStream out = new ByteArrayOutputStream();
		// metatdata.copy(queue, new DataOutputStream(out));
	}

	public static void resolve(File file) throws FileNotFoundException,
			IOException {
		assert file.exists();
		FlvInputStream fis = new FlvInputStream(new FileInputStream(file));
		EcmaArray<String, Object> ecma = fis.readMetadata();
		FMetadata metatdata = new FMetadata(ecma);
		List<Double> times = metatdata.getTimes();
		List<Double> pos = metatdata.getPosition();
		Iterator<Double> it = times.iterator();
		Iterator<Double> itp = pos.iterator();
		fis.close();
		RandomAccessFile raf = new RandomAccessFile(file, "rw");
		int counter = 0;
		while (it.hasNext()) {
			double time = it.next();
			long ttt = (long) time;
			double pp = itp.next();
			long ppp = (long) pp;
			raf.seek(ppp);
			if (9 == raf.read()) {
				// System.out.printf("%06x : %06x", ttt, ppp);
				// System.out.println();
				System.out.print(String.format("0x%06x,", ttt));
				counter++;
			}

		}
		System.out.println(counter + ":" + times.size());
	}

	public static void mergeFlv2(File[] files, File file) throws Exception{
		FMetadata metadata = null;
		for(File f : files){
			FlvInputStream fis =new FlvInputStream(f);
			if(null == metadata){
				metadata = fis.readMetadata2();
				metadata.decreaseH1();
			}else{
				metadata.append(fis.readMetadata2());
			}
		}
		
		
	}
	
	
	public static void mergeFlv(File[] files, File file) throws Exception {
		long pos = 0;
		FMetadata metatdata = null;
		HashMap<FlvInputStream, Double> map = new HashMap<FlvInputStream, Double>();
		DataOutputStream out = new DataOutputStream(new FileOutputStream(file));

		for (File f : files) {
			assert (f.exists());
			FlvInputStream fis = new FlvInputStream(new FileInputStream(f));
			EcmaArray<String, Object> ecma = fis.readMetadata();
			if (null == metatdata) {
				map.put(fis, 0d);
				metatdata = new FMetadata(ecma);
				System.out.println(metatdata.getDoubleValue("duration"));
			} else {
				map.put(fis, metatdata.getDoubleValue("duration"));
				metatdata.update(ecma);
			}
		}
		byte[] tmp = metatdata.toBytes();
		out.write(tmp);
		double pre = 0;
		for (FlvInputStream fis : map.keySet()) {
			Double ls = map.get(fis);
			pre = fis.copyTag(out, ls, pre);
		}
	}

	public static void copyStream(InputStream in, OutputStream out,
			MResource control) throws IOException {
		byte[] tmp = new byte[cacheByte];
		while (true) {
			try {
				int num = in.read(tmp);
				if (num < 1) {
					break;
				}
				out.write(tmp, 0, num);
				control.addContent(num);
			} catch (IOException e) {
				break;
			}
		}
		out.flush();
	}
}