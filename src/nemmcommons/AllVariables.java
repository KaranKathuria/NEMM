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
	public static final int firstrealtick = 36; //Currently this cannot be between 1 and 11 (inclusive). This fucks up the contextbuilder.
	public static double[] historiccertprices = new double[]{19.09,	17.36,	23.55,	17.74,	17.26,	17.36,	18.72,	19.11,	19.84,	21.1,	21.66,	22.77,	22.3823529411765,	23.7450980392157,	21.8921568627451,	23.3039215686275,	20.4019607843137,	18.9705882352941,	19.9901960784314,	19.8725490196078,	21.1862745098039,	21.3333333333333,	20.4019607843137,	19.9607843137255,	18.5601691657055,	19.963475586313,	21.5494040753556,	18.5890042291426,	18.6082276047674,	18.8485198000769,	18.4159938485198,	18.9638600538255,	19.2714340638216,	18.8677431757017,	18.4928873510188,	18.4928873510188};
	public static double bankatstarttick = 10000000; 	//Total banked certificates in the market at firstrealtick.
	public static boolean betw12_24 = true;
	public static boolean betw24_36 = true;
	
	
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

	public static double[] cutoffPAExit = new double[]{0.50,0.85,1.0};
	public static double[] cutoffOPExit = new double[]{0.6,1.0};


	// number of ticks for each strategy (e.g. 12 means the agent will try to sell its current physical position
	// over the next 12 ticks - i.e. it will sell 1/12th in the next tick
	public static int[] numTicksPAExit = new int[]{2,24,64};
	public static int[] numTicksOPExit = new int[]{1,2};
	// Tactics - number of ticks to exit positions
	// Number of exit strategies for each agent type
	public static int numPAExitStrategies = numTicksPAExit.length;
	public static int numOPExitStrategies = numTicksOPExit.length;
	// ---- end GJB Added
	
	
	// ---- PRODUCER STRATEGIES AND TACTIC PARAMETERS
	// Note - not all of these need be used in any given tactic & strategy
//	public static double multOfferVol_PASellStrategy1 = 2; // default max fraction of that month's physical position than can be sold in the month
	public static int tacticDefaultLearn_PASellStrategy1 = 0; // Default learning algorithm for producer tactics (0 = none)
	// Sell share max and min levels (min used for must sell, max used for rest volume)
	public static double tacticMinPhysPosSellShare_PASellStrategy1 = 0.10; // Minimum must sell % of physical position
	public static double tacticMaxPhysPosSellShare_PASellStrategy1 = 1.0; // Sell up to this fraction of the physical position
//	public static double tacticDefaultMustSellPriceMult_PASellStrategy1 = 0.5;  // Default must sell price multiplier for the producer tactics (if used)
	// Define the range of price multipliers for the target sales (must sell) and rest volumes 
	public static double tacticMinMustSellPriceMult_PASellStrategy1 = 0.9;
	public static double tacticMaxMustSellPriceMult_PASellStrategy1 = 1.1;
	public static int tacticNumMustSellSteps_PASellStrategy1 = 5;
	public static double tacticMinRestPriceMult_PASellStrategy1 = 0.9; // Specify the multiplier range
	public static double tacticMaxRestPriceMult_PASellStrategy1 = 1.5;
	public static int tacticNumRestSteps_PASellStrategy1 = 7;
	// Define the max and min allowable change in price multiplier when direct learning permitted
	public static double tacticMinRestPriceStep_PASellStrategy1 = 0.025; // Step size range for the multiplier if learning is used
	public static double tacticMaxRestPriceStep_PASellStrategy1 = 0.025;
	// Additional discount rates for the floor price calculation
	public static double tacticExtraFloorDiscountRate = 0.05;
	// Exponential smoothing parameter for utility learning
	public static double tacticMaxUtilityAlphaPA = 0.4;
	public static double tacticMinUtilityAlphaPA = 0.2;
	// Penalty ratio for not selling "turnover" (Must sell) certificates
	public static double tacticTurnoverPenaltyPA = 0.5;
	
	// ---- CERTIFICATE VALUE CALCULATION PARAMETERS
	public static double certMaxPrice = 250;
	public static double certMinPrice = 0;
	public static double[] ratioAdjFactor = new double[]{0.8,1,1.2}; // used to capture uncertainty in the ratio estimates
	public static double[] ratioAdjProb = new double[]{0.3,0.4,0.3}; // same
	public static double sdevCVSupply = 0.0; // Std dev of supply prognosis used in CV calcs in %
	public static double valueCertShortfall = 150; // value (cost) of not having enough certificates
	public static boolean flagDiscountCV = true;
	
	// ---- OBLIGATED PURCHASER STRATEGIES AND TACTIC PARAMETERS
	public static int tacticDefaultLearn_OPBuyStrategy1 = 0; // Default learning algorithm for producer tactics (0 = none)
	// Define the max and min volumes to be purchased in any given period
	public static double tacticMinPhysPosBuyShare_OPBuyStrategy1 = 0.75; // Default must Buy share for the producer tactics (if used)
	public static double tacticMaxPhysPosBuyShare_OPBuyStrategy1 = 1; // default max fraction of physical position that can be purchased in the month
	public static double tacticDefaultMustBuyPriceMult_OPBuyStrategy1 = 1.5;  // Default must Buy price multiplier for the producer tactics (if used)
	public static double tacticMinMustBuyPriceMult_OPBuyStrategy1 = 0.9;
	public static double tacticMaxMustBuyPriceMult_OPBuyStrategy1 = 1.1;
	public static int tacticNumMustBuySteps_OPBuyStrategy1 = 5;
	public static double tacticMinRestPriceMult_OPBuyStrategy1 = 0.5; // Specify the multiplier range
	public static double tacticMaxRestPriceMult_OPBuyStrategy1 = 1.1;
	public static int tacticNumRestSteps_OPBuyStrategy1 = 7;
