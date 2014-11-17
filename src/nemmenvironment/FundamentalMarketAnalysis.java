/*
 * Version info:
 *     A class creating the perfect foresight version of alle the LRMC curves for the years ahead. 
 *     
 *     Last altered data: 20141010
 *     Made by: Karan Kathuria
 */
package nemmenvironment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import repast.simphony.random.RandomHelper;
import nemmcommons.AllVariables;

public class FundamentalMarketAnalysis {
	
	//List containing all the fundamental price for the years ahead.
	private static double MPE;
	private static double LPE;
	private static ArrayList<Double> equilibriumpricesyearsahead = new ArrayList<Double>();
	private static double certificatebalance;				//Certbalance at end of given year.
	//Random.createNormal(.5, .3);
	//In order to preform a fundamental market analysis (with perfect foresight) for all years ahead. A copy of all projects are needed.
	
	public static ArrayList<PowerPlant> allPowerPlants_copy = new ArrayList<PowerPlant>();
	public static ArrayList<PowerPlant> projectsunderconstruction_copy = new ArrayList<PowerPlant>();				//PowerPlants currently under construction. Not needed as they are already decided.
	public static ArrayList<PowerPlant> projectsawaitinginvestmentdecision_copy = new ArrayList<PowerPlant>();		//PowerPlants projects awaiting investment decision
	public static ArrayList<PowerPlant> projectinprocess_copy = new ArrayList<PowerPlant>();						//All powerplant in process
	public static ArrayList<PowerPlant> projectsidentifyed_copy = new ArrayList<PowerPlant>();						//All projects identifyed
	public static ArrayList<PowerPlant> potentialprojects_copy = new ArrayList<PowerPlant>();						//Auto-generated potential projects
	public static ArrayList<PowerPlant> allendogenousprojects = new ArrayList<PowerPlant>();	
	
	private static ArrayList<PowerPlant> tempendogenousprojects = new ArrayList<PowerPlant>();
	
	//For storing the result from historic fundamental market analysis
	public class HistoricFundamentalMarketAnalysis {
		private ArrayList<Double> equilibriumpricesyearsahead = new ArrayList<Double>();
		private int yearran;
		}
	
	FundamentalMarketAnalysis() {};	
	
