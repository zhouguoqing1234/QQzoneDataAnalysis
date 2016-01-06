package com.whut.zhou.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.whut.zhou.login.QQInfo;

import us.codecraft.webmagic.Site;


public class HelpUtils {
	//周国庆的数据
	private static  String START_URL = "http://taotao.qq.com/cgi-bin/emotion_cgi_msglist_v6?uin=qqhao&inCharset=utf-8&outCharset=utf-8&hostUin=qqhao&notice=0&sort=0&pos=page&num=20&cgi_host=http%3A%2F%2Ftaotao.qq.com%2Fcgi-bin%2Femotion_cgi_msglist_v6&code_version=1&format=jsonp&need_private_comment=1&g_tk=";
	
	private static  String ZAN_URL = "http://users.qzone.qq.com/cgi-bin/likes/get_like_list_app?uin=qqhao&unikey=http%3A%2F%2Fuser.qzone.qq.com%2Fqqhao%2Fmood%2Fzanurl.1&begin_uin=0&query_count=60&if_first_page=1&g_tk=";
	
	private static  String MESSAGE_URL = "http://m.qzone.qq.com/cgi-bin/new/get_msgb?uin=qqhao&hostUin=qqhao&num=10&start=page&hostword=0&essence=1&r=0.7908703845459968&iNotice=0&inCharset=utf-8&outCharset=utf-8&format=jsonp&ref=qzone&g_tk=";
	
	private static  String BlogNewList_URL = "http://user.qzone.qq.com/p/b1/cgi-bin/blognew/get_abs?hostUin=qqhao&uin=qqhao&blogType=0&cateName=&cateHex=&statYear=&reqInfo=1&pos=page&num=15"
			 				+ "&sortType=0&absType=0&source=0&rand=0.8597690088208765&ref=qzone&verbose=0&iNotice=0&inCharset=utf-8&outCharset=utf-8&format=jsonp&g_tk=";
	
	private static  String FriendBlogNewList_URL = "http://user.qzone.qq.com/p/b1/cgi-bin/blognew/get_abs?hostUin=741896367&uin=qqhao&blogType=0&cateName=&cateHex=&statYear=2015&reqInfo=5&pos=0&num=15&sortType=0&absType=0&source=0&rand=0.13947617961093783&inCharset=utf-8&outCharset=utf-8&ref=qzone&verbose=1&g_tk=";
	
	private static  String BlogNewContent_URL = "http://user.qzone.qq.com/p/b1/cgi-bin/blognew/get_blog?uin=qqhao&hostUin=qqhao&numperpage=15&blogId=blogidnum&arch=0&pos=0&direct=1&r=0.24552848655730486&iNotice=0&inCharset=utf-8&outCharset=utf-8&format=jsonp&ref=qzone&g_tk=";
	
	private static  String FriendEffect_URL = "http://sns.qzone.qq.com/cgi-bin/effect/effect_cgi_get?uin=qqhao&getuin=qqhao&random=0.6486926972866058&g_tk=";
	
	private static  String UserInfor_URL = "http://base.s8.qzone.qq.com/cgi-bin/user/cgi_userinfo_get_all?uin=qqhao&vuin=qqhao&fupdate=1&rd=0.12143053160980344&g_tk=";
	
	private static  String PhotoVistor_URL = "http://g.qzone.qq.com/cgi-bin/friendshow/cgi_get_visitor_simple?uin=qqhao&mask=2&mod=2&fupdate=1&g_tk=" ;
	
	private static  String QzoneVistor_URL = "http://g.qzone.qq.com/cgi-bin/friendshow/cgi_get_visitor_more?uin=qqhao&mask=7&page=1&fupdate=1&clear=1&sd=0.1124417798127979&g_tk=" ;
    
	private static  String PhotoComment_URL= "http://app.photo.qzone.qq.com/cgi-bin/app/cgi_pcomment_xml_v2?&callback=shine2_Callback&t=1448284000623&hostUin=qqhao&uin=qqhao&appid=4&cmtType=1&start=0&num=0&order=1"
    		      			+ "&inCharset=utf-8&outCharset=utf-8&source=qzone&plat=qzone&format=jsonp&topicId=V13Kr7Aj4YfwCl&callbackFun=shine2&_=1448283999791&g_tk=";
         

