package nemmenvironment;

import java.util.ArrayList;

import nemmcommons.CommonMethods;

public class Region {
	
	// In the world, there are regions
	private String regionName;
	private ArrayList<PowerPlant> allpowerplants;
	private MarketDemand myDemand;
	private MarketPrice myPowerPrice;
	
	
	/**
	 * @param regionName
	 */
	public Region(String regionName) {
		regionName = regionName;
		myDemand = new MarketDemand();
		myPowerPrice = new MarketPrice();
	}
	

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}
	
	public MarketDemand getMyDemand() {
		return myDemand;
	}
	
	public ArrayList<PowerPlant> getallpowerplants() {
		return allpowerplants;}
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