	public static void runfundamentalmarketanalysis() {
	//Initialization	
		//The ArrayLists must be clear as the Collection.copy method would risk keeping some of the original values in the List. 
		allPowerPlants_copy.clear();
		projectsunderconstruction_copy.clear();
		projectsawaitinginvestmentdecision_copy.clear();
		projectinprocess_copy.clear();
		projectsidentifyed_copy.clear();
		potentialprojects_copy.clear();
		allendogenousprojects.clear();
		equilibriumpricesyearsahead.clear();
		
		//Then create the deep copies based on the current year environment.
		for (PowerPlant pp: TheEnvironment.allPowerPlants){
			allPowerPlants_copy.add(pp.clone());}
		
		for (PowerPlant pp: TheEnvironment.projectsunderconstruction){
			projectsunderconstruction_copy.add(pp.clone());}
		
		for (PowerPlant pp: TheEnvironment.potentialprojects){
			potentialprojects_copy.add(pp.clone());}
		
		for (PowerPlant pp: TheEnvironment.projectsawaitinginvestmentdecision){
			projectsawaitinginvestmentdecision_copy.add(pp.clone());}
		
		for (PowerPlant pp: TheEnvironment.projectinprocess){
			projectinprocess_copy.add(pp.clone());}
		
		for (PowerPlant pp: TheEnvironment.projectsidentifyed){
			projectsidentifyed_copy.add(pp.clone());}

		allendogenousprojects.addAll(projectsawaitinginvestmentdecision_copy);
		allendogenousprojects.addAll(projectinprocess_copy);
		allendogenousprojects.addAll(projectsidentifyed_copy);
		allendogenousprojects.addAll(potentialprojects_copy);
		
		int currenttick = TheEnvironment.theCalendar.getCurrentTick();
		int currentyear = TheEnvironment.theCalendar.getTimeBlock(currenttick).year + TheEnvironment.theCalendar.getStartYear();
		int numberofyears = 2035 - currentyear + 1;
		int numberofticksinyear = TheEnvironment.theCalendar.getNumTradePdsInYear();		
		certificatebalance = TheEnvironment.GlobalValues.totalmarketphysicalposition; 						
		
		//Calculation for all future LRMC curves begins. Loops through all future years.
		for (int i = 0; i < numberofyears; ++i ) {
			tempendogenousprojects.clear();												//Important to clear so that the same endog project is not buildt twice.
			double totalannudemand = 0;
			double totalannucertproduction = 0;
		
		//For the years before the endogenous projects can be build. In order to  have correct certificatebalane at year = current year + 3.
		if (i < AllVariables.yearstoendogprojects) {
			
		//First get annual demand this year. 
		for (Region R : TheEnvironment.allRegions) {																	
			for (int j = 0; j < numberofticksinyear; j++) { 																	
			totalannudemand = totalannudemand + R.getMyDemand().getCertDemand(currenttick+j+(numberofticksinyear*i)); } 	//j runs from 0-11. Currentick is the starttick. i is the iterated year.
		}
		//Note that this does not take account for projects beeing realised and NOT reciving certs. KK 17.11.2014
		//Adding to allPowerPlants from the plants in process that will be finished. Notice that this is done BEFORE the sum annual production to include the ones finished this year.
		for (PowerPlant PP : projectsunderconstruction_copy) {
			if (PP.getstartyear() == currentyear+i) {									 //Currentyear + i is the iterated year. Hence if they start this year --> Move.
			PP.setendyear(Math.min(PP.getlifetime()+currentyear+i-1, currentyear+i+14)); //Setting endyear in order to not count the certificates after 15 years. And take care of projects in overgangsperioden with lifetime = 1. Does not take care of Norway after 2020. this is a weakness, but arguably no projects will be realized in Norway after 2020 anyways, and this stage is not setting the Investment Deceison but only conting certs correctl. Hence no it sort of supports both situations (certs and nocerts post2020 in Norway).
			//PP.setstatus(1);															 Not needed to remove projects from the projectsunderconstruction_copy as only those with startyear are added. Hence no chance of doublecounting.
			allPowerPlants_copy.add(PP);
			}
		}
		
		//Get total production from plants i operation, for iterated year. Notie the use of getestimannualprod() and not the exact or expected production. 
																			
		for (PowerPlant PP : allPowerPlants_copy) { 																		//All operational PP.
			if (PP.getstartyear() == (currentyear+i) && PP.getstartyear() != TheEnvironment.theCalendar.getStartYear()) {	//Special rule if the plant startet "this" iteration-year. But not for start year as projects in operation at start should be counted at start and not by *0.5
				totalannucertproduction = totalannucertproduction + (PP.getestimannualprod() * 0.5);}						//This is sexy! On average the projects finished this year is estimated to start in june.
			else {
				if (PP.getendyear() >= (currentyear+i) ) {																	//If started earlier, only count certs for eligable years (recall that Norway post2020 might be mistakenly counted if here, but, they would noe be invested inn) 
			totalannucertproduction = totalannucertproduction + PP.getestimannualprod();} }									//Starting at year i. Later method returns the calculated normal year production.
			
			if (PP.getendyear()+1 == (currentyear+i) && PP.getstartyear() != TheEnvironment.theCalendar.getStartYear()) {	//Extreamly sexy. For counting the years at end, we need to add the 50 % that where cut in the start year. 
			totalannucertproduction = totalannucertproduction + (PP.getestimannualprod() * 0.5);} 
			}	
		
		
		//Calculate the certificatebalance before new investments are made. 
		certificatebalance = certificatebalance + totalannucertproduction - totalannudemand;
		//Adds the equilibrium price based on this.
		double temp = 0.0;
			if (certificatebalance >= 0){
				temp = 0.0;}																					//Or should this be currentprice. 
			else {
				temp = TheEnvironment.GlobalValues.currentmarketprice * 1.5;}									//The 1.5 should be set from AllVariables. 
			
			equilibriumpricesyearsahead.add(temp);
}		
		
else  {
		//First add up demand and production already given for this year.
		for (Region R : TheEnvironment.allRegions) {																	//All regions
			for (int j = 0; j < numberofticksinyear; j++) { 															//All ticks in a year			
			totalannudemand = totalannudemand + R.getMyDemand().getCertDemand(currenttick+(numberofticksinyear*i)+j);}} //TheEnvironment.theCalendar.getCurrentTick()+1;
		
		//Including this exougenous part in the endougenous loop because the input data defines projects other then Wind to be exougneous, and "overgangsordningen" as one year projects.
		for (PowerPlant PP : projectsunderconstruction_copy) {
			if (PP.getstartyear() == currentyear+i) {															//Currentyear + i is the iterated year. By definition projects are set in operation 1.1 for the start year.
			PP.setendyear(Math.min(PP.getlifetime()+currentyear+i-1, currentyear+i+14));	 					//Setting endyear in order to not count the certificates after 15 years. And take care of projects in overgangsperioden with lifetime = 1.
			//PP.setstatus(1);															 						Not needed. No need to remove the realized projects from the projectsunderconstruction_copy as only those with startyear are added. Hence no chance of doublecounting.
			allPowerPlants_copy.add(PP);
			}
		}
		
		//Get total production from plants i operation, for iterated year.
		for (PowerPlant PP : allPowerPlants_copy) { 															//All operational PP. Notice that this is the original "allPowerPlants".
			if (PP.getstartyear() == (currentyear+i) ) {														//Special rule if the plant startet "this" iteration-year. 
				totalannucertproduction = totalannucertproduction + (PP.getestimannualprod() * 0.5);}			//This is sexy! On average the projects finished this year is estimated to start in june.
			else {
				if (PP.getendyear() >= (currentyear+i) ) {														//Only count certificates when the plant is eligable. 
					totalannucertproduction = totalannucertproduction + PP.getestimannualprod();} 				//Starting at year i. Later method returns the calculated normal year production.
			}
			if (PP.getendyear()+1 == (currentyear+i) && PP.getstartyear() != TheEnvironment.theCalendar.getStartYear()) {	//Extreamly sexy. For counting the years at end, we need to add the 50 % that where cut in the start year. 
				totalannucertproduction = totalannucertproduction + (PP.getestimannualprod() * 0.5);} 		
		}
		certificatebalance = certificatebalance - totalannudemand + totalannucertproduction;					//Give the current balance and hence how many new project needs to be realized.
		
		//Then: if, and only if, there is a shortcoming. 
		if (certificatebalance < 0) {											
			
			for (PowerPlant PP : allendogenousprojects) {						//All endogenous projects. Pooling together all projects in another stage than under construction.
				if ((PP.getearlieststartyear()+1) <= (currentyear+i)) {			//If they can earliest be finished in time for this year. Added +1 as its highly unlikely that all are finished in "best case" time.
			tempendogenousprojects.add(PP);	}}									//Add all relevant endogenous projects to this list.
		
		//What if there is a shortcumming and there are no certificate plants available? Needs to be handle
			if (tempendogenousprojects.size() < 1) {throw new Error("There is no projects that can be finished in order to meet demand");}
			
			LRMCCurve yearcurve = new LRMCCurve(currentyear, currentyear+i);		//Calculates the certpriceneeded for all objects in the list and calculates the equilibrium price. 
			yearcurve.calculatelrmccurve(tempendogenousprojects, certificatebalance); 
			int test2 = tempendogenousprojects.size();
			for (PowerPlant PP : tempendogenousprojects) {
				if (PP.getcertpriceneeded() <= yearcurve.getequilibriumprice()) {
					allendogenousprojects.remove(PP); 									
					PP.setendyear(currentyear+i+14);									//Just setting the endyear is sufficient for calculating the future certproduction of this plant. 
					allPowerPlants_copy.add(PP);										//Adds the same powerplant to list of powerplants in production. 
																						//Not needed to remove from tempendogenousprojects as this is cleard each iteration.
					}
				}
			equilibriumpricesyearsahead.add(yearcurve.getequilibriumprice());			//Stores just the equilibrium price from the LRMC curve. Could be an idea to store the object itself, or the curvepair. 
			}
		else { //Positive certificate balance.
			equilibriumpricesyearsahead.add(0.0);}	//In case there are no shortcommings, the price is set to 0.
		
	}
//Ended iteration over endouengous years for all i`s. Store the LRMC curves? 
}
	//End of iteration-year iteration.
	//THe final operation of FMA: Setting the MPE and LPE. 
	ArrayList<Double> test = new ArrayList<Double>();
	test = equilibriumpricesyearsahead;													//For testing purposes.
	
	int MPEcount = Math.min(AllVariables.MPECount, numberofyears);  					//MPE "sees" only XX years ahead
	int LPEcount = Math.min(AllVariables.LPECount, numberofyears); 						//Startingpoint year for Long term price			//LPE "sees" all the future

	MPE = 0;
	LPE = 0;
	
	double temp=0;
	for (int k = 1; k < MPEcount; k++) {
		if(equilibriumpricesyearsahead.get(k) > MPE){
		temp = equilibriumpricesyearsahead.get(k);}
		MPE = temp;
	}
	//Select the Long term fundamental price
	double temp2=0;
	for (int k = 1; k < LPEcount; k++) {
		if(equilibriumpricesyearsahead.get(k) > LPE){
		temp2 = equilibriumpricesyearsahead.get(k);}
		LPE = temp2;
	}
	double m = MPE;
	double l = LPE;
	int a = 2;
	}
	
	
	public static double getMPE() {
		return MPE;
	}
	public static double getLPE() {
		return LPE;
	}

	
	//Method calculationg the expected supply/demand balance of certificates at current tick and at the given future tick (defined as current + the number of future ticks given).
	public static double[] getcertbalanceratio(int thetick) {
		
		ArrayList<PowerPlant> allPowerPlants_kopi = new ArrayList<PowerPlant>();
		ArrayList<PowerPlant> projectsunderconstruction_kopi = new ArrayList<PowerPlant>();	
		
		for (PowerPlant pp: TheEnvironment.allPowerPlants){
			allPowerPlants_kopi.add(pp.clone());}
		for (PowerPlant pp: TheEnvironment.projectsunderconstruction){
			projectsunderconstruction_kopi.add(pp.clone());}
		

		
		//First get all future demand from current tick and futuretick. (Use perfect foresight).
		int currentick = TheEnvironment.theCalendar.getCurrentTick();							//Startingpoint Demand 1
		int thetick_tickID = currentick + thetick;												//Startingpoint Demand 2 in tickID
		int totalticks = TheEnvironment.theCalendar.getNumTicks();								//Total number of ticks in simulation
		int currentyear = TheEnvironment.theCalendar.getCurrentYear();
		int yearsleft = TheEnvironment.theCalendar.getNumYears() - (currentyear - TheEnvironment.theCalendar.getStartYear());
		int ticksinayear = TheEnvironment.theCalendar.getNumTradePdsInYear();
		
		double currentfuturedemand = 0;															//All future demand from now
		double thetickfuturedemand = 0;															//All future demand from thetick.
		double currentfuturesupply = 0;
		double thetickfuturesupply = 0;
		
		//Calculating all future demand for both tickIDs (current and future)
		for (int i = currentick; i < totalticks; i++)	{										//For all tick from now and to the end
		for (Region R : TheEnvironment.allRegions) {																	
				currentfuturedemand = currentfuturedemand + R.getMyDemand().getExpectedCertDemand(i);
				if (i >= thetick_tickID) {														//Counting future demand from "thetick" in the same loop.
					thetickfuturedemand = thetickfuturedemand + R.getMyDemand().getExpectedCertDemand(i);
					} 	
				}
		}
		
		//Add the projects that are finished this year to the lokal kopi of powerplants in operation, with a random start and end-tick.
		for (int i=0; i < yearsleft;i++) {
		int currentyearstarttickID = ((currentyear - TheEnvironment.theCalendar.getStartYear())*ticksinayear);
		for (PowerPlant PP : projectsunderconstruction_kopi) {
			if (PP.getstartyear() == currentyear+i) {									 //Currentyear + i is the iterated year. Hence if they start this year --> Move.
			int temp = (currentyearstarttickID+(i*TheEnvironment.theCalendar.getNumTradePdsInYear())) + RandomHelper.nextIntFromTo(0, TheEnvironment.theCalendar.getNumTradePdsInYear()-1);
			PP.setendyear(Math.min(PP.getlifetime()+currentyear+i-1, currentyear+i+14)); //Setting endyear in order to not count the certificates after 15 years. And take care of projects in overgangsperioden with lifetime = 1. Does not take care of Norway after 2020. this is a weakness, but arguably no projects will be realized in Norway after 2020 anyways, and this stage is not setting the Investment Deceison but only conting certs correctl. Hence no it sort of supports both situations (certs and nocerts post2020 in Norway).
			PP.setStarttick(temp);	//Randoml set starttick between now and 12 tick ahead.
			PP.setendtick(temp+(TheEnvironment.theCalendar.getNumTradePdsInYear()*Math.min(PP.getlifetime(), 15)));															// Not needed to remove projects from the projectsunderconstruction_copy as only those with startyear are added. Hence no chance of doublecounting.
			allPowerPlants_kopi.add(PP);
			}
		}
		}

		//Calculating supply from existing powerplants and (added existing powerplants). The only difference is that thetickfuturesupply is less than currentfuturesupply as plants can be out of operation
		for (PowerPlant PP : allPowerPlants_kopi) {
		for (int i = currentick; i < totalticks; i++) {
			if((PP.getStartTick() <= i) && (PP.getendtick() >= i)) {
				currentfuturesupply = currentfuturesupply + PP.getExpectedProduction(i);
				if (i >= thetick_tickID) {
					thetickfuturesupply = thetickfuturesupply + PP.getExpectedProduction(i);
				}
			}
		}	
		}
		
		double[] ret = new double[2];
		
		//Just for now
		double currentsupplyratio = (currentfuturesupply/currentfuturedemand);
		double futuresupplyratio = (thetickfuturesupply/thetickfuturedemand);
		ret[0] = currentsupplyratio;
		ret[1] = futuresupplyratio;
		
		return ret;
	}
	
	
		
}
		
	
