package net.dreamlu.model;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.dreamlu.config.Consts;

import com.jfinal.ext.plugin.db.DbModel;
import com.jfinal.kit.StrKit;

/**
 * 网站相关的链接 广告，友情链接
 * @author L.cm
 * email: 596392912@qq.com
 * site:  http://www.dreamlu.net
 * @date 2013-5-30 下午3:28:32
 */
public class Links extends DbModel<Links> {

    private static final long serialVersionUID = -1561161846934854254L;
    public static final Links dao = new Links();

    public static final String TABLE_NAME = "links";
    public static final String ID = "id";                               // id
    public static final String TYPE = "type";                           // 分类  默认:0 友情链接  广告：1 其他待定
    public static final String TITLE = "title";                         // 链接说明
    public static final String URL = "url";                             // 链接url
    public static final String IMG = "img";                             // 链接图片
    public static final String ORDERS = "orders";                       // 排序id
    public static final String DEL_STATUS = "del_status";               // 是否删除 默认：0  删除：1 
    public static final String CREATE_TIME = "create_time";             // 添加时间
    public static final String UPDATE_TIME = "update_time";             // 更新时间

    public static final int DEL_N = 0;    // 未删
    public static final int DEL_Y = 1;    // 已删
    
    // 统一保存
    public boolean saveLinks() {
        this.set(Links.DEL_STATUS, Links.DEL_N);
        this.set(Links.CREATE_TIME, new Date());
        this.set(Links.UPDATE_TIME, new Date());
        return this.save();
    }
    
    // 统一保存
    public boolean updateLinks() {
        this.set(Links.UPDATE_TIME, new Date());
        return this.update();
    }
    
    /**
     * 从缓存查找连接 
     */
    public List<Links> findListByType(int type, Integer status) {
        final List<Object> parameters = new ArrayList<Object>();
        StringBuilder sql = new StringBuilder("SELECT l.* FROM links l WHERE 1 = 1 AND l.type =? ");
        parameters.add(type);
        if ( null != status ) {
            sql.append(" AND l.del_status = ? ");
            parameters.add(status.intValue());
        }
        sql.append(" ORDER BY l.orders, l.id ASC");
        return dao.findByCache(Consts.CACHE_TIME_MAX, sql.toString(), parameters.toArray());
    }

    /**
     * 链接管理
     */
    public Map<String, Object> pageDataTables(int pageNum, int pageSize, String sEcho, String search) {
        final List<Object> parameters = new ArrayList<Object>();
        String select = "SELECT l.*";
        StringBuilder sqlExceptSelect = new StringBuilder(" FROM links AS l");
        if (StrKit.notBlank(search)) {
            sqlExceptSelect.append(" AND l.title like ?");
            parameters.add("%" + search + "%");
        }
        sqlExceptSelect.append(" ORDER BY l.id DESC");
        return dao.paginateDataTables(pageNum, pageSize, select, sqlExceptSelect.toString(), sEcho, parameters.toArray());
    }
}