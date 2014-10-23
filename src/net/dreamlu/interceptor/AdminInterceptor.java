package net.dreamlu.interceptor;

import net.dreamlu.config.Consts;
import net.dreamlu.model.User;

import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation;
import com.jfinal.core.Controller;

/**
 * 后台拦截
 * @author L.cm
 * @date 2013-5-30 下午9:52:46
 */
public class AdminInterceptor implements Interceptor {
    
    @Override
    public void intercept(ActionInvocation ai) {
        Controller controller = ai.getController();
        User user = controller.getSessionAttr(Consts.USER_SESSION);
        if(null == user){
            controller.redirect("/sign_in");
            return;
        } else {
            // 方法名
            String methodName = ai.getMethodName();
            // 用户的权限进行控制
            int authority = user.getInt(User.AUTHORITY);
            // 普通用户没有 save update 和 delete 权限
            if (User.V_P == authority && (methodName.startsWith("save") || methodName.startsWith("delete"))) {
                controller.renderJson(Consts.AJAX_STATUS, Consts.AJAX_S);
            } else if (User.V_V == authority && methodName.startsWith("delete")) {
                controller.renderJson(Consts.AJAX_STATUS, Consts.AJAX_S);
            } else {
                ai.invoke();
            }
        }
    }
}
