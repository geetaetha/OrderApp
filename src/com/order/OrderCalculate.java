package com.order;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.order.constants.OrderConstants;
import com.order.vo.OrderItemVO;

/**
 * This class is to apply the eligible discounts
 * for the items in the cart and return the total price.
 * @author gsree
 *
 */
public class OrderCalculate {

	private final static Logger LOGGER = Logger.getLogger(OrderCalculate.class.getName());
	JSONParser parser = new JSONParser();
	private double offerPrice = 0;
	
	/**
	 * This method call all the eligible promotions for the OrderItems
	 * returns the totalPrice.
	 * @param itemsLIst
	 * @param orderItemMap
	 * @return
	 */
	public double applyEligiblePromotions(List<String> itemsList, Map<String, OrderItemVO> orderItemMap) {
		LOGGER.entering("OrderCalculate", "applyEligiblePromotions");
		Object promotionObj;
		double totalOrderPrice=0.0;
		try {
			File currFile = new File(getClass().getClassLoader().getResource(OrderConstants.FILE_LOCATION).getFile());
			promotionObj = parser.parse(new FileReader(currFile));
			JSONObject promoList = (JSONObject) promotionObj;
			for (int i = 0; i < itemsList.size(); i++) {
				OrderItemVO itemData = orderItemMap.get(itemsList.get(i));
				offerPrice = Integer.parseInt(itemData.getPrice()) * Integer.parseInt(itemData.getQty());
				if (Integer.parseInt(itemData.getQty()) > 1) {
					offerPrice = applyXatYpromo(itemData, promoList);
				}
				offerPrice = applyPercentOffPromo(offerPrice, promoList);
				offerPrice = applyFixedPricePromo(offerPrice, itemData, orderItemMap, promoList);
				System.err.println("final price............" + offerPrice);
				totalOrderPrice = totalOrderPrice +offerPrice;
			}
		} catch (IOException | ParseException e) {
			LOGGER.logp(Level.WARNING, OrderCalculate.class.getName(), "applyPromotions",
					"error while calculating promo discount");
		}
		LOGGER.exiting("OrderCalculate", "applyEligiblePromotions");
		return totalOrderPrice;
	}

	/**
	 * This method gives the discount for multiples of X
	 * qunatity of item at respective multiples of Y price 
	 * and calculates the remaining quantity of the item price
	 * with the actual Price and finally gives promotionapplied
	 *  price for the item
	 * and 
	 * @param ordItemDetails
	 * @param promoList
	 * @return
	 */
	public double applyXatYpromo(OrderItemVO ordItemDetails, JSONObject promoList) {
		LOGGER.entering("OrderCalculate", "applyXatYpromo");
		JSONObject promoDetails = (JSONObject) promoList.get(OrderConstants.BUYXATY);
		int itemXQty = Integer.parseInt(promoDetails.get(OrderConstants.ITEM_X_QTY).toString());
		int itemYQty = Integer.parseInt(promoDetails.get(OrderConstants.ITEM_Y_QTY).toString());

		if (Integer.parseInt(ordItemDetails.getQty()) >= itemXQty) {
			LOGGER.logp(Level.INFO, OrderCalculate.class.getName(), "applyXatYpromo",
					"Item with Id"+ordItemDetails.getOrderItemId()+" is eligible for BuyXatYQtyPrice promotion");
			int excludedItemQty = Integer.parseInt(ordItemDetails.getQty()) % itemXQty;
			int discoutPrice = Integer.parseInt((ordItemDetails.getPrice()))*itemYQty*(Integer.parseInt(ordItemDetails.getQty())/itemXQty);
			offerPrice = (excludedItemQty * Integer.parseInt(ordItemDetails.getPrice())) + discoutPrice;
		} else {
			LOGGER.logp(Level.INFO, OrderCalculate.class.getName(), "applyXatYpromo",
					"Item with Id"+ordItemDetails.getOrderItemId()+" is not eligible for BuyXatYQtyPrice promotion");
		}
		LOGGER.exiting("OrderCalculate", "applyXatYpromo");
		return offerPrice;
	}

	/**
	 * This method gives percentage discount for 
	 * all the items in the cart and return the price
	 * after discount is applied.
	 * @param price
	 * @param promoList
	 * @return
	 */
	public double applyPercentOffPromo(double price, JSONObject promoList) {
		LOGGER.entering("OrderCalculate", "applyPercentOffPromo");
		JSONObject promoDetails = (JSONObject) promoList.get("promo2");
		double discountPercent = Double.parseDouble(promoDetails.get("xPercentage").toString());
		offerPrice =price;
		double discountPrice = (price * discountPercent) / Integer.parseInt(OrderConstants.NUMERIC_100);
		offerPrice=offerPrice-discountPrice;
		LOGGER.exiting("OrderCalculate", "applyPercentOffPromo");
		return offerPrice;
	}

	/**
	 * This method checks whether both eligible items 
	 * are presnt in the selected items and then compares 
	 * the quantities of the two items and calculates discount 
	 * for that quantity.
	 * @param price
	 * @param itemData
	 * @param itemPriceMap
	 * @param promoList
	 * @return
	 */
	public double applyFixedPricePromo(double price, OrderItemVO itemData, Map<String, OrderItemVO> itemPriceMap,
			JSONObject promoList) {
		LOGGER.entering("OrderCalculate", "applyFixedPricePromo");
		// Assuming that X and Y are single catalog entries.
		JSONObject promoDetails = (JSONObject) promoList.get(OrderConstants.BUYXANDY_AT_FP);
		String xItemValue = promoDetails.get(OrderConstants.X_ITEM).toString();
		String yItemValue = promoDetails.get(OrderConstants.Y_ITEM).toString();
		double fixedPrice = Double.parseDouble(promoDetails.get(OrderConstants.FIXED_PRICE).toString())/Integer.parseInt(OrderConstants.NUMERIC_2);
		String quantity="";
		int diffInQty ;
		double excludedItemPrice = 0.0;
		if (itemPriceMap.get(xItemValue) != null && itemPriceMap.get(yItemValue) != null) {
			LOGGER.logp(Level.INFO, OrderCalculate.class.getName(), "applyXatYpromo",
					"Item with Id"+itemData.getOrderItemId()+" is eligible for BuyXandYatFixedPrice promotion");
			String xItemQty = itemPriceMap.get(xItemValue).getQty();
			String yItemQty = itemPriceMap.get(yItemValue).getQty();
			if (xItemQty.compareTo(yItemQty) >= 0) {
				quantity = xItemQty;
			} else {
				quantity = yItemQty;
			}
			double discountedPrice = price / Integer.parseInt(itemData.getQty());
			if (itemData.getQty().compareTo(quantity) > 0) {
				diffInQty = Integer.parseInt(itemData.getQty()) - Integer.parseInt(quantity);
				excludedItemPrice = diffInQty * discountedPrice;
			}
			offerPrice = excludedItemPrice + fixedPrice * Double.parseDouble((quantity));
		} else {
			LOGGER.logp(Level.INFO, OrderCalculate.class.getName(), "applyXatYpromo",
					"Item with Id"+itemData.getOrderItemId()+" is not eligible for BuyXandYatFixedPrice promotion");
		}
		LOGGER.exiting("OrderCalculate", "applyFixedPricePromo");
		return offerPrice;
	}
}
