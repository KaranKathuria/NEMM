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
	public static int numberoftacticsBuyStrategy1 = 20;
	public static int numberoftacticsSellStrategy1 = 20;
	public static int numberoftacticsTraderStrategy1 = 8;
	public static double OPAgentmustbuypremium = 0.5;
	public static double PAgentmustselldiscount = 0.5;
	
	public static double tradermaximumshortpos = -2000;
	public static double tradermaximumlongpos = 2000;
	
	
	public static int numofhistutilitiesincluded = 3; //Used by method that deterines the tactics best utility.
	

}

