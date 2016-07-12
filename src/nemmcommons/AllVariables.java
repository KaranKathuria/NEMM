/*
 * Version info:
 *     All variables used in the model collected at one place
 *     
 *     Last altered date: 2016
 *     Made by: Karan Kathuria
 */
package nemmcommons;

import static nemmcommons.ParameterWrapper.*;

public class AllVariables {
	
	//================================================================================= Setting the model paramaters =================================================================================
	// Step 1: Set Case specific parameters - files mapping and intial setup
	// Step 2: Set modell parameters - e.g market design and agent behaviour
	// Step 3: Adjust tuning parameters
	//================================================================================================================================================================================================
	//================================================================================================================================================================================================
	//Step 1: Starts.
	
	public static String casename = "2016_H1_BaseCase"; 					//Name of the case ran. That is not the simulation, not the run, but the base case (or sensitivity case).
	public static String inputfile = "2016_NVE_2016_BC.xlsx";			//Name of inputfile (in case not a backtest. If backtest, the below is used).
	public static String backtestfile = "Q4_2015_NEMM_2012_backtest_typ2.xlsx";
	public static boolean isbacktest = false;					//Alters parts of the code (readexcel and scenario and Context) in order to run backtest (that is 2012 as start year and 300 ticks). Does not later the input data in AllVariables (bank, price etc).
	public static boolean useTestData = false;					
	public static final int IRRcalculationtick = 287;			//If above is true, this is 299, else 287.

	/* ---- IN CASE OF BACKTEST 
	//In addition to unmarking this section, For the years until the real tick, the projects must be finalized, hence the Contextbuilder these schduals must be added (or removed) (line 166 onwards). 
	//Also the values of this AllVariable file must be altered: Cutoff-date Norway = 2020.
	public static final int obintr = 12;
	public static final int firstrealtick = 12;
	public static double[] historiccertprices = new double[]{28.18,	26.84,	25.91,	24.59,	21.52,	19.28,	20.15,	20.88,	21.25,	20.94,	20.34,	18.67}; //
	public static double bankPAfirstrealtick =  8400000;																									
	public static double bankOPAfirstrealtick =  100000;
	public static double bankTAfirstrealtick = 0;
	*/
	
	//---- IN CASE NOT BACKTEST - INCLUDE THIS SECTION.
	public static final int obintr = 12; 
	public static final int firstrealtick = 48; //Altering this means you got to alter the contextbuilder! Currently this cannot be between 1 and 11. 48 = january 2016.
	public static double[] historiccertprices = new double[]{20.58,	18.72,	25.39,	19.13,	18.61,	18.72,	20.18,	20.6,	21.39,	22.75,	23.35,	24.55,	24.16,	25.63,	23.63,	25.15,	22.02,	20.47,	21.57,	21.45,	22.87,	23.02,	22.02,	21.54,	20.45,	22.35,	20.86,	19.85,	20.13,	19.95,	19.91,	20.58,	21.14,	20.52,	19.84,	18.55,	17.83,	16.24,	15.24,	15.88,	16.06,	16.05,	15.13,	15.58,	17.14,	18.13,	17.85,	17.00};   //{20.2,	18.37,	24.92,	18.77,	18.26,	18.37,	19.81,	20.22,	20.99,	22.33,	22.92,	24.09,	23.71,	25.15,	23.19,	24.68,	21.61,	20.09,	21.17,	21.05,	22.44,	22.59,	21.61,	21.14,	20.06,	21.93,	20.47,	19.48,	19.75,	19.58,	19.54,	20.2,	20.75,	20.14,	19.47,	18.2};
	public static double bankPAfirstrealtick =   16800000;		//Faktisk bank //
	public static double bankOPAfirstrealtick =    800000;		//Soure: http://downloads.montel.no/ELSERT15/Hans%20Petter%20Kildal.pdf
	public static double bankTAfirstrealtick = 0;				
	
	
	//Step 1: Ends
	//================================================================================================================================================================================================
	//Step 2: Starts
	
