/**
 * 
 */
package org.cateyes.core.flv;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.cateyes.core.media.io.AMFOutputStream;

/**
 * @author sankooc
 * 
 */
public class FMetadata {

	final EcmaArray<String, Object> metadata;

	public void resetTimes(double offset) {
		List<Double> times  = getTimes();
		List<Double> tmp = new ArrayList<Double>(times.size());
		for (double time : times) {
			tmp.add(time + offset);
		}
		times.clear();
		times.addAll(tmp);
	}

	
	public void removeHeader(){
		 getPosition().remove(0);
		 getTimes().remove(0);
	}
	public void resetPos(double offset) {
		List<Double> plist = getPosition();
		List<Double> tmp = new LinkedList<Double>();
		for (double p : plist) {
			tmp.add(p - offset);
		}
		plist.clear();
		plist.addAll(tmp);
	}

	// public double decreaseH1(){
	// // int count = getPosition().size();
	// // double offset1 = count*18+9+289;
	// List<Double> plist = getPosition();
	// double offset2 = plist.get(0) -4;
	// // tagsize =filesize- offset2;
	// // List<Double> tmp = new LinkedList<Double>();
	// // for(double p : plist){
	// // tmp.add(p-offset2);
	// // }
	// // plist.clear();
	// // plist.addAll(tmp);
	// return offset2;
	// }

	// public void decreaseH2(){
	// List<Double> plist = getPosition();
	// plist.remove(0);
	// getTimes().remove(0);
	// double f = plist.get(0);
	// double offset = f-4;
	// tagsize =filesize- offset;
	// List<Double> tmp = new LinkedList<Double>();
	// for(double p : plist){
	// tmp.add(p-offset);
	// }
	// plist.clear();
	// plist.addAll(tmp);
	// }

	public FMetadata(EcmaArray<String, Object> arr) {
		metadata = arr;
	}

	public double getduration() {
		return (Double) metadata.get("duration");
	}

	public void setDuration(double duration){
		metadata.put("duration", duration);
	}
	
	public void append(FMetadata meta) {

		List<Double> list = getTimes();

		List<Double> tlist = meta.getTimes();
		for (double t : tlist) {
			list.add(t);
		}

		list = getPosition();
		List<Double> plist = meta.getPosition();
		for (double p : plist) {
			list.add(p);
		}
	}

//	public long getTotleSize() {
//		return totleSize;
//	}
//
//	public void setTotleSize(long totleSize) {
//		this.totleSize = totleSize;
//	}

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
	@SuppressWarnings("unchecked")
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
	@SuppressWarnings("unchecked")
	public List<Double> getPosition(EcmaArray<String, Object> arr) {
		Map<String, Object> cMap = (Map<String, Object>) arr.get("keyframes");
		return getList(FlvConstants.filepositions, cMap);
	}

	@SuppressWarnings("unchecked")
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
//			arr.get(key).equals(metadata.get(key));
		}

	}

	public FLVTag toTag() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream os = new DataOutputStream(baos);
		AMFOutputStream aos = new AMFOutputStream(os);
		aos.write("onMetaData");
		aos.write(metadata);
		baos.flush();
		byte[] datas = baos.toByteArray();
		return new FLVTag(0,18,0,datas,0);
	}
	
	@Deprecated
	public byte[] toBytes2() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream os = new DataOutputStream(baos);
		os.write(new byte[] { 0, 0, 0, 0, 18, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });
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

	@Deprecated
	public byte[] toBytes() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream os = new DataOutputStream(baos);
		os.write("FLV".getBytes());
		os.write(1);// version
		os.write(5);// type flag =5
		os.write(new byte[] { 0, 0, 0, 9, 0, 0, 0, 0, 18, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });
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

//	double presize = 0;
//	long totleSize = 0;

