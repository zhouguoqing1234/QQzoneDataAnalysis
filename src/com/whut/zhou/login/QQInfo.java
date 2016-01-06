package com.whut.zhou.login;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;


@SuppressWarnings("deprecation")
public class QQInfo {
	/** QQ帐号 **/
	private String qqhao;
	
	/** QQ密码 **/
	private String password;
	/** QQ空间cookies**/
	protected HashMap<String,String> cookies;
	/** QQ密码加密结果**/
	protected String p;
	
	/**
	 * 链接之间各种关联�?
	 * 链接2的login_sig=链接1 cookie的pt_login_sig
	 * 链接2的返回结果checkVC的第�?个参数为0时的关系：链�?3的verifycode=checkVC的第二个参数�?
	 * 链接3的pt_verifysession_v1=链接2cookie的ptvfsession�?
	 * 链接3的p=加密后的密码
	 */
	
	private static String login_url1 = "http://xui.ptlogin2.qq.com/cgi-bin/xlogin?proxy_url=http%3A//qzs.qq.com/qzone/v6/portal/proxy.html&daid=5&pt_qzone_sig=1&hide_title_bar=1&low_login=0&qlogin_auto_login=1&no_verifyimg=1&link_target=blank&appid=549000912&style=22&target=self&s_url=http%3A%2F%2Fqzs.qq.com%2Fqzone%2Fv5%2Floginsucc.html%3Fpara%3Dizone&pt_qr_app=%E6%89%8B%E6%9C%BAQQ%E7%A9%BA%E9%97%B4&pt_qr_link=http%3A//z.qzone.com/download.html&self_regurl=http%3A//qzs.qq.com/qzone/v6/reg/index.html&pt_qr_help_link=http%3A//z.qzone.com/download.html";
	private static String login_url2 = "http://check.ptlogin2.qq.com/check?regmaster=&pt_tea=1&pt_vcode=1&uin=【QQ号码�?&appid=549000912&js_ver=10132&js_type=1&login_sig=【pt_login_sig�?&u1=http%3A%2F%2Fqzs.qq.com%2Fqzone%2Fv5%2Floginsucc.html%3Fpara%3Dizone&r=0.8120620810567006";
	private static String login_url3 = "http://ptlogin2.qq.com/login?u=【QQ号码�?&verifycode=【verifycode�?&pt_vcode_v1=0&pt_verifysession_v1=【ptvfsession�?&p=【p�?&pt_randsalt=0&u1=http%3A%2F%2Fqzs.qq.com%2Fqzone%2Fv5%2Floginsucc.html%3Fpara%3Dizone&ptredirect=0&h=1&t=1&g=1&from_ui=1&ptlang=2052&action=5-15-1441425206927&js_ver=10132&js_type=1&login_sig=【pt_login_sig�?&pt_uistyle=32&aid=549000912&daid=5&pt_qzone_sig=1";
	
	
	
	
	public QQInfo(String qqhao,String password){
		this.qqhao = qqhao;
		this.password = password;
		System.out.println("qqhao:"+qqhao);
		System.out.println("password:"+password);
	}
	
	public String getQqhao() {
		return qqhao;
	}

	
	public static  String getCookie(String url) {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(url);
        String cookie = null;
        // 执行get请求
        try {
            HttpResponse response = httpclient.execute(httpget);
            CookieStore cookieStore = httpclient.getCookieStore();
            cookie=cookieStore.getCookies().toString();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
       return cookie;
    }


	public HashMap getCookies() {
		 //从访问第�?个链接login_url1的response中提取cookies pt_login_sig的�??
		 String cookieFromLogin_url1=QQInfo.getCookie(login_url1);
		 int indexbegin=cookieFromLogin_url1.indexOf("pt_login_sig")+21;
	     String pt_login_sig= cookieFromLogin_url1.substring(indexbegin, indexbegin+64);
	     
	   //从访问第二个链接login_url2的response中提取verifycode的�?�和 ptvfsession的�??
	     login_url2 = "http://check.ptlogin2.qq.com/check?regmaster=&pt_tea=1&pt_vcode=1&uin="+qqhao+"&appid=549000912&js_ver=10132&js_type=1&login_sig="+pt_login_sig+"&u1=http%3A%2F%2Fqzs.qq.com%2Fqzone%2Fv5%2Floginsucc.html%3Fpara%3Dizone&r=0.8120620810567006";
	     GetRequest request=new GetRequest();
	     String cookieFromLogin_url2 = request.GetRequest(login_url2);
	     String ptvfsession =cookieFromLogin_url2.substring(60, 172);
	     String verifycode = cookieFromLogin_url2.substring(18, 22);
	  
	     
	     
	   //调用qq.js里的getEncryption方法获得加密后的p
	     try {
	    	 ScriptEngineManager m = new ScriptEngineManager();
	    	 ScriptEngine se = m.getEngineByName("javascript");
	    	 File file = new File(QQInfo.class.getClassLoader().getResource("../../qq_RSA.js").getPath());
	    	 se.eval(new FileReader(file));
	    	 Invocable invocable = (Invocable)se;
	    	 Object o = invocable.invokeFunction("getEncryption", new Object[]{password,qqhao,verifycode,null});
	    			// .invokeFunction("getEncryption", password,uin,verifycode,false);
	    	  p=o.toString();
	    	 } catch (Exception e) {
	    	 e.printStackTrace();
	    	 }
	     
	     //访问第三个链接login_url3登录qq获取cookies
	     
	     login_url3="http://ptlogin2.qq.com/login?u="+qqhao+"&verifycode="+verifycode+"&pt_vcode_v1=0&pt_verifysession_v1="+ptvfsession+"&p="+p+"&pt_randsalt=0&u1=http%3A%2F%2Fqzs.qq.com%2Fqzone%2Fv5%2Floginsucc.html%3Fpara%3Dizone&ptredirect=0&h=1&t=1&g=1&from_ui=1&ptlang=2052&action=5-15-1441425206927&js_ver=10132&js_type=1&login_sig="+pt_login_sig+"&pt_uistyle=32&aid=549000912&daid=5&pt_qzone_sig=1";

	     String cookieFromLogin_url3=QQInfo.getCookie(login_url3);
	     System.out.println(cookieFromLogin_url3);
	     indexbegin =cookieFromLogin_url3.indexOf("uin")+12;
	    
	     String uin= cookieFromLogin_url3.substring(indexbegin, indexbegin+11);
	     System.out.println("uin:"+uin);
		 indexbegin=cookieFromLogin_url3.indexOf("ptcz")+13;
	     String ptcz= cookieFromLogin_url3.substring(indexbegin, indexbegin+64);
	     indexbegin=cookieFromLogin_url3.indexOf("skey")+13;
	     String skey=cookieFromLogin_url3.substring(indexbegin, indexbegin+10);
	     cookies=new HashMap<String,String>();
	     cookies.put("uin",uin);
	     cookies.put("password",password);
	     cookies.put("ptcz",ptcz);
	     cookies.put("skey",skey);
	  
	     return cookies;
	}
}
