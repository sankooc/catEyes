/**
 * 
 */
package org.cateyes.core.flv;

/**
 * @author sankooc
 * 
 */
public interface FlvConstants {
	int TYPE_METADATA = 0x12;
	int TYPE_VIDEODATA = 0x09;
	int TYPE_AUDIODATA = 0x08;
	byte MASK_AUDIO = 1;
	byte MASK_VIDEO = 4;
	int TAG_INCREASE = 11;
	int META_INCREASE = 18;
	int FLV_HEADLENGH = 9;
	int TAG_SPLIT = 4;

	String time = "times";
	String filepositions = "filepositions";
}