	// ---- HOLDING HORIZONT AND SHORT TERM MARKET AGENTS DISTRIBUTION
	
	// DISTRIBUTION AND LENGTH OG HOLDING HORIZONT FOR AGENTS.
	public static double[] cutoffPAExit = new double[]{0.2,0.4,0.65,1.0};	
	public static double[] cutoffOPExit = new double[]{0.36,0.5,0.9,1.0};
	public static double[] cutoffTExit = new double[]{0.5,1.0};
	public static int[] numTicksPAExit = new int[]{2,6,12,72};			
	public static int[] numTicksOPExit = new int[]{2,6,12,60};			
	public static int[] numTicksTExit = new int[]{86,86};
	public static int cvvaluehorizont = 60;						//72 = 6 years	- Corresponding holdinghorisont (that is CV value horizont) for developer agents.
	public static boolean limitproducerCV = true; 				//If true CVproducer <= CVdeveloper. Logical constraint.
	
	//FORWARD BUYING AND MARKET BALANCE
	public static double shareoffuturehhdemandpurchased = 0.02; //20160120 Share of future demand within the holding horizont that a OPA can buy (in rest vol).
	public static double MaxPhysPosmulitiplier = 2;				//Indicating the maximum the total sell volume can be lager than total demand for certs. Calcuate the above dynamically. THe latter not including forward buying from OPA.
		
	// ---- COMPANY AND DEVELOPER DISTRIBUTION, STRATEGIES AND CONSTRAINTS
	
	//COMPANY REGIOAND AND INVESTMENT STRATEGY DISTRIBUTION
	public static double[] companyregiondistribution = new double[]{0.2,0.8,1.0};	//{0.2,1.0,1.0} //first the prob for Norway, Sweden and third is both. Determines the chance for a new added company beeing in either one (then which) or both countires. (region part 1 = N, 2 = both,  3 = sweden)
	public static double[] developerinvestmenttypedistribution = new double[]{0.2,0.5,0.95,1};	//In use for agents in both regions {F,FP,PF,R}. Distribution of investmentagents-type 1 and 2 is the fundamental, where 2 is fundamental and dependent on price times a mulitplicator. 3 is the current price for all years with some varing dependent on cost curve, 4 is restricted to current price for x years.
	public static double[] developerinvestmenttypedistribution_Norway = new double[]{0.1,0.3,0.95,1};	//{0.1,0.6,0.9,1} 45//{F,FP,PF,R} Distribution of investmentagents-type 1 and 2 is the fundamental, where 2 is fundamental and dependent on price times a mulitplicator. 3 is the current price for all years with some varing dependent on cost curve, 4 is restricted to current price for x years.
	public static double[] developerinvestmenttypedistribution_Sweden = new double[]{0.2,0.5,0.95,1};	//{0.1,0.6,0.9,1} 45//{F,FP,PF,R} Distribution of investmentagents-type 1 and 2 is the fundamental, where 2 is fundamental and dependent on price times a mulitplicator. 3 is the current price for all years with some varing dependent on cost curve, 4 is restricted to current price for x years.
	
	//DISTRIBUTIONCODE OF PLANT, PROJECTS AND DEMAND SHARES
	public static int powerplantdistributioncode = 1; 	// 1=Unifrom, but siezecode=1 gets zero. 2=probabilityadjusted1 3=probabilityadjusted2. Se distributeprjectsandpowerplants.java for details.
	public static int projectsdistributioncode = 1; 	// 1=Unifrom, 2=probabilityadjusted1 3=probabilityadjusted2
	public static int demandsharedistrubutioncode = 1; 	// 1=Unifrom, 2=probabilityadjusted1 3=probabilityadjusted2
	
