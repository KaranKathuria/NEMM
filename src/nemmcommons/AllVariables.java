/*
 * Version info:
 *     All variables used in the model collected at one place
 *     
 *     Last altered data: 20140904
 *     Made by: Karan Kathuria
 */
package nemmcommons;

import java.util.Random;

import nemmenvironment.TheEnvironment;
import repast.simphony.random.RandomHelper;

public class AllVariables {
	
	//Unfortuanaty there is now other way then setting the obligation period intervall (ticks) manually.
	public static final int obintr = 12; // TheEnvironment.theCalendar.getNumTradePdsInObligatedPd();
	
	//Number of tactics in each of the respective strategies. More tactics gives the agent more alternatives.
	public static int numberoftacticsBuyStrategy1 = 1;
//	public static int numberoftacticsSellStrategy1 = 1;
	public static int numberoftacticsTraderStrategy1 = 8;
	public static double OPAgentmustbuypremium = 0.5;
//	public static double PAgentmustselldiscount = 0.5;
//	public static double PAgentmustsellshare = 0.5;//RandomHelper.nextDoubleFromTo(0.4,0.6); //0.5;
	public static double OPAgentmustsellshare = 0.5; //RandomHelper.nextDoubleFromTo(0.4,0.6); //0.5;
	
	public static double stdmediumrunpriceexpect = 0.1;     //The standard deviation in the Normaly distributed error for MPE (where mean is the perfect foresight price)
	public static double stdlongrunpriceexpect = 0.1;     //The standard deviation in the Normaly distributed error for MPE (where mean is the perfect foresight price)
	 
	public static double tradermaximumshortpos = -5000; //These values should have some realtion to the initial portfoliovalue by allowing traders
	public static double tradermaximumlongpos = 5000;	//to go bust.
	public static double portfoliocapitalexitlimit = 100000; //reaching this limit triggers and "exit" behavior. This each reach with strong price drop/increase
	
	public static int numofhistutilitiesincluded = 3; //Used by method that deterines the tactics best utility.
	
//	public static double forcastweights[] = {0.2, 0.3, 0.5};  Not in used anymore as the AA have individually randomly generated forecastweights. 
	
	// Utilities
	public static int utilityDefault_PA = 1; // default utility for a purchaser agent
	public static int utilityDefault_OP = 1; // default utility for a obligated agent
	public static int utilityDefault_TR = 1; // default utility for a trader agent
	
	// Producer Strategies & Tactics
	// Note - not all of these need be used in any given tactic & strategy
	public static double multOfferVol_PASellStrategy1 = 2; // default max fraction of that month's production than can be sold in the month
	public static int tacticDefaultLearn_PASellStrategy1 = 0; // Default learning algorithm for producer tactics (0 = none)
	public static double tacticDefaultMustSellShare_PASellStrategy1 = 0.5; // Default must sell share for the producer tactics (if used)
	public static double tacticDefaultMustSellPriceMult_PASellStrategy1 = 0.5;  // Default must sell price multiplier for the producer tactics (if used)
	public static double tacticMinMustSellPriceMult_PASellStrategy1 = 0.5;
	public static double tacticMaxMustSellPriceMult_PASellStrategy1 = 0.5;
	public static int tacticNumMustSellSteps_PASellStrategy1 = 1;
	public static double tacticMinRestPriceMult_PASellStrategy1 = 0.5; // Specify the multiplier range
	public static double tacticMaxRestPriceMult_PASellStrategy1 = 1.5;
	public static int tacticNumRestSteps_PASellStrategy1 = 11;
	public static int numTactics_PASellStrategy1 = 11; // The number of tactics to make
	public static double tacticMinRestPriceStep_PASellStrategy1 = 0.025; // Step size range for the multiplier if learning is used
	public static double tacticMaxRestPriceStep_PASellStrategy1 = 0.025;
	
	//public static double randomfactorinintialstpriceexpectations = X; //Se market prognoisis constructor
	//public static double randomfactorininmediummrundpriceexpectations = X; //Se market prognoisis constructor //This two could have the same random numer!
	
	//public static double mediumrundpriceexpectations = 220; //2 year expected price. Used by tactics to calculate floor on variable bids. This together with the risk free rate and the agents RAR.
	//public static double longrundpriceexpectations = 300; //10 year expected price - not in use.
	
	//FundamentalMarketAnalysis
	public static int yearstoendogprojects = 4;
	public static double maxpricecerts = 1000;
	public static double RRR = 0.06;
	
	//Initial distribution of powerplants, projects and demandshares.
	public static int powerplantdistributioncode = 1; 	// 1=Unifrom, 2=probabilityadjusted1 3=probabilityadjusted2
	public static int projectsdistributioncode = 1; 	// 1=Unifrom, 2=probabilityadjusted1 3=probabilityadjusted2
	public static int demandsharedistrubutioncode = 1; 	// 1=Unifrom, 2=probabilityadjusted1 3=probabilityadjusted2

	// Generic Strategy
	public static int MaxTacticPreferenceScore = 6;
	public static int MinTacticPreferenceScore = 2;

	//Regulations for certificates deadline
	public static boolean certificatespost2020_Norway = false;
	public static boolean certificatespost2020_Sweden = true;
	
	//The concession and preconstruction process
	public static int maxyearsinconcessionqueue = 7;				//With minconcessionyear = 3, that 4 years with 0.3 chance of getting concession. If not, the project is trashed.
	public static double annualprobforreceivingconcession = 0.3;	//Only with one decimal as the random generater uses int.
	public static int expectedyersinconcession = 5;					//When deciding for concession, how long to the developers expected the project to be in line. Needed due to learningcurve/CAPEX estimation.
	
}












