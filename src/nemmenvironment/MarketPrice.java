package nemmenvironment;

import nemmcommons.TickArray;
import nemmtime.NemmTime;


public class MarketPrice {
// A storage class for market prices
	
	private double defaultPrice;
	private TickArray marketPrice;

	/*
	public MarketPrice(double... defaultPrice) {
		if (defaultPrice.length >0) {
			this.defaultPrice = defaultPrice[0];
		}
		else {
			this.defaultPrice = 200;
		}
	}
*/
	
	
	// Constructor methods. The set up is slightly unusual - when the object is instantiated
	// the constructor is called and does nothing. Before use the initMarketPrice
	// method should be called - this does the actual constructing.
	// I've implemented in this way to enable the same code to be used for updating the 
	// market prices if and as desired
	
	public MarketPrice() {
		marketPrice = new TickArray();
	}

	public void initMarketPrice(double[] mktPrice){
		// Currently this just calls the setAllPrices. We can add additional initialisation
		// stuff here later if desired
		setAllPrices(mktPrice);		
	}

// Methods
	
	public void setAllPrices(double[] mktPrice) {

		int numPoints = mktPrice.length;
		int numTicks = TheEnvironment.theCalendar.getNumTicks();

		// set the defaults equal to the first data elements
		defaultPrice = mktPrice[0];
		// set the demands and quotas for each tick
		if(numPoints==1){
			for (int y = 0; y < numTicks; ++y){
				marketPrice.setElement(mktPrice[0], y);
			}			
		}
		else {
			marketPrice.setArray(mktPrice);
		}
	}
	
	
	// Return market price for a given tickID
	public double getPrice(int... tickID) {
		double pricecalc;
		if (tickID.length > 0) {	
			pricecalc = this.marketPrice.getElement(tickID[0]);
		}
		else {
			pricecalc = defaultPrice;
		}				
		return pricecalc;
	}		
	
	// Set market price for a given tickID
	public void setPrice(double newPrice, int... tickID) {
		if (tickID.length > 0) {	
			this.marketPrice.setElement(newPrice, tickID[0]);
		}
		else {
			this.defaultPrice = newPrice;
		}	
	}
	
} // Class MarketPrice	
	
