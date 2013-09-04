package org.cateyes.core.comics;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Iterator;

import org.cateyes.core.comics.utils.CharsetUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.google.gson.Gson;

public class Download {

	void retc(JSONObject obj) {
		Iterator<?> ite = obj.keys();
		while (ite.hasNext()) {
			Object key = ite.next();
			Object object = obj.get(key);
			if (object instanceof JSONObject) {
				retc((JSONObject) object);
			} else if (object instanceof JSONArray) {
				retcs((JSONArray) object);
			} else {
				System.out.println(object);
			}
		}
	}

	void retcs(JSONArray obj) {
		for (int i = 0; i < obj.size(); i++) {
			Object object = obj.get(i);
			if (object instanceof JSONObject) {
				retc((JSONObject) object);
			} else if (object instanceof JSONArray) {
				retcs((JSONArray) object);
			} else {
				System.out.println(object);
			}
		}
	}
	public void json() throws IOException {
		FileInputStream stream = new FileInputStream(
				"C:\\Users\\sankooc\\Desktop\\metadata.json");
		InputStreamReader rd = new InputStreamReader(stream,Charset.forName("ISO-8859-1"));
		BufferedReader reader = new BufferedReader(rd);
		String content = reader.readLine();
		content = CharsetUtil.unicode2Str(content);
		Gson gson = new Gson();
		Manga manga = gson.fromJson(content, Manga.class);
		System.out.println(manga);
//		JSONObject obj = JSONObject.fromObject(content);
//		retc(obj);
//		obj.size();
		// System.out.println(obj);
		//
		//
		//
		//
		// obj.put("id", 3221);
		//
		// obj.put("author", "sankooc");
		// obj.put("name","照明商店");
		//
		// JSONArray arry = obj.getJSONArray("episode");
		// JSONObject oob = arry.getJSONObject(0);
		// oob.clear();
		// oob.put("name", "照明商店");
		// oob.put("size", 1965);
		// JSONArray ar = new JSONArray();
		// for(int i=1;i<1965;i++){
		// ar.add("lights1-25/lights-"+i+".jpg");
		// }
		//
		// oob.put("picture", ar);
		// String st = obj.toString();
		// st =utf8ToUnicode(st);
		// System.out.println(st);
		// FileOutputStream out = new FileOutputStream("d:/metadata.json");
		// Writer writer = new OutputStreamWriter(out);
		// BufferedWriter bw = new BufferedWriter(writer);
		// bw.write(st);
		// bw.flush();
		// bw.close();
	}

}
