package net.dreamlu.model;


import com.jfinal.plugin.activerecord.Model;

/**
 * 第三方登录实体
 * @author L.cm
 * @date 2013-5-27 下午4:43:35
 */
public class WbLogin extends Model<WbLogin> {
    
    private static final long serialVersionUID = -2208881735661609831L;
    
    public static final WbLogin dao = new WbLogin();

    public static final String TABLE_NAME = "wb_login";
    public static final String ID = "id";                               // id
    public static final String OPENID = "open_id";                      // open_id
    public static final String USERID = "user_id";                      // user_id
    public static final String CREATETIME = "createtime";               // createtime
    public static final String NICKNAME = "nickname";                   // Nickname
    public static final String HEAD_PHOTO = "head_photo";               // 头像
    public static final String TYPE = "type";                           // type
    public static final String STATUS = "status";                       // 状态 默认0,1校验通过
    public static final int STATUS_N = 0;
    public static final int STATUS_Y = 1;
    
    /**
     * 根据openid查找
     * @param openid
     * @return
     */
    public WbLogin findByOpenID(String openid, String type) {
        String sql = "SELECT wb.* FROM wb_login wb WHERE wb.open_id = ? AND wb.type = ? limit 1";  
        return dao.findFirst(sql, openid, type);
    }
}
