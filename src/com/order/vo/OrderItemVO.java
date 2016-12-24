/**
 * 
 */
package com.order.vo;

/**
 * @author gsree
 *
 */
public class OrderItemVO {

	private String orderItemId;
	private String qty;
	private String price;
	
	public String getOrderItemId() {
		return orderItemId;
	}
	public void setOrderItemId(String orderItemId) {
		this.orderItemId = orderItemId;
	}
	public String getQty() {
		return qty;
	}
	public void setQty(String qty) {
		this.qty = qty;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
}
