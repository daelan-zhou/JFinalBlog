package net.dreamlu.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.dreamlu.api.life.HelpApi;
import net.dreamlu.config.Consts;
import net.dreamlu.model.Blog;
import net.dreamlu.model.WxLeaveMsg;
import net.dreamlu.model.WxRule;
import net.dreamlu.utils.HtmlFilter;
import net.dreamlu.utils.StringUtil;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gson.bean.Articles;
import com.gson.bean.InMessage;
import com.gson.bean.OutMessage;
import com.gson.inf.MessageProcessingHandler;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Logger;

/**
 * 微信消息回复
 * @author L.cm
 * email: 596392912@qq.com
 * site:  http://www.dreamlu.net
 * @date 2013-6-20 下午3:03:34
 */
public class WeChatMessageHandler implements MessageProcessingHandler {

    private static final Logger log = Logger.getLogger(WeChatMessageHandler.class);

    // 定义基本的关键字符
    private static final String[] KEYS = {"@", ":", "#"};
    private OutMessage out = null;
    
    // 消息处理
    @Override
    public OutMessage exeMsg(InMessage msg) {
        out = new OutMessage();
        out.setCreateTime(new Date().getTime());
        out.setToUserName(msg.getFromUserName());
        out.setFromUserName(msg.getToUserName());
        // 根据 msg 类型分别执行
        String type = msg.getMsgType();
        //针对不同类型消息进行处理
        if (type.equals(MSG_TYPE_TEXT)) {
            textTypeMsg(msg);
        } else if (type.equals(MSG_TYPE_LOCATION)) {
            locationTypeMsg(msg);
        } else if (type.equals(MSG_TYPE_LINK)) {
            linkTypeMsg(msg);
        } else if (type.equals(MSG_TYPE_IMAGE)) {
            imageTypeMsg(msg);
        } else if (type.equals(MSG_TYPE_EVENT)) {
            eventTypeMsg(msg);
        } else if (type.equals(MSG_TYPE_VOICE)) {
            voiceTypeMsg(msg);
        }
        // 如果信息都没找到 *这个流程设计得不够好仅供参考...
        String reply = out.getContent();
        List<Articles> lists = out.getArticles();
        if (StrKit.isBlank(reply) && (null == lists || lists.size() < 1)) {
            query(WxRule.D);
        }
        return out;
    }
    // 图片消息处理
    private void imageTypeMsg(InMessage msg) {
        query(msg.getMsgType());
    }
    // 链接消息
    private void linkTypeMsg(InMessage msg) {
        query(msg.getMsgType());
    }

    // 声音消息, *注意开通语音识别后能返回识别的信息
    private void voiceTypeMsg(InMessage msg) {
        query(msg.getMsgType());
    }
    
    // 返回地理位置信息
    private void locationTypeMsg(InMessage msg) {
        query(msg.getMsgType());
    }
    
    // 执行查询
    public void query(String rule) {
        WxRule wxRule = WxRule.dao.findByRule(rule);
        if (null != wxRule) {
            out.setContent(wxRule.getStr(WxRule.REPLY));
        }
    }
    
    // 事件消息
    private void eventTypeMsg(InMessage msg) {
        // Event subscribe(订阅)、unsubscribe(取消订阅)、CLICK(自定义菜单点击事件)
        // EventKey 事件KEY值，与自定义菜单接口中KEY值对应
        String event = msg.getEvent();
        String rule = WxRule.D;
        if(StrKit.isBlank(event)){
        } else if ("subscribe".equals(event.toLowerCase())){
            rule = "subscribe";
        } else if ("unsubscribe".equals(event.toLowerCase())){
            rule = "unsubscribe";
        } else if ("click".equals(event.toLowerCase())){
            rule = msg.getEventKey();
        }
        query(rule);
    }
    
