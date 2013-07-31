package org.cateyes.core.comics;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.Character.UnicodeBlock;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.StringTokenizer;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.cateyes.core.ApacheConnector;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import com.google.gson.Gson;

public class Download {

	public static String unicodeToUtf8(String theString) {
		char aChar;
		int len = theString.length();
		StringBuffer outBuffer = new StringBuffer(len);
		for (int x = 0; x < len;) {
			aChar = theString.charAt(x++);
			if (aChar == '\\') {
				aChar = theString.charAt(x++);
				if (aChar == 'u') {
					// Read the xxxx
					int value = 0;
					for (int i = 0; i < 4; i++) {
						aChar = theString.charAt(x++);
						switch (aChar) {
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							value = (value << 4) + aChar - '0';
							break;
						case 'a':
						case 'b':
						case 'c':
						case 'd':
						case 'e':
						case 'f':
							value = (value << 4) + 10 + aChar - 'a';
							break;
						case 'A':
						case 'B':
						case 'C':
						case 'D':
						case 'E':
						case 'F':
							value = (value << 4) + 10 + aChar - 'A';
							break;
						default:
							throw new IllegalArgumentException(
									"Malformed   \\uxxxx   encoding.");
						}
					}
					outBuffer.append((char) value);
				} else {
					if (aChar == 't')
						aChar = '\t';
					else if (aChar == 'r')
						aChar = '\r';
					else if (aChar == 'n')
						aChar = '\n';
					else if (aChar == 'f')
						aChar = '\f';
					outBuffer.append(aChar);
				}
			} else
				outBuffer.append(aChar);
		}
		return outBuffer.toString();
	}

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
	public static String str2Unicode(String str) {
		StringBuffer sb = new StringBuffer();
		char[] charArr = str.toCharArray();
		for (char ch : charArr) {
			if (ch > 128) {
				sb.append("\\u" + Integer.toHexString(ch));
			} else {
				sb.append(ch);
			}
		}
		return sb.toString();
	}
	public static String unicode2Str(String str) {
		StringBuffer sb = new StringBuffer();
		String[] arr = str.split("\\\\\\\\u");
		int len = arr.length;
		sb.append(arr[0]);
		for(int i=1; i<len; i++){
			String tmp = arr[i];
			char c = (char)Integer.parseInt(tmp.substring(0, 4), 16);
			sb.append(c);
			sb.append(tmp.substring(4));
		}
		return sb.toString();
	}
	@Test
	public void json() throws IOException {
		FileInputStream stream = new FileInputStream(
				"C:\\Users\\sankooc\\Desktop\\metadata.json");
		InputStreamReader rd = new InputStreamReader(stream,Charset.forName("ISO-8859-1"));
		BufferedReader reader = new BufferedReader(rd);
		String content = reader.readLine();
		content = unicode2Str(content);
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

	public static String Unicode2GBK(String dataStr) {
		int index = 0;
		StringBuffer buffer = new StringBuffer();

		int li_len = dataStr.length();
		while (index < li_len) {
			if (index >= li_len - 1
					|| !"\\u".equals(dataStr.substring(index, index + 2))) {
				buffer.append(dataStr.charAt(index));

				index++;
				continue;
			}

			String charStr = "";
			charStr = dataStr.substring(index + 2, index + 6);

			char letter = (char) Integer.parseInt(charStr, 16);

			buffer.append(letter);
			index += 6;
		}

		return buffer.toString();
	}

	public static String utf8ToUnicode(String inStr) {
		char[] myBuffer = inStr.toCharArray();

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < inStr.length(); i++) {
			UnicodeBlock ub = UnicodeBlock.of(myBuffer[i]);
			if (ub == UnicodeBlock.BASIC_LATIN) {
				sb.append(myBuffer[i]);
			} else if (ub == UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
				int j = (int) myBuffer[i] - 65248;
				sb.append((char) j);
			} else {
				short s = (short) myBuffer[i];
				String hexS = Integer.toHexString(s);
				String unicode = "\\\\u" + hexS;
				sb.append(unicode.toLowerCase());
			}
		}
		return sb.toString();
	}

	// @Test
	public void commons() {
		ApacheConnector connetor = new ApacheConnector();
		int pInx = 1;
		int inx = 1;
		File dir = new File("d:/mong");

		for (; pInx < 56; pInx++) {
			String page = "http://tieba.baidu.com/p/2372877265?pn=" + pInx;
			Document doc = connetor.getPage(page);
			Elements eles = doc.getElementsByAttributeValue("class",
					"BDE_Image");
			if (eles.size() < 1) {
				continue;
			}
			Iterator<Element> ite = eles.iterator();
			while (ite.hasNext()) {
				Element ele = ite.next();
				if ("img".equalsIgnoreCase(ele.tagName())) {
					String imgSrc = ele.attr("src");
					String prefix = imgSrc.substring(imgSrc.lastIndexOf("."));
					URI uri = URI.create(imgSrc);
					File temp = new File(dir, "lights-" + (inx++) + prefix);
					try {
						connetor.download(uri, temp, null);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
		}
	}

}
