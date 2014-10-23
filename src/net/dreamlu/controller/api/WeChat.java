package net.dreamlu.controller.api;

import java.io.IOException;

import javax.servlet.ServletInputStream;

import net.dreamlu.handler.WeChatMessageHandler;

import com.gson.bean.Articles;
import com.gson.bean.InMessage;
import com.gson.bean.OutMessage;
import com.gson.util.Tools;
import com.gson.util.XStreamFactory;
import com.jfinal.core.Controller;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.log.Logger;
import com.thoughtworks.xstream.XStream;

/**
 * 微信开放平台api
 * @author L.cm
 * email: 596392912@qq.com
 * site:  http://www.dreamlu.net
 * @date 2013-5-14 下午5:08:12
 * 参考自：<url>http://git.oschina.net/gson/wechat</url>
 */
@ControllerBind(controllerKey = "/api/wechat")
public class WeChat extends Controller {

	private static final Logger log = Logger.getLogger(WeChat.class);
	
	/**
	 * URL		：http://www.dreamlu.net/api/wechat/******
	 * Token	: 为url中的 ******
	 * @param @throws IOException    设定文件
	 * @return void    返回类型
	 * @throws
	 */
	public void index() throws IOException {
		if( "GET".equals(getRequest().getMethod()) ) {
			do_get();
		}else{
			do_post();
		}
	}
	
	public void do_get() {
		log.info("wechat...init...get...");
		String token = getPara();					// token
		String signature = getPara("signature");	// 微信加密签名
		String timestamp = getPara("timestamp");	// 时间戳
		String nonce = getPara("nonce");			// 随机数
		String echostr = getPara("echostr");		// 随机字符串
		//验证
		if (Tools.checkSignature(token, signature, timestamp, nonce)) {
			renderText(echostr);
		}
	}
	
	public void do_post() throws IOException {
		log.info("wechat...init...post...");
		getResponse().setCharacterEncoding("UTF-8");
		ServletInputStream in = getRequest().getInputStream();
		// 转换微信post过来的xml内容
		XStream xs = XStreamFactory.init(false);
		xs.alias("xml", InMessage.class);
		String xmlMsg = Tools.inputStream2String(in);
		log.info("输入消息:[" + xmlMsg + "]");

		InMessage msg = (InMessage) xs.fromXML(xmlMsg);
		// 获取自定消息处理器，如果自定义处理器则使用默认处理器。
		WeChatMessageHandler handler = new WeChatMessageHandler();
		OutMessage oms = handler.exeMsg(msg);
		// 把发送发送对象转换为xml输出
		xs = XStreamFactory.init(true);
		xs.alias("xml", oms.getClass());
		xs.alias("item", Articles.class);
		String xml = xs.toXML(oms);
		log.info("输出消息:[" + xml + "]");
		renderText(xs.toXML(oms), "text/xml");
	}
}
