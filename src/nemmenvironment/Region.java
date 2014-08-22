package nemmenvironment;

import java.util.ArrayList;

import nemmcommons.CommonMethods;

public class Region {
	
	// In the world, there are regions
	private String regionName;
	private MarketDemand myDemand;
	private MarketPrice myPowerPrice;
	
	
	/**
	 * @param regionName
	 */
	public Region(String regionName) {
		this.regionName = regionName;
		this.myDemand = new MarketDemand();
		this.myPowerPrice = new MarketPrice();
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
	public MarketPrice getMyPowerPrice() {
		return myPowerPrice;
	}
	// commented out as you should not need to set a new price object
/*	public void setMyPowerPrice(MarketPrice myPowerPrice) {
		this.myPowerPrice = myPowerPrice;
	}
*/


}
