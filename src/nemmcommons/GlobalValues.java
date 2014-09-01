/*
 * Version info:
 *     A simple class which exists to store global current values. Note that this is a static class as all its member variables are static. 
 *     Last altered data: 20140721
 *     Made by: Karan Kathuria  
 */

package nemmcommons;

// Import
import repast.simphony.random.RandomHelper;
import nemmagents.CompanyAgent.ActiveAgent;
import nemmprocesses.ShortTermMarket;


//Class definitions. Note that this is a static class as all its member variables are static.
public final class GlobalValues {
	
	public static double currentmarketprice;
	public static double currentinterestrate;
	public static int numberofbuyoffersstm;
	public static int numberofselloffersstm;
	// Future cert prices
	public static double endofyearpluss1;
	public static double endofyearpluss2;
	public static double endofyearpluss3;
	public static double endofyearpluss4;
	public static double endofyearpluss5;
	// Power prices (pp), current and future. 
	public static double powerprice;
	public static double ppendofyearpluss1;
	public static double ppendofyearpluss2;
	public static double ppendofyearpluss3;
	public static double ppendofyearpluss4;
	public static double ppendofyearpluss5;
	
	public static double producersphysicalposotion = 0;
	public static double tradersphysicalposotion = 0;
	public static double obligatedpurchasersphysiclaposotion = 0;
	
		
	public GlobalValues() {
		currentmarketprice = ParameterWrapper.getpriceexpectation();
	}
	
	public static void initglobalvalues() {
		//Initially the global market price is set to that of the price expectations
		currentmarketprice = ParameterWrapper.getpriceexpectation();
		currentinterestrate = ParameterWrapper.getinitialinterestrate();
		}
	
	// Monthly update of current global values
	public static void monthlyglobalvalueupdate() {
		currentmarketprice = ShortTermMarket.getcurrentmarketprice();
		currentinterestrate = currentinterestrate + RandomHelper.nextDoubleFromTo(-0.002, 0.002);
		numberofbuyoffersstm = ShortTermMarket.getnumberofbuyoffers();
		numberofselloffersstm = ShortTermMarket.getnumberofselloffers();
		
		producersphysicalposotion = 0;
		tradersphysicalposotion = 0;
		obligatedpurchasersphysiclaposotion = 0;
		
		for (ActiveAgent pa: CommonMethods.getPAgentList()){
			producersphysicalposotion = producersphysicalposotion + pa.getphysicalnetposition();	
		}
		for (ActiveAgent opa: CommonMethods.getOPAgentList()){
			obligatedpurchasersphysiclaposotion = obligatedpurchasersphysiclaposotion + opa.getphysicalnetposition();	
		}
		for (ActiveAgent ta: CommonMethods.getTAgentList()){
			tradersphysicalposotion = tradersphysicalposotion + ta.getphysicalnetposition();	
		}
		
	}
	
	// Annual update of annual chaning global values
	// These prices should be updated every months I guess and displayed in one curve dispalying future prises. 
	public static void annualglobalvalueupdate() {
		endofyearpluss1 = currentmarketprice*(1+currentinterestrate);
		endofyearpluss2 = currentmarketprice*Math.pow((1+currentinterestrate), 2);
		endofyearpluss3 = currentmarketprice*Math.pow((1+currentinterestrate), 3);
		endofyearpluss4 = currentmarketprice*Math.pow((1+currentinterestrate), 4);
		endofyearpluss5 = currentmarketprice*Math.pow((1+currentinterestrate), 5);
		//TBD
	}
}