package com.whut.com.domain;
 /**
  * 好友信息
  * @author guoqing
  *
  */
public class Friend {
	private long friend_id;    //好友QQ号
	private String friend_name;    //好友昵称
	private int friend_gender;      //好友性别
	private String friend_constellation;    //好友星座
	private String friend_address;            //好友地址
	private int if_special_care;      //是否特别关注
	
	
	public long getFriend_id() {
		return friend_id;
	}
	public void setFriend_id(long friend_id) {
		this.friend_id = friend_id;
	}
	public String getFriend_name() {
		return friend_name;
	}
	public void setFriend_name(String friend_name) {
		this.friend_name = friend_name;
	}
	public int getFriend_gender() {
		return friend_gender;
	}
	public void setFriend_gender(int friend_gender) {
		this.friend_gender = friend_gender;
	}
	public String getFriend_constellation() {
		return friend_constellation;
	}
	public void setFriend_constellation(String friend_constellation) {
		this.friend_constellation = friend_constellation;
	}
	public String getFriend_address() {
		return friend_address;
	}
	public void setFriend_address(String friend_address) {
		this.friend_address = friend_address;
	}
	public int getIf_special_care() {
		return if_special_care;
	}
	public void setIf_special_care(int if_special_care) {
		this.if_special_care = if_special_care;
	}
	

}
