package com.whut.zhou.login;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;

public class GetRequest {
	
	/** 代理 */
	protected Proxy proxy;
	/** 当前用户Cookie */
	protected String cookie = "";
	/** 浏览�? */
	protected String userAgent;
	
	/**
	 * �?次Get请求
	 * 
	 * 1.默认发�?�该对象的{@link #cookie Cookie}�?
	 * 2.默认保存从服务器接手到的{@link #cookie Cookie}
	 * 3.默认使用该对象的{@link #proxy 代理}，若没有{@link #proxy 代理}，则直接请求
	 * 4.默认使用该对象，通过{{@link #userAgent}指定的浏览器
	 * @param urlStr  请求地址
	 * @return  请求获得的网页内�?
	 */
	public String GetRequest(String urlStr){
		return request(urlStr, "get", null, null);
	}
	
	/**
	 * �?个HTTP请求，包括该对象�? proxy属�?�，cookie属�?�，userAgent属�??
	 * 
	 * 1.默认发�?�该对象的{@link #cookie Cookie}�?
	 * 2.默认保存从服务器接手到的{@link #cookie Cookie}
	 * 3.默认使用该对象的{@link #proxy 代理}，若没有{@link #proxy 代理}，则直接请求
	 * 4.默认使用该对象，通过{{@link #userAgent}指定的浏览器
	 * @param urlStr  请求地址
	 * @param type    请求类型 "post","get",默认"get"
	 * @param sc      
	 * @param params  请求参数，Post方法才会使用
	 * @see {@SendRequestUtil#doRequest(String urlStr, String type, SetHttpConnection sc, String params, String cookie, Proxy proxy, String userAgent)}
	 * @return  请求获得的网页内�?
	 */
	public String request(String urlStr, String type, SetHttpConnection sc, String params){
		return request(urlStr, type, sc, params, this.userAgent);
	}
	
