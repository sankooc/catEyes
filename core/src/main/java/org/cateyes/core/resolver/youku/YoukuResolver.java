package org.cateyes.core.resolver.youku;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.cateyes.core.VideoConstants.Provider;
import org.cateyes.core.resolver.AbstractResolver;
import org.cateyes.core.resolver.Resolver;
import org.cateyes.core.volumn.Volumn;
import org.cateyes.core.volumn.VolumnImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * @author sankooc
 */
public class YoukuResolver extends AbstractResolver implements Resolver {

	private final static Pattern pattern = Pattern
			.compile("var videoId2= \'(\\w+)\';");

	public Volumn createVolumn(String uri) throws Exception {
		String vid = connector.getPageRegix(uri, pattern);
		return createVolumnFromVid(vid);
	}

	private static Logger logger = LoggerFactory.getLogger(YoukuResolver.class);

	private static final String YOUKU_LIST = "http://v.youku.com/player/getPlayList/VideoIDS/%s";

	private static final String REAL_FORMAT = "http://f.youku.com/player/getFlvPath/sid/%s_%s/st/%s/fileid/%s%s%s?K=%s&ts=%s";
	private static String TOKEN = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ/\\:._-1234567890";
	private static Random counter = new Random();
	private static String TYPE = "flv";

	public Volumn createVolumnFromVid(String vid) throws Exception {
		String desc = String.format(YOUKU_LIST, vid);
		JSONObject metadata = connector.getPageAsJson(desc);

		JSONObject data = metadata.getJSONArray("data").getJSONObject(0);

		String title = data.getString("title");
		VolumnImpl volumn = new VolumnImpl(title, vid, Provider.YOUKU);
		addUrlAndSize(data, volumn);
		return volumn;

	}

	public void addUrlAndSize(JSONObject data, Volumn volumn) {
		int seed = getSeed(data);
		char[] maxStr = getMixString(seed);
		JSONObject sf = data.getJSONObject("streamfileids");
		// JSONObject sizes = data.getJSONObject("streamsizes");
		String token = (String) sf.get(TYPE);
		String[] vid = token.split("\\*");
		String vv = getVid(maxStr, vid);
		final JSONObject segs = data.getJSONObject("segs");
		JSONArray flvSegs = segs.getJSONArray(TYPE);
		for (int i = 0; i < flvSegs.size(); i++) {
			JSONObject fragment = flvSegs.getJSONObject(i);
			String numSt = fragment.getString("no");
			String sizeStr = fragment.getString("size");
			int in = Integer.parseInt(numSt);
			long size = Long.parseLong(sizeStr);
			String no = String.format("%02x", in);
			String urlString = String.format(REAL_FORMAT, getSerialId(), no,
					TYPE, vv.substring(0, 8), no.toUpperCase(),
					vv.substring(10), fragment.get("k"),
					fragment.get("seconds"));
			volumn.addUrl(urlString, size);
		}
	}

	protected int getSeed(JSONObject obj) {
		Integer value = (Integer) obj.get("seed");
		return value;
	}

	protected String getSerialId() {
		return "" + System.currentTimeMillis() + (1000 + counter.nextInt(999))
				+ (1000 + counter.nextInt(9000));
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
		return new String[] { "^http://v.youku.com/v_show/id_([\\w=]+).html",
				"^http://player.youku.com/player.php/sid/([\\w=]+)/v.swf" };
	}

}
