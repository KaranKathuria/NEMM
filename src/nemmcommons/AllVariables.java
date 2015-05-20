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
import static nemmcommons.ParameterWrapper.*;

public class AllVariables {
	
	//Unfortuanaty there is now other way then setting the obligation period intervall (ticks) manually.
	public static final int obintr = 12; // TheEnvironment.theCalendar.getNumTradePdsInObligatedPd();
	public static final int firstrealtick = 32; //Currently this cannot be between 1 and 11 (inclusive). This fucks up the contextbuilder.
	public static double[] historiccertprices = new double[]{20.2,	18.37,	24.92,	18.77,	18.26,	18.37,	19.81,	20.22,	20.99,	22.33,	22.92,	24.09,	23.71,	25.15,	23.19,	24.68,	21.61,	20.09,	21.17,	21.05,	22.44,	22.59,	21.61,	21.14,	20.06,	21.93,	20.47,	19.48,	19.75,	19.58,	19.54,	20.2,	20.75,	20.14,	19.47,	18.2};
	public static double bankPAfirstrealtick =  10400000;//8302000;//	//Bank at thefirstrealtick. If tick 0 this should be 8800 000. For memo Dec 2014, 10500000 was used.  2014: 8302000
	public static double bankOPAfirstrealtick =   400000;//3558000;//		//Bank at thefirstrealtick.	If tick 0 this should be 0. For memo Dec 2014, 0 was used. 2014: 3558000 soure: http://downloads.montel.no/ELSERT15/Hans%20Petter%20Kildal.pdf
	public static double bankTAfirstrealtick = 0;				//Bank at thefirstrealtick.	If tick 0 this should be 0

	public static boolean useTestData = false;

	
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
		
	// ST price prognosis - exponential smoothing parameters
	public static double maxAlphaSTPrice = 0.8;
	public static double minAlphaSTPrice = 0.5;
	
	// distribution cutoffs used for assigning a strategy to a given agent
	public static double[] cutoffPAExit = new double[]{0.1,0.8,1.0};		//Last increasse the end. Middel increses the level, the first tend to give "correct" prices earlier.
	public static double[] cutoffOPExit = new double[]{0.7,1.0};

	// number of ticks for each strategy (e.g. 12 means the agent will try to sell its current physical position
	// over the next 12 ticks - i.e. it will sell 1/12th in the next tick
	public static int[] numTicksPAExit = new int[]{3,36,72};
	public static int[] numTicksOPExit = new int[]{2,12};
	// Tactics - number of ticks to exit positions
	// Number of exit strategies for each agent type
	public static int numPAExitStrategies = numTicksPAExit.length;
	public static int numOPExitStrategies = numTicksOPExit.length;	
	
	// ---- PRODUCER STRATEGIES AND TACTIC PARAMETERS
	// Note - not all of these need be used in any given tactic & strategy
//	public static double multOfferVol_PASellStrategy1 = 2; // default max fraction of that month's physical position than can be sold in the month
	public static int tacticDefaultLearn_PASellStrategy1 = 0; // Default learning algorithm for producer tactics (0 = none)
	// Sell share max and min levels (min used for must sell, max used for rest volume)
	public static double tacticMinPhysPosSellShare_PASellStrategy1 = 0.07; // Minimum must sell % of physical position . Alot to say.This flattens and pushes it all down, price more dependent on curren balance dispite ratio and holding horizont.
	public static double tacticMaxPhysPosSellShare_PASellStrategy1 = 1.0; // Sell up to this fraction of the physical position
//	public static double tacticDefaultMustSellPriceMult_PASellStrategy1 = 0.5;  // Default must sell price multiplier for the producer tactics (if used)
	// Define the range of price multipliers for the target sales (must sell) and rest volumes 
	public static double tacticMinMustSellPriceMult_PASellStrategy1 = 0.9;
	public static double tacticMaxMustSellPriceMult_PASellStrategy1 = 1.2;
	public static int tacticNumMustSellSteps_PASellStrategy1 = 5;
	public static double tacticMinRestPriceMult_PASellStrategy1 = 0.7; // Specify the multiplier range
	public static double tacticMaxRestPriceMult_PASellStrategy1 = 1.3;
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
	public static double certMaxPrice = 200;
	public static double certMinPrice = 0;
	public static double[] ratioAdjFactor = new double[]{0.8,1,1.2}; // used to capture uncertainty in the ratio estimates
	public static double[] ratioAdjProb = new double[]{0.3,0.4,0.3}; // same
	public static double sdevCVSupply = 0.0; // Std dev of supply prognosis used in CV calcs in %
	public static double valueCertShortfall = 200; // value (cost) of not having enough certificates
	public static boolean flagDiscountCV = true;
	
