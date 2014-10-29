package net.dreamlu.model;

import net.dreamlu.kit.HttpKit;
import net.dreamlu.utils.DateUtil;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.ext.plugin.redis.JedisKit;

public class DailyEnglish {
	private static final String ENG_API = "http://open.iciba.com/dsapi";
	private static final String KEY_PREFIX = "DAILYENGLISH_";
	
	private String sid;
	private String tts;
	private String content;
	private String note;
	private String translation;
	private String date;
	private String picture;
	private String pictureBig;
	public String getSid() {
		return sid;
	}
	public void setSid(String sid) {
		this.sid = sid;
	}
	public String getTts() {
		return tts;
	}
	public void setTts(String tts) {
		this.tts = tts;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getTranslation() {
		return translation;
	}
	public void setTranslation(String translation) {
		this.translation = translation;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getPicture() {
		return picture;
	}
	public void setPicture(String picture) {
		this.picture = picture;
	}
	public String getPictureBig() {
		return pictureBig;
	}
	public void setPictureBig(String pictureBig) {
		this.pictureBig = pictureBig;
	}
	
	public static DailyEnglish make(String jsonStr) {
		try {
			JSONObject json = JSONObject.parseObject(jsonStr);
			DailyEnglish de = new DailyEnglish();
			de.setSid(json.getString("sid"));
			de.setContent(json.getString("content"));
			de.setNote(json.getString("note"));
			de.setTranslation(json.getString("translation"));
			de.setDate(json.getString("dateline"));
			de.setPicture(json.getString("picture"));
			de.setPictureBig(json.getString("picture2"));
			return de;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static DailyEnglish get() throws Exception{
		String today = DateUtil.getToday();
		String key = KEY_PREFIX+today;
		String resp;
		if(JedisKit.exists(key)){
			resp = JedisKit.get(key);
		}else{
			resp = HttpKit.get(ENG_API);
			JedisKit.set(key, resp);
		}
		return make(resp);
	}
	
	
	public static void main(String[] args) throws Exception {
		DailyEnglish d = DailyEnglish.get();
		System.out.println(d.getDate());
	}
}
