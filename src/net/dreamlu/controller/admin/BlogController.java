package net.dreamlu.controller.admin;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import net.dreamlu.api.oauth.OauthOsc;
import net.dreamlu.config.Consts;
import net.dreamlu.controller.api.OscLogin;
import net.dreamlu.interceptor.AdminInterceptor;
import net.dreamlu.model.Blog;
import net.dreamlu.model.BlogTag;
import net.dreamlu.model.User;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;

/**
 * 后台博文管理
 * @author L.cm
 * @date 2013-5-31 下午9:47:55
 */
@Before(AdminInterceptor.class)
@ControllerBind(controllerKey="/admin/blog")
public class BlogController extends Controller {
    /**
     * 后台 blog 列表页
     * @param     设定文件
     * @return void    返回类型
     * @throws
     */
    public void index() {}
    
    /**
     * 后台 blog 列表
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
        renderJson(Blog.dao.pageDataTables(pageNum, pageSize, sEcho, search));
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
            Blog blog = Blog.dao.findById(id);
            List<BlogTag> list = BlogTag.dao.findByBlogId(blog.getInt(Blog.ID));
            setAttr("blog", blog);
            setAttr("tags", list);
        }
    }

    /**
     * @throws IOException 
     * 保存或更新博客
     * @param     设定文件
     * @return void    返回类型
     * @throws
     */
    public void save_update() throws IOException {
        Blog blog = getModel(Blog.class);
        // seo 添加图片alt信息
        String title = blog.getStr(Blog.TITLE);
        String content = blog.getStr(Blog.CONTENT);
        if (StrKit.notBlank(title, content)) {
            blog.set(Blog.CONTENT, content.replaceAll("alt=\"\"", "alt=\"" + title + "\""));
        }
        boolean state = false;
        if(null == blog.getInt(Blog.ID)){
            User user = getSessionAttr("user");
            blog.set(Blog.USER_ID, user.getInt(User.ID));
            state = blog.saveBlog();
        } else {
            state = blog.updateBlog();
        }
        //===========================================
        // 发送动弹，[title](url)
        // 从缓存中拿取accessToken，避免accessToken被刷新
        String accessToken = OscLogin.cacheToken();
        if (StrKit.notBlank(accessToken)) {
            OauthOsc.me().tweetPub(accessToken, '[' + title + "](" + Consts.DOMAIN_URL + "/blog/" + blog.getInt(Blog.ID) + ')');
        }
        //===========================================
        // 同步标签，写的不够好...
        Integer blogId = blog.get(Blog.ID);
        List<Integer> list = BlogTag.dao.findIdsByBlogId(blogId);
        Integer[] tags = getParaValuesToInt("tags");
        // 1.没有标签
        if (null == list && null == tags) {
            renderJson(Consts.AJAX_STATUS, state);
            return;
        }
        // 2.数据库没有该博文的标签
        if (null == list) {
            BlogTag.dao.saveAllTags(blogId, tags);
            renderJson(Consts.AJAX_STATUS, state);
            return;
        }
        // 3.删除完tags
        if (null == tags) {
            BlogTag.dao.removeAllTags(blogId);
            renderJson(Consts.AJAX_STATUS, state);
            return;
        }
        List<Integer> tagsList = Arrays.asList(tags);
        // 4.没有改变
        if (list.size() == tagsList.size() && list.containsAll(tagsList)) {
            renderJson(Consts.AJAX_STATUS, state);
            return;
        }
        // 5.交叉部分 因为标签不可能那么多所以整的比较简单
        // L.cm 2014-05-04 优化为《编写高质量代码：改善java程序的151个建议》 130条
        //   5.01去除已有的和空字符串
        tagsList.removeAll(list);
        tagsList.remove("");
        if (tagsList.size() > 0) {
            BlogTag.dao.saveAllTags(blogId, tagsList.toArray());
        }
        //   5.02删除数据库已经不存在的
        list.removeAll(tagsList);
        list.remove("");
        if (list.size() > 0) {
            BlogTag.dao.removeTags(blogId, list.toArray());
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
        Blog blog = Blog.dao.findById(getParaToInt());
        boolean temp = false;
        if(StrKit.notNull(blog)){
            int status = blog.getInt(Blog.DEL_STATUS) == Blog.DEL_N ? Blog.DEL_Y : Blog.DEL_N;
            temp = blog.set(Blog.DEL_STATUS, status).update();
        } 
        renderJson(Consts.AJAX_STATUS, temp);
    }
}