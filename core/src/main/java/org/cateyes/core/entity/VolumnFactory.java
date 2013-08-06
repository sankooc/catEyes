package org.cateyes.core.entity;

import java.util.Collection;
import java.util.LinkedList;
import org.cateyes.core.Resolver;
import org.cateyes.core.iqiyi.IqiyiResolver;
import org.cateyes.core.sohu.SohuResolver;
import org.cateyes.core.tudou.TudouResolver;
import org.cateyes.core.youku.YoukuResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author sankooc
 */
public class VolumnFactory {
	static Logger logger = LoggerFactory.getLogger(VolumnFactory.class);

	final static Collection<Resolver> rList;

	public static Collection<Resolver> getResolvers() {
		return rList;
	}

	static {
		rList = new LinkedList<Resolver>();
		rList.add(new TudouResolver());
		rList.add(new IqiyiResolver());
		rList.add(new SohuResolver());
		rList.add(new YoukuResolver());
	}

	public static Volumn createVolumn(String uri) throws Exception {
		for (Resolver resovler : rList) {
			if (resovler.isPrefer(uri)) {
				return resovler.createVolumn(uri);
			}
		}
		return null;
	}

	// public static Volumn createVolumn(URI uri){
	// //TODO 解析uri
	// return null;
	// }
	// public static Volumn createVolumn(String uri,File file){
	// String yid = match(youkuPattern,uri);
	// Volumn volumn = null;
	// if(null != yid){
	// // JSONObject data = YoukuResolver.getData(yid);
	// // String title = data.getString("title");
	// // String[] uris = YoukuResolver.getRealUri(data, VideoType.FLV);
	// // if (ArrayUtils.isEmpty(uris)) {
	// // logger.error("cannot download {}", yid);
	// // return null;
	// // }
	// // volumn = new VolumnImp(yid,file,YoukuResolver.getConnector());
	// // volumn.setTitle(title);
	// // volumn.setUris(uris);
	//
	// }
	// return volumn;
	// }
	//
	// public static String match(Collection<Pattern> patterns,String uri){
	// for(Pattern pattern : patterns){
	// Matcher matcher = pattern.matcher(uri);
	// if(matcher.find()){
	// return matcher.group(1);
	// }
	// }
	// return null;
	// }

}
