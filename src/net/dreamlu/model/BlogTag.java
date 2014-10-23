package net.dreamlu.model;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.dreamlu.config.Consts;
import net.dreamlu.utils.StringUtil;

import com.jfinal.ext.plugin.db.DbModel;
import com.jfinal.plugin.activerecord.Db;

/**
 * 博文标签表
 * @author L.cm
 * email: 596392912@qq.com
 * site:  http://www.dreamlu.net
 * @date Jun 18, 2013 11:14:28 PM
 */
public class BlogTag extends DbModel<BlogTag> {

    private static final long serialVersionUID = -2208881735662609833L;
    public static final BlogTag dao = new BlogTag();

    public static final String TABLE_NAME = "blog_tag";

    public static final String ID = "id";                    // id
    public static final String BLOG_ID = "blog_id";            // blog_id
    public static final String TAG_ID = "tag_id";            // tag_id

    // 根据博文id查询
    public List<BlogTag> findByBlogId(Integer blogId) {
        String sql = "SELECT bt.id, t.tag_name FROM blog_tag bt, tags t WHERE bt.blog_id = ? AND t.id = bt.tag_id";
        return dao.find(sql, blogId);
    }
    
    // 根据博文id查询
    public List<BlogTag> findCacheListByBlogId(Integer blogId) {
        String sql = "SELECT bt.id, t.tag_name FROM blog_tag bt, tags t WHERE bt.blog_id = ? AND t.id = bt.tag_id";
        return dao.findByCache(Consts.CACHE_TIME_MINI, sql, blogId);
    }

    // 根据博文id查询
    public List<Integer> findIdsByBlogId(Integer blogId) {
        String sql = "SELECT bt.id FROM blog_tag bt WHERE bt.blog_id = ?";
        return Db.query(sql, blogId);
    }

    // 保存所有标签
    public boolean saveAllTags(Integer blogId, Object[] tags) {
        final List<Object> parameters = new ArrayList<Object>();
        StringBuilder sql = new StringBuilder("insert into blog_tag(blog_id, tag_id) values");
        for (int i = 0; i < tags.length; i++) {
            sql.append("(?, ?)");
            if (tags.length -1 != i) {
                sql.append(",");
            }
            parameters.add(blogId);
            parameters.add(tags[i]);
        }
        int count = Db.update(sql.toString(), parameters.toArray());
        return count == tags.length;
    }

    //删除所有的标签 
    public boolean removeAllTags(Integer blogId) {
        String sql = "DELETE FROM blog_tag WHERE blog_tag.blog_id = ?";
        int count = Db.update(sql, blogId);
        return count > 0;
    }

    //删除遗留标签
    public boolean removeTags(Integer blogId, Object[] tags) {
        final List<Object> parameters = new ArrayList<Object>();
        String sql = "DELETE FROM blog_tag WHERE blog_tag.blog_id = ? AND blog_tag.tag_id IN (" + StringUtil.sqlHolder(tags.length) + ")";
        parameters.add(blogId);
        parameters.addAll(Arrays.asList(tags));
        int count = Db.update(sql, parameters.toArray());
        return count > 0;
    }

    // 删除博文标签
    public int deleteByTagId(int tagid) {
        String sql = "DELETE FROM blog_tag WHERE tag_id = ?";
        return Db.update(sql, tagid);
    }
}
