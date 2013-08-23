package org.cateyes.core.flv;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.cateyes.core.flv.utils.DataStreamUtils;

public class FlvOutputStream extends DataOutputStream {

	File file;
	long counter;
	boolean log;

	public FlvOutputStream(File target) throws IOException {
		super(new FileOutputStream(target));
		this.file = target;
		write('F');
		write('L');
		write('V');
		write(1);
		write(5);
		writeInt(9);
		counter = 9;
	}
	long presize = 0;

	// ms
	public void writeTags(DataInputStream stream, long pretime)
			throws Exception {
		while (true) {
			if (stream.available() < 4) {
				break;
			}
			stream.readInt();

			writeInt((int) presize);

			int type = stream.read();
			if (-1 == type) {
				break;
			}
			if (type / 2 != 4) {
				throw new Exception("wrong tag type");
			}
			write(type); // type 1

			int dataSize = DataStreamUtils.copyAndReadUInt24(stream, this); // size
																			// 3
			presize = dataSize + 11; // 1+3+4+3

			long time = DataStreamUtils.readTime(stream) + pretime;
			DataStreamUtils.writeTime(this, time); // time 4

			DataStreamUtils.copyAndReadUInt24(stream, this);// stream id 3

			byte[] data = new byte[dataSize];
			stream.read(data);
			write(data);
		}

	}

	public final void writeUI24(int v) throws IOException {
		write((v >>> 16) & 0xFF);
		write((v >>> 8) & 0xFF);
		write((v >>> 0) & 0xFF);
	}

	public void writeTime(long time) throws IOException {
		writeUI24((int) time);
		write((int) ((time >>> 24) & 0xff));
	}

	public void writeTag(FLVTag tag) throws IOException {
		writeInt((int) presize);// presize
		write(tag.getType());
		int datasize = tag.getData().length;
		writeUI24(datasize);
		presize = datasize + 11;
		writeTime(tag.getTime());
		writeUI24(0);
		write(tag.getData());
	}

	public static final int initMeta = 289;

}
