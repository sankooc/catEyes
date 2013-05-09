package org.cateyes.core.flv;

public class FLVTag {
	public enum TagType {
		VIDEO, AUDIO, SCRIPTDATA
	}

	private TagType type;
	private FLVTag preTag;
	private long length;
	private long time;

	public FLVTag(int type) {
		switch (type) {
		case 0x09:
			this.type = TagType.VIDEO;
			break;
		case 0x08:
			this.type = TagType.AUDIO;
			break;
		case 0x12:
			this.type = TagType.SCRIPTDATA;
			break;
		}
	}
}
