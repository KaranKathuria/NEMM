package nemmenvironment;

import java.util.ArrayList;

import nemmcommons.CommonMethods;
import nemmenvironment.MarketDemand;

public class Region {
	
	// In the world, there are regions
	private String regionName;
	private MarketDemand myDemand;
	private AnnualMarketSeries myPowerPrice;
	private AnnualMarketSeries[] myForwardPrice;	//Array of AnnualMarketSeries. The array lenght is years. AnnualMarketseries[1] is the future prices standing in year 2013, with 24 doubles.
	
	
	/**
	 * @param regionName
	 */
	public Region(String regionName) {
		
		this.regionName = regionName;
		this.myDemand = new MarketDemand();
		this.myPowerPrice = new AnnualMarketSeries();
		this.myForwardPrice = new AnnualMarketSeries[TheEnvironment.theCalendar.getNumYears()];
		for (int i=0; i < myForwardPrice.length;i++) {
			myForwardPrice[i] = new AnnualMarketSeries();
		}
	}
	

	public String getRegionName() {
		return this.regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}
	
	public MarketDemand getMyDemand() {
		return myDemand;
	}
	
	// commented out as you should not need to set a new demand object
/*	public void setMyDemand(MarketDemand myDemand) {
		this.myDemand = myDemand;
	}
*/
	public AnnualMarketSeries getMyPowerPrice() {
		return myPowerPrice;
	}
	
	public AnnualMarketSeries getMyForwardPrice(int i) {	//I refers to the year you are standing in looking at the forward prices.
		return myForwardPrice[i];
	}



}
