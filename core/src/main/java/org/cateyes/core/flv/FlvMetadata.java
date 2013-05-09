/**
 * 
 */
package org.cateyes.core.flv;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author sankooc
 * 
 */
public class FlvMetadata {
	private boolean hasKeyframes = true;
	private double duration;
	private double height = 288.0;
	private boolean hasMetadata = true;
	private boolean hasVideo = true;
	private boolean hasAudio = true;
	private double audiosamplerate = 22050.0;
	private double width = 512.0;
	private String metadatacreator = "123";
	private double framerate = 15.0;
	private Map<String, List<Double>> keyframes;

	public List<Double> getTimeFrame() {
		return getList(FlvConstants.time);
	}

	public List<Double> getPosFrame() {
		return getList(FlvConstants.filepositions);
	}

	public List<Double> getList(String key) {
		if (null == keyframes) {
			keyframes = new HashMap<String, List<Double>>();
		}
		List<Double> list = keyframes.get(key);
		if (null == list) {
			list = new LinkedList<Double>();
			keyframes.put(key, list);
		}
		return list;
	}

	public void addKeyframe(double t, double f) {
		List<Double> timeList = getList(FlvConstants.time);
		timeList.add(t);
		List<Double> fpList = getList(FlvConstants.filepositions);
		fpList.add(f);
	}

	public boolean isHasKeyframes() {
		return hasKeyframes;
	}

	public void setHasKeyframes(boolean hasKeyframes) {
		this.hasKeyframes = hasKeyframes;
	}

	public double getDuration() {
		return duration;
	}

	public void setDuration(double duration) {
		this.duration = duration;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public boolean isHasMetadata() {
		return hasMetadata;
	}

	public void setHasMetadata(boolean hasMetadata) {
		this.hasMetadata = hasMetadata;
	}

	public boolean isHasVideo() {
		return hasVideo;
	}

	public void setHasVideo(boolean hasVideo) {
		this.hasVideo = hasVideo;
	}

	public boolean isHasAudio() {
		return hasAudio;
	}

	public void setHasAudio(boolean hasAudio) {
		this.hasAudio = hasAudio;
	}

	public double getAudiosamplerate() {
		return audiosamplerate;
	}

	public void setAudiosamplerate(double audiosamplerate) {
		this.audiosamplerate = audiosamplerate;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public String getMetadatacreator() {
		return metadatacreator;
	}

	public void setMetadatacreator(String metadatacreator) {
		this.metadatacreator = metadatacreator;
	}

	public double getFramerate() {
		return framerate;
	}

	public void setFramerate(double framerate) {
		this.framerate = framerate;
	}

	public Map<String, List<Double>> getKeyframes() {
		return keyframes;
	}

	public void setKeyframes(Map<String, List<Double>> keyframes) {
		this.keyframes = keyframes;
	}
	public void update (FlvInputStream stream) throws IOException{
		stream.readMetadata();
	//TODO	
	}
	public void write(File file){
//		int offset = compute();
	}

	private int compute() {
		// TODO Auto-generated method stub
		return 0;
	}
}
