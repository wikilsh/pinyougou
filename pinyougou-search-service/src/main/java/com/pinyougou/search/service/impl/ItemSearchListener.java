package com.pinyougou.search.service.impl;

import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
@Component
public class ItemSearchListener implements MessageListener {
	@Autowired
	private ItemSearchService itemSearchService;
	@Override
	public void onMessage(Message message) {
		TextMessage textMessage=(TextMessage)message;
		
		try {
			String text = textMessage.getText();//json字符串
			System.out.println("监听到消息："+text);
			List<TbItem> itemList = JSON.parseArray(text, TbItem.class);
			for(TbItem item:itemList){
				System.out.println(item.getId()+" "+item.getTitle());
				Map specMap=JSON.parseObject(item.getSpec());//将psec字段中的json字符串转换为map
				item.setSpecMap(specMap);
			}
			itemSearchService.importList(itemList);//导入
			System.out.println("成功导入到solr索引库！");
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
