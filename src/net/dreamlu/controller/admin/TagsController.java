package net.dreamlu.controller.admin;

import java.util.List;

import net.dreamlu.config.Consts;
import net.dreamlu.interceptor.AdminInterceptor;
import net.dreamlu.model.BlogTag;
import net.dreamlu.model.Tags;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.ext.route.ControllerBind;

/**
 * 标签管理
 * @author L.cm
 * @date 2013-5-29 下午11:31:43
 */
@Before(AdminInterceptor.class)
@ControllerBind(controllerKey="/admin/tags")
public class TagsController extends Controller {

    /**
     * 标签管理页
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
        renderJson(Tags.dao.pageDataTables(pageNum, pageSize, sEcho, search));
    }

    /**
     * 所有标签 select2 ?? textext 插件?
     * @param     设定文件
     * @return void    返回类型
     * @throws
     */
    public void all() {
        String query = getPara("query");
        int count    = getParaToInt("count", 5);
        List<Tags> tagsList = Tags.dao.findByNameCount(query, count);
        renderJson(tagsList);
    }
    
    /**
     * 添加标签
     * @param     设定文件
     * @return void    返回类型
     * @throws
     */
    public void add_tags() {
    	String tagName = getPara("tagName");
    	Tags tags = Tags.dao.findByName(tagName);
    	if (null == tags) {
    		tags = new Tags();
    		tags.set(Tags.TAG_NAME, tagName).save();
    		setAttr("tagsid", tags.get(Tags.ID));
    		setAttr(Consts.AJAX_STATUS, Consts.AJAX_Y);
    	} else {
    		setAttr(Consts.AJAX_STATUS, Consts.AJAX_N);
    	}
    	renderJson(new String[]{"tagsid", Consts.AJAX_STATUS});
    }
    
    /**
     * 删除或显示博文
     * @param     设定文件
     * @return void    返回类型
     * @throws
     */
    public void delete() {
    	int tagid = getParaToInt();
    	boolean temp = Tags.dao.deleteById(getParaToInt());
    	if (temp) {
    		BlogTag.dao.deleteByTagId(tagid);
    	}
        renderJson(Consts.AJAX_STATUS, temp);
    }
}
