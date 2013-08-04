package org.cateyes.core.youku;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.ArrayUtils;
import org.cateyes.core.AbstractResolver;
import org.cateyes.core.ApacheConnector;
import org.cateyes.core.Resolver;
import org.cateyes.core.VideoConstants.VideoType;
import org.cateyes.core.entity.Volumn;
import org.cateyes.core.entity.VolumnImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YoukuResolver2 extends AbstractResolver implements Resolver {

	final ApacheConnector connetor = ApacheConnector.getInstance();

	private final static Pattern pattern = Pattern.compile("var videoId2= \'(\\w+)\';");

	public Volumn createVolumn(String uri) throws Exception {
		String vid = connetor.getPageRegix(uri, pattern);
		return createVolumnFromVid(vid);
	}

	private static Logger logger = LoggerFactory.getLogger(YoukuResolver2.class);

	private static final String YOUKU_LIST = "http://v.youku.com/player/getPlayList/VideoIDS/%s";

	private static final String REAL_FORMAT = "http://f.youku.com/player/getFlvPath/sid/%s_%s/st/%s/fileid/%s%s%s?K=%s&ts=%s";
	private static String TOKEN = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ/\\:._-1234567890";
	private static Random counter = new Random();
	private static String TYPE="flv";

	public Volumn createVolumnFromVid(String vid) throws Exception {
		String desc = String.format(YOUKU_LIST, vid);
		JSONObject metadata = connetor.getPageAsJson(desc);

		JSONObject data = metadata.getJSONArray("data").getJSONObject(0);

		String title = data.getString("title");
		String[] uris = getRealUri(data);
		if (ArrayUtils.isEmpty(uris)) {
			logger.error("cannot download {}", vid);
			return null;
		}
		Volumn volumn = new VolumnImpl(title, vid, uris);
		volumn.setTitle(title);
		volumn.setUris(uris);
		return volumn;

	}

	public String[] getRealUri(JSONObject data) {
		return getReadUriFromYID(data);
	}

	public String[] getReadUriFromYID(JSONObject data) {
		int seed = getSeed(data);
		char[] maxStr = getMixString(seed);
		JSONObject sf = data.getJSONObject("streamfileids");
		String token = (String) sf.get(TYPE);
		String[] vid = token.split("\\*");
		String vv = getVid(maxStr, vid);
		final JSONObject segs = data.getJSONObject("segs");
		JSONArray flvSegs = segs.getJSONArray(TYPE);
		String[] uris = new String[flvSegs.size()];
		for (int i = 0; i < flvSegs.size(); i++) {
			JSONObject fragment = flvSegs.getJSONObject(i);
			String numSt = fragment.get("no").toString();
			int in = Integer.parseInt(numSt);
			String no = String.format("%02x", in);
			String urlString = String.format(REAL_FORMAT, getSerialId(), no, TYPE, vv.substring(0, 8), no.toUpperCase(), vv.substring(10), fragment.get("k"),
					fragment.get("seconds"));
			uris[i] = urlString;
		}
		return uris;
	}

	public int getFragmentCount(VideoType type, JSONObject data) {
		final JSONObject segs = data.getJSONObject("segs");
		JSONArray flvSegs = segs.getJSONArray(TYPE);
		return flvSegs.size();
	}

	protected int getSeed(JSONObject obj) {
		Integer value = (Integer) obj.get("seed");
		return value;
	}

	protected String getSerialId() {
		return "" + System.currentTimeMillis() + (1000 + counter.nextInt(999)) + (1000 + counter.nextInt(9000));
	}

	protected String getVid(final char[] maxStr, String[] idx) {
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

	protected char[] getMixString(int seed) {
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

	@Override
	protected String[] getRegexStrings() {
		return new String[] { "^http://v.youku.com/v_show/id_([\\w=]+).html", "^http://player.youku.com/player.php/sid/([\\w=]+)/v.swf" };
	}

}
