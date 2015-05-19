package nemmenvironment;

import java.util.ArrayList;

import nemmcommons.AllVariables;
import nemmcommons.CommonMethods;
import nemmenvironment.MarketDemand;

public class Region {
	
	// In the world, there are regions
	private String regionName;
	private MarketDemand myDemand;
	private AnnualMarketSeries myPowerPrice;
	private AnnualMarketSeries[] myForwardPrice;				//Array of AnnualMarketSeries. The array lenght is years. AnnualMarketseries[1] is the future prices standing in year 2013, with 24 doubles.¨
	// NOT NEEDED private AnnualMarketSeries[] myForwardPrice_mulitplicators; //Initial array of spot-year multiplicators used to generate all FWD prices. This is stored as a duplicate intially to save the multiplicators themself so the table can be rewined after each run without having to read in data again.
	private boolean certificatespost2020;						//Flag indicationg if the PowerPlants in the region are eligable for certs if finished after 2020.¨
	private int cutoffyear;										//Indicating the cut-off year for beeing certificate eligable. By default 2020 for Norway (hence the name above).
	
	
	public Region(String regionName) {
		
		this.regionName = regionName;
		this.myDemand = new MarketDemand();
		this.myPowerPrice = new AnnualMarketSeries();
		this.myForwardPrice = new AnnualMarketSeries[TheEnvironment.theCalendar.getNumYears()];
		//this.myForwardPrice_mulitplicators = new AnnualMarketSeries[TheEnvironment.theCalendar.getNumYears()];
		
			if (regionName.equals("Norway")) {
			certificatespost2020 = AllVariables.certificatespost2020_Norway;
			cutoffyear = AllVariables.cutoffyear_Norway;
			}
			else { certificatespost2020 = AllVariables.certificatespost2020_Sweden;
			cutoffyear = AllVariables.cutoffyear_Sweden;
			}
		for (int i=0; i < myForwardPrice.length;i++) {
			myForwardPrice[i] = new AnnualMarketSeries();
		}
	}
	

	public String getRegionName() {return this.regionName;}
	public void setRegionName(String regionName) {this.regionName = regionName;}
	public MarketDemand getMyDemand() {return myDemand;}
	public boolean getcertificatespost2020flag() {return certificatespost2020;}
	public int getcutoffyear() {return cutoffyear;}
	
	// commented out as you should not need to set a new demand object
/*	public void setMyDemand(MarketDemand myDemand) {
		this.myDemand = myDemand;
	}
*/
	public AnnualMarketSeries getMyPowerPrice() {
		return myPowerPrice;
	}
	
	public AnnualMarketSeries getMyForwardPrice(int i) {	//I refers to the year (0-23) you are standing in looking at the forward prices.
		return myForwardPrice[i];
	}

	//public void clonesetmyForwardPrice_mulitplicators() {		//Cloning (deep) copy of all forward price multiplayers. Only used initally (by readExcel)
	//	myForwardPrice_mulitplicators = myForwardPrice.clone();
	//}

}
