package org.cateyes.core.deprecated;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.cateyes.core.VideoConstants.VideoType;
import org.cateyes.core.conn.ApacheConnector;
import org.cateyes.core.volumn.Volumn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class YoukuResolver {
	static final Logger logger = LoggerFactory.getLogger(YoukuResolver.class);

	public void resolv(URI url) {

	}

	static final ApacheConnector connetor = ApacheConnector.getInstance();

	public static ApacheConnector getConnector() {
		return connetor;
	}

	final static Map<String, VideoType> typeMapper = new HashMap<String, VideoType>();
	final static Map<VideoType, String> segMapper = new HashMap<VideoType, String>();
	static {
		typeMapper.put("flv", VideoType.FLV);
		typeMapper.put("mp4", VideoType.MP4);
		typeMapper.put("hd2", VideoType.HD2);
		segMapper.put(VideoType.FLV, "flv");
		segMapper.put(VideoType.MP4, "mp4");
		segMapper.put(VideoType.HD2, "flv");

	}
	public static final String YOUKU_LIST = "http://v.youku.com/player/getPlayList/VideoIDS/";

	public static final String REAL_FORMAT = "http://f.youku.com/player/getFlvPath/sid/%s_%s/st/%s/fileid/%s%s%s?K=%s&ts=%s";

	public static void resolv(String yid, VideoType type) {
		String[] uris = getReadUriFromYID(yid, type);
		if (null == uris || uris.length == 0) {
			logger.error("no uri");
			return;
		}
		for (String str : uris) {
			logger.info("download from {} ", str);

		}

	}

	public static JSONObject getMetadata(String yid) {
		String flcJsonURI = YOUKU_LIST + yid;
		logger.debug("youku paly-list {}", flcJsonURI);
		byte[] bts = connetor.doGet(flcJsonURI);
		if (null == bts) {
			logger.error("cannot get play-list json data");
			return null;
		}
		String json = new String(bts);
		if (logger.isDebugEnabled()) {
			logger.debug("json data : {}", json);
		}
		return JSONObject.fromObject(json);
	}

	public static JSONObject getData(String yid) {
		JSONObject metadata = getMetadata(yid);
		return metadata.getJSONArray("data").getJSONObject(0);
	}

	public static int getFragmentCount(VideoType type, JSONObject data) {
		final JSONObject segs = data.getJSONObject("segs");
		String t = segMapper.get(type);
		if (null == t) {
			return 0;
		}
		JSONArray flvSegs = segs.getJSONArray(t);
		return flvSegs.size();
	}

	public static String[] getRealUri(JSONObject data ,VideoType type){
		final JSONObject segs = data.getJSONObject("segs");
		Collection<VideoType> tlist = getVideoType(data);
		
		logger.info("{} kinds types ", tlist.size());
		if (null == type) {
			type = VideoType.FLV;
		}
		if (tlist.contains(type)) {
		} else {
			logger.error("cannot get type {}", type.name());
			throw new RuntimeException("no such type " + type.name());
		}
		return getReadUriFromYID(data, type);
	}
	
	public static String[] getReadUriFromYID(String yid, VideoType type) {
		final JSONObject data = getData(yid);
//		final JSONObject segs = data.getJSONObject("segs");
		Collection<VideoType> tlist = getVideoType(data);
		
		logger.info("{} kinds types ", tlist.size());
		if (null == type) {
			type = VideoType.FLV;
		}
		if (tlist.contains(type)) {
		} else {
			logger.error("cannot get type {}", type.name());
			throw new RuntimeException("no such type " + type.name());
		}
		return getReadUriFromYID(data, type);
	}

	public static String[] getReadUriFromYID(JSONObject data, VideoType type) {
		int seed = getSeed(data);
		char[] maxStr = getMixString(seed);
		JSONObject sf = data.getJSONObject("streamfileids");
		String token = (String) sf.get("flv");
		String[] vid = token.split("\\*");
		String vv = getVid(maxStr, vid);
		
		String sType = segMapper.get(type);
		final JSONObject segs = data.getJSONObject("segs");
		JSONArray flvSegs = segs.getJSONArray(sType);
		String[] uris = new String[flvSegs.size()];
		for (int i = 0; i < flvSegs.size(); i++) {
			JSONObject fragment = flvSegs.getJSONObject(i);
			String numSt = fragment.get("no").toString();
			int in = Integer.parseInt(numSt);
			String no = String.format("%02x", in);
			String urlString = String.format(REAL_FORMAT, getSerialId(), no,
					sType, vv.substring(0, 8), no.toUpperCase(),
					vv.substring(10), fragment.get("k"),
					fragment.get("seconds"));
//			logger.info("read address {} ", urlString);
			uris[i] = urlString;
		}
		return uris;
	}

	public static Collection<VideoType> getVideoType(JSONObject data) {
		final JSONObject segs = data.getJSONObject("segs");
		Set<?> videoTypes = segs.keySet();
		if (null == videoTypes || videoTypes.isEmpty()) {
			throw new RuntimeException("no types");
		}
		Iterator<?> it = videoTypes.iterator();
		Collection<VideoType> types = new HashSet<VideoType>();
		for (int i = 0; i < videoTypes.size(); i++) {
			VideoType type = typeMapper.get(it.next());
			if (null == type) {
				continue;
			}
			types.add(type);
		}
		return types;
	}

	public static Volumn[] getVolumns(JSONObject data){
		JSONArray array = data.getJSONArray("list");
		if(null == array){
			return null;
		}
		
		
		
		return null;
	}
	
	
	public static void resolvSid(String sid) {
		String flcJsonURI = YOUKU_LIST + sid;
		byte[] bts = connetor.doGet(flcJsonURI);
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

	protected static int getSeed(JSONObject obj) {
		Integer value = (Integer) obj.get("seed");
		return value;
	}

	protected static String genSid() {
		int i1 = (int) (1000 + Math.floor(Math.random() * 999));
		int i2 = (int) (1000 + Math.floor(Math.random() * 9000));
		return System.currentTimeMillis() + "" + i1 + "" + i2;
	}

	protected static String TOKEN = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ/\\:._-1234567890";

	/**
	 * @return
	 */
	static Random counter = new Random();

	static String getSerialId() {
		return "" + System.currentTimeMillis() + (1000 + counter.nextInt(999))
				+ (1000 + counter.nextInt(9000));
	}

	static String getVid(final char[] maxStr, String[] idx) {
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

	static char[] getMixString(int seed) {
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
	}
}
