package net.dreamlu.controller.admin;

import net.dreamlu.config.Consts;
import net.dreamlu.interceptor.AdminInterceptor;
import net.dreamlu.model.Options;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.ext.route.ControllerBind;

/**
 * 网站设置
 * @author L.cm
 * @date 2013-6-4 上午10:16:14
 */
@Before(AdminInterceptor.class)
@ControllerBind(controllerKey="/admin/options", viewPath="admin")
public class OptionsController extends Controller {
    
    /**
     * 网站配置 * OptionsInterceptor拦截器里已有数据
     * @param     设定文件
     * @return void    返回类型
     * @throws
     */
    public void index() {
        render("options.vm");
    }
    
    /**
     * 跟新网站配置
     * @Title: save_update
     * @param     设定文件
     * @return void    返回类型
     * @throws
     */
    public void save_update(){
    	Options options = getModel(Options.class);
		boolean state = false;
		if(null == options.getInt(Options.ID)){
			state = options.saveOptions();
		} else {
			state = options.updateOptions();
		}
		renderJson(Consts.AJAX_STATUS, state);
    }
}
