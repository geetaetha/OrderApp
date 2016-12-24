package com.order.test;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Before;
import org.junit.Test;

import com.order.OrderCalculate;
import com.order.constants.OrderConstants;
import com.order.vo.OrderItemVO;

import junit.framework.Assert;

public class OrderCalculateTest {
	
	List<String> itemsList = new ArrayList<String>();
	Map<String, OrderItemVO> orderItemMap = new HashMap<String, OrderItemVO>();
	JSONParser parser = new JSONParser();
	OrderItemVO itemVO= new OrderItemVO();
	JSONObject promoList =null;
	@Before
	public void setUp() throws Exception{
		Object promotionObj;
		File currFile = new File(getClass().getClassLoader().getResource(OrderConstants.FILE_LOCATION).getFile());
		promotionObj = parser.parse(new FileReader(currFile));
			promoList = (JSONObject) promotionObj;
			itemsList.add("1111");
			itemVO.setOrderItemId("1111");
			itemVO.setQty("1");
			itemVO.setPrice("10");
			orderItemMap.put("1111", itemVO);
	}
	
	
	@Test
	public void testApplyXatYpromo(){
		OrderCalculate ordCalculate = new OrderCalculate();
		double value= ordCalculate.applyXatYpromo(itemVO, promoList);
		Assert.assertEquals(10.0, value,0.05);		
	}
	
	@Test
	public void testApplyPercentOffPromo(){
		OrderCalculate ordCalculate = new OrderCalculate();
		double discountedPrice= 10.0;
		double value= ordCalculate.applyPercentOffPromo(discountedPrice, promoList);
		Assert.assertEquals(9.0, value,0.05);		
	}
	
	@Test
	public void testApplyFixedPricePromo(){
		OrderCalculate ordCalculate = new OrderCalculate();
		itemsList.add("2222");
		itemVO.setOrderItemId("2222");
		itemVO.setQty("1");
		itemVO.setPrice("10");
		orderItemMap.put("2222", itemVO);
		double discountedPrice= 10.0;
		double value= ordCalculate.applyFixedPricePromo(discountedPrice,itemVO,orderItemMap,promoList);
		Assert.assertEquals(5.0, value,0.05);		
	}
	
	@Test
	public void testApplyEligiblePromotions(){
		OrderCalculate ordCalculate = new OrderCalculate();
		itemsList.add("2222");
		itemVO.setOrderItemId("2222");
		itemVO.setQty("1");
		itemVO.setPrice("10");
		orderItemMap.put("2222", itemVO);
		double value= ordCalculate.applyEligiblePromotions(itemsList, orderItemMap);
		Assert.assertEquals(10.0, value,0.05);	
	}
	
	
}
