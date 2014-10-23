package net.dreamlu.controller.admin;

import net.dreamlu.config.Consts;
import net.dreamlu.interceptor.AdminInterceptor;
import net.dreamlu.model.WxLeaveMsg;
import net.dreamlu.model.WxRule;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;

/**
 * 微信管理
 * @author L.cm
 * @date Sep 22, 2013 8:45:54 PM
 */
@Before(AdminInterceptor.class)
@ControllerBind(controllerKey="/admin/wechat")
public class WeChatController extends Controller {

	// index
	public void index(){}

	/**
     * 后台 微信规则 列表
     * @param     设定文件
     * @return void    返回类型
     * @throws
     */
    public void list_json() {
        int iDisplayStart = getParaToInt("iDisplayStart", 0);
        int pageSize = getParaToInt("iDisplayLength", 10);
        int pageNum =  iDisplayStart / pageSize + 1;
        String sEcho = getPara("sEcho", "1");
        String search = getPara("sSearch");
        renderJson(WxRule.dao.pageDataTables(pageNum, pageSize, sEcho, search));
    }

    /**
     * 后台添加或跟新规则
     * @param     设定文件
     * @return void    返回类型
     * @throws
     */
	public void add_edit() {
		Integer id = getParaToInt();
		if(null != id){
			WxRule rule = WxRule.dao.findById(id);
			setAttr("wxRule", rule);
		}
	}

	/**
	 * 保存或更新规则
	 * @param     设定文件
	 * @return void    返回类型
	 * @throws
	 */
	public void save_update() {
		WxRule rule = getModel(WxRule.class);
		boolean state = false;
		if(null == rule.getInt(WxRule.ID)){
			state = rule.save();
		} else {
			state = rule.update();
		}
		renderJson(Consts.AJAX_STATUS, state);
	}

	/**
	 * 删除规则
	 * @param     设定文件
	 * @return void    返回类型
	 * @throws
	 */
	public void delete() {
		WxRule rule = WxRule.dao.findById(getParaToInt());
		boolean temp = false;
		if(StrKit.notNull(rule)){
			temp = rule.delete();
		} 
		renderJson(Consts.AJAX_STATUS, temp);
	}
	
    /**
     * 留言管理
     * @param     设定文件
     * @return void    返回类型
     * @throws
     */
    public void leave_msgs() {}
    
    /**
     * 留言列表
     * @param     设定文件
     * @return void    返回类型
     * @throws
     */
    public void msgs_list_json() {
        int iDisplayStart = getParaToInt("iDisplayStart", 0);
        int pageSize = getParaToInt("iDisplayLength", 10);
        int pageNum =  iDisplayStart / pageSize + 1;
        String sEcho = getPara("sEcho", "1");
        String search = getPara("sSearch");
        renderJson(WxLeaveMsg.dao.pageDataTables(pageNum, pageSize, sEcho, search));
    }

    /**
	 * 删除留言
	 * @param     设定文件
	 * @return void    返回类型
	 * @throws
	 */
	public void delete_msg() {
		WxLeaveMsg msg = WxLeaveMsg.dao.findById(getParaToInt());
		boolean temp = false;
		if(StrKit.notNull(msg)){
			temp = msg.delete();
		} 
		renderJson(Consts.AJAX_STATUS, temp);
	}
}