	// ---- OBLIGATED PURCHASER STRATEGIES AND TACTIC PARAMETERS
	public static int tacticDefaultLearn_OPBuyStrategy1 = 0; // Default learning algorithm for producer tactics (0 = none)
	// Define the max and min volumes to be purchased in any given period
	public static double tacticMinPhysPosBuyShare_OPBuyStrategy1 = 0.75; // Default must Buy share for the producer tactics (if used)
	public static double tacticMaxPhysPosBuyShare_OPBuyStrategy1 = 1; // default max fraction of physical position that can be purchased in the month
	public static double tacticDefaultMustBuyPriceMult_OPBuyStrategy1 = 1.5;  // Default must Buy price multiplier for the producer tactics (if used)
	public static double tacticMinMustBuyPriceMult_OPBuyStrategy1 = 0.8;
	public static double tacticMaxMustBuyPriceMult_OPBuyStrategy1 = 1.1;
	public static int tacticNumMustBuySteps_OPBuyStrategy1 = 5;
	public static double tacticMinRestPriceMult_OPBuyStrategy1 = 0.7; // Specify the multiplier range
	public static double tacticMaxRestPriceMult_OPBuyStrategy1 = 1.3;
	public static int tacticNumRestSteps_OPBuyStrategy1 = 7;
//	public static int numTactics_OPBuyStrategy1 = 11; // The number of tactics to make
	public static double tacticMinRestPriceStep_OPBuyStrategy1 = 0.025; // Step size range for the multiplier if learning is used
	public static double tacticMaxRestPriceStep_OPBuyStrategy1 = 0.025;	
	// Additional discount rates for the ceiling price calculation
	public static double tacticExtraCeilingDiscountRate = 0.05;
	// Exponential smoothing parameter for utility learning
	public static double tacticMaxUtilityAlphaOP = 0.4;
	public static double tacticMinUtilityAlphaOP = 0.2;

	// ------- FundamentalMarketAnalysis and Project Developement
	public static double penaltyratio = 1.5;						//What is the penalty compared to current market price?
	public static int yearstoendogprojects = 2;						//Just to save time in the FMA.
	public static double maxpricecerts = 200;						//To not get an errror in the FMA.
	public static double initialRRRcorrector = 1.0;					//Corrector used to corrct the project specificRRR to usedRRR in the Fundamental Market Analysis. Copnsate (0.098) for the learningfactor in inputt.
	public static double RRRpostpondpremium = 0.01;					//Risk premium (basispoints 0.01 = 1%) need to be covered if the investment decision is to be postpond. 
	public static int minpostpondyears = 1;							//How long the investment decision is postpond if postponed.Cannot see why this should be larger than 1. (only argument is if this is the real deal).
	public static int MPECount = 18;								//Number of futuer years seen by the MPE-analysis. THats number-1 years ahead (including this year).
	public static int LPECount = MPECount+minpostpondyears;			//Number of futuer years seen by the LPE-analysis
	public static int yearsbuildout = 16;							//Number of years aggragate shortcoming that is assumbed build in one year in the FMA.

	//THE FMA and MPE/LPE
	public static double stdmediumrunpriceexpect = 0.04;    		//The standard deviation (percent) in the Normaly distributed error for MPE (where mean is the perfect foresight price)
	public static double stdlongrunpriceexpect = 0.06;       		//The standard deviation (percent) in the Normaly distributed error for MPE (where mean is the perfect foresight price)
	
