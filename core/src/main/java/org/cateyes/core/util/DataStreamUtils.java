package org.cateyes.core.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class DataStreamUtils {
	public static long readUInt32(DataInputStream input) throws IOException {
		return input.readInt() & 0xFFFFFFFFL;
	}

	public static long copyAndReadUInt32(DataInputStream input,
			DataOutputStream output) throws IOException {
		int value = input.readInt();
		if (null != output) {
			output.writeInt(value);
		}
		return value & 0xFFFFFFFFL;
	}

	public static long readTime(DataInputStream input) throws IOException {
		int value = readUInt24(input);
		int ex = input.read();
		if (0 > ex) {
			value = value + (ex << 24);
		}
		return 0xffffffff & value;
	}

	public static void writeTime(DataOutputStream out, long time) throws IOException {
		out.write((int) ((time >>> 16) & 0xff));
		out.write((int) ((time >>> 8) & 0xff));
		out.write((int) (time & 0xff));
		out.write((int) ((time >>> 24) & 0xff));
	}

	public static int readUInt24(DataInputStream input) throws IOException {
		int uint = input.read() << 16;
		uint += input.read() << 8;
		uint += input.read();
		return uint;
	}

	public static byte[] copy(DataInputStream input, DataOutputStream output,
			int length) throws IOException {
		byte[] data = new byte[length];
		input.read(data);
		output.write(data);
		return data;
	}

	public static int copy24(DataInputStream input, DataOutputStream output,
			int offset) throws IOException {
		int value = input.read();
		if (null != output) {
			output.write(value);
		}
		return value << offset;
	}

	public static int copyAndReadUInt24(DataInputStream input,
			DataOutputStream output) throws IOException {
		return copy24(input, output, 16) + copy24(input, output, 8)
				+ copy24(input, output, 0);
	}
}
