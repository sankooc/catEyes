package org.cateyes.core.flv;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.cateyes.core.flv.util.DataStreamUtils;
/**
 * @author sankooc
 */
public class AMFInputStream extends DataInputStream {

	public AMFInputStream(InputStream in) {
		super(in);
	}

	public Object getNextObject() throws IOException {
		int type = readUnsignedByte();
		switch (type) {
		case 0:
			return readDouble();
		case 1:
			return readUnsignedByte() == 1;
		case 2:
			return readAMFString();
		case 3:
			return readAMFObject();
		case 8:
			return readAMFEcmaArray();
		case 9:
			return null;
		case 10:
			return readAMFStrictArray();
		case 11:
			final Date date = new Date((long) readDouble());
			readShort(); // time zone
			return date;
		case 13:
			return "UNDEFINED";
		default:
			return null;
		}

	}

	private List<?> readAMFStrictArray() throws IOException {
		long count = DataStreamUtils.readUInt32(this);
		ArrayList<Object> list = new ArrayList<Object>((int) count);
		for (int i = 0; i < count; i++) {
			list.add(getNextObject());
		}
		return list;
	}

	private EcmaArray<String, ?> readAMFEcmaArray() throws IOException {
		long size = DataStreamUtils.readUInt32(this);
		EcmaArray<String, Object> array = new EcmaArray<String, Object>(
				(int) size);
		for (int i = 0; i < size; i++) {
			String key = readAMFString();
			array.put(key, getNextObject());
		}
		return array;
	}

	private HashMap<String, ?> readAMFObject() throws IOException {
		HashMap<String, Object> array = new HashMap<String, Object>();
		while (true) {
			String key = readAMFString();
			Object value = getNextObject();
			if (null == value) {
				break;
			}
			array.put(key, value);
		}
		return array;
	}

	private String readAMFString() throws IOException {
		int size = readUnsignedShort();
		byte[] chars = new byte[size];
		readFully(chars);
		return new String(chars);
	}
}
