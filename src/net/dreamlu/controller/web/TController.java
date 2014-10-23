package net.dreamlu.controller.web;

import java.util.HashMap;
import java.util.Map;

import net.dreamlu.kit.MailKit;
import net.dreamlu.model.Options;
import net.dreamlu.utils.StringUtil;

import com.jfinal.core.Controller;

/**
 * 测试专用
 * @author l.cm
 * @site:www.dreamlu.net
 * 2014年3月23日 上午10:51:54
 */
public class TController extends Controller {
	
	public void index () {
		Map<String, Object> model = new HashMap<String, Object>();
    	Options options = Options.dao.findByCache();
    	String pwd = StringUtil.randomPwd(8);
        model.put("options", options);
        model.put("user", "dreamlu");
        model.put("pwd", pwd);
        MailKit.sendTemplateMail("找回密码-DreamLu.net", "596392912@qq.com", model, "reset_pwd.vm");
        renderNull();
	}
}
