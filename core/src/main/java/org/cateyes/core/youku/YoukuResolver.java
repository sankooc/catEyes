package org.cateyes.core.youku;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.ArrayUtils;
import org.cateyes.core.ApacheConnector;
import org.cateyes.core.Resolver;

public class YoukuResolver implements Resolver {
	public void resolv(URI url) {

	}

	public String getYoukuSid(URI url) {
		String str = url.toString();

		return str;
	}

	ApacheConnector connetor = new ApacheConnector();

	public void resolvSid(String sid) {
		String flcJsonURI = "http://v.youku.com/player/getPlayList/VideoIDS/" + sid;
		byte[] bts = connetor.doGet(URI.create(flcJsonURI));
		JSONObject object = JSONObject.fromObject(new String(bts));
		JSONArray dataObj = object.getJSONArray("data");
		JSONObject data = dataObj.getJSONObject(0);
		JSONObject segs = data.getJSONObject("segs");
		for (Object key : segs.keySet()) {
			System.out.println(key);
		}
		JSONArray flvSegs = segs.getJSONArray("flv");
		System.out.println(flvSegs.size());
		int seed = getSeed(data);
		System.out.println("seed is " + seed);
		char[] maxStr = getMixString(seed);
		JSONObject sf = data.getJSONObject("streamfileids");
		String token = (String) sf.get("flv");
		System.out.println(token);
		String[] vid = token.split("\\*");
		String vv = getVid(maxStr, vid);
		System.out.println(vv);
		JSONObject fragment = flvSegs.getJSONObject(0);
		String numSt = (String) fragment.get("no");
		int in = Integer.parseInt(numSt);
		String no = String.format("%02x",in);
		String urlString = String.format("http://f.youku.com/player/getFlvPath/sid/%s_%s/st/%s/fileid/%s%s%s?K=%s&ts=%s",
				getSerialId(),no,"flv",vv.substring(0, 8),no.toUpperCase(),vv.substring(10),
				fragment.get("k"),fragment.get("seconds"));
		System.out.println(urlString);
		
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
		return "" + System.currentTimeMillis() + (1000 + counter.nextInt(999)) + (1000 + counter.nextInt(9000));
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
