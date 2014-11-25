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
//	public static double OPAgentmustbuypremium = 0.5;
//	public static double PAgentmustselldiscount = 0.5;
//	public static double PAgentmustsellshare = 0.5;				//RandomHelper.nextDoubleFromTo(0.4,0.6); //0.5;
//	public static double OPAgentmustsellshare = 0.75; 		 //RandomHelper.nextDoubleFromTo(0.4,0.6); //0.5;
	
	public static double tradermaximumshortpos = -4000; //These values should have some realtion to the initial portfoliovalue by allowing traders
	public static double tradermaximumlongpos = 4000;	//to go bust.
	public static double portfoliocapitalexitlimit = 100000; //reaching this limit triggers and "exit" behavior. This each reach with strong price drop/increase
	
	public static int numofhistutilitiesincluded = 3; //Used by method that deterines the tactics best utility.
	
//	public static double forcastweights[] = {0.2, 0.3, 0.5};  Not in used anymore as the AA have individually randomly generated forecastweights. 
	
	// Utilities
	public static int utilityDefault_PA = 2; // default utility for a purchaser agent
	public static int utilityDefault_OP = 2; // default utility for a obligated agent
	public static int utilityDefault_TR = 1; // default utility for a trader agent
	
	// ---- GJB Added
	
	// ST price prognosis - exponential smoothing parameters
	public static double maxAlphaSTPrice = 0.8;
	public static double minAlphaSTPrice = 0.5;
	
	// distribution cutoffs used for assigning a strategy to a given agent

	public static double[] cutoffPAExit = new double[]{0.35,0.75,1.0};
	public static double[] cutoffOPExit = new double[]{0.6,1.0};

	// number of ticks for each strategy (e.g. 12 means the agent will try to sell its current physical position
	// over the next 12 ticks - i.e. it will sell 1/12th in the next tick
	public static int[] numTicksPAExit = new int[]{2,12,64};
	public static int[] numTicksOPExit = new int[]{1,2};
	// Tactics - number of ticks to exit positions
	// Number of exit strategies for each agent type
	public static int numPAExitStrategies = numTicksPAExit.length;
	public static int numOPExitStrategies = numTicksOPExit.length;
	// ---- end GJB Added
	
	
	// Producer Strategies & Tactics
	// Note - not all of these need be used in any given tactic & strategy
//	public static double multOfferVol_PASellStrategy1 = 2; // default max fraction of that month's physical position than can be sold in the month
	public static int tacticDefaultLearn_PASellStrategy1 = 0; // Default learning algorithm for producer tactics (0 = none)
	// Sell share max and min levels (min used for must sell, max used for rest volume)
	public static double tacticMinPhysPosSellShare_PASellStrategy1 = 0.10; // Minimum must sell % of physical position
	public static double tacticMaxPhysPosSellShare_PASellStrategy1 = 1.0; // Sell up to this fraction of the physical position
//	public static double tacticDefaultMustSellPriceMult_PASellStrategy1 = 0.5;  // Default must sell price multiplier for the producer tactics (if used)
	// Define the range of price multipliers for the target sales (must sell) and rest volumes 
	public static double tacticMinMustSellPriceMult_PASellStrategy1 = 0.7;
	public static double tacticMaxMustSellPriceMult_PASellStrategy1 = 1.0;
	public static int tacticNumMustSellSteps_PASellStrategy1 = 4;
	public static double tacticMinRestPriceMult_PASellStrategy1 = 0.9; // Specify the multiplier range
	public static double tacticMaxRestPriceMult_PASellStrategy1 = 1.5;
	public static int tacticNumRestSteps_PASellStrategy1 = 7;
	// Define the max and min allowable change in price multiplier when direct learning permitted
	public static double tacticMinRestPriceStep_PASellStrategy1 = 0.025; // Step size range for the multiplier if learning is used
	public static double tacticMaxRestPriceStep_PASellStrategy1 = 0.025;
	
	// Certificate value calculation parameters
	public static double certMaxPrice = 500;
	public static double certMinPrice = 0;
	public static double[] ratioAdjFactor = new double[]{0.9,1,1.1}; // used to capture uncertainty in the ratio estimates
	public static double[] ratioAdjProb = new double[]{0.3,0.4,0.3}; // same
	public static double sdevCVSupply = 0.0; // Std dev of supply prognosis used in CV calcs in %
	public static double valueCertShortfall = 250; // value (cost) of not having enough certificates
	
	// Supplier (Obligated Purchaser) strategies and tactics
	public static int tacticDefaultLearn_OPBuyStrategy1 = 0; // Default learning algorithm for producer tactics (0 = none)
	// Define the max and min volumes to be purchased in any given period
	public static double tacticMinPhysPosBuyShare_OPBuyStrategy1 = 0.75; // Default must Buy share for the producer tactics (if used)
	public static double tacticMaxPhysPosBuyShare_OPBuyStrategy1 = 1; // default max fraction of physical position that can be purchased in the month
	public static double tacticDefaultMustBuyPriceMult_OPBuyStrategy1 = 1.5;  // Default must Buy price multiplier for the producer tactics (if used)
	public static double tacticMinMustBuyPriceMult_OPBuyStrategy1 = 0.9;
	public static double tacticMaxMustBuyPriceMult_OPBuyStrategy1 = 1.1;
	public static int tacticNumMustBuySteps_OPBuyStrategy1 = 5;
	public static double tacticMinRestPriceMult_OPBuyStrategy1 = 0.6; // Specify the multiplier range
	public static double tacticMaxRestPriceMult_OPBuyStrategy1 = 1.2;
	public static int tacticNumRestSteps_OPBuyStrategy1 = 7;
