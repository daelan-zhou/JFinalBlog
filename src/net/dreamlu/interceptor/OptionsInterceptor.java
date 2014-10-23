package net.dreamlu.interceptor;

import javax.servlet.http.HttpServletRequest;

import net.dreamlu.config.Consts;
import net.dreamlu.model.Options;

import org.apache.commons.codec.binary.Base64;

import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation;
import com.jfinal.kit.StrKit;

/**
 * 网站的基本设置
 * @author L.cm
 * @date 2013-5-30 下午9:44:56
 */
public class OptionsInterceptor implements Interceptor {

	@Override
	public void intercept(ActionInvocation ai) {
		ai.invoke();
		// 网站的基本设置
		Options options = Options.dao.findByCache();
		ai.getController().setAttr("options", options);
		HttpServletRequest request = ai.getController().getRequest();
		
		String requestUri = request.getRequestURI();
		String queryString = request.getQueryString();
		StringBuilder sb = new StringBuilder(Consts.DOMAIN_URL);
		if (StrKit.notBlank(requestUri)) {
			sb.append(requestUri);
		}
		if (StrKit.notBlank(queryString)) {
			sb.append('?').append(queryString);
		}
		String code = Base64.encodeBase64URLSafeString(sb.toString().getBytes());
		ai.getController().setAttr("qrcode", code);
	}
}
