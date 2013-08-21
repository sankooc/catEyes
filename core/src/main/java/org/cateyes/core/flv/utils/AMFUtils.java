package org.cateyes.core.flv.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cateyes.core.flv.FlvMetadata;
/**
 * @author sankooc
 */
public class AMFUtils {
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void writeData(Object obj, DataOutputStream os)
			throws IOException {
		if (null == obj) {
			return;
		}
		if (obj instanceof String) {
			os.write(2);
			writeAMFString((String) obj, os);
		} else if (obj instanceof Boolean) {
			os.write(1);
			writeAMFBoolean((Boolean) obj, os);
		} else if (obj instanceof Number) {
			os.write(0);
			writeAMFNumber((Number) obj, os);
		} else if (obj instanceof Map) {
			os.write(3);
			writeAMFMap((Map) obj, os);
		} else if (obj instanceof List) {
			os.write(10);
			writeAMFSTRICTList((List) obj, os);
		} else if (obj instanceof FlvMetadata) {
			os.write(8);
			writeEmcaArray((FlvMetadata) obj, os);
		}
	}

	protected static void writeAMFSTRICTList(List<?> obj, DataOutputStream os)
			throws IOException {
		int length = obj.size();
		os.writeInt(length);
		for (Object oob : obj) {
			writeData(oob, os);
		}
		// for (int i = 0; i < obj.size(); i++) {
		// writeData(obj.get(i), os);
		// }
	}

	protected static void writeAMFMap(Map<String, ?> obj, DataOutputStream os)
			throws IOException {

		// int size = obj.keySet().size();
		// for (int i = size; i > 0; i++) {
		//
		// }
		// writeAMFString("filepositions", os);
		// writeData(obj.get("filepositions"), os);
		// writeAMFString("times", os);
		// writeData(obj.get("times"), os);

		for (String key : obj.keySet()) {
			writeAMFString(key, os);
			writeData(obj.get(key), os);
		}
		os.write(new byte[] { 0, 0 });
		os.write(9);
	}

	protected static void writeAMFNumber(Number obj, DataOutputStream os)
			throws IOException {
		Double dob = (Double) obj;
		os.writeDouble(dob);
	}

	protected static void writeAMFBoolean(Boolean obj, DataOutputStream os)
			throws IOException {
		if (obj) {
			os.write(1);
		} else {
			os.write(0);
		}
	}

	protected static void writeEmcaArray(FlvMetadata data, DataOutputStream os)
			throws IOException {
		os.writeInt(11);
		Field[] fs = FlvMetadata.class.getDeclaredFields();
		if (null != fs && fs.length > 0) {
			for (Field f : fs) {
				writeAMFString(f.getName(), os);
				f.setAccessible(true);
				try {
					writeData(f.get(data), os);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		os.write(new byte[] { 0, 0, 9 });
	}

	protected static void writeAMFString(String obj, DataOutputStream os)
			throws IOException {
		int length = obj.length();
		os.writeShort(length);
		os.write(obj.getBytes());
	}

	public static Object readAMFData(DataInputStream input, int type)
			throws IOException {
		if (type == -1) {
			type = input.readUnsignedByte();
		}
		switch (type) {
		case 0:
			return input.readDouble();
		case 1:
			return input.readUnsignedByte() == 1;
		case 2:
			return readAMFString(input);
		case 3:
			return readAMFObject(input);
		case 8:
			return readAMFEcmaArray(input);
		case 10:
			return readAMFStrictArray(input);
		case 11:
			final Date date = new Date((long) input.readDouble());
			input.readShort(); // time zone
			return date;
		case 13:
			return "UNDEFINED";
		default:
			return null;
		}
	}

	protected static Object readAMFStrictArray(DataInputStream input)
			throws IOException {
		long count = DataStreamUtils.readUInt32(input);
		ArrayList<Object> list = new ArrayList<Object>();
		for (int i = 0; i < count; i++) {
			list.add(readAMFData(input, -1));
		}
		return list;
	}

	protected static String readAMFString(DataInputStream input)
			throws IOException {
		int size = input.readUnsignedShort();
		byte[] chars = new byte[size];
		input.readFully(chars);
		return new String(chars);
	}

	protected static Object readAMFObject(DataInputStream input)
			throws IOException {
		HashMap<String, Object> array = new HashMap<String, Object>();
		while (true) {
			String key = readAMFString(input);
			int dataType = input.read();
			if (dataType == 9) { // object end marker
				break;
			}
			array.put(key, readAMFData(input, dataType));
		}
		return array;
	}

	protected static Object readAMFEcmaArray(DataInputStream input)
			throws IOException {
		long size = DataStreamUtils.readUInt32(input);
		HashMap<String, Object> array = new HashMap<String, Object>();
		for (int i = 0; i < size; i++) {
			String key = readAMFString(input);
			int dataType = input.read();
			array.put(key, readAMFData(input, dataType));
		}
		return array;
	}
}
