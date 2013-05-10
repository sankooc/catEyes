/**
 * 
 */
package org.cateyes.core.flv;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author sankooc
 * 
 */
public class FMetadata {
	final EcmaArray<String, Object> metadata;

	public FMetadata(EcmaArray<String, Object> arr) {
		assert null != arr;
		metadata = arr;
		System.out.println("read duration:"+(Double) arr.get("duration"));
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
		System.out.println("sum duration:"+value);
	}

	public List<Double> getList(String key, Map<String, Object> keyframes) {
		List<Double> list = (List<Double>) keyframes.get(key);
		return list;
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
//		Map<String, Object> cMap = (Map<String, Object>) arr.get("keyframes");

//		Double inc = (Double) arr.get("duration");
//		Double value = (Double) metadata.get("duration");

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
}
