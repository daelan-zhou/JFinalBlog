package net.dreamlu.controller.web;

import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.dreamlu.config.Consts;
import net.dreamlu.kit.MailKit;
import net.dreamlu.model.Blog;
import net.dreamlu.model.MailVerify;
import net.dreamlu.model.Options;
import net.dreamlu.model.User;
import net.dreamlu.model.WbLogin;
import net.dreamlu.utils.DateUtil;
import net.dreamlu.utils.StringUtil;
import net.dreamlu.validator.EmailValidator;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;

/**
 * 首页
 * @author L.cm
 * email: 596392912@qq.com
 * site:  http://www.dreamlu.net
 * @date 2013-5-7 上午9:42:21
 */
@ControllerBind(controllerKey = "/", viewPath="web")
public class IndexController extends Controller {

    /**
     * 首页
     * @param     设定文件
     * @return void    返回类型
     * @throws
     */
    public void index() {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(Blog.BLOG_TYPE, null);
        Page<Blog> page = Blog.dao.page(getParaToInt(0, 1), Consts.BLOG_PAGE_SIZE, result);
        if (page.getList().size() <= 0) {
            renderError(404);
        }
        setAttr("actionUrl", "");
        setAttr("postsby", false);
        setAttr("blogPage", page);
        render("index.vm");
    }
    
    /**
     * 文章
     * @param     设定文件
     * @return void    返回类型
     * @throws
     */
    public void blogs() {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(Blog.BLOG_TYPE, 0);
        Page<Blog> page = Blog.dao.page(getParaToInt(0, 1), Consts.BLOG_PAGE_SIZE, result);
        if (page.getList().size() <= 0) {
            renderError(404);
        }
        setAttr("blogPage", page);
        setAttr("postsby", Blog.TYPE_NAME[0]);
        setAttr("actionUrl", "/blogs/");
        render("index.vm");
    }
    
    /**
     * 收藏
     * @param     设定文件
     * @return void    返回类型
     * @throws
     */
    public void favorites() {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(Blog.BLOG_TYPE, 1);
        Page<Blog> page = Blog.dao.page(getParaToInt(0, 1), Consts.BLOG_PAGE_SIZE, result);
        if (page.getList().size() <= 0) {
            renderError(404);
        }
        setAttr("blogPage", page);
        setAttr("postsby", Blog.TYPE_NAME[1]);
        setAttr("actionUrl", "/favorites/");
        render("index.vm");
    }
    
    /**
     * 搜索
     * @param     设定文件
     * @return void    返回类型
     * @throws
     */
    public void search() {
        Map<String, Object> result = new HashMap<String, Object>();
        String s = getPara("s");
        if (StrKit.isBlank(s)) {
            s = getPara(0);
        }
        result.put("s", s);
        int pageNum = getParaToInt(1, 1);
        Page<Blog> page = Blog.dao.page(pageNum, Consts.BLOG_PAGE_SIZE, result);
        if (page.getList().size() <= 0) {
            renderError(404);
        }
        setAttr("keywords", s);
        setAttr("blogPage", page);
        setAttr("postsby", "搜索：" + s);
        setAttr("actionUrl", "/search/" + s + "-");
        render("index.vm");
    }
    
    /**
     * @throws Exception 
     * 标签 url /tags/nginx-1 
     * @param     设定文件
     * @return void    返回类型
     * @throws
     */
    public void tags() throws Exception {
        String tags = getPara(0);
        if (StrKit.isBlank(tags)) {
            redirect("/");
            return;
        }
        tags = URLDecoder.decode(tags,"utf-8");
        int pageNum = getParaToInt(1, 1);
        Page<Blog> page = Blog.dao.pageByTags(pageNum, Consts.BLOG_PAGE_SIZE, tags);
        if (page.getList().size() <= 0) {
            renderError(404);
        }
        setAttr("blogPage", page);
        setAttr("postsby", "tags：" + tags);
        setAttr("actionUrl", "/tags/" + tags + "-");
        render("index.vm");
    }
    
    /**
     * 关于
     * @param     设定文件
     * @return void    返回类型
     * @throws
     */
    public void about() {
    	render("about.vm");
    }
    
    /**
     * 登录
     * @param     设定文件
     * @return void    返回类型
     * @throws
     */
    public void sign_in() {
        User user = getSessionAttr("user");
        if(StrKit.notNull(user)) {
            redirect("/admin");
        }else {
        	render("/admin/sign-in.vm");
        }
    }
    
    /**
     * 重置密码
     * @param     设定文件
     * @return void    返回类型
     * @throws
     */
    @Before(EmailValidator.class)
    public void reset_pwd() {
        String mailTo = getPara("email");
        User user = User.dao.findByEmail(mailTo);
        boolean status = false;
        if (null != user) {
        	Map<String, Object> model = new HashMap<String, Object>();
        	Options options = Options.dao.findByCache();
        	String pwd = StringUtil.randomPwd(8);
        	user.set(User.PASSWORD, StringUtil.pwdEncrypt(pwd)).update();
            model.put("options", options);
            model.put("user", user.getStr(User.NICK_NAME));
            model.put("pwd", pwd);
            status = MailKit.sendTemplateMail("找回密码-DreamLu.net", mailTo, model, "reset_pwd.vm");
        }
        renderJson(Consts.AJAX_STATUS, status);
    }

    /**
     * 邮箱返回连接
     */
    public void finish() {
        String code = getPara("code");
        MailVerify mv = MailVerify.dao.getByCode(code);
        Date createTime = mv.getTimestamp(MailVerify.CREATE_TIME);
        if (null != mv && createTime.after(DateUtil.hourBefor(24))) {
        	WbLogin wb = WbLogin.dao.findById(mv.getInt(MailVerify.USER_ID));
        	wb.set(WbLogin.STATUS, WbLogin.STATUS_Y).update();
        	User user = User.dao.findById(wb.getInt(WbLogin.USERID));
        	user.set(User.EMAIL_VERIFY, User.EMAIL_Y).update();
            setSessionAttr(Consts.USER_SESSION, user);
            redirect("/admin");
        } else {
            redirect("/");
        }
    }
    
    /**
     * 登出
     * @param     设定文件
     * @return void    返回类型
     * @throws
     */
    public void logout() {
        removeCookie("userId", "/", Consts.DOMAIN_COOKIE);
        removeSessionAttr(Consts.USER_SESSION);
        redirect("/sign_in");
    }
    
    /**
     * 提供给github pages jsonp跨域使用
     * @url         http://dreamlu.net/
     * @param       设定文件
     * @return void    返回类型
     * @throws
     */
    public void json() {
        String callback = getPara("callback");
        List<Blog> blogs = Blog.dao.find4github();
        getResponse().setHeader("Access-Control-Allow-Origin", "*");
        if (StrKit.isBlank(callback)) {
            renderJson(JsonKit.listToJson(blogs, 2));
        } else {
            renderJavascript(getPara("callback") + "(" + JsonKit.listToJson(blogs, 2) + ")");
        }
    }
}
