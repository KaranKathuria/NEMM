/*
 * Version info:
 *     A simple wrapper class that handles the parameters and their values specified for the simulation.
 *     Last altered data: 20140721
 *     Made by: Karan Kathuria  
 */

package nemmcommons;

// Import
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;

//Class definitions
public final class ParameterWrapper {
	
	// Parameter variables (and default values)
	private static int randomSeed;
	private static int produceragentsnumber;
	private static int obligatedpurchaseragentsnumber;
	private static int analysisagentsnumber;
	private static int traderagentsnumber;
	private static double priceexpectation = 20.2;		//This is the initial expectations used by agents for the first tick. Now (20.2) set to what it actually was.
	private static int monthlydemand;
	private static int monthlysupply;
	private static double initialinterestrate = 0.02;			//Used to initiate the GlobalValue current interest rate. This again is used to calculate the risk-free rate in bidding.
	private static double meanwindproductionfactor = 1;
	private static double stdwindproductionfactor;
	private static int scenarionumber;					//Indicates which scenario to run. Number is refering to the position in the read in table (0 is the first)
	//private static int startYear = 2012;
	//private static int endYear = 2014;
	//private static int numObPdsInYear = 1;
	//private static int numTradePdsInObPd = 12;
	//
	
	
	// Getter methods associated with parameters
	
	
	public static int getrandomseed() {
		return randomSeed;
	}
	public static double getmeanwindproductionfactor() {
		return meanwindproductionfactor;
	}
	public static double getstdwindproductionfactor() {
		return stdwindproductionfactor;
	}
	public static int getproduceragentsnumber() {
		return produceragentsnumber;
	}

	public static int getobligatedpurchaseragentsnumber() {
		return obligatedpurchaseragentsnumber;
	}
	public static int getscenarionumber() {
		return scenarionumber;
	}
	
	public static int getanalysisagentsnumber() {
		return analysisagentsnumber;
	}
	public static int gettraderagentsnumber() {
		return traderagentsnumber;
	}

	public static double getpriceexpectation() {
		return priceexpectation;
	}
	
	public static int getmonthlydemand() {
		return monthlydemand;
	}

	public static int getmonthlysupply() {
		return monthlysupply;
	}
	
	public static double getinitialinterestrate() {
		return initialinterestrate;
	}
	
	/*public static int getstartyear() {
		return startYear;
	}
	
	public static int getendyear() {
		return endYear;
	}

	public static int getnumObPdsInYear() {
		return numObPdsInYear;
	}
	
	public static int getnumTradePdsInObPd() {
		return numTradePdsInObPd;
	}
	*/


	public static void reinit() {
		final Parameters parameters = RunEnvironment.getInstance().getParameters();
		
		randomSeed = ((Integer) parameters.getValue("randomSeed")).intValue();
		
		produceragentsnumber = ((Integer) parameters.getValue("produceragentsnumber")).intValue();

		obligatedpurchaseragentsnumber = ((Integer) parameters.getValue("obligatedpurchaseragentsnumber")).intValue();
		
		analysisagentsnumber = ((Integer) parameters.getValue("analysisagentsnumber")).intValue();
		
		traderagentsnumber = ((Integer) parameters.getValue("traderagentsnumber")).intValue();

		//priceexpectation = ((Double) parameters.getValue("priceexpectation")).doubleValue();
		
		//initialinterestrate = ((Double) parameters.getValue("initialinterestrate")).doubleValue();
		
		stdwindproductionfactor = ((Double) parameters.getValue("stdwindproductionfactor")).doubleValue();
		
		//meanwindproductionfactor = ((Double) parameters.getValue("meanwindproductionfactor")).doubleValue(); 
		
		scenarionumber = ((Integer) parameters.getValue("scenarionumber")).intValue();
		
		/*startYear = ((Integer) parameters.getValue("startYear")).intValue();
		
		endYear = ((Integer) parameters.getValue("endYear")).intValue();
		
		numObPdsInYear = ((Integer) parameters.getValue("numObPdsInYear")).intValue();
		
		numTradePdsInObPd = ((Integer) parameters.getValue("numObPdsInYear")).intValue();*/
		
		//KK 20160101
		RandomHelper.setSeed(randomSeed);
		RandomHelper.createUniform();
		
	}


	// ========================================================================

	/**
	 * This class should be utilized by its static interface so it should not be
	 * instantiated. For this reason the constructor is hidden.
	 */
	 
	private ParameterWrapper() {
		;
	}
	
	

}

//	<parameter defaultValue="1" displayName="Mean annual wind production factor" name="meanwindproductionfactor" type="double"></parameter>
//	<parameter defaultValue="16.7" name="priceexpectation" displayName="Price Expectation" type="double"></parameter>
// 	<parameter defaultValue="0.020" displayName="Intial market interest rate" name="initialinterestrate" type="double"></parameter>

