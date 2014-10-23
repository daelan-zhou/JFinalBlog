package net.dreamlu.controller.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.dreamlu.config.Consts;
import net.dreamlu.interceptor.AdminInterceptor;
import net.dreamlu.kit.MailKit;
import net.dreamlu.model.Blog;
import net.dreamlu.model.MailVerify;
import net.dreamlu.model.Options;
import net.dreamlu.model.User;
import net.dreamlu.model.WbLogin;
import net.dreamlu.utils.StringUtil;
import net.dreamlu.validator.EmailValidator;
import net.dreamlu.validator.PwdValidator;

import com.jfinal.aop.Before;
import com.jfinal.aop.ClearInterceptor;
import com.jfinal.aop.ClearLayer;
import com.jfinal.core.Controller;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.StrKit;

/**
 * 网站后台
 * @author L.cm
 * @date 2013-6-1 下午5:29:58
 */
@Before(AdminInterceptor.class)
@ControllerBind(controllerKey="/admin")
public class IndexController extends Controller{

    /**
     * 后台首页,博文热度图
     */
    public void index() {
        List<List<Integer>> hostBlogList = Blog.dao.hostBlogList();
        setAttr("data", JsonKit.listToJson(hostBlogList, 2));
    }

    /**
     * 安装
     * @param     设定文件
     * @return void    返回类型
     * @throws
     */
    @ClearInterceptor(ClearLayer.ALL)
    @Before({EmailValidator.class, PwdValidator.class})
    public void install() {
    	String email = getPara("email");
        String pwd = getPara("pwd");
        boolean temp = User.dao.saveUser(email, pwd);
        if (temp) {
        	Consts.IS_INSTALL = true;
        }
        renderJson(Consts.AJAX_STATUS, temp);
    }
    
    /**
     * 登录
     */
    @ClearInterceptor
    @Before({EmailValidator.class, PwdValidator.class})
    public void session() {
        String email = getPara("email");
        String pwd = getPara("pwd");
        Integer remember = getParaToInt("remember", 0);
        User user = User.dao.login(email, StringUtil.pwdEncrypt(pwd));
        if(StrKit.notNull(user)) {
            if(remember == 1){
                String code = StringUtil.cookieEncrypt(email, pwd);
                setCookie("userId", code, 60 * 60 * 24 * 30, "/", Consts.DOMAIN_COOKIE);
            }
            user.updateUser();
            setSessionAttr(Consts.USER_SESSION, user);
            setAttr(Consts.AJAX_STATUS, 0);
        }else {
            setAttr(Consts.AJAX_STATUS, 1);
        }
        renderJson(new String[]{Consts.AJAX_STATUS});
    }
    
    /**
     * 用户绑定
     */
    @ClearInterceptor
    @Before({EmailValidator.class})
    public void binding() {
        String email = getPara("email");
        int wbId = getParaToInt("id");
        WbLogin wb = WbLogin.dao.findById(wbId);
        if (null == wb) {
            renderJson(Consts.AJAX_STATUS, Consts.AJAX_N);
            return;
        }
        User user = User.dao.findByEmail(email);
        if (null == user) {
        	user = new User();
            user.saveWbUser(email, wb);
        }
        boolean status = wb.set(WbLogin.USERID, user.getInt(User.ID)).update();
        if (status){
            MailVerify verify = new MailVerify();
            // 设置成WbLogin
            verify.set(MailVerify.USER_ID, wb.getInt(WbLogin.ID));
            status = MailVerify.dao.createVerify(verify);
            // 邮件校验
            Map<String, Object> model = new HashMap<String, Object>();
            Options options = Options.dao.findByCache();
            model.put("options", options);
            model.put("verifyUrl",  "/finish?code=" + verify.getStr(MailVerify.VERIFY_CODE));
            // options.SITE_URL, options.wb_qq, options.wb_sina
            MailKit.sendTemplateMail("DreamLu博客邮箱绑定", email, model, "signup_send.vm");
        }
        renderJson(Consts.AJAX_STATUS, status ? Consts.AJAX_Y : Consts.AJAX_N);
    }
}
