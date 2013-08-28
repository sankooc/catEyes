//package org.cateyes.core.flv;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.DataInputStream;
//import java.io.DataOutputStream;
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//
//import org.cateyes.core.media.utils.AMFUtils;
//import org.cateyes.core.media.utils.DataStreamUtils;
///**
// * @author sankooc
// */
//public class FLVParser {
//
//	protected static boolean checkSignature(DataInputStream fis)
//			throws IOException {
//		return fis.read() == 'F' && fis.read() == 'L' && fis.read() == 'V';
//	}
//
//	public static InputStream createMetaData(FlvMetadata metadata)
//			throws IOException {
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		DataOutputStream os = new DataOutputStream(baos);
//		os.write("FLV".getBytes());
//		os.write(1);// version
//		os.write(5);// type flag =5
//		os.write(new byte[] { 0, 0, 0, 9, 0, 0, 0, 0, 18, 0, 0, 0, 0, 0, 0, 0,
//				0, 0, 0 });
//		String pi = "onMetaData";
//		AMFUtils.writeData(pi, os);
//		AMFUtils.writeData(metadata, os);
//		byte[] datas = baos.toByteArray();
//		int length = datas.length - 0x18;
//		datas[0x0e] = (byte) (length >>> 16);
//		datas[0x0f] = (byte) ((length >>> 8) & 0xff);
//		datas[0x10] = (byte) (length & 0xff);
//		return new ByteArrayInputStream(datas);
//	}
//
//	public void build(File target, FlvMetadata metadata) throws IOException {
//		// if (!target.getParentFile().exists()) {
//		// target.getParentFile().mkdirs();
//		// }
//		// ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		// FileOutputStream fos = new FileOutputStream(target);
//		// DataOutputStream os = new DataOutputStream(baos);
//		// os.write("FLV".getBytes());
//		// os.write(1);// version
//		// os.write(5);// type flag =5
//		// os.write(new byte[] { 0, 0, 0, 9, 0, 0, 0, 0, 18, 0, 0, 0, 0, 0, 0,
//		// 0,
//		// 0, 0, 0 });
//		// String pi = "onMetaData";
//		// AMFUtils.writeData(pi, os);
//		// AMFUtils.writeData(metadata, os);
//		// byte[] datas = baos.toByteArray();
//		// int length = datas.length - 0x17;
//		// datas[0x0e] = (byte) (length >>> 16);
//		// datas[0x0f] = (byte) ((length >>> 8) & 0xff);
//		// datas[0x10] = (byte) (length & 0xff);
//		// fos.write(datas);
//		// fos.flush();
//		// fos.close();
//	}
//
//	@SuppressWarnings("unchecked")
//	public FlvMetadata parseMetaData(InputStream stream) throws Exception {
//		DataInputStream datainput = new DataInputStream(stream);
//		if (!checkSignature(datainput)) {
//			throw new Exception("FLV signature not detected");
//		}
//		// header
//		int version = datainput.readUnsignedByte();
//		if (version != 1) {
//			// should be 1, perhaps this is not flv?
//			throw new Exception("Unpexpected FLV version: " + version);
//		}
//
//		int typeFlags = datainput.readUnsignedByte();// TODO set metadata
//														// contain video,audio
//		long len = DataStreamUtils.readUInt32(datainput);
//		if (len != 9) {
//			throw new Exception("Unpexpected FLV header length: " + len);
//		}
//
//		long sizePrev = DataStreamUtils.readUInt32(datainput);
//		if (sizePrev != 0) {
//			throw new Exception("Unpexpected FLV first previous block size: "
//					+ sizePrev);
//		}
//
//		FlvMetadata metadata = null;
//		int type = datainput.read();
//		if (type != 18) {
//			throw new Exception("no metadata");
//		}
//		tagLen = DataStreamUtils.readUInt24(datainput); // body length
//		DataStreamUtils.readUInt32(datainput); // timestamp
//		DataStreamUtils.readUInt24(datainput); // streamid
//		byte[] metaBytes = new byte[tagLen];
//		for (int readCount = 0; readCount < tagLen;) {
//			int r = stream.read(metaBytes, readCount, tagLen - readCount);
//			if (r != -1) {
//				readCount += r;
//			} else {
//				break;
//			}
//		}
//		ByteArrayInputStream is = new ByteArrayInputStream(metaBytes);
//		DataInputStream dis = new DataInputStream(is);
//		Object data = null;
//		for (int i = 0; i < 2; i++) {
//			data = AMFUtils.readAMFData(dis, -1);
//		}
//		if (data instanceof Map) {
//			metadata = new FlvMetadata(null);
//			Map<String, Object> extractedMetadata = (Map<String, Object>) data;
//			for (Entry<String, Object> entry : extractedMetadata.entrySet()) {
//				String key = entry.getKey();
//				// TODO set keyframes
//				if ("keyframes".equals(key)) {
//					Object value = entry.getValue();
//					if (value instanceof Map) {
//						metadata.setKeyframes((Map<String, List<Double>>) value);
//					}
//				} else if ("duration".equals(key)) {
//					Object value = entry.getValue();
//					if (value instanceof Double) {
//						metadata.setDuration((Double) value);
//					}
//				}
//			}
//		}
//		return metadata;
//	}
//
//	int tagLen;
//
//	public void copyTags(InputStream in, OutputStream out, long time)
//			throws Exception {
//		copyTags(new DataInputStream(in), new DataOutputStream(out), time);
//	}
//
//	@SuppressWarnings("unchecked")
//	public void copyTags(DataInputStream datainput, DataOutputStream out,
//			long time) throws Exception {
//		int sizePrev = 0;
//		int datalen = tagLen;
//		int videoInx = 0;
//		int audioInx = 0;
//		try {
//			while (true) {
//				sizePrev = (int) DataStreamUtils.copyAndReadUInt32(datainput,
//						out);
//				if (sizePrev != datalen + 11) {
//					System.err.println("length err");
//					break;
//				}
//				int type = datainput.read();
//				if (type == -1) {
//					return;
//				}
//				if (null != out) {
//					out.write(type);
//				}
//				datalen = DataStreamUtils.copyAndReadUInt24(datainput, out);
//				// DataStreamUtils.copyAndReadUInt32(datainput, out); //
//				// timestamp
//				long cur = DataStreamUtils.readTime(datainput);
//				DataStreamUtils.writeTime(out, cur + time);
//				
//				DataStreamUtils.copyAndReadUInt24(datainput, out); // streamid
//
//				byte[] data = DataStreamUtils.copy(datainput, out, datalen);// tags
//				if (type == 9) {
//					
//					videoInx++;
//				} else if (type == 8) {
////					audioInx++;
//				}
//
//			}
//		} finally {
//			System.out.println("video " + videoInx);
////			System.out.println("audio " + audioInx);
//		}
//	}
//}
