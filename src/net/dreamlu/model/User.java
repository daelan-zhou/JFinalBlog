package net.dreamlu.model;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.dreamlu.config.Consts;
import net.dreamlu.utils.StringUtil;

import org.apache.commons.codec.digest.DigestUtils;

import com.jfinal.ext.plugin.db.DbModel;
import com.jfinal.kit.StrKit;

/**
 * 用户表
 * @author L.cm
 * email: 596392912@qq.com
 * site:  http://www.dreamlu.net
 */
public class User extends DbModel<User>{

    private static final long serialVersionUID = 4049101852276240918L;

    public static final User dao = new User();

    public static final String TABLE_NAME = "user";
    public static final String ID = "id";                                // id
    public static final String NICK_NAME = "nick_name";                    // 网名
    public static final String PASSWORD = "password";                    // 密码
    public static final String EMAIL = "email";                            // 邮箱
    public static final String EMAIL_VERIFY = "email_verify";            // 邮箱校验      默认：0 通过：1
    public static final int EMAIL_N = 0;                                // 邮箱校验      默认：0 
    public static final int EMAIL_Y = 1;                                // 邮箱校验      通过：1
    public static final String HEAD_PHOTO = "head_photo";                // 头像
    public static final String SEX = "sex";                                // 性别         默认：0 女   男：1 
    public static final String BIRTHDAY = "birthday";                    // 生日        
    public static final String STATUS = "status";                        // 是否登录状态  默认：0 在线：1
    public static final String LAST_LOGIN_TIME = "last_login_time";        // 最后登录时间
    public static final String SIGNATURE = "signature";                    // 个性签名
    public static final String URL = "url";                                // 网站
    public static final String LIVENESS = "liveness";                    // 活跃度
    public static final String CONTRIBUTION = "contribution";            // 贡献
    public static final String AUTHORITY = "authority";                    // 权限     默认：0[查看]  vip：1[可读写]  admin：2[可读写删除]
    public static final int    V_P = 0; // 查看
    public static final int    V_V = 1; // 可读写
    public static final int    V_A = 2; // 可读写删除
    public static final String DEL_STATUS = "del_status";                // 是否删除 默认：0  删除：1 
    public static final int DEL_N = 0;	// 未删
	public static final int DEL_Y = 1;	// 已删
    public static final String CREATE_TIME = "create_time";             // 添加时间
    public static final String UPDATE_TIME = "update_time";             // 更新时间

    // 添加用户的统一处理, 设置成超级权限
    public boolean saveUser(String email, String pwd) {
        this.set(EMAIL, email);
        this.set(NICK_NAME, email.substring(0, email.indexOf("@")));
        this.set(PASSWORD, StringUtil.pwdEncrypt(pwd));
        // avatar 头像
        this.set(HEAD_PHOTO, Consts.PHOTO_URL + DigestUtils.md5Hex(email.trim()));
        this.set(EMAIL_VERIFY, 1);
        this.set(AUTHORITY, V_A); // 设置成admin
        this.set(CREATE_TIME, new Date());
        this.set(UPDATE_TIME, new Date());
        return this.save();
    }
    
    // 微博用户的统一处理
    public boolean saveWbUser(String email, WbLogin wb) {
    	this.set(EMAIL, email);
    	this.set(NICK_NAME, wb.getStr(WbLogin.NICKNAME));
    	this.set(PASSWORD, StringUtil.pwdEncrypt(StringUtil.randomPwd(8)));
    	this.set(HEAD_PHOTO, wb.getStr(WbLogin.HEAD_PHOTO));
    	this.set(AUTHORITY, V_P); // 设置成admin
    	this.set(CREATE_TIME, new Date());
    	this.set(UPDATE_TIME, new Date());
        return this.save();
    }
    
    // 更新用户的统一处理
    public boolean updateUser(){
        return this.set(UPDATE_TIME, new Date()).update();
    }
    
    // 用户登录
    public User login(String email, String pwd){
        return dao.findFirst("select * from user where email = ? and password = ? and email_verify = 1 and del_status = 0 limit 1", email, pwd);
    }
    
    // 根据邮箱查找
    public User findByEmail(String mailTo) {
        String sql = "select u.id, u.nick_name from user u where u.email = ? and u.email_verify = 1 and u.del_status = 0 limit 1";
        return dao.findFirst(sql, mailTo);
    }

    // 根据id和状态
    public User findByIdStatus(Integer id) {
        String sql = "select u.* from user u where u.id = ? and u.email_verify = 1 and u.del_status = 0 limit 1";
        return dao.findFirst(sql, id);
    }

    // 查找用户数
    public long findCounts() {
        String sql = "SELECT COUNT(1) count FROM user";
        User user = dao.findFirst(sql);
        return null != user ? user.getLong("count") : null;
    }

    // dataTable
	public Map<String, Object> pageDataTables(int pageNum, int pageSize, String sEcho,
			String search, int userid) {
		final List<Object> parameters = new ArrayList<Object>();
		String select = "SELECT u.id, u.nick_name, u.sex, (SELECT COUNT(b.id) FROM  blog b WHERE u.id = b.user_id ) count , u.del_status, u.authority";
		StringBuilder sqlExceptSelect = new StringBuilder(" FROM  user u where u.id != ?");
		parameters.add(userid);
		if (StrKit.notBlank(search)) {
			sqlExceptSelect.append(" AND u.nick_name LIKE ?");
    		parameters.add("%" + search + "%");
		}
		sqlExceptSelect.append(" ORDER BY u.id DESC");
		return dao.paginateDataTables(pageNum, pageSize, select, sqlExceptSelect.toString(), sEcho, parameters.toArray());
	}
}
