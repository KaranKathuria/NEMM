/*
 * Version info:
 *     All variables used in the model collected at one place
 *     
 *     Last altered data: 20140904
 *     Made by: Karan Kathuria
 */
package nemmcommons;

public class AllVariables {
	
	//Number of tactics in each of the respective strategies. More tactics gives the agent more alternatives.
	public static int numberoftacticsBuyStrategy1 = 1;
	public static int numberoftacticsSellStrategy1 = 1;
	public static int numberoftacticsTraderStrategy1 = 8;
	public static double OPAgentmustbuypremium = 0.5;
	public static double PAgentmustselldiscount = 0.5;
	public static double PAgentmustsellshare = 0.7;
	public static double OPAgentmustsellshare = 0.7;
	
	public static double mediumrundpriceexpectations = 220; //2 year expected price. Used by tactics to calculate floor on variable bids. This together with the risk free rate and the agents RAR.
	public static double longrundpriceexpectations = 300; //10 year expected price - not in use.
	 
	public static double tradermaximumshortpos = -5000; //These values should have some realtion to the initial portfoliovalue by allowing traders
	public static double tradermaximumlongpos = 5000;	//to go bust.
	public static double portfoliocapitalexitlimit = 50000; //reaching this limit triggers and "exit" behavior. This each reach with strong price drop/increase
	
	public static int numofhistutilitiesincluded = 4; //Used by method that deterines the tactics best utility.
	
	public static double forcastweights[] = {0.2, 0.3, 0.5};
	
	
	
}