	//DEVELOPER EASE FACTORS
	//Given to investmentstrategy type 1 and 2:
	public static double fundamentalfundamentaleasefactordistribution = 1.08; //Indicates the fundamental ease factor for fundamental agents. Use as the FMA underestsimats the FMAprice as both agents and analys people know that all investmetns are not taken perfectly. quicfiks 20151201 KK.
	public static double fundamentalfundamentaleasefactordistribution_Norway = 1.08; //Indicates the fundamental ease factor for fundamental agents. Use as the FMA underestsimats the FMAprice as both agents and analys people know that all investmetns are not taken perfectly. quicfiks 20151201 KK. 1.1 means that FMA is 10 % higher than perfect FMA.
	public static double fundamentalfundamentaleasefactordistribution_Sweden = 1.15; //Indicates the fundamental ease factor for fundamental agents. Use as the FMA underestsimats the FMAprice as both agents and analys people know that all investmetns are not taken perfectly. quicfiks 20151201 KK.
	public static double[] developerinvestmentpriceeasefactordistribution = new double[]{1.05,1.15};		//Started with 1.15, 1.4	//The distribution of priceeasefactor given to investmentagent type 2 (fundamental with ease price). High number indicates little restriction.(Type 1 typically has 500 on this).
	public static double[] developerinvestmentpriceeasefactordistribution_Norway = new double[]{1.0,1.05};		//Started with 1.15, 1.4	//The distribution of priceeasefactor given to investmentagent type 2 (fundamental with ease price). High number indicates little restriction.(Type 1 typically has 500 on this).
	public static double[] developerinvestmentpriceeasefactordistribution_Sweden = new double[]{1.15,1.15};		//The distribution of priceeasefactor given to investmentagent type 2 (fundamental with ease price). High number indicates little restriction.(Type 1 typically has 500 on this).
	//Given to investmentstrategy type 3 and 4
	public static double[] developerinvestmentfundamentaleasefactordistribution = new double[]{1.2,1.3};  //The distribution of fundamentaleasefaactor to investmentagent type 3 (price based with ease cost curve). High number indicates little restriction.
	public static double pricedeveloperspriceeasefactordistribution = 1.0; 								  //Priceeasefactor used by pricedevelopers (3). added 20151204 KK. Should be 1, but to midofy earlier round-off in cert prices.

	//DEVELOPER CONSTRAINTS
	public static int numberoftickstocalculatehistcertprice = 2;	//This could be exptended to about 6. THis is basically the parameter setting how long memory developers have when taking investment decison based on current price. 
	public static int numberofyearcertscanbehedged = 3;
	public static int constructionconstraints = 6; 					//Gives number of projects under construction -limit. Reduing this makes the market "less optimal" 
	public static int preprojectandidentifyconstraint = 6;			//Times sizecode gives projects for prep and ident
	public static int backtesteaseconstruction = 2;					//Multiplicator to ease construction constraint when backtesting.This needs to be calibrate in realtion to easefactors. 
	
	//RRRs
	public static double earlystageInvestRRRAdjustFactor = 0.0025;		//Premium when determining if the project identyfied should apply for concession.
	//Spread in developer RRR
	public static double minInvestRRRAdjustFactor = 0.7;				//Corrector of 0.9 implies a lower bond on about 0.9*8=7.2% RRR. This is aboute 2-3 Euro per MW difference in cert price needed (NPV).
	public static double maxInvestRRRAdjustFactor = 1.18;				//Not sure this is autually used ad Project development uses the market RRR
	
	// ---- MARKET DESIGN PARAMETERS
	
	//Regulations for certificates deadline
	public static boolean certificatespost2020_Norway = false;
	public static boolean certificatespost2020_Sweden = false;
	public static int cutoffyear_Norway = 2021;						//The last year the plant must be in operation in order to be eligable for certificates in Norway. Currently 2020 or 2021
	public static int cutoffyear_Sweden = 2021;						//As above for Sweden. Not in use if certificatespost2020_Sweden = true.
	
	public static int buildouttargetyear = 2021; 					//Year for build out target (by this year, hence 2020, means 31.12.2020).
	public static double totalbuildouttarget = 28400000;//28400000; //46400000		//Total targeted buildout by the system measured in normal year annual production (MWh).
	
	
	//MARKET COORDINATION STRENGTH - Factor determining how aggressiv the build out is. Each developer recives a number between min and max that is used to estimate the future shortfall/overinvestment.
	public static double maxbuildoutaggressivness = 1.03; 			//As it is the final value that really limits the build out the spread must to large. 1 = no overinvestment.
	public static double minbuildoutaggressivness = 0.95; 			//Remember that this is multiplied with the total future demand, hence 1.01 implies 67 MW of new capacity added in 2020 (and about half that in 2026).

