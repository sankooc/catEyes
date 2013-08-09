package org.cateyes.core.volumn;

import java.util.Collection;
import java.util.LinkedList;

import org.cateyes.core.resolver.Resolver;
import org.cateyes.core.resolver.acfun.AcfunResolver;
import org.cateyes.core.resolver.cntv.CntvResolver;
import org.cateyes.core.resolver.feng.FengResolver;
import org.cateyes.core.resolver.gametrailers.GTResolver;
import org.cateyes.core.resolver.iqiyi.IqiyiResolver;
import org.cateyes.core.resolver.ku6.Ku6Resolver;
import org.cateyes.core.resolver.lesh.LeshResolver;
import org.cateyes.core.resolver.pps.PPSResolver;
import org.cateyes.core.resolver.pptv.PPTVResolver;
import org.cateyes.core.resolver.sina.SinaResolver;
import org.cateyes.core.resolver.sohu.SohuResolver;
import org.cateyes.core.resolver.tencent.TencentResolver;
import org.cateyes.core.resolver.tudou.TudouResolver;
import org.cateyes.core.resolver.wuliu.WulResolver;
import org.cateyes.core.resolver.xlkk.XLkkResolver;
import org.cateyes.core.resolver.youku.YoukuResolver;
import org.cateyes.core.resolver.youtube.YoutubeResolver;
import org.cateyes.core.resolver.yyt.YinyuetaiResolver;
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
		rList.add(new FengResolver());
		
		rList.add(new LeshResolver());
		rList.add(new WulResolver());
		rList.add(new YoutubeResolver());
		rList.add(new AcfunResolver());
		rList.add(new CntvResolver());
		
		rList.add(new PPSResolver());
		rList.add(new PPTVResolver());
		rList.add(new SinaResolver());
		rList.add(new TencentResolver());
		rList.add(new XLkkResolver());
		
		rList.add(new YinyuetaiResolver());
		rList.add(new Ku6Resolver());
		rList.add(new GTResolver());
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
