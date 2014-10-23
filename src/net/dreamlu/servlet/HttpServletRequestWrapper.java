package net.dreamlu.servlet;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import net.dreamlu.utils.HtmlFilter;

/**
 * 对HttpServletRequestWrapper重写，为了统一XSS处理加上懒写成这样了
 * @author L.cm
 * email: 596392912@qq.com
 * site:  http://www.dreamlu.net
 * @date 2014-5-5 上午9:10:18
 */
public class HttpServletRequestWrapper extends javax.servlet.http.HttpServletRequestWrapper{

	public HttpServletRequestWrapper(HttpServletRequest request) {
		super(request);
	}

	/**
	 * 重写并过滤getParameter方法
	 */
	@Override
	public String getParameter(String name) {
		return HtmlFilter.getBasicHtmlandimage(super.getParameter(name));
	}
	
	/**
	 * 重写并过滤getParameterValues方法
	 */
	@Override
	public String[] getParameterValues(String name) {
		String[] values = super.getParameterValues(name);
		if (null == values){
			return null;
		}
		String[] newValues = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			newValues[i] = HtmlFilter.getBasicHtmlandimage(values[i]);
		}
		return newValues;
	}
	
	/**
	 * 重写并过滤getParameterMap方法
	 */
	@Override
	public Map<String, String[]> getParameterMap() {
		Map<String, String[]> paraMap = super.getParameterMap();
		// 对于paraMap为空的直接return
		if (null == paraMap || paraMap.isEmpty()) {
			return paraMap;
		}
		Map<String, String[]> newParaMap = new HashMap<String, String[]>();
		for (Entry<String, String[]> entry : paraMap.entrySet()) {
			String key = entry.getKey();
			String[] values     = entry.getValue();
			if (null == values) {
				continue;
			}
			String[] newValues  = new String[values.length];
			for (int i = 0; i < values.length; i++) {
				newValues[i] = HtmlFilter.getBasicHtmlandimage(values[i]);
			}
			newParaMap.put(key, newValues);
		}
		return newParaMap;
	}
}
