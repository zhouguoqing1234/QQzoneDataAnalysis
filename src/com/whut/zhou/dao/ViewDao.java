package com.whut.zhou.dao;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.whut.zhou.utils.JDBCUtils;


/*
 * 主要用于数据库的读取，包括说说的评论信息，说说的点赞信息，空间最近访客信息，日志最近访客信息，相册最近访客信息等
 */
public class ViewDao {
	
	Connection conn = null;
	PreparedStatement  ps = null ;
	ResultSet resultSet = null;
	List<Map<String,String>> list = new ArrayList<Map<String,String>>()  ;
	
	/**
	 * 	 * facebook行为数据的权重分配 
			 * 点赞：3.103   
			 * 评论：14.678
			 * 点击或者访客：1
			 * 互动亲密度计算模型：u*w*d  (互动行为次数，权重，时间衰减因子)
	 * @return map  datalist
	 * @throws SQLException
	 */
	public  List<Map<String,String>> getDataList(String qqNum) throws SQLException{
		    Map<String,Integer> map2  =  new HashMap();
			conn = JDBCUtils.getConnection();
			String count_sql = getRightSql(qqNum);
			ps = conn.prepareStatement(count_sql);
			resultSet = ps.executeQuery();
			if(!list.isEmpty()){
				list.clear();
			}
			while(resultSet.next()){
				Map<String,String> map = new HashMap<String,String>();
				map.put("name", resultSet.getString(1));
				map.put("score",resultSet.getString(2));
				map2.put(resultSet.getString(1),resultSet.getInt(2));
				list.add(map);
			}
			writerFile(map2);          //把得到的结果集写入到文件，供WordCram工具读取
			
		return list;
		
	}
	



	public static void writerFile(Map<String, Integer> map) {
		File file = new File("C:\\MySoftware\\Java\\WorkSpace\\WebBestFriends\\result.txt");
		BufferedWriter writer = null;
		Iterator iterator = map.entrySet().iterator();
		try {
			 writer = new BufferedWriter(new FileWriter(file)) ;
			 while(iterator.hasNext()){
				writer.write(iterator.next().toString());
				writer.newLine();
			}
			 writer.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
            	try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        }
	}
	
	
	/**
	 * 使用用户的qq号替换sql语句中的qqNum
	 * @param qqNum
	 * @return sql
	 */
	public  String getRightSql(String qqNum) {
		String sql = "SELECT friend_name, (comment_count*14.678+zan_count*3.103) as sum_count,comment_count ,zan_count " 
				+"FROM(SELECT friend_name,friend_id,comment_count,zan_count FROM (SELECT zan_friend_id  , count(zan_friend_id) AS zan_count "
				+"FROM `zan` WHERE zan.host_id = 'qqNum' GROUP BY zan_friend_id ) AS t1 INNER JOIN (SELECT friend_id ,count(friend_id) AS comment_count ,friend_name "
				+"FROM `comment` WHERE comment.host_id = 'qqNum' GROUP BY friend_id ) AS t2 "
				+"ON t1.zan_friend_id=t2.friend_id ) AS t3 ORDER BY sum_count DESC LIMIT 91";
		return sql.replace("qqNum", qqNum);
	}

}
