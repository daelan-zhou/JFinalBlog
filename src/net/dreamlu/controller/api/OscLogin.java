package net.dreamlu.controller.api;

import java.util.Date;
import java.util.Locale;
import java.util.Map;

import net.dreamlu.api.oauth.OauthOsc;
import net.dreamlu.api.util.TokenUtil;
import net.dreamlu.config.Consts;
import net.dreamlu.kit.MailKit;
import net.dreamlu.model.User;
import net.dreamlu.model.WbLogin;

import org.apache.commons.lang.time.DateFormatUtils;

import com.jfinal.core.Controller;
import com.jfinal.ext.plugin.config.ConfigKit;
import com.jfinal.ext.plugin.redis.JedisKit;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Logger;

/**
 * 开源中国api登录
 * @author L.cm
 * email: 596392912@qq.com
 * site:  http://www.dreamlu.net
 * @date Jun 24, 2013 9:58:25 PM
 */
@ControllerBind(controllerKey = "/api/osc", viewPath="admin")
public class OscLogin extends Controller {
     
    private static final Logger log = Logger.getLogger(OscLogin.class);
    
    private static final String SESSION_STATE = "_SESSION_STATE_OSC_";
    
    public void index() {
        log.error("进入osc authorize api...");
        try {
            String state = TokenUtil.randomState();
            setSessionAttr(SESSION_STATE, state);
            redirect(OauthOsc.me().getAuthorizeUrl(state));
        } catch (Exception e) {
            log.error(e.getMessage());
            redirect("/");
        }
    }

    /**
     * osc回调
     * @Title: callback
     * @param     设定文件
     * @return void    返回类型
     * @throws
     * 返回json:<url>http://www.oschina.net/openapi/docs/openapi_user</url>
     */
    public void callback() {
        log.error("进入osc callback...");
        String code = getPara("code");
        String state = getPara("state");
        String session_state = getSessionAttr(SESSION_STATE);
        // 取消了授权
        if (StrKit.isBlank(state) || StrKit.isBlank(session_state) || !state.equals(session_state) || StrKit.isBlank(code)) {
            redirect("/admin");
            return;
        }
        removeSessionAttr(SESSION_STATE);
        try{
            OauthOsc osc = new OauthOsc();
            // 使用code换取accessToken
            String accessToken = osc.getTokenByCode(getPara("code"));
            // 获取用户信息
            Map<String, Object> userInfo = osc.getUserInfo(accessToken);
            log.error(null != userInfo ? userInfo.toString() : "userInfo is null...");
            String type = "osc";
            // {id=813039, email=596392912@qq.com, location=北京 海淀, name=孤独的3, gender=male, avatar=http://static.oschina.net/uploads/user/406/813039_50.jpg?t=1348824049000, url=http://my.oschina.net/qq596392912}
            String openid = String.valueOf(userInfo.get("id")); // osc id
            String nickname = userInfo.get("name").toString();  // osc name
            String photoUrl = userInfo.get("avatar").toString();// osc 头像
            String email = userInfo.get("email").toString();    // osc email
            String url = userInfo.get("url").toString();        // osc 博客url
            //===========================================
            // 发送动弹，DreamLu博客使用 osc api登录成功...
            osc.tweetPub(accessToken, "@" + nickname + " \t使用osc api成功登录[DreamLu博客](http://www.dreamlu.net/)");
            //===========================================
            // 第三方登录实体，后面部分没怎么改
            WbLogin login = WbLogin.dao.findByOpenID(openid, type);
            log.error("login1:\t" + login);
            if(null == login) {
                login = new WbLogin();
                login.set(WbLogin.OPENID, openid);
                login.set(WbLogin.TYPE, type);
                login.set(WbLogin.HEAD_PHOTO, photoUrl);
                login.set(WbLogin.CREATETIME, new Date());
                login.set(WbLogin.STATUS, WbLogin.STATUS_N);
                login.set(WbLogin.NICKNAME, nickname).save();
            }
            log.error("login2:\t" + login);
            // 是否邮件校验通过
            int status = login.getInt(WbLogin.STATUS);
            if (null != login.getInt(WbLogin.ID) && WbLogin.STATUS_N == status) {
                if (null == login.getInt(WbLogin.USERID)) {
                    setAttr("nouser", true);
                }
                // 跳转到绑定页
                setAttr("id", login.getInt(WbLogin.ID));
                setAttr("type", type);
                setAttr("nickname", nickname);
                setAttr("photourl", photoUrl);
                setAttr("email", email);
                setAttr("blogurl", url);
                render("binding.vm");
                return;
            }
            if (WbLogin.STATUS_Y == status) {
                User user = User.dao.findById(login.getInt(WbLogin.USERID));
                setSessionAttr(Consts.USER_SESSION, user);
                // 如果用户是管理员，则缓存accessToken
                if (user.getInt(User.AUTHORITY) == User.V_A) {
                    cacheToken(accessToken);
                }
            }
        }catch(Exception e){
            log.error(e.getMessage());
        }
        redirect("/admin");
    }
    
    // 用于缓存的key
    private static final String ACCESS_TOKEN_OSC_ADMIN = "access_token_osc_admin";
    // token 超时时间, 43199，去除部分时间
    private static final int ACCESS_TOKEN_EXPIRY = 40000 * 1000;
    
    /**
     * 缓存osc的token用户发布动弹
     * redis环境有问题时会有异常会影响程序，故处理异常之
     * @return
     */
    public static String cacheToken(String... accessToken) {
        try {
            if (StrKit.notBlank(accessToken)) {
                JedisKit.set(ACCESS_TOKEN_OSC_ADMIN, accessToken[0], ACCESS_TOKEN_EXPIRY);
                return accessToken[0];
            } else {
                return JedisKit.get(ACCESS_TOKEN_OSC_ADMIN);
            }
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 开源中国git钩子，用于项目自动发布
     */
    public void hook() {
        log.info("init git hook...");
        String password = getPara("password");
        String gitPwd   = ConfigKit.getStr("git.pwd");
        if (password.equals(gitPwd)) {
            final String shPath = ConfigKit.getStr("sh.build");
            final String shMail = ConfigKit.getStr("sh.mail");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 得先发邮件，要不然tomcat都关闭了
                        MailKit.syncSend("git hook build OK!", DateFormatUtils.format(new Date(), "yyyy年MM月dd日 HH时mm分ss秒", Locale.CHINA) + " jnode 自动编译重启中...", shMail);
                        Runtime.getRuntime().exec(shPath);
                    } catch (Exception e) {
                        log.error("build.sh IO error...");
                    }
                }
            }).start();
        }
        renderNull();
        return;
    }
}
