package com.whut.zhou.login;

import java.net.HttpURLConnection;
import java.net.ProtocolException;
/**
 * <p>提供在请求前后，设置请求参数的接�?/p>
 * @category  类名
 */
public interface SetHttpConnection {

	/** 设置发�?�请求前的处理请求方�? 
	 * @throws ProtocolException */
	public String before(HttpURLConnection httpConn) throws ProtocolException;
	/** 设置发�?�后的请求处理方�? */
	public String after(HttpURLConnection httpConn);
}