	//CONCESSION PROSESS
	public static int maxyearsinconcessionqueue = 3;				//Number of years in addition to minimum number of years in concession queue given as input from excel. After this, if not having received concession, the project is trashed.
	public static double annualprobforreceivingconcession = 0.4;	//Only with one decimal as the random generater uses int.
	public static int expectedyersinconcession = 4;					//When deciding for concession, how long to the developers expected the project to be in line. Needed due to learningcurve/CAPEX estimation.

	//PROJECT MARKET
	public static double[] chanceofownershipchange = new double[]{0.2,0.2,0.3,0.4};		//Indicates the chance (%) of ownership change according to years postponed by curren owner when setting criteria flag. Thus, of a project have been postpone (not invested when it could) one year, there is a [0] chance for owernship change. For the second year, there is [1] chance, etc. Notice that one year is 2015 right after the decisions are made/not made..
	public static double initialowenershipchangepercentile = 0.2;						//As the cert price needed has not been calculated this value indicates which project in line determines the marginal project. If 100 projects, a value of 0.2 would indicate that the 20th best project is the cutoff. For all project better than this, there is a chanceofownershipchange[0] chance for redistirbution.

	//PENSION FUND DEVELOPERS 
	public static int numberofpensionfunds = 1;							//They are all investment strategy type 3. (price, with some respekct for fundamntal) and in both countries.
	public static int holdinghorizontpensionproducer = 60;
	public static double pensionfundInvestRRRAdjustFactor = 0.82;		//wACC = origonal prject specific times this, hence 0.5*8% = 4 %
	public static int pfconstructionconstraints = 4;
	public static int pfregioncode = 2; //1 = Norway, 2 = Both, 3 = Sweden.
	
	//CONTROL STATION MARKET ADJUSTMENTS
	public static boolean certificatedemandinqouta = true; //True indicating that certficatea is written as qouta in the law (2015). If true, then the demand is adjusted following the control station. IF false, its adjusted every year.
	public static int[] controlstationtick = new int[]{60, 84, 108, 132, 156, 180, 204, 228, 252, 276}; //timing of controll station. If above is true, this tick indicates sertificate bank corrections. If false, not in use. 
	
	//CHANGE IN HOLDINGHORIZON
	public static boolean changeinholdinghorzint = true; //If false, this is not modelled
	public static double changeshare1 = 0.5; //Probability that buyers and sellers from the lowest hh whom changes hh, in the first hhchange process.
	public static double changeshare2 = 0.9; //Probability that buyers and sellers from the lowest hh whom changes hh, in the second hhchange process.
	public static int newhh1 = 12;			//Ticks added to the hh of the shortest market participants  [0]
	public static int newhh2 = 30;			//Ticks added to the hh of the second shortest market participants [1]
	public static int tickforchangeinhh1 = 73; //75 = april 2018 first pricess start tick
	public static int tickforchangeinhh2 = 80; //75 = april 2018 second pricess start tick

	
	//Step 2: Ends
	//================================================================================================================================================================================================
	//Step 3: Starts
	
	//Number of tactics in each of the respective strategies. More tactics gives the agent more alternatives.
	public static int numberoftacticsBuyStrategy1 = 1;
	//public static int numberoftacticsSellStrategy1 = 1;
	public static int numberoftacticsTraderStrategy1 = 8;
	//public static double OPAgentmustbuypremium = 0.5;
	//public static double PAgentmustselldiscount = 0.5;
	//public static double PAgentmustsellshare = 0.5;			 //RandomHelper.nextDoubleFromTo(0.4,0.6); //0.5;
	//public static double OPAgentmustsellshare = 0.75; 		 //RandomHelper.nextDoubleFromTo(0.4,0.6); //0.5;
	
