package com.whut.zhou.qqzoneSpider;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.whut.com.domain.Friend;
import com.whut.com.domain.Zan;
import com.whut.zhou.dao.ShuoShuoDao;
import com.whut.zhou.utils.HelpUtils;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.selector.JsonPathSelector;


public class ZanPipeline implements Pipeline {
	
	private ShuoShuoDao dao = null;
	
	public ZanPipeline() {
		dao = new ShuoShuoDao();
	}

	public void process(ResultItems resultItems, Task task) {
		String zanJson = resultItems.get("zan");
		//System.out.println("==========================="+zanJson);
		String shuoshuo_id = resultItems.get("shuoshuo_id");
		String host_id = resultItems.get("host_id");
		
		//String zanString =new JsonPathSelector("$.data").select(zanJson);
		String zanString = new JsonPathSelector("data").select(zanJson);
		
		JSONArray jsonArray = JSONObject.parseObject(zanString).getJSONArray("like_uin_info");
		
		addZan(jsonArray,shuoshuo_id);
		addFriend(jsonArray, host_id);
	}
	
	private void addZan(JSONArray jsonArray,String shuoshuo_id) {
		
		List<Zan> zan_list = new ArrayList<Zan>();
		
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			
			Zan zan = new Zan();
			zan.setFriend_id(jsonObject.getLongValue("fuin"));
			zan.setShuoshuo_id(shuoshuo_id);
			zan.setHost_id(Long.parseLong(HelpUtils.getHost_id()));
			
			zan_list.add(zan);
		}
		dao.addZan(zan_list);
	}
	
	private void addFriend(JSONArray jsonArray,String host_id) {
		
		List<Friend> friend_list = new ArrayList<Friend>();
		
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			
			Friend friend = new Friend();
			friend.setFriend_id(jsonObject.getLongValue("fuin"));
			friend.setFriend_name(jsonObject.getString("nick"));
			
			String gender = jsonObject.getString("gender");
			if ("男".equals(gender)) {
				friend.setFriend_gender(1);
			}else {
				friend.setFriend_gender(0);
			}
			
			friend.setFriend_constellation(jsonObject.getString("constellation"));
			friend.setFriend_address(jsonObject.getString("addr"));
			friend.setIf_special_care(jsonObject.getIntValue("if_special_care"));
			
			friend_list.add(friend);
		}
		
		dao.addFriend(friend_list,Long.parseLong(host_id));
	}

}
