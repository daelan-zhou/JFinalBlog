package net.dreamlu.interceptor;

import net.dreamlu.config.Consts;
import net.dreamlu.model.User;
import net.dreamlu.utils.StringUtil;

import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;

/**
 * cookie 登录
 * @author L.cm
 * @date 2013-5-9 下午4:58:42
 */
public class CookieLoginInterceptor implements Interceptor {

    @Override
    public void intercept(ActionInvocation ai) {
        Controller controller = ai.getController();
        // 检查session
        User u = controller.getSessionAttr(Consts.USER_SESSION);
        if(null == u){
            // 检查是否有cookie
            String userCookie = controller.getCookie("userId");
            if(StrKit.notBlank(userCookie)){
                try {
                    // 解密校验cookie 
                    String[] data = StringUtil.cookieDecryption(userCookie);
                    if(null != data && data.length > 1){
                        User user = User.dao.login(data[0], data[1]);
                        controller.setSessionAttr(Consts.USER_SESSION, user);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        ai.invoke();
    }
}