//	public static int numTactics_OPBuyStrategy1 = 11; // The number of tactics to make
	public static double tacticMinRestPriceStep_OPBuyStrategy1 = 0.025; // Step size range for the multiplier if learning is used
	public static double tacticMaxRestPriceStep_OPBuyStrategy1 = 0.025;	
	// Additional discount rates for the ceiling price calculation
	public static double tacticExtraCeilingDiscountRate = 0.05;
	// Exponential smoothing parameter for utility learning
	public static double tacticMaxUtilityAlphaOP = 0.4;
	public static double tacticMinUtilityAlphaOP = 0.2;
	
	
	//public static double randomfactorinintialstpriceexpectations = X; //Se market prognoisis constructor
	//public static double randomfactorininmediummrundpriceexpectations = X; //Se market prognoisis constructor //This two could have the same random numer!

	//FundamentalMarketAnalysis and Project Developement
	public static double penaltyratio = 1.5;						//What is the penalty compared to current market price?
	public static int yearstoendogprojects = 2;						//Just to save time in the FMA.
	public static double maxpricecerts = 250;						//To not get an errror in the FMA.
	public static double initialRRRcorrector = 1.2;					//Corrector used to corrct the project specificRRR to usedRRR in the Fundamental Market Analysis. Copnsate (0.098) for the learningfactor in inputt.
	public static double RRRpostpondpremium = 0.005;				//Risk premium (basispoints 0.01 = 1%) need to be covered if the investment decision is to be postpond. 
	public static int minpostpondyears = 1;							//How long the investment decision is postpond if postponed.Cannot see why this should be larger than 1. (only argument is if this is the real deal).
	public static int MPECount = 18;								//Number of futuer years seen by the MPE-analysis. THats number-1 years ahead (including this year).
	public static int LPECount = MPECount+minpostpondyears;			//Number of futuer years seen by the LPE-analysis

	public static double stdmediumrunpriceexpect = 0.04;    		//The standard deviation (percent) in the Normaly distributed error for MPE (where mean is the perfect foresight price)
	public static double stdlongrunpriceexpect = 0.06;       		//The standard deviation (percent) in the Normaly distributed error for MPE (where mean is the perfect foresight price)
	public static double[] developerinvestmenttypedistribution = new double[]{0.5,0.99,1};	//Share of type 1, 2 and 3. 1 is the fundamental, 2 is the current price for all years, 3 is current price for x years.

	public static int numberoftickstocalculatehistcertprice = 3;	
	public static int numberofyearcertscanbehedged = 2;

	public static int constructionconstraints = 8; 					//Times sizecode gives projects under constrction 
	public static int preprojectandidentifyconstraint = 6;			//Times sizecode gives projects for prep and ident

	public static int yearsbuildout = 16;							//Number of years aggragate shortcoming that is assumbed build in one year in the FMA.
	public static double easeDAtype2 = 1.08;
	
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
	public static int maxyearsinconcessionqueue = 3;				//Number of years in addition to minimum number of years in concession queue given as input from excel. After this, if not having received concession, the project is trashed.
	public static double annualprobforreceivingconcession = 0.3;	//Only with one decimal as the random generater uses int.
	public static int expectedyersinconcession = 5;					//When deciding for concession, how long to the developers expected the project to be in line. Needed due to learningcurve/CAPEX estimation.
	
	// ---- GJB Added
	public static boolean useTestData = false;
	// ---- end GJB Added
}












