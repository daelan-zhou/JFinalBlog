package net.dreamlu.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.dreamlu.config.Consts;

import com.jfinal.ext.plugin.db.DbModel;
import com.jfinal.kit.StrKit;

/**
 * 博文标签
 * @author L.cm
 * email: 596392912@qq.com
 * site:  http://www.dreamlu.net
 * @date 2013-6-6 下午1:34:44
 */
public class Tags extends DbModel<Tags> {

    private static final long serialVersionUID = -2208881735662609833L;

    public static final Tags dao = new Tags();
    
    public static final String TABLE_NAME = "tags";
    public static final String ID = "id";                    // id
    public static final String TAG_NAME = "tag_name";        // 标签名
    
    // 根据标签名查找
    public Tags findByName(String tagName) {
        String sql = "SELECT t.id from tags t WHERE t.tag_name = ? LIMIT 1";
        return dao.findFirstByCache(Consts.CACHE_TIME_MINI, sql, tagName);
    }
    
    // tags list
    public List<Tags> findTagList(int num) {
        String sql = "SELECT t.* from tags t LIMIT ?";
        return dao.findByCache(Consts.CACHE_TIME_MINI, sql, num);
    }

    // 根据标签名查询
    public List<Tags> findByNameCount(String query, int count) {
        final List<Object> parameters = new ArrayList<Object>();
        StringBuilder sql = new StringBuilder("SELECT t.id, t.tag_name FROM tags t");
        if (StrKit.notBlank(query)) {
            sql.append(" WHERE t.tag_name  LIKE ? ");
            parameters.add("%" + query + "%");
        }
        sql.append(" LIMIT ?");
        parameters.add(count);
        return dao.findByCache(Consts.CACHE_TIME_MINI, sql.toString(), parameters.toArray());
    }

    // 查找热门标签 
    public List<Tags> findHostTags(int count) {
        String sql = "SELECT t.tag_name, COUNT(bt.id) AS num FROM tags t, blog_tag bt WHERE t.id = bt.tag_id GROUP BY t.id ORDER BY num DESC LIMIT ?";
        return dao.findByCache(Consts.CACHE_TIME_MINI, sql, count);
    }

    // data table
    public Map<String, Object> pageDataTables(int pageNum, int pageSize, String sEcho,
            String search) {
    	final List<Object> parameters = new ArrayList<Object>();
		String select = "SELECT t.*, (SELECT count(1) FROM blog_tag bt WHERE t.id = bt.tag_id ) num";
		StringBuilder sqlExceptSelect = new StringBuilder(" FROM tags t");
    	if (StrKit.notBlank(search)) {
    		sqlExceptSelect.append(" AND t.tag_name like ?");
    		parameters.add("%" + search + "%");
    	}
		sqlExceptSelect.append(" ORDER BY num DESC");
    	return dao.paginateDataTables(pageNum, pageSize, select, sqlExceptSelect.toString(), sEcho, parameters.toArray());
    }
}
