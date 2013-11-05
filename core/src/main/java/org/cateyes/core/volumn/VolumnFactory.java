package org.cateyes.core.volumn;

import java.util.Collection;
import java.util.LinkedList;

import org.cateyes.core.IHeader;
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

	//TODO add resolver automatic
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

	public static Resolver getResolver(String uri) {
		for (Resolver resovler : rList) {
			if (resovler.isPrefer(uri)) {
				return resovler;
			}
		}
		return null;
	}

	public static Volumn createVolumn(String uri) throws Exception {
		Resolver resolver = getResolver(uri);
		if (null != resolver) {
			return resolver.createVolumn(uri);
		}
		return null;
	}

	public static Volumn createVolumn(String uri, IHeader headers)
			throws Exception {
		for (Resolver resovler : rList) {
			if (resovler.isPrefer(uri)) {
				return resovler.createVolumn(uri, headers);
			}
		}
		return null;
	}

}