	//Trader specific parameters if trader agents are present.
	public static boolean cantradershortsellflag = false;	//If false, traders can only sell if they have a positiv physical position. 
	//If not at limit and allowed to sell, this is what the selloffer volumes for the traders are
	public static double tradermustsellvol = 10;
	public static double traderrestsellvol = 100000;
	//As above but for buy
	public static double tradermustbuyvol = 10;
	public static double traderrestbuyvol = 100000;
	public static double tradermaximumshortpos = -0; 	
	public static double tradermaximumlongpos = 400000;	
	//Per 2014 var total beholdning tradere ca 2.5 millioner Så gitt 5 tradere så burde ikke denne være mer en 500 000.s
	
	// ---- BIDDING STRATEGY AND UTILITY PARAMETERS 
	public static int numofhistutilitiesincluded = 3; //Used by method that deterines the tactics best utility.
	// Utilities for agents
	public static int utilityDefault_PA = 2; // default utility for a purchaser agent
	public static int utilityDefault_OP = 2; // default utility for a obligated agent
	public static int utilityDefault_TR = 2; // default utility for a trader agent
		
	// ST price prognosis - exponential smoothing parameters
	public static double maxAlphaSTPrice = 0.8;
	public static double minAlphaSTPrice = 0.5;
	
	// Tactics - number of ticks to exit positions
	// Number of exit strategies for each agent type
	public static int numPAExitStrategies = numTicksPAExit.length;
	public static int numOPExitStrategies = numTicksOPExit.length;
	public static int numTExitStrategies = numTicksTExit.length;
	
	// ---- PRODUCER STRATEGIES AND TACTIC PARAMETERS
	// Note - not all of these need be used in any given tactic & strategy
//	public static double multOfferVol_PASellStrategy1 = 2; // default max fraction of that month's physical position than can be sold in the month
	public static int tacticDefaultLearn_PASellStrategy1 = 0; // Default learning algorithm for producer tactics (0 = none)
	// Sell share max and min levels (min used for must sell, max used for rest volume)
	public static double tacticMinPhysPosSellShare_PASellStrategy1 = 0.1; // Minimum must sell % of physical position . Alot to say.This flattens and pushes it all down, price more dependent on curren balance dispite ratio and holding horizont.
	public static double tacticMaxPhysPosSellShare_PASellStrategy1 = 0.75; // Sell up to this fraction of the physical position in total. initially.
//	public static double tacticDefaultMustSellPriceMult_PASellStrategy1 = 0.5;  // Default must sell price multiplier for the producer tactics (if used)
	// Define the range of price multipliers for the target sales (must sell) and rest volumes 
	public static double tacticMinMustSellPriceMult_PASellStrategy1 = 0.7;
	public static double tacticMaxMustSellPriceMult_PASellStrategy1 = 1.3;
	public static int tacticNumMustSellSteps_PASellStrategy1 = 7;
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
	//Minimum price steps for all bids.
	public static double minimumpricstepineuro_up = 0.5;
	public static double minimumpricstepineuro_down = 0.5;
	
	// ---- OBLIGATED PURCHASER STRATEGIES AND TACTIC PARAMETERS
	public static int tacticDefaultLearn_OPBuyStrategy1 = 0; // Default learning algorithm for producer tactics (0 = none)
	// Define the max and min volumes to be purchased in any given period
	public static double tacticMinPhysPosBuyShare_OPBuyStrategy1 = 0.35; // Default must Buy share for the purchaser tactics (if used)
	public static double tacticMaxPhysPosBuyShare_OPBuyStrategy1 = 1; 	 //default max fraction of physical position that can be purchased in the month
	public static double tacticDefaultMustBuyPriceMult_OPBuyStrategy1 = 1.5;  // Default must Buy price multiplier for the producer tactics (if used)
	public static double tacticMinMustBuyPriceMult_OPBuyStrategy1 = 0.7;
	public static double tacticMaxMustBuyPriceMult_OPBuyStrategy1 = 1.3;
	public static int tacticNumMustBuySteps_OPBuyStrategy1 = 7;
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
	
