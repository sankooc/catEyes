package org.cateyes.core.resolver.youku;

import java.util.Collection;
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

import com.jayway.jsonpath.JsonPath;

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
	// private static String TYPE = "flv";

	protected static final JsonPath jpath_title = JsonPath
			.compile("$.data[0].title");
	protected static final JsonPath jpath_seed = JsonPath
			.compile("$.data[0].seed");
	protected static final JsonPath jpath_type = JsonPath
			.compile("$.data[0].streamtypes");
	
	public Volumn createVolumnFromVid(String vid) throws Exception {
		String desc = String.format(YOUKU_LIST, vid);
		JSONObject metadata = connector.getPageAsJson(desc);

		String title = jpath_title.read(metadata);
		VolumnImpl volumn = new VolumnImpl(title, vid, Provider.YOUKU);

		Integer seed = jpath_seed.read(metadata);

		Collection<String> types = jpath_type.read(metadata);
		if (types.contains("flv")) {
			addFlv(volumn, metadata, seed, "flv", 0);
			logger.info("add flv fragment");
		}
		if (types.contains("mp4")) {
			addFlv(volumn, metadata, seed, "mp4", 1);
			logger.info("add mp4 fragment");
		}
//		if(types.contains("hd2")){
//			addFlv(volumn, metadata, seed, "hd2", 2);
//		}
		return volumn;

	}

	public void addFlv(Volumn volumn, JSONObject metadata, int seed,
			String type, int quality) {
		char[] maxStr = getMixString(seed);
		JsonPath jpath_seg_flv = JsonPath.compile("$.data[0].streamfileids."
				+ type);
		String token = jpath_seg_flv.read(metadata);
		String[] vid = token.split("\\*");
		String vv = getVid(maxStr, vid);
		JsonPath jpath_seg = JsonPath.compile("$.data[0].segs." + type);
		JSONArray flvSegs = jpath_seg.read(metadata);
		for (int i = 0; i < flvSegs.size(); i++) {
			JSONObject fragment = flvSegs.getJSONObject(i);
			int in = fragment.getInt("no");
			long size = fragment.getLong("size");
			String no = String.format("%02x", in);
			String urlString = String.format(REAL_FORMAT, getSerialId(), no,
					type, vv.substring(0, 8), no.toUpperCase(),
					vv.substring(10), fragment.get("k"),
					fragment.get("seconds"));
			volumn.addFragment(quality, type, urlString, size);
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
