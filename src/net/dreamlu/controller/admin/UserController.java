package net.dreamlu.controller.admin;

import net.dreamlu.config.Consts;
import net.dreamlu.interceptor.AdminInterceptor;
import net.dreamlu.model.User;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.ext.route.ControllerBind;

/**
 * 后台用户管理
 * @author L.cm
 * @date 2013-5-31 下午9:52:04
 */
@Before(AdminInterceptor.class)
@ControllerBind(controllerKey="/admin/user")
public class UserController  extends Controller {

    public void index() {
        User user = getSessionAttr(Consts.USER_SESSION);
        user = User.dao.findById(user.get(User.ID));
        setAttr("userInfo", user);
    }

    // 更新用户信息
    public void update() {
        // session 中的 user 避免用户修改id
        User sessionUser = getSessionAttr(Consts.USER_SESSION);
        User user = getModel(User.class);
        user.set(User.ID, sessionUser.getInt(User.ID));
        renderJson(Consts.AJAX_STATUS, user.updateUser());
    }

    // 列表页
    public void list() {}

    // DataTable
    public void list_json() {
        int iDisplayStart = getParaToInt("iDisplayStart", 0);
        int pageSize = getParaToInt("iDisplayLength", 10);
        int pageNum =  iDisplayStart / pageSize + 1;
        String sEcho = getPara("sEcho", "1");
        String search = getPara("sSearch");
        User user = getSessionAttr(Consts.USER_SESSION);
        renderJson(User.dao.pageDataTables(pageNum, pageSize, sEcho, search, user.getInt(User.ID)));
    }

    // 更新状态
    public void update_status() {
        User admin = getSessionAttr(Consts.USER_SESSION);
        if (User.V_A != admin.getInt(User.AUTHORITY)) {
            renderJson(Consts.AJAX_STATUS, Consts.AJAX_S); // 你没有权限
            return;
        }
        User user = User.dao.findById(getParaToInt());
        if (user.getInt(User.ID) == admin.getInt(User.ID)) {
            renderJson(Consts.AJAX_STATUS, Consts.AJAX_O);
            return;
        }
        int status = user.getInt(User.DEL_STATUS) == User.DEL_N ? User.DEL_Y : User.DEL_N;
        boolean temp = user.set(User.DEL_STATUS, status).updateUser();
        renderJson(Consts.AJAX_STATUS, temp);
    }

    // 更新角色
    public void update_role() {
        User admin = getSessionAttr(Consts.USER_SESSION);
        if (User.V_A != admin.getInt(User.AUTHORITY)) {
            renderJson(Consts.AJAX_STATUS, Consts.AJAX_S); // 你没有权限
            return;
        }
        int role = getParaToInt("role");
        if (role < 0 || role > 2) {
            renderJson(Consts.AJAX_STATUS, Consts.AJAX_O); // 不存在该权限
            return;
        }
        User user = User.dao.findById(getParaToInt());
        if (user.getInt(User.ID) == admin.getInt(User.ID)) {
            renderJson(Consts.AJAX_STATUS, Consts.AJAX_O); // 请不要修改管理员的权限
            return;
        }
        boolean temp = user.set(User.AUTHORITY, role).updateUser();
        renderJson(Consts.AJAX_STATUS, temp); // 不存在该权限
    }
}
