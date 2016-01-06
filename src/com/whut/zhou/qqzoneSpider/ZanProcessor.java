package com.whut.zhou.qqzoneSpider;

import java.util.List;

import com.whut.zhou.dao.ShuoShuoDao;
import com.whut.zhou.utils.HelpUtils;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;


public class ZanProcessor implements PageProcessor {
	static String zan_url;
	private static int count = 0;
	private static List<String> shuoshuoid_list;

	public Site getSite() {
		Site site = HelpUtils.getSite();
		return site;
	}
	

	public ZanProcessor() {
		zan_url = HelpUtils.getZanUrl();
		shuoshuoid_list = new ShuoShuoDao().getAllShuoShuo_id(Long.parseLong(HelpUtils.getHost_id()));
	}

	public void process(Page page) {
		page.putField("host_id", HelpUtils.getHost_id());
		page.putField("shuoshuo_id", shuoshuoid_list.get(count));
		page.putField("zan", HelpUtils.getJsonFromJsonp(HelpUtils.formatJsonp(page.getRawText().trim())));
		if (++count < shuoshuoid_list.size()) {
			page.addTargetRequest(getZanUrl(count));
		}
		System.out.println("当前正抽取第 " + count +"条赞");

	}

	private String getZanUrl(int index) {
		return zan_url.replace("zanurl", shuoshuoid_list.get(index));
	}
	
	public void saveZanDataToDatabase(){
		Spider.create(new ZanProcessor()).thread(10)
		  .addUrl(zan_url.replace("zanurl", shuoshuoid_list.get(0)))
		  .addPipeline(new ZanPipeline())
		  .run();
	}

}