//	public void copy(Queue<FlvInputStream> queue, DataOutputStream out) throws IOException {
//
//		long[] sign = new long[] { 0x000000, 0x000000, 0x000002, 0x000007, 0x000009, 0x00000c, 0x00000f, 0x000014, 0x000016, 0x00001a, 0x00001d, 0x000020, 0x000023, 0x000027,
//				0x00002c, 0x00002f, 0x000032, 0x000035, 0x000036, 0x000039, 0x00003b, 0x00003d, 0x00003f, 0x000044, 0x000047, 0x00004b, 0x000051, 0x000053, 0x000055, 0x000057,
//				0x000059, 0x00005c, 0x00005e, 0x000063, 0x000064, 0x000065, 0x000068, 0x00006b, 0x00006d, 0x00006e, 0x000072, 0x000074, 0x000076, 0x000078, 0x00007a, 0x00007d,
//				0x00007f, 0x000082, 0x000085, 0x000087, 0x00008a, 0x00008e, 0x000090, 0x000092, 0x000094, 0x000097, 0x000099, 0x00009b, 0x00009d, 0x0000a0, 0x0000a3, 0x0000a6,
//				0x0000ab, 0x0000ad, 0x0000b0, 0x0000b2, 0x0000b7, 0x0000b9, 0x0000bc, 0x0000be, 0x0000c0, 0x0000c3, 0x0000c5, 0x0000c9, 0x0000cf, 0x0000d3, 0x0000d5, 0x0000d7,
//				0x0000db, 0x0000de, 0x0000e4, 0x0000e4, 0x0000e7, 0x0000ed, 0x0000f3, 0x0000f9, 0x0000fe, 0x000102, 0x000105, 0x000107, 0x00010a, 0x00010d, 0x00010f, 0x000112,
//				0x000115, 0x000118, 0x00011c, 0x00011e, 0x000120, 0x000124, 0x000126, 0x000129, 0x00012b, 0x00012c, 0x00012e, 0x000130, 0x000134, 0x000136, 0x000137, 0x00013d,
//				0x00013e, 0x000142, 0x000146, 0x000148, 0x00014b, 0x00014d, 0x000150, 0x000153, 0x000156, 0x000158, 0x00015a, 0x00015c, 0x00015e, 0x000160, 0x000162, 0x000163,
//				0x000166, 0x000168, 0x00016c };
//		LinkedList<Long> list = new LinkedList<Long>();
//		for (long l : sign) {
//			list.add(l);
//		}
//		FlvInputStream stream = queue.poll();
//		List<Long> times = new LinkedList<Long>();
//		List<Long> pos = new LinkedList<Long>();
//		stream.readInt();
//		int pretime = 0;
//		totleSize += 4;
//		long counter = 0;
//		int csize = -1;
//		totleSize += toBytes().length;
//		int test = 0;
//
//		while (true) {
//			int type = stream.read();
//			if (-1 == type) {
//				if (queue.isEmpty()) {
//					break;
//				}
//				stream = queue.poll();
//				continue;
//			}
////			assert (type >>> 1) == 4;
//			out.write(type);
//			int dataSize = DataStreamUtils.copyAndReadUInt24(stream, out);
//			presize = dataSize + FlvConstants.TAG_INCREASE;
//			long time = DataStreamUtils.readTime(stream) + pretime;
//			if (list.isEmpty()) {
//
//			} else {
//				if (list.peek().equals(time)) {
//					list.poll();
//					System.out.println(test);
//				}
//			}
//			// if (counter <= time) {
//			// System.out.printf("%06x : %06x",counter,totleSize);
//			// System.out.println();
//			// times.add(counter);
//			// pos.add(totleSize);
//			// counter = time+3;
//			// }
//			DataStreamUtils.writeTime(out, time);
//			DataStreamUtils.copyAndReadUInt24(stream, out);
//			DataStreamUtils.copy(stream, out, dataSize);
//			csize = stream.readInt();
//			out.writeInt(csize);
//			totleSize += csize;
//			totleSize += 4;
//			test++;
//		}
//
//	}
}
