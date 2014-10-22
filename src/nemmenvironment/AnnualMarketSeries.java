package nemmenvironment;

import nemmcommons.YearArray;
import nemmtime.NemmTime;


public class AnnualMarketSeries {
// A storage class for Annual market prices (thats powerprices)
	
	private YearArray seriesValues;

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
	
	public AnnualMarketSeries() {
		seriesValues = new YearArray();
	}

	public void initAnnualMarketSeries(double[] mktPrice){
		setAllValues(mktPrice);		
	}

// Methods
	
	public void setAllValues(double[] mktPrice) {

		int numPoints = mktPrice.length;
		int numYears = TheEnvironment.theCalendar.getNumYears();

		// set the demands and quotas for each tick
		if(numPoints==1){
			for (int y = 0; y < numYears; ++y){
				seriesValues.setElement(mktPrice[0], y);
			}			
		}
		else {
			seriesValues.setArray(mktPrice);
		}
	}
	
	
	// Return market series for a given tickID
	public double getValue(int... Year) {
		double pricecalc;
		if (Year.length > 0) {	
			pricecalc = this.seriesValues.getElement(Year[0]);
		}
		else {
			int curYear = TheEnvironment.theCalendar.getTimeBlock(TheEnvironment.theCalendar.getCurrentTick()).year;
			pricecalc = this.seriesValues.getElement(curYear);
		}				
		return pricecalc;
	}		
	
	
} // Class MarketPrice	
	
