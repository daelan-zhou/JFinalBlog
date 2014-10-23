package net.dreamlu.controller.admin;

import net.dreamlu.config.Consts;
import net.dreamlu.interceptor.AdminInterceptor;
import net.dreamlu.model.Links;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;

/**
 * 网站设置
 * @author L.cm
 * @date 2013-6-4 上午10:16:14
 */
@Before(AdminInterceptor.class)
@ControllerBind(controllerKey="/admin/links")
public class LinksController extends Controller {

    /**
     * 网站链接
     * @param     设定文件
     * @return void    返回类型
     * @throws
     */
    public void index() {}

    /**
     * 后台 链接 列表
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
        renderJson(Links.dao.pageDataTables(pageNum, pageSize, sEcho, search));
    }

    /**
     * 后台写博或跟新
     * @param     设定文件
     * @return void    返回类型
     * @throws
     */
    public void add_edit() {
        Integer id = getParaToInt();
        if(null != id){
        	Links links = Links.dao.findById(id);
            setAttr("links", links);
        }
    }

    /**
     * 跟新网站友链
     * @Title: save_update
     * @param     设定文件
     * @return void    返回类型
     * @throws
     */
    public void save_update(){
        Links links = getModel(Links.class);
        boolean state = false;
        if(null == links.getInt(Links.ID)){
            state = links.saveLinks();
        } else {
            state = links.updateLinks();
        }
        renderJson(Consts.AJAX_STATUS, state);
    }
    
    /**
     * 删除或显示博文
     * @param     设定文件
     * @return void    返回类型
     * @throws
     */
    public void delete_show() {
    	Links links = Links.dao.findById(getParaToInt());
        boolean temp = false;
        if(StrKit.notNull(links)){
            int status = links.getInt(Links.DEL_STATUS) == Links.DEL_N ? Links.DEL_Y : Links.DEL_N;
            links.set(Links.DEL_STATUS, status);
            temp = links.updateLinks();
        }
        renderJson(Consts.AJAX_STATUS, temp);
    }
}
