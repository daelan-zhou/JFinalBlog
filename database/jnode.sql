/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50534
Source Host           : localhost:3306
Source Database       : jnode

Target Server Type    : MYSQL
Target Server Version : 50534
File Encoding         : 65001

Date: 2013-11-09 23:32:28
*/

SET FOREIGN_KEY_CHECKS=0;
SET names utf8;

-- ----------------------------
-- Table structure for blog
-- ----------------------------
DROP TABLE IF EXISTS `blog`;
CREATE TABLE `blog` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(200) DEFAULT NULL,
  `content` text,
  `user_id` int(11) NOT NULL,
  `view_count` int(11) DEFAULT '0',
  `blog_type` int(1) DEFAULT '0',
  `share_url` varchar(128) DEFAULT NULL,
  `del_status` int(1) DEFAULT '0',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of blog
-- ----------------------------
INSERT INTO `blog` VALUES ('1', '欢迎使用Jnode——JFinal之BAE开源博客', '<!--StartFragment -->\r\n<p>\r\n	&nbsp;这是一个神奇的网站，采用超带感的java web框架<a href=\"http://www.oschina.net/p/jfinal\" target=\"_blank\"><strong><span style=\"color:#E53333;\">JFinal</span></strong></a>和<span>缩进控爱好者的<a href=\"https://github.com/neuland/jade4j\" target=\"_blank\"><strong><span style=\"color:#E53333;\">Jade4j</span></strong></a>模板引擎！</span>\r\n</p>\r\n<p>\r\n	搭建于<a href=\"http://developer.baidu.com/bae\" target=\"_blank\"><strong><span style=\"color:#E53333;\">BAE</span></strong></a>（百度云）针对BAE 2.0设计，请勿在别的云环境安装，肯定会有各种问题！<img src=\"http://localhost:8080/kindeditor/plugins/emoticons/images/13.gif\" border=\"0\" alt=\"\" />\r\n</p>\r\n<p>\r\n	关于网站的配置敬请请关注：<span style=\"color:#222222;font-family:\'dejavu sans mono\', monospace;font-size:11px;line-height:normal;background-color:#FFFFFF;\"><a href=\"http://www.dreamlu.net/\" target=\"_blank\">DreamLu开源博客</a></span> \r\n</p>', '1', '1', '0', null, '0', '2013-11-02 15:25:47', '2013-11-09 23:28:27');

-- ----------------------------
-- Table structure for blog_tag
-- ----------------------------
DROP TABLE IF EXISTS `blog_tag`;
CREATE TABLE `blog_tag` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `blog_id` int(11) DEFAULT NULL,
  `tag_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of blog_tag
-- ----------------------------
INSERT INTO `blog_tag` VALUES ('1', '1', '1');
INSERT INTO `blog_tag` VALUES ('2', '1', '2');

-- ----------------------------
-- Table structure for links
-- ----------------------------
DROP TABLE IF EXISTS `links`;
CREATE TABLE `links` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` int(2) DEFAULT '0',
  `title` varchar(100) DEFAULT NULL,
  `url` varchar(256) DEFAULT NULL,
  `img` varchar(256) DEFAULT NULL,
  `orders` int(2) DEFAULT '0',
  `del_status` int(1) DEFAULT '0',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of links
-- ----------------------------
INSERT INTO `links` VALUES ('1', '0', 'DreamLu开源博客', 'http://www.dreamlu.net', null, '0', '0', '2013-11-02 15:26:27', '2013-11-02 15:26:27');

-- ----------------------------
-- Table structure for mail_verify
-- ----------------------------
DROP TABLE IF EXISTS `mail_verify`;
CREATE TABLE `mail_verify` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `verify_code` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of mail_verify
-- ----------------------------

