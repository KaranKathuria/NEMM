package nemmenvironment;

import nemmtime.NemmTime;


public class MarketPrice {
// A storage class for market prices
	
	private double defaultPrice;

	/**
	 * @param defaultPrice
	 */
	public MarketPrice(double... defaultPrice) {
		if (defaultPrice.length >0) {
			this.defaultPrice = defaultPrice[0];
		}
		else {
			this.defaultPrice = 200;
		}
	}
		
		// Return market price
	
	public double getPrice(NemmTime... pricepd){
		return this.defaultPrice;
	}
	
		// Set market price
	public void setPrice(double newPrice, NemmTime... pricepd) {
		this.defaultPrice = newPrice;
	}
	
}