	// ---- CERTIFICATE VALUE CALCULATION PARAMETERS
	public static double certMaxPrice = 200;
	public static double certMinPrice = 0;
	public static double[] ratioAdjFactor = new double[]{0.8,1,1.2}; // used to capture uncertainty in the ratio estimates
	public static double[] ratioAdjProb = new double[]{0.3,0.4,0.3}; // same
	public static double sdevCVSupply = 0.0; // Std dev of supply prognosis used in CV calcs in %
	public static double valueCertShortfall = 200; // value (cost) of not having enough certificates
	public static boolean flagDiscountCV = true;

	// ---- FUNDAMENTAL MARKET ANALYSIS AND DEVELOPER LRMC
	public static double penaltyratio = 1.5;						//What is the penalty compared to current market price?
	public static int yearstoendogprojects = 2;						//Just to save time in the FMA. KK 20151118 var 2 i Q2 (2 og 3 har lite å si i Base Case). 
	public static double maxpricecerts = 150;						//To not get an errror in the FMA.Also used to cap max buy price for rest volume.
	public static double maxroofprice = 125;
	public static double initialRRRcorrector = 1.0;					//Corrector used to corrct the project specificRRR to usedRRR in the Fundamental Market Analysis. Copnsate (0.098) for the learningfactor in inputt.
	public static double RRRpostpondpremium = 0.025;				//Risk premium (basispoints 0.01 = 1%) need to be covered if the investment decision is to be postpond. 
	public static int minpostpondyears = 1;							//How long the investment decision is postpond if postponed.Cannot see why this should be larger than 1. (only argument is if this is the real deal).
	public static int MPECount = 17;								//Number of futuer years seen by the MPE-analysis. THats number-1 years ahead (including this year).
	public static int LPECount = MPECount+minpostpondyears;			//Number of futuer years seen by the LPE-analysis
	public static int yearsbuildout = 9;							//Number of years aggragate shortcoming that is assumbed build in one year in the FMA. KK20151118: Brukt 16 histoisk, men kan ikke forstå hvorfor det er rett! Med verdi på 1 så bygger man ut for neste års underskudd i hvert iterert år, det betyr generalt at man bygger senere og ergo må bygge mere og dermed dyrere.
	public static double[] RAR = new double[]{0.34,0.50};			//1 is maksimum. Higher number gives a wider range of roof and floor around the FMA. A bigger spread of numbers gives bigger variation between agents.
	public static double backtestminFMA = 40;						//20151130 KK: added for backtest qickfix of FMA at 2012.
	public static double stdmediumrunpriceexpect = 0.03;    		//The standard deviation (percent) in the Normaly distributed error for MPE (where mean is the perfect foresight price)
	public static double stdlongrunpriceexpect = 0.035;       		//The standard deviation (percent) in the Normaly distributed error for MPE (where mean is the perfect foresight price)
	public static double maximumfloorprice = 80;					//The maximum floor used by SellStrategies. Even though the discointed FMA should imply a floorprice higher than this, this would be limited her. In pracis, this number can be divided on 2 to get the actual floor.
	
	//WIND VARIATION
	public static double meanwindproductionfactor = getmeanwindproductionfactor();		//read from GUI
	public static double stdwindfactor = getstdwindproductionfactor();					//read from GUI
	public static double maxstdwindfactor = 2.4;					//Cutoff deviation in wind production factor. If 3 this means that it cannot blow less or more then 3 times the standard deviation.

	//CERT DEMAND VARIATION
	public static double meancertdemandfactor = 1;					//Systematically over/under estimation
	public static double stdcertdemandfactor = 0.031;				//Based on Optimeering uncertainty analysis.
	public static double maxstdcertdemandfactor = 1.8;				//Cut-off as more extreame outcomes than this would trigger other heating sources or less demand.
	
	//Generic Strategy 
	public static int MaxTacticPreferenceScore = 6; //6   Not sure how strongly this affects prices.
	public static int MinTacticPreferenceScore = 2; //2
	
	//Step 3: Ends
	//================================================================================================================================================================================================


}