-- ----------------------------
-- Table structure for options
-- ----------------------------
DROP TABLE IF EXISTS `options`;
CREATE TABLE `options` (
  `id` int(11) NOT NULL,
  `site_name` varchar(50) DEFAULT '',
  `site_url` varchar(64) DEFAULT '',
  `git_url` varchar(64) DEFAULT '',
  `cdn_path` varchar(64) DEFAULT '',
  `duoshuo_domain` varchar(20) DEFAULT '',
  `google` varchar(128) DEFAULT NULL,
  `wb_qq` varchar(128) DEFAULT NULL,
  `wb_sina` varchar(128) DEFAULT NULL,
  `record_number` varchar(10) DEFAULT NULL COMMENT '备案号',
  `site_version` varchar(8) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of options
-- ----------------------------
INSERT INTO `options` VALUES ('1', 'demo', 'http://demo.duapp.com', 'http://git.oschina.net/596392912/jnode', null, 'snode', 'https://plus.google.com',  'http://t.qq.com/', 'http://weibo.com/', null, 'v0.0.1');

-- ----------------------------
-- Table structure for tags
-- ----------------------------
DROP TABLE IF EXISTS `tags`;
CREATE TABLE `tags` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tag_name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tags
-- ----------------------------
INSERT INTO `tags` VALUES ('1', 'JFinal');
INSERT INTO `tags` VALUES ('2', 'Jnode');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nick_name` varchar(50) DEFAULT NULL,
  `password` varchar(64) NOT NULL,
  `email` varchar(50) DEFAULT NULL,
  `email_verify` int(1) DEFAULT '0',
  `head_photo` varchar(100) DEFAULT NULL,
  `sex` int(1) DEFAULT '0',
  `birthday` date DEFAULT NULL,
  `status` int(1) DEFAULT '0',
  `last_login_time` datetime DEFAULT NULL,
  `signature` varchar(100) DEFAULT NULL,
  `url` varchar(100) DEFAULT NULL,
  `liveness` int(11) DEFAULT '0',
  `contribution` int(11) DEFAULT '0',
  `authority` int(1) DEFAULT '0',
  `del_status` int(1) DEFAULT '0',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------

-- ----------------------------
-- Table structure for wb_login
-- ----------------------------
DROP TABLE IF EXISTS `wb_login`;
CREATE TABLE `wb_login` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `open_id` varchar(64) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `createtime` datetime DEFAULT NULL,
  `nickname` varchar(64) DEFAULT NULL,
  `head_photo` varchar(128) DEFAULT NULL,
  `type` varchar(64) DEFAULT NULL,
  `status` int(1) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of wb_login
-- ----------------------------

-- ----------------------------
-- Table structure for wx_leave_msg
-- ----------------------------
DROP TABLE IF EXISTS `wx_leave_msg`;
CREATE TABLE `wx_leave_msg` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `wx_user` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '微信用户',
  `msg` text COLLATE utf8_unicode_ci COMMENT '留言',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Records of wx_leave_msg
-- ----------------------------

-- ----------------------------
-- Table structure for wx_rule
-- ----------------------------
DROP TABLE IF EXISTS `wx_rule`;
CREATE TABLE `wx_rule` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `rule` varchar(128) DEFAULT NULL,
  `pid` int(11) DEFAULT NULL,
  `reply` text,
  `directions` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of wx_rule
-- ----------------------------
INSERT INTO `wx_rule` VALUES ('1', 'default', null, '/:,@-D你好，我是博客助手小v。您可以回复下列命令和我互动：\r\n输入“0”查看近期博文\r\n输入“1”查看热门博文\r\n输入“:中英文”进行翻译\r\n发送\"@城市\"进行天气查询\r\n输入“#内容”留言反馈\r\n回复“m”返回主菜单', null);
INSERT INTO `wx_rule` VALUES ('2', 'subscribe', null, '/:showlove欢迎关DreamLu开源博客，我是博客助手小v。您可以回复下列命令和我互动：\r\n输入“0”查看近期博文\r\n输入“1”查看热门博文\r\n输入“:中英文”进行翻译\r\n发送\"@城市\"进行天气查询\r\n输入“#内容”留言反馈\r\n回复“m”返回主菜单', null);
INSERT INTO `wx_rule` VALUES ('3', 'unsubscribe', null, '亲，你就这么离开了么！', null);
INSERT INTO `wx_rule` VALUES ('4', 'music', null, '这段音乐真美妙，我一定分享给我的主人！', null);
INSERT INTO `wx_rule` VALUES ('5', 'voice', null, '你的声音真好听，这是小v听到最美的声音了！', null);
INSERT INTO `wx_rule` VALUES ('6', 'image', null, '这张图片真好看！<a href=\"http://www.dreamlu.net/\">DreamLu开源博客</a>', null);
INSERT INTO `wx_rule` VALUES ('7', 'link', null, '是草榴的链接么？小v只喜欢逛逛草榴。<a href=\"http://www.dreamlu.net/\">DreamLu开源博客</a>', null);
INSERT INTO `wx_rule` VALUES ('8', 'leavemsg', null, '您的反馈已收到，感谢你的支持！', null);
INSERT INTO `wx_rule` VALUES ('9', 'location', null, '/::<位置信息还在开发中！', null);
