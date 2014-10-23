package net.dreamlu.controller.web;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.dreamlu.model.Blog;
import net.dreamlu.model.BlogTag;
import net.dreamlu.model.Tags;
import net.dreamlu.utils.StringUtil;

import org.apache.commons.lang.StringUtils;

import com.jfinal.core.Controller;
import com.jfinal.ext.route.ControllerBind;

/**
 * 博文详情
 * @author L.cm
 * @date 2013-5-14 下午5:08:12
 */
@ControllerBind(controllerKey="/blog", viewPath="web")
public class BlogController extends Controller {

    public void index() {
        int id = getParaToInt(0, 1);
        // 读写分离
        Blog blog = Blog.dao.findFallById(id);
        if (null == blog) { // 对于没有的博文跳转到404
            renderError(404);
        }
        Blog db_blog = Blog.dao.findById(id);
        db_blog.set(Blog.VIEW_COUNT, db_blog.getInt(Blog.VIEW_COUNT) + 1).update();
        // 查找标签tags，标签相关
        List<BlogTag> list = BlogTag.dao.findCacheListByBlogId(id);
        Set<String> tagSet = new HashSet<String>();
        for (BlogTag blogTag : list) {
        	tagSet.add(blogTag.getStr(Tags.TAG_NAME));
        }
        String keyWords = StringUtils.join(tagSet, ",");
        // 标签栏
        String tags = StringUtil.getTags(list);
        setAttr("blog", blog);
        setAttr("keyWords", keyWords);
        setAttr("tags", tags);
        setAttr("typeName", Blog.TYPE_NAME[blog.getInt(Blog.BLOG_TYPE)]);
        setAttr("typeUrl", Blog.TYPE_URL[blog.getInt(Blog.BLOG_TYPE)]);
        render("blog.vm");
    }
}