	/**
	 * �?个HTTP请求，包括该对象�? proxy属�?�，cookie属�?�，userAgent属�??
	 * 1.默认发�?�该对象的{@link #cookie Cookie}�?
	 * 2.默认保存从服务器接手到的{@link #cookie Cookie}
	 * 3.默认使用该对象的{@link #proxy 代理}，若没有{@link #proxy 代理}，则直接请求
	 * 4.默认使用该对象，通过{{@link #userAgent}指定的浏览器
	 * @param urlStr  请求地址
	 * @param type    请求类型 "post","get",默认"get"
	 * @param sc      
	 * @param params  请求参数，Post方法才会使用
	 * @param userAgent 指定浏览器参�?
	 * 
	 * @see {@SendRequestUtil#doRequest(String urlStr, String type, SetHttpConnection sc, String params, String cookie, Proxy proxy, String userAgent)}
	 * @return  请求获得的网页内�?
	 */
	public String request(String urlStr, String type, SetHttpConnection sc, String params, String userAgent){
		return doRequest(urlStr, type, sc!=null?sc:new SetHttpConnection() {
			
			
			public String before(HttpURLConnection httpConn) throws ProtocolException {
				return null;
			}
			
		
			public String after(HttpURLConnection httpConn) {
//				List<String> lists = httpConn.getHeaderFields().get("Set-Cookie");
				List lists = (List) httpConn.getHeaderFields().get("Set-Cookie");
//				cookie = mergeCookies(cookie, lists);
				cookie = "";
				return cookie;
			}
		}, params, cookie, proxy, userAgent);
	}
	
	
	/**
	 * 
	 * @param urlStr    请求地址
	 * @param type      请求类型 "post" "get",若没有则默认get
	 * @param sc        当次请求的需要设置请求前或请求后的操作→{@link SetHttpConnection}
	 * @param params    当次请求的参�?
	 * @param cookie    当次请求的Cookie
	 * @param Proxy     {@link Proxy 代理}
	 * @param userAgent 使用浏览�?
	 * 
	 * @return 获取请求的网页字符串
	 */
	public static String doRequest(String urlStr, String type, SetHttpConnection sc, String params, String cookie, Proxy proxy, String userAgent) {
		// 下面的index.jsp�?<servlet-mapping>映射�?
		// �?个Servlet(com.quantanetwork.getClientDataServlet)
		// 该Servlet的注意点下边会提�?
		URL url = null;
		URLConnection rulConnection = null;
		HttpURLConnection httpUrlConnection =null;
		OutputStream outStrm = null;
		BufferedReader br = null;
		//如果浏览器没有指定，则使用默认的浏览�?
//		userAgent = userAgent!=null && !userAgent.equals("")?userAgent:SendRequestUtil.userAgent;
		
		try {
			url = new URL(urlStr);
			//如果�?要代�?
			if( null != proxy)
				rulConnection = url.openConnection(proxy);
			else
				rulConnection = url.openConnection();
			
			if(urlStr.toLowerCase().indexOf("https") == 0){        //如果是Https请求，更改httpss=https 2013�?7�?24�? 09:51:53
				httpUrlConnection = (HttpsURLConnection) rulConnection;
			}else                                                  //如果是Http请求
				httpUrlConnection = (HttpURLConnection) rulConnection;
				

			// 设置是否向httpUrlConnection输出，因为这个是post请求，参数要放在
			// http正文内，因此�?要设为true, 默认情况下是false;
			//如果是Post请求
			if(type.equalsIgnoreCase("post"))
				httpUrlConnection.setDoOutput(true);

			// 设置是否从httpUrlConnection读入，默认情况下是true;
			httpUrlConnection.setDoInput(true);

			// 请求不能使用缓存
			httpUrlConnection.setUseCaches(false);

			// 设定传�?�的内容类型是可序列化的java对象
			// (如果不设此项,在传送序列化对象�?,当WEB服务默认的不是这种类型时可能抛java.io.EOFException)

			//如果是Post请求
			if(type.equalsIgnoreCase("post")){
				httpUrlConnection.setRequestProperty("Content-type","application/x-www-form-urlencoded");
				httpUrlConnection.setRequestProperty("Content-Length","1024");
			}
			//如果有Cookie
			if(null != cookie)
				httpUrlConnection.setRequestProperty("Cookie",cookie);
			if (null != sc) {
				sc.before(httpUrlConnection);
			}
			httpUrlConnection.setRequestProperty("Accept-Charset","utf-8, gbk,* ;q=0.1"); // 设置请求�?
			httpUrlConnection.setRequestProperty("User-Agent",userAgent); // 设置请求�?
			httpUrlConnection.setRequestProperty("accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			httpUrlConnection.setRequestProperty("accept-language","zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
			httpUrlConnection.setRequestProperty("Connection", "keep-alive");
			
			// 设定请求的方法为"POST"，默认是GET
			//如果是Post请求
			if(type.equalsIgnoreCase("post")){
				httpUrlConnection.setRequestMethod("POST");
				// 连接，从上述�?2条中url.openConnection()至此的配置必须要在connect之前完成�?
				// httpUrlConnection.connect();
	
				// 此处getOutputStream会隐含的进行connect(即：如同调用上面的connect()方法�?
				// �?以在�?发中不调用上述的connect()也可�?)�?
				if (params != null && params.indexOf("=") > 0) {
					outStrm = httpUrlConnection.getOutputStream();
	
					// 现在通过输出流对象构建对象输出流对象，以实现输出可序列化的对�?.
					outStrm.write(params.getBytes("UTF-8"));
	
					outStrm.flush();
					
					outStrm.close();
				}
			}
			try{
				if( httpUrlConnection.getResponseCode()==404
						|| httpUrlConnection.getResponseCode()==403){
					return null;
				}
			}catch(Exception e){
				System.out.println(e.getMessage());
				return null;
			}
			// ObjectOutputStream objOutputStrm = new
			// ObjectOutputStream(outStrm);
			//
			// // 向对象输出流写出数据，这些数据将存到内存缓冲区中
			// objOutputStrm.writeObject(new String("我是测试数据"));
			//
			// // 刷新对象输出流，将任何字节都写入潜在的流中（些处为ObjectOutputStream�?
			// objOutputStrm.flush();
			//
			// // 关闭流对象�?�此时，不能再向对象输出流写入任何数据，先前写入的数据存在于内存缓冲区中,
			// // 在调用下边的getInputStream()函数时才把准备好的http请求正式发�?�到服务�?
			// objOutputStrm.close();

			// 调用HttpURLConnection连接对象的getInputStream()函数,
			// 将内存缓冲区中封装好的完整的HTTP请求电文发�?�到服务端�??
			// <===注意，实际发送请求的代码段就在这�?
			br = new BufferedReader(new InputStreamReader(httpUrlConnection.getInputStream(),"UTF8"));

			// 上边的httpConn.getInputStream()方法已调�?,本次HTTP请求已结�?,下边向对象输出流的输出已无意义，
			// 既使对象输出流没有调用close()方法，下边的操作也不会向对象输出流写入任何数�?.
			// 因此，要重新发�?�数据时�?要重新创建连接�?�重新设参数、重新创建流对象、重新写数据�?
			// 重新发�?�数�?(至于是否不用重新这些操作�?要再研究)
			StringBuffer sb = new StringBuffer();
			String lineStr = "";
			while ((lineStr = br.readLine()) != null) { // 读取信息
				sb.append(lineStr + "\r\n");
			}
			if (null != sc) {
				sc.after(httpUrlConnection);
			}
			return sb.toString();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
				try {
					if(null != br)
						br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return null;
	}
	

}