	//Project Development
	public static double[] developerinvestmenttypedistribution = new double[]{0.1,0.7,0.9,1};	//{F,FP,PF,R} Distribution of investmentagents-type 1 and 2 is the fundamental, where 2 is fundamental and dependent on price times a mulitplicator. 3 is the current price for all years with some varing dependent on cost curve, 4 is restricted to current price for x years.
	public static int numberoftickstocalculatehistcertprice = 3;	//This could be exptended to about 6. THis is basically the parameter setting how long memory developers have when taking investment decison based on current price. 
	public static int numberofyearcertscanbehedged = 2;
	public static int constructionconstraints = 3; 					//Times sizecode gives projects under constrction 
	public static int preprojectandidentifyconstraint = 6;			//Times sizecode gives projects for prep and ident
	public static double[] developerinvestmentpriceeasefactordistribution = new double[]{1.1,8};			//The distribution of priceeasefactor given to investmentagent type 2 (fundamental with ease price). High number indicates little restriction.(Type 1 typically has 500 on this).
	public static double[] developerinvestmentfundamentaleasefactordistribution = new double[]{1.01,1.07}; //The distribution of fundamentaleasefaactor to investmentagent type 3 (price based with ease cost curve). High number indicates little restriction.
	public static double buildoutaggressivness = 1; 				//Factor determining how aggressiv the build out is. Less then one means that the construction limit for any given year is less then what is needed in terms of annual producion, whereas much higher then one limits to what all developers are willing to build (financially and resoruce wise)
	
	
	//Initial distribution of powerplants, projects and demandshares per region.
	public static int powerplantdistributioncode = 1; 	// 1=Unifrom, 2=probabilityadjusted1 3=probabilityadjusted2
	public static int projectsdistributioncode = 1; 	// 1=Unifrom, 2=probabilityadjusted1 3=probabilityadjusted2
	public static int demandsharedistrubutioncode = 1; 	// 1=Unifrom, 2=probabilityadjusted1 3=probabilityadjusted2

	public static double earlystageInvestRRRAdjustFactor = 0.0025;
	public static double minInvestRRRAdjustFactor = 0.99;		//Corrector of 0.9 implies a lower bond on about 0.9*8=7.2% RRR. This is aboute 2-3 Euro per MW difference in cert price needed (NPV).
	public static double maxInvestRRRAdjustFactor = 1.01;		//Not sure this is autually used ad Project development uses the market RRR
	
	
	
	// Generic Strategy 
	public static int MaxTacticPreferenceScore = 6;
	public static int MinTacticPreferenceScore = 2;

	//Regulations for certificates deadline
	public static boolean certificatespost2020_Norway = false;
	public static boolean certificatespost2020_Sweden = true;
	public static int cutoffyear_Norway = 2020;						//The last year the plant must be in operation in order to be eligable for certificates in Norway. Currently 2020 or 2021
	public static int cutoffyear_Sweden = 2020;						//As above for Sweden. Not in use if certificatespost2020_Sweden = true.
	
	//The concession and preconstruction process
	public static int maxyearsinconcessionqueue = 3;				//Number of years in addition to minimum number of years in concession queue given as input from excel. After this, if not having received concession, the project is trashed.
	public static double annualprobforreceivingconcession = 0.2;	//Only with one decimal as the random generater uses int.
	public static int expectedyersinconcession = 5;					//When deciding for concession, how long to the developers expected the project to be in line. Needed due to learningcurve/CAPEX estimation.
		
	// The first tick banks. 11.02.2015 KK: Not sure this is used.
	public static double bankPAFirstTick = 	0;//8000000;
	public static double bankOPAFirstTick = 0;//20000;
	public static double bankTAFirstTick = 0;
	
	//For scenarios (wind and power price)
	
	
	
	//For initial weather simulations setup. Old inputs. Used for Q1 2015:
	public static double meanwindproductionfactor = getmeanwindproductionfactor();
	public static double stdwindfactor = getstdwindproductionfactor();
	public static double maxstdwindfactor = 3;										//Cutoff deviation in wind production factor. If 3 this means that it cannot blow less or more then 3 times the standard deviation. 
	
}














