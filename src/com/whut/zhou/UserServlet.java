package com.whut.zhou;


import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.whut.zhou.dao.ViewDao;
import com.whut.zhou.login.QQInfo;
import com.whut.zhou.qqzoneSpider.ZanProcessor;
import com.whut.zhou.utils.HelpUtils;
import com.whut.zhou.utils.JDBCUtils;
import com.whut.zhou.utils.MywordCram;
import net.sf.json.JSONArray;


import com.whut.zhou.qqzoneSpider.ShuoShuoProcessor;



public class UserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	ViewDao viewDao = new ViewDao();
    public UserServlet() {
       
    }

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		String qqNum = request.getParameter("qqNum");
		String password = request.getParameter("password");
		boolean flag = validate(qqNum,password);
		
		if(flag){
			spiderData(qqNum,password);
			//把datalist转化为Json格式 返回到android端    
			try {
				List<Map<String,String>> dataList=viewDao.getDataList(qqNum);
				JSONArray jsonArray = JSONArray.fromObject(dataList);
				//System.out.println(jsonArray.toString());
				response.getWriter().write(jsonArray.toString());
				   
			} catch (SQLException e) {
				e.printStackTrace();
			}
		
			MywordCram.getWordCram();
		}else{
			response.sendError(400);
		}
		
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	/**
	 * 若qq号在数据库表userInfor中不存在，则执行爬去数据操作；
	 * 若qq号存在且时间差大于一个星期，则爬去数据；
	 * 否则不用爬去数据
	 * @param qqNum
	 * @param password
	 */
	public void spiderData(String qqNum, String password) {
		long oldTime = 0 ;
		long newTime =0;
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		try {
			conn = JDBCUtils.getConnection();
			String sql = "select * from userInfor where host_id = '" + qqNum +"'";
			preparedStatement = conn.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			if(resultSet.next()){
				System.out.println("有数据啊啊  "+System.currentTimeMillis());
				oldTime = resultSet.getLong(2);
				newTime = System.currentTimeMillis();  
				System.out.println((newTime-oldTime));
				if((newTime-oldTime)>604800000){
					spider();
				}
			}else{
				spider();
				oldTime = System.currentTimeMillis();
				String insert_sql = "insert into userInfor (host_id,time) values (?,?)";
				preparedStatement = conn.prepareStatement(sql);
				preparedStatement.setString(1, qqNum);
				preparedStatement.setLong(2, oldTime);
				preparedStatement.executeUpdate();
			
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	//执行爬去数据操作
	public void spider() {
			ShuoShuoProcessor shuoshuoProcessor = new ShuoShuoProcessor();
			shuoshuoProcessor.saveShuoshuoDataToDatabase();
			System.out.println("说说提取完毕");
			
			ZanProcessor zanProcessor = new ZanProcessor();
			zanProcessor.saveZanDataToDatabase();
			System.out.println("赞保存完毕");
	}

	/**
	 * 验证QQ号码和密码是否匹配
	 * @param qqNum
	 * @param password
	 * @return true or false
	 */

	public boolean  validate(String qqNum, String password) {
		QQInfo qq = new QQInfo(qqNum,password);
		HelpUtils.getQQInfor(qq);
		HashMap cookies=qq.getCookies();
		String skey = (String) cookies.get("skey");
		String uin = (String) cookies.get("uin");
		if(uin.contains(qqNum)&&skey.contains("@")){
			return true;
		}
		return false;
	}

}
