package com.order;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.order.constants.OrderConstants;
import com.order.vo.OrderItemVO;

/**
 * 
 */

/**
 * servlet class which gets the selected items 
 * and respective quantities from JSP and
 * calcuclates the toatal Price with discounts
 * andd redirecting to orderSummary page
 * @author gsree
 *
 */
public class OrderItemAdd extends HttpServlet {
	private final static Logger LOGGER = Logger.getLogger(OrderCalculate.class.getName());
	
	/**
	 * This method gets the selectedItems from request 
	 * and gets totalPrice after applying promotions
	 *  and forwards response to a view
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		LOGGER.entering("OrderItemAdd", "doPost");
		double totalPrice = 0;
		String[] selectedItems = request.getParameterValues(OrderConstants.MY_ITEM);
		LOGGER.logp(Level.INFO, OrderCalculate.class.getName(), "doPost",
				"selectedItems :"+selectedItems);
		if(null != selectedItems && selectedItems.length>0){
		totalPrice = calculateOrderTotal(request, selectedItems);
		}
		request.setAttribute("totalPrice", totalPrice);
		request.getRequestDispatcher(OrderConstants.FORWARD_VIEW).forward(request, response);
		LOGGER.exiting("OrderItemAdd", "doPost");
	}

	/**
	 * This method helps in creating a list of selectedItems
	 * and calls OrderCalculation class for applying promotional 
	 * discounts and gets the toatlOrderPrice.
	 * @param request
	 * @param selectedItems
	 * @return totalPrice after applying promotions
	 */
	private double calculateOrderTotal(HttpServletRequest request, String[] selectedItems) {
		LOGGER.entering("OrderItemAdd", "calculateOrderTotal");
		List<String> orderItemsList = new ArrayList<String>();
		Map<String, OrderItemVO> orderItemsMap = new HashMap<String, OrderItemVO>();
		double totalPrice = 0.0;
		for (String selectedItem : selectedItems) {
			orderItemsList.add(selectedItem);
			OrderItemVO itemVO = new OrderItemVO();
			itemVO.setOrderItemId(selectedItem);
			itemVO.setQty(request.getParameter(OrderConstants.CHECKBOX_IDENTIFIER + selectedItem));
			itemVO.setPrice(OrderConstants.PRICE);
			orderItemsMap.put(selectedItem, itemVO);
		}
			OrderCalculate orderCalculate = new OrderCalculate();
			totalPrice = orderCalculate.applyEligiblePromotions(orderItemsList, orderItemsMap);
		LOGGER.exiting("OrderItemAdd", "calculateOrderTotal");
		return totalPrice;
	}

}
