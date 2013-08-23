package org.cateyes.core.flv;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.cateyes.core.flv.utils.DataStreamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author sankooc
 */
public class FlvInputStream extends DataInputStream {
	// int offSet;
	static Logger logger = LoggerFactory.getLogger(FlvInputStream.class);
	long avi;
	int curser = 0;

	public FlvInputStream(File file) throws IOException {
		super(new FileInputStream(file));
		avi = file.length();
		if (read() == 'F' && read() == 'L' && read() == 'V') {
			readUnsignedByte();
			readUnsignedByte();
			if (9 == readInt()) {
				curser = 9;
				return;
			}
		}
		throw new IOException("wrong source");
	}

	public FlvInputStream(InputStream in) throws IOException {
		super(in);
		if (read() == 'F' && read() == 'L' && read() == 'V') {
			readUnsignedByte();
			readUnsignedByte();
			if (9 == readInt()) {
				curser = 9;
				return;
			}
		}
		throw new IOException("wrong source");
	}

	public double copyTag(DataOutputStream out, double pretime, double presize)
			throws IOException {
		long pt = (long) (pretime * 100);
		while (true) {
			int pr = readInt();
			if (-1 == presize) {
				presize = pr;
			}
			out.writeInt((int) presize);

			int type = read();
			if (-1 == type) {
				break;
			}
			out.write(type);
			int dataSize = DataStreamUtils.copyAndReadUInt24(this, out);
			presize = dataSize + TAG_INCREASE;

			long time = DataStreamUtils.readTime(this) + pt;
			DataStreamUtils.writeTime(out, time);

			DataStreamUtils.copyAndReadUInt24(this, out);

			byte[] data = new byte[dataSize];
			read(data);
			out.write(data);
		}
		out.flush();
		return presize;
	}

	public static final int TAG_INCREASE = 11;

	public long getCursor() {
		return curser;
	}

	public FLVTag readTag() throws IOException {
		int leave = available();
		if (leave < 4) {
			return null;
		}
		long presize = DataStreamUtils.readUInt32(this);
		curser += 4;
		long pos = curser;
		int type = read();
		curser++;
		if (type == -1) {
			return null;
		}
		int dataSize = DataStreamUtils.readUInt24(this);
		long time = DataStreamUtils.readTime(this);
		curser += 7;
		// System.out.println("time:" + time);
		DataStreamUtils.readUInt24(this);// always 0
		curser += 3;
		byte[] data = new byte[dataSize];
		read(data);
		curser += dataSize;
		return new FLVTag(presize, type, time, data, pos);
	}

	@SuppressWarnings("unchecked")
	public EcmaArray<String, Object> readMetadata() throws IOException {
		DataStreamUtils.readUInt32(this);
		if (0x12 != read()) {
			throw new IOException("wrong type");
		}
		int dataSize = DataStreamUtils.readUInt24(this); // body length
		DataStreamUtils.readUInt32(this); // timestamp
		DataStreamUtils.readUInt24(this); // streamid
		curser += 15;
		byte[] data = new byte[dataSize];
		read(data);
		curser += dataSize;
		AMFInputStream ais = new AMFInputStream(new ByteArrayInputStream(data));
		Object obj = ais.getNextObject();
		return (EcmaArray<String, Object>) ais.getNextObject();
	}

	public FMetadata readMetadata2() throws IOException {
		EcmaArray<String, Object> mta = readMetadata();
		FMetadata metadata = new FMetadata(mta);
		metadata.setTotleSize(avi);
		return metadata;
	}

}
