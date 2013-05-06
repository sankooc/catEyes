package org.cateyes.core.youku;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.cateyes.core.ApacheConnector;
import org.cateyes.core.Resolver;
import org.cateyes.core.VideoConstants.VideoType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YoukuResolver implements Resolver {
	static final Logger logger = LoggerFactory.getLogger(YoukuResolver.class);

	public void resolv(URI url) {

	}

	// public String getYoukuSid(URI url) {
	// String str = url.toString();
	// if(){
	//
	// }
	// return str;
	// }

	ApacheConnector connetor = new ApacheConnector();

	public static final String YOUKU_LIST = "http://v.youku.com/player/getPlayList/VideoIDS/";

	public static final String REAL_FORMAT = "http://f.youku.com/player/getFlvPath/sid/%s_%s/st/%s/fileid/%s%s%s?K=%s&ts=%s";

	 public void resolv(String yid, VideoType type) {
	 String[] uris = getReadUriFromYID(yid,type);
	 if (null == uris || uris.length == 0) {
	 logger.error("no uri");
	 return;
	 }
	 for (String str : uris) {
	 logger.info("download from {} ", str);
	
	 }
	
	 }

	protected String[] getReadUriFromYID(String yid, VideoType type) {
		String flcJsonURI = YOUKU_LIST + yid;
		logger.debug("youku paly-list {}", flcJsonURI);
		byte[] bts = connetor.doGet(URI.create(flcJsonURI));
		if (null == bts) {
			logger.error("cannot get play-list json data");
			return null;
		}
		String json = new String(bts);
		if (logger.isDebugEnabled()) {
			logger.debug("json data : {}", json);
		}
		JSONObject object = JSONObject.fromObject(json);
		JSONArray dataObj = object.getJSONArray("data");
		final JSONObject data = dataObj.getJSONObject(0);

		final JSONObject segs = data.getJSONObject("segs");
		Set<?> videoTypes = segs.keySet();
		logger.info("{} kinds types ", videoTypes.size());
		if (null == type) {
			type = VideoType.FLV;
		}
		String sType = type.name().toLowerCase();
		if (videoTypes.contains(sType)) {

		} else {
			logger.error("cannot get type {}", sType);
		}
		return getReadUriFromYID(data, sType);
	}

	private String[] getReadUriFromYID(JSONObject data, String sType) {
		int seed = getSeed(data);
		char[] maxStr = getMixString(seed);
		JSONObject sf = data.getJSONObject("streamfileids");
		String token = (String) sf.get("flv");
		String[] vid = token.split("\\*");
		String vv = getVid(maxStr, vid);
		final JSONObject segs = data.getJSONObject("segs");
		JSONArray flvSegs = segs.getJSONArray(sType);
		String[] uris = new String[flvSegs.size()];
		for (int i = 0; i < flvSegs.size(); i++) {
			JSONObject fragment = flvSegs.getJSONObject(i);
			String numSt = (String) fragment.get("no");
			int in = Integer.parseInt(numSt);
			String no = String.format("%02x", in);
			String urlString = String.format(REAL_FORMAT, getSerialId(), no,
					sType, vv.substring(0, 8), no.toUpperCase(),
					vv.substring(10), fragment.get("k"),
					fragment.get("seconds"));
			logger.info("read address {} ", urlString);
			uris[i] = urlString;
		}
		return uris;
	}

	public void resolvSid(String sid) {
		String flcJsonURI = YOUKU_LIST + sid;
		byte[] bts = connetor.doGet(URI.create(flcJsonURI));
		JSONObject object = JSONObject.fromObject(new String(bts));
		JSONArray dataObj = object.getJSONArray("data");
		JSONObject data = dataObj.getJSONObject(0);
		JSONObject segs = data.getJSONObject("segs");

		JSONArray flvSegs = segs.getJSONArray("flv");
		int seed = getSeed(data);
		char[] maxStr = getMixString(seed);
		JSONObject sf = data.getJSONObject("streamfileids");
		String token = (String) sf.get("flv");
		String[] vid = token.split("\\*");
		String vv = getVid(maxStr, vid);
		JSONObject fragment = flvSegs.getJSONObject(0);

		String numSt = (String) fragment.get("no");
		int in = Integer.parseInt(numSt);
		String no = String.format("%02x", in);
		String urlString = String.format(REAL_FORMAT, getSerialId(), no, "flv",
				vv.substring(0, 8), no.toUpperCase(), vv.substring(10),
				fragment.get("k"), fragment.get("seconds"));

	}

	protected int getSeed(JSONObject obj) {
		Integer value = (Integer) obj.get("seed");
		return value;
	}

	protected String genSid() {
		int i1 = (int) (1000 + Math.floor(Math.random() * 999));
		int i2 = (int) (1000 + Math.floor(Math.random() * 9000));
		return System.currentTimeMillis() + "" + i1 + "" + i2;
	}

	protected static String TOKEN = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ/\\:._-1234567890";

	/**
	 * 13677 42260 459 1184 8692 13677 45216 107 1117 1572 13677 45504 891 1989
	 * 8837
	 * 
	 * @return
	 */
	Random counter = new Random();

	String getSerialId() {
		return "" + System.currentTimeMillis() + (1000 + counter.nextInt(999))
				+ (1000 + counter.nextInt(9000));
	}

	String getVid(final char[] maxStr, String[] idx) {
		if (null == idx || idx.length == 0) {
			return null;
		}
		char[] chs = new char[idx.length];
		for (int i = 0; i < idx.length; i++) {
			try {
				Integer index = Integer.parseInt(idx[i]);
				chs[i] = maxStr[index];
			} catch (NumberFormatException e) {
				return null;
			}
		}
		return new String(chs);
	}

	char[] getMixString(int seed) {
		// List<Character> queue =
		// Arrays.asList(ArrayUtils.toObject(TOKEN.toCharArray()));
		List<Character> queue = new LinkedList<Character>();
		for (char ch : TOKEN.toCharArray()) {
			queue.add(ch);
		}
		final int length = queue.size();
		char[] ret = new char[length];
		for (int i = 0; i < length; ++i) {
			seed = (seed * 211 + 30031) & 0xffff;
			int index = seed * queue.size() >> 16;
			ret[i] = queue.remove(index);
		}
		return ret;
		// return new String(ret);
	}
}
