package nemmenvironment;

import nemmcommons.TickArray;
import nemmtime.NemmTime;


public class MarketSeries {
// A storage class for market prices
	
	private TickArray seriesValues;

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
	
	public MarketSeries() {
		seriesValues = new TickArray();
	}

	public void initMarketSeries(double[] mktPrice){
		// Currently this just calls the setAllPrices. We can add additional initialisation
		// stuff here later if desired
		setAllValues(mktPrice);		
	}

// Methods
	
	public void setAllValues(double[] mktPrice) {

		int numPoints = mktPrice.length;
		int numTicks = TheEnvironment.theCalendar.getNumTicks();

		// set the demands and quotas for each tick
		if(numPoints==1){
			for (int y = 0; y < numTicks; ++y){
				seriesValues.setElement(mktPrice[0], y);
			}			
		}
		else {
			seriesValues.setArray(mktPrice);
		}
	}
	
	
	// Return market series for a given tickID
	public double getValue(int... tickID) {
		double pricecalc;
		if (tickID.length > 0) {	
			pricecalc = this.seriesValues.getElement(tickID[0]);
		}
		else {
			int curTick = TheEnvironment.theCalendar.getCurrentTick();
			pricecalc = this.seriesValues.getElement(curTick);
		}				
		return pricecalc;
	}		
	
	// Set market series for a given tickID
	public void setValue(double newValue, int... tickID) {
		if (tickID.length > 0) {	
			this.seriesValues.setElement(newValue, tickID[0]);
		}
		else {
			int curTick = TheEnvironment.theCalendar.getCurrentTick();
			this.seriesValues.setElement(newValue, curTick);
		}	
	}
	
} // Class MarketPrice	
	
