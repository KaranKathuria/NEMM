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

//Class definitions
public final class ParameterWrapper {
	
	// Parameter variables (and default values)
	private static int produceragentsnumber;
	private static int obligatedpurchaseragentsnumber;
	private static int analysisagentsnumber;
	private static double priceexpectation;
	private static int monthlydemand;
	private static int monthlysupply;
	private static double initialinterestrate;
	
	
	// Getter methods associated with parameters
	
	public static int getproduceragentsnumber() {
		return produceragentsnumber;
	}

	public static int getobligatedpurchaseragentsnumber() {
		return obligatedpurchaseragentsnumber;
	}
	
	public static int getanalysisagentsnumber() {
		return analysisagentsnumber;
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


	public static void reinit() {
		final Parameters parameters = RunEnvironment.getInstance().getParameters();

		produceragentsnumber = ((Integer) parameters.getValue("produceragentsnumber")).intValue();

		obligatedpurchaseragentsnumber = ((Integer) parameters.getValue("obligatedpurchaseragentsnumber")).intValue();
		
		analysisagentsnumber = ((Integer) parameters.getValue("analysisagentsnumber")).intValue();

		priceexpectation = ((Double) parameters.getValue("priceexpectation")).doubleValue();

		monthlydemand = ((Integer) parameters.getValue("monthlydemand")).intValue();
		
		monthlysupply = ((Integer) parameters.getValue("monthlysupply")).intValue();
		
		initialinterestrate = ((Double) parameters.getValue("initialinterestrate")).doubleValue();
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