/**
 * 
 */
package org.cateyes.core.flv;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.cateyes.core.util.DataStreamUtils;

/**
 * @author sankooc
 * 
 */
public class FMetadata {

	public static int META_INCREASE = 18;

	final EcmaArray<String, Object> metadata;

	public FMetadata(EcmaArray<String, Object> arr) {
		assert null != arr;
		metadata = arr;
		System.out.println("read duration:" + (Double) arr.get("duration"));
	}

	public Double getDoubleValue(String key) {
		return (Double) metadata.get(key);
	}

	public int getFrameCount() {
		Map<?, ?> map = (Map<?, ?>) metadata.get("keyframes");
		List<?> list = (List<?>) map.get("times");
		return list.size();
	}

	void sum(String key, EcmaArray<String, ?> arr) {
		Double inc = (Double) arr.get(key);
		Double value = (Double) metadata.get(key) + inc;
		metadata.put(key, value);
		System.out.println("sum duration:" + value);
	}

	public List<Double> getList(String key, Map<String, Object> keyframes) {
		List<Double> list = (List<Double>) keyframes.get(key);
		return list;
	}

	public List<Double> getPosition() {
		return getPosition(metadata);
	}

	public List<Double> getTimes() {
		return getTimes(metadata);
	}

	public List<Double> getPosition(EcmaArray<String, Object> arr) {
		Map<String, Object> cMap = (Map<String, Object>) arr.get("keyframes");
		return getList(FlvConstants.filepositions, cMap);
	}

	public List<Double> getTimes(EcmaArray<String, Object> arr) {
		Map<String, Object> cMap = (Map<String, Object>) arr.get("keyframes");
		return getList(FlvConstants.time, cMap);
	}

	public void update(EcmaArray<String, ?> arr) {
		for (String key : arr.keySet()) {
			if ("keyframes".equals(key)) {
				continue;
			}
			if ("duration".equals(key)) {
				sum(key, arr);
				continue;
			}
			assert arr.get(key).equals(metadata.get(key));
		}
		// Map<String, Object> cMap = (Map<String, Object>)
		// arr.get("keyframes");

		// Double inc = (Double) arr.get("duration");
		// Double value = (Double) metadata.get("duration");

	}

	public byte[] toBytes() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream os = new DataOutputStream(baos);
		os.write("FLV".getBytes());
		os.write(1);// version
		os.write(5);// type flag =5
		os.write(new byte[] { 0, 0, 0, 9, 0, 0, 0, 0, 18, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0 });
		AMFOutputStream aos = new AMFOutputStream(os);
		aos.write("onMetaData");
		aos.write(metadata);
		baos.flush();
		byte[] datas = baos.toByteArray();
		int length = datas.length - 0x18;
		datas[0x0e] = (byte) (length >>> 16);
		datas[0x0f] = (byte) ((length >>> 8) & 0xff);
		datas[0x10] = (byte) (length & 0xff);
		return datas;
	}

	double presize = 0;
	long totleSize = 0;

	void copy(Queue<FlvInputStream> queue, DataOutputStream out)
			throws IOException {
		FlvInputStream stream = queue.poll();
		List<Long> times = new LinkedList<Long>();
		List<Long> pos = new LinkedList<Long>();
		int csize = stream.readInt();
		out.writeInt(csize);
		int pretime = 0;
		totleSize += 4;
		long counter = 0;
		// TODO COPY
		main: while (true) {
			tag: while (true) {
				int type = stream.read();
				if (-1 == type) {
					if (queue.isEmpty()) {
						break main;
					}
					stream = queue.poll();
					continue main;
				}
				assert (type >>> 1) == 4;
				out.write(type);
				int dataSize = DataStreamUtils.copyAndReadUInt24(stream, out);
				presize = dataSize + FlvInputStream.TAG_INCREASE;
				long time = DataStreamUtils.readTime(stream) + pretime;
				if (counter + 3 < time) {
					counter = time;
					times.add(counter);
					pos.add(totleSize);
				}
				DataStreamUtils.writeTime(out, time);
				DataStreamUtils.copyAndReadUInt24(stream, out);
				DataStreamUtils.copy(stream, out, dataSize);
			}
		
		}
		totleSize += csize;
	}

	boolean copyTag(FlvInputStream fis, DataOutputStream out, long pretime,
			List<Double> times, List<Double> pos) throws IOException {
		int type = fis.read();
		if (-1 == type) {
			return false;
		}
		assert (type >>> 1) == 4;
		out.write(type);
		int dataSize = DataStreamUtils.copyAndReadUInt24(fis, out);
		presize = dataSize + FlvInputStream.TAG_INCREASE;
		long time = DataStreamUtils.readTime(fis) + pretime;

		DataStreamUtils.writeTime(out, time);
		DataStreamUtils.copyAndReadUInt24(fis, out);
		byte[] data = new byte[dataSize];
		fis.read(data);
		out.write(data);
		return true;
	}

	public double copyTags(FlvInputStream fis, DataOutputStream out,
			double pretime, double presize) throws IOException {
		long pt = (long) (pretime * 100);
		while (true) {
			int pr = fis.readInt();
			if (-1 == presize) {
				presize = pr;
			}
			out.writeInt((int) presize);

			int type = fis.read();
			if (-1 == type) {
				break;
			}
			assert type / 2 == 4;
			out.write(type);
			int dataSize = DataStreamUtils.copyAndReadUInt24(fis, out);
			presize = dataSize + FlvInputStream.TAG_INCREASE;
			long time = DataStreamUtils.readTime(fis) + pt;
			DataStreamUtils.writeTime(out, time);

			DataStreamUtils.copyAndReadUInt24(fis, out);

			byte[] data = new byte[dataSize];
			fis.read(data);
			out.write(data);
		}
		out.flush();
		return presize;
	}

	public static void offset(File file, InputStream is) throws IOException {
		int capacity = is.available();
		byte[] buffer = new byte[capacity];
		long old = file.length();
		RandomAccessFile raf = new RandomAccessFile(file, "rw");
		long mod = (old - 1) / capacity;
		for (int i = 0; i < mod; i++) {
			old -= capacity;
			raf.seek(old);
			raf.read(buffer);
			raf.write(buffer);
		}
		raf.seek(0);
		raf.read(buffer, 0, (int) old);
		raf.seek(capacity);
		raf.write(buffer, 0, (int) old);
		is.read(buffer);
		raf.seek(0);
		raf.write(buffer);
	}
}