    // 文字消息处理 {"m", "0", "1"}
    private void textTypeMsg(InMessage msg) {
        // 获取发送的内容
        String content = msg.getContent();
        if (StrKit.isBlank(content)) {
            query(WxRule.D);
            return;
        }
        // 全角半角转换
        content = StringUtil.togglecase(content);
        // 定义 @ # ：符号
        List<String> list = Arrays.asList(KEYS);
        if (list.contains(content) || content.equals("m")) {
            query(WxRule.D);
            return;
        }
        // 0，查看近期文章 1,查看热门文章 8条
        if ("0".equals(content)){
            blogs(0);
        } else if ("1".equals(content)) {
            blogs(1);
        }else if (content.startsWith(KEYS[0])) {
            content = content.substring(1, content.length());
            weather(content);
        }else if (content.startsWith(KEYS[1])) {
            content = content.substring(1, content.length());
            translate(content);
        }else if (content.startsWith(KEYS[2])) {
            content = content.substring(1, content.length());
            // 保存留言信息
            WxLeaveMsg wxMsg = new WxLeaveMsg();
            wxMsg.set(WxLeaveMsg.WX_USER, msg.getFromUserName()).set(WxLeaveMsg.MSG, content).save();
            query("leavemsg");
        } else {
            query(content);
        }
    }
    // 天气处理
    private void weather(String city) {
        JSONArray infoList = null;
        try {
            infoList = HelpApi.weather(city);
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        
        List<Articles> list = new ArrayList<Articles>();
        boolean isFirst = true;
        Iterator<Object> iterator = infoList.iterator();
        while (iterator.hasNext()) {
            JSONObject json = (JSONObject) iterator.next();
            Articles articles = new Articles();
            Calendar now = Calendar.getInstance();
            int hour = now.get(Calendar.HOUR_OF_DAY);
            // 12点后的显示的第一天为晚上
            if (hour > 12 && isFirst) {
                articles.setPicUrl(json.getString("nightPictureUrl"));
                isFirst = false;
            } else {
                articles.setPicUrl(json.getString("dayPictureUrl"));
            }
            StringBuffer sb = new StringBuffer();
            sb.append(json.getString("date")).append(" ");
            sb.append(json.getString("weather")).append(" ");
            sb.append(json.getString("wind")).append(" ");
            sb.append(json.getString("temperature"));
            articles.setTitle(sb.toString());
            articles.setDescription(sb.toString());
            articles.setUrl(Consts.DOMAIN_URL);
            list.add(articles);
        }
        out.setArticles(list);
        out.setArticleCount(list.size());
        out.setMsgType(MSG_TYPE_NEWS);
    }
    // 翻译
    private void translate(String word) {
        StringBuffer outmsg = new StringBuffer();
        try {
            JSONArray result = HelpApi.translate(word);
            Iterator<Object> iterator = result.iterator();
            while (iterator.hasNext()) {
                JSONObject json = (JSONObject) iterator.next();
                outmsg.append(json.getString("dst")).append("\t");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        out.setContent(outmsg.toString());
    }
    // 近期博文8条
    private void blogs(int type) {
        List<Articles> list = new ArrayList<Articles>();
        List<Blog> blogs;
        if (type == 0) {
            blogs = Blog.dao.findLateList(8);
        } else {
            blogs = Blog.dao.findHotList(8);
        }
        for (Blog blog : blogs) {
            Articles articles = new Articles();
            // 内容
            articles.setTitle(blog.getStr(Blog.TITLE));
            String content = blog.getStr(Blog.CONTENT);
            articles.setDescription(content);
            // 提取文章里的图片，没图片则默认
            String imgSrc = HtmlFilter.getImgSrc(content);
            if (StrKit.isBlank(imgSrc)) {
                articles.setPicUrl(Consts.DOMAIN_URL + "/images/weixin.jpg");
            } else {
                articles.setPicUrl(imgSrc);
            }
            articles.setUrl(Consts.DOMAIN_URL + "/blog/" + blog.getNumber(Blog.ID));
            list.add(articles);
        }
        out.setArticles(list);
        out.setArticleCount(list.size());
        out.setMsgType(MSG_TYPE_NEWS);
    }
}
