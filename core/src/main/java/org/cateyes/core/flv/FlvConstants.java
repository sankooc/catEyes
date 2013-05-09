/**
 * 
 */
package org.cateyes.core.flv;

/**
 * @author sankooc
 *
 */
public class FlvConstants {
	protected static int TYPE_METADATA = 0x12;
	protected static int TYPE_VIDEODATA = 0x09;
	protected static int TYPE_AUDIODATA = 0x08;
	protected static byte MASK_AUDIO = 1;
	protected static byte MASK_VIDEO = 4;

	public static final String time = "times";
	public static final String filepositions = "filepositions";
}
