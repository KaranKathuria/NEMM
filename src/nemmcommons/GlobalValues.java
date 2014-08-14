/*
 * Version info:
 *     A simple class which exists to store global current values. Note that this is a static class as all its member variables are static. 
 *     Last altered data: 20140721
 *     Made by: Karan Kathuria  
 */

package nemmcommons;

// Import
import repast.simphony.random.RandomHelper;
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
	
	
	
	public GlobalValues() {
		currentmarketprice = nemmcommons.ParameterWrapper.getpriceexpectation();
	}
	
	public static void initglobalvalues() {
		//Initially the global market price is set to that of the price expectations
		currentmarketprice = nemmcommons.ParameterWrapper.getpriceexpectation();
		currentinterestrate = nemmcommons.ParameterWrapper.getinitialinterestrate();
		}
	
	// Monthly update of current global values
	public static void monthlyglobalvalueupdate() {
		currentmarketprice = ShortTermMarket.getcurrentmarketprice();
		currentinterestrate = currentinterestrate + RandomHelper.nextDoubleFromTo(-0.002, 0.002);
		numberofbuyoffersstm = ShortTermMarket.getnumberofbuyoffers();
		numberofselloffersstm = ShortTermMarket.getnumberofselloffers();
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