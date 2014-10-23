package net.dreamlu.controller.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.dreamlu.api.life.DianpingApi;
import net.dreamlu.api.life.HelpApi;
import net.dreamlu.utils.StringUtil;

import com.alibaba.fastjson.JSONArray;
import com.jfinal.core.Controller;
import com.jfinal.ext.route.ControllerBind;

/**
 * 生活娱乐相关
 * 调用大众点评系列周边信息api，没有用百度的
 * 大众点评真在测试佣金，有钱便是爷
 * @author L.cm
 * email: 596392912@qq.com
 * site:  http://www.dreamlu.net
 * @date 2013-5-29 下午9:09:09
 */
@ControllerBind(controllerKey="/life", viewPath="web/life")
public class LifeController extends Controller {
	
	public void index() throws IOException {
		// 查找地理位置信息 可以基于 html5 先获取精确位置再 没有获取到的再应用ip定问
		// TODO 闲暇了或者做到m.dreamlu.net中，计划中... 
		Map<String, Object> map = HelpApi.ipData(StringUtil.getIP(getRequest()));
		DianpingApi dianpin = new DianpingApi();
		String url = "http://api.dianping.com/v1/business/find_businesses";
		Map<String, String> paramMap = new HashMap<String, String>();
		// 先这样 以后用 坐标 暂时取出的坐标有问题 只支持“北京” 居然不支持 北京市
		paramMap.put("city", StringUtil.cityMatcher(map.get("city").toString()));
		Map<String, Object> dpjson =  dianpin.doApi(url, paramMap);
		// 根据地理位置获取团购信息
		renderJson(dpjson);
	}
	
	// 获取天气 JSON ajax调用
	public void weather_json() throws IOException {
		JSONArray arrayInfo = HelpApi.weather(getPara("city", HelpApi.DEFAULT_CITY));
		renderJson(arrayInfo == null ? false : arrayInfo.toJSONString());
	}
	
	
	public void itunes() {
		render("itunes.vm");
	}
	
	public void html5(){
		render("html5.vm");
	}
}
