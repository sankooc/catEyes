package org.cateyes.core.media.io;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.cateyes.core.flv.EcmaArray;
/**
 * @author sankooc
 */
public class AMFOutputStream extends DataOutputStream {

	public AMFOutputStream(OutputStream out) throws IOException {
		super(out);
	}

	void writeAMFString(String str) throws IOException {
		int length = str.length();
		writeShort(length);
		write(str.getBytes());
	}

	void writeAMFObject(Map<String, ?> value) throws IOException {
		for (String key : value.keySet()) {
			writeAMFString(key);
			write(value.get(key));
		}
		write(new byte[] { 0, 0, 9 });
	}

	void writeAMFEcmaArray(EcmaArray<String, ?> value) throws IOException {
		writeInt(value.size());
		for (String key : value.keySet()) {
			writeAMFString(key);
			write(value.get(key));
		}
		write(new byte[] { 0, 0, 9 });
	}

	void writeAMFStrictArray(List<?> value) throws IOException {
		int length = value.size();
		writeInt(length);
		for (Object ele : value) {
			write(ele);
		}
	}

	void writeAMFBoolean(Boolean value) throws IOException {
		if (value) {
			write(1);
		} else {
			write(0);
		}
	}

	void writeAMFNumber(Number obj) throws IOException {
		Double dob = (Double) obj;
		writeDouble(dob);
	}

	@SuppressWarnings("unchecked")
	public void write(Object obj) throws IOException {
		if (null == obj) {
			return;
		}
		if (obj instanceof String) {
			write(2);
			writeAMFString((String) obj);
		} else if (obj instanceof EcmaArray) {
			write(8);
			writeAMFEcmaArray((EcmaArray<String, ?>) obj);
		} else if (obj instanceof Boolean) {
			write(1);
			writeAMFBoolean((Boolean) obj);
		} else if (obj instanceof Number) {
			write(0);
			writeAMFNumber((Number) obj);
		} else if (obj instanceof Map) {
			write(3);
			writeAMFObject((Map<String, ?>) obj);
		} else if (obj instanceof List) {
			write(10);
			writeAMFStrictArray((List<?>) obj);
		}
	}

}
