package com.whut.zhou.qqzoneSpider;
import com.whut.zhou.utils.HelpUtils;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.JsonPathSelector;

/**
 * 说说信息
 * @author Mr.Zhou
 *
 */
public class ShuoShuoProcessor implements PageProcessor{
	static String start_url ;
	private static int NOW_PAGE= 1;
	private int totalNumOfShuoShuo = 0;
	public Site getSite() {
		Site site = HelpUtils.getSite();
		return site;
	}
	
	
	 public ShuoShuoProcessor(){
		 start_url = HelpUtils.getStartUrl();
	}

	public void process(Page page) {
		 System.out.println("page.getRawText():"+page.getRawText());
		 String json = HelpUtils.getJsonFromJsonp(page.getRawText());
		
		 totalNumOfShuoShuo =  Integer.parseInt(new JsonPathSelector("$.total").select(json));
		 System.out.println("totalNumOfShuoShuo:"+totalNumOfShuoShuo);
		 
		 if (NOW_PAGE < getTotalPages(totalNumOfShuoShuo)) {
				page.addTargetRequest(start_url.replace("page", NOW_PAGE*20+""));
				NOW_PAGE++;
			}
		 page.putField("json", json);
		 page.putField("host_id", HelpUtils.getHost_id());
	}
	
	/**
	 * @param totalShuoShuo the num of shuoshuo
	 * @return the num of pages
	 */
	private int getTotalPages(int totalNumOfShuoShuo) {
		if (totalNumOfShuoShuo % 20 == 0) {
			return totalNumOfShuoShuo / 20;
		}else {
			return totalNumOfShuoShuo / 20 + 1;
		}
	}
	
	public void saveShuoshuoDataToDatabase(){
		Spider.create(new ShuoShuoProcessor()).thread(5)
		.addUrl(start_url.replace("page", "0"))
		.addPipeline(new ShuoShuoPipeline())
		.run();
	}
}