//	public static int numTactics_OPBuyStrategy1 = 11; // The number of tactics to make
	public static double tacticMinRestPriceStep_OPBuyStrategy1 = 0.025; // Step size range for the multiplier if learning is used
	public static double tacticMaxRestPriceStep_OPBuyStrategy1 = 0.025;	
	
	//public static double randomfactorinintialstpriceexpectations = X; //Se market prognoisis constructor
	//public static double randomfactorininmediummrundpriceexpectations = X; //Se market prognoisis constructor //This two could have the same random numer!

	//FundamentalMarketAnalysis and Project Developement
	public static double penaltyratio = 1.5;						//What is the penalty compared to current market price?
	public static int yearstoendogprojects = 2;						//Just to save time in the FMA.
	public static double maxpricecerts = 250;						//To not get an errror in the FMA.
	public static double initialRRRcorrector = 1;					//Corrector used to corrct the project specificRRR to usedRRR in the Fundamental Market Analysis
	public static double RRRpostpondpremium = 0.005;				//Risk premium (basispoints 0.01 = 1%) need to be covered if the investment decision is to be postpond. 
	public static int minpostpondyears = 1;							//How long the investment decision is postpond if postponed.Cannot see why this should be larger than 1. (only argument is if this is the real deal).
	public static int MPECount = 18;								//Number of futuer years seen by the MPE-analysis. THats number-1 years ahead (including this year).
	public static int LPECount = MPECount+minpostpondyears;			//Number of futuer years seen by the LPE-analysis
	public static double stdmediumrunpriceexpect = 0.06;    		//The standard deviation (percent) in the Normaly distributed error for MPE (where mean is the perfect foresight price)
	public static double stdlongrunpriceexpect = 0.08;       		//The standard deviation (percent) in the Normaly distributed error for MPE (where mean is the perfect foresight price)
	public static double[] developerinvestmenttypedistribution = new double[]{0.4,0.8,1};	//Share of type 1, 2 and 3. 1 is the fundamental, 2 is the current price for all years, 3 is current price for x years.
	public static int numberoftickstocalculatehistcertprice = 6;	
	public static int numberofyearcertscanbehedged = 2;
	public static int constructionconstraints = 2; 					//2->12	//Factor from 1-3 that determines the degree of construction constraints (how many projects can be constructed simultainasly per agent. 1 = 1*(sizecode*regioncode + 2), 2 = 2*(sizecode*regioncode + 2), etc  
	public static int preprojectandidentifyconstraint = 2;			//2->16 //Factor from 1-3 that determines the degree of construction constraints (how many projects can be constructed simultainasly per agent. 1 = 1*(sizecode*regioncode + 4), etc
	public static int yearsbuildout = 8;							//Number of years aggragate shortcoming that is assumbed build in one year in the FMA.
	
	//Initial distribution of powerplants, projects and demandshares per region.
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
	public static int maxyearsinconcessionqueue = 4;				//Number of years in addition to minimum number of years in concession queue given as input from excel. After this, if not having received concession, the project is trashed.
	public static double annualprobforreceivingconcession = 0.3;	//Only with one decimal as the random generater uses int.
	public static int expectedyersinconcession = 5;					//When deciding for concession, how long to the developers expected the project to be in line. Needed due to learningcurve/CAPEX estimation.
	
	// ---- GJB Added
	public static boolean useTestData = false;
	// ---- end GJB Added
}