	private static String HOST_ID ;
	private static String SKEY ;
	private static String PTCZ ; 
	private static String UIN ;
	public static void getQQInfor(QQInfo qq){
		HOST_ID = qq.getQqhao();
		HashMap<String,String> cookies = qq.getCookies();
		SKEY = cookies.get("skey");
		PTCZ = cookies.get("ptcz");
		UIN = cookies.get("uin");
	}
	
	
	
	/**
	 * @param str
	 * cookie中skey值
	 * @return 计算g_tk值
	 */
	public static String getG_TK(String str) {
		int hash = 5381;
		for (int i = 0, len = str.length(); i < len; ++i) {
			hash += (hash << 5) + (int) (char) str.charAt(i);
		}
		return (hash & 0x7fffffff) + "";
	}
	
	public static Site getSite() {
		Site site = Site.me()
				.setUserAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.91 Safari/537.36")
				.setSleepTime(3000)
				.setTimeOut(20000)
				.setCharset("UTF-8")
				.addCookie("ptcz",PTCZ)
				.addCookie("skey", HelpUtils.getSkey())
				.addCookie("uin", UIN);
		return site;
	}
	
	public static String getJsonFromJsonp(String jsonp) {
		Pattern pattern = Pattern.compile("_Callback\\((.*)\\);");
		Matcher matcher = pattern.matcher(jsonp);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}
	
	/**
	 * the zan jsonp is not format,so must rebuild the jsonp
	 * @param jsonp
	 * @return
	 */
	public static String formatJsonp(String jsonp) {
		BufferedReader bufferedReader = null;
		bufferedReader = new BufferedReader(new StringReader(jsonp));
		String temp = null;
		StringBuilder builder = new StringBuilder();
		try {
			while ((temp = bufferedReader.readLine()) != null) {
				builder.append(temp);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}finally {
			try {
				bufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return builder.toString();
		
	}
	/**
	 * @return the start url with g_tk
	 */
	public static String getStartUrl() {
		//System.out.println("START_URL:" + START_URL);
		return (START_URL + getG_TK(SKEY)).replace("qqhao", HOST_ID);
	}
	
	public static String getZanUrl() {
		return (ZAN_URL + getG_TK(SKEY)).replace("qqhao", HOST_ID);
	}
	
	public static String getMESSAGE_URL() {
		return (MESSAGE_URL + getG_TK(SKEY)).replace("qqhao", HOST_ID);
	}
	
	public static String getBlogNewList_URL() {
		return (BlogNewList_URL+ getG_TK(SKEY)).replace("qqhao", HOST_ID);
	
	}
	
	public static String getFriendBlogNewList_URL() {
		return (FriendBlogNewList_URL+ getG_TK(SKEY)).replace("qqhao", HOST_ID);
	}

	public static String getBlogNewContent_URL(String blogidnum) {
		return (BlogNewContent_URL + getG_TK(SKEY)).replace("qqhao", HOST_ID).replace("blogidnum", blogidnum);   
		//return (BlogNewContent_URL + getG_TK(SKEY)).replace("qqhao", HOST_ID);
	}
	
	
	public static String getFriendEffect_URL() {
		return (FriendEffect_URL + getG_TK(SKEY)).replace("qqhao", HOST_ID);
	}
	
	public static String getUserInfor_URL() {
		return (UserInfor_URL + getG_TK(SKEY)).replace("qqhao", HOST_ID);
	}
	
	public static String getPhotoVistor_URL() {
		return (PhotoVistor_URL + getG_TK(SKEY)).replace("qqhao", HOST_ID);
	}

	public static String getQzoneVistor_URL() {
		return (QzoneVistor_URL + getG_TK(SKEY)).replace("qqhao", HOST_ID);
	}
	public static String getPhotoComment_URL() {
		return (PhotoComment_URL+ getG_TK(SKEY)).replace("qqhao", HOST_ID);
	}

	

	/**
	 * @return host_id
	 */
	public static String getHost_id() {
		return HOST_ID;
	}
	/**
	 * @return SKEY
	 */
	public static String getSkey() {
		return SKEY;
	}
	
}
