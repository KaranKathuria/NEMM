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

import nemmcommons.AllVariables;

public class FundamentalMarketAnalysis {
	
	//List containing all the fundamental price for the years ahead.
	private static double MPE;
	private static double LPE;
	private static ArrayList<Double> equilibriumpricesyearsahead = new ArrayList<Double>();
	private static double certificatebalance;																		//Certbalance at end of given year.
	
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
		//The ArrayLists must be clear as the Collection.copy method would risk keeping some of the original values in the List. 
		allPowerPlants_copy.clear();
		projectsunderconstruction_copy.clear();
		projectsawaitinginvestmentdecision_copy.clear();
		projectinprocess_copy.clear();
		projectsidentifyed_copy.clear();
		potentialprojects_copy.clear();
		allendogenousprojects.clear();
		
		//Then create the deep copies based on the current year. Only the lists of projects which are endougneous are kept. 
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
		
		//Calcuations begin
		int currenttick = TheEnvironment.theCalendar.getCurrentTick();
		int currentyear = TheEnvironment.theCalendar.getTimeBlock(currenttick).year + TheEnvironment.theCalendar.getStartYear();		//Gets the current year.
		int numberofyears = 2035 - currentyear + 1;
		int numberofticksinyear = TheEnvironment.theCalendar.getNumTradePdsInYear();
		
		certificatebalance = TheEnvironment.GlobalValues.totalmarketphysicalposition; 							//Based on balance from previous year.
				
		for (int i = 0; i < numberofyears; ++i ) {
			tempendogenousprojects.clear();
		
		//For the years before the endogenous projects can be build. In order to  have correct certificatebalane at year = current year + 3.
if (i < AllVariables.yearstoendogprojects) {
		//Get total demand for the iterated year. 
		double totalannudemand = 0;
		for (Region R : TheEnvironment.allRegions) {																	//All regions
			for (int j = 0; j < numberofticksinyear; j++) { 															//All ticks in a year			
			totalannudemand = totalannudemand + R.getMyDemand().getCertDemand(currenttick+(numberofticksinyear*i)+j); } //TheEnvironment.theCalendar.getCurrentTick()+1;
		}
		
		//Adding to allPowerPlants from the plants in process that will be finished. Notice that this is done BEFORE the sum annual production to include the ones finished this year.
		for (PowerPlant PP : projectsunderconstruction_copy) {
			if (PP.getstartyear() == currentyear+i) {									 //Currentyear + i is the iterated year. By definition projects are set in operation 1.1 for the start year.
			PP.setendyear(currentyear+i+14);											 //Setting endyear in order to not count the certificates after 15 years.
			//PP.setstatus(1);															 Not needed. No need to remove the realized projects from the projectsunderconstruction_copy as only those with startyear are added. Hence no chance of doublecounting.
			allPowerPlants_copy.add(PP);
			}
		}
		
		//Get total production from plants i operation, for iterated year. Notie the use of getestimannualprod() and not the exact or expected production. 
		double totalannucertproduction = 0;																	
		for (PowerPlant PP : allPowerPlants_copy) { 																//All operational PP. Notice that this is the original "allPowerPlants".
			if (PP.getendyear() >= (currentyear+i) ) {																	//Only count certificates when the plant is eligable. 
			totalannucertproduction = totalannucertproduction + PP.getestimannualprod();} 								//Starting at year i. Later method returns the calculated normal year production.
			}
		
		int a = allPowerPlants_copy.size();
		//Calculate the certificatebalance before new investments are made. 
		certificatebalance = certificatebalance + totalannucertproduction - totalannudemand;
		//Adds the equilibrium price based on this.
		double temp = 0.0;
			if (certificatebalance >= 0){
				temp = 0.0;}																							//Or should this be currentprice. 
			else {
				temp = TheEnvironment.GlobalValues.currentmarketprice * 1.5;}											//The 1.5 should be set from AllVariables. 
			
			equilibriumpricesyearsahead.add(temp);
}		
		//Now the years when LRMCCurve is calculated, as there are no endogenous projects in the next three years.
else  {
				
		double totalannudemand = 0;																						//Demand this year (current + i (>3))
		for (Region R : TheEnvironment.allRegions) {																	//All regions
			for (int j = 0; j < numberofticksinyear; j++) { 															//All ticks in a year			
			totalannudemand = totalannudemand + R.getMyDemand().getCertDemand(currenttick+(numberofticksinyear*i)+j);}} //TheEnvironment.theCalendar.getCurrentTick()+1;
		
		//Get total production from plants i operation, for iterated year.
		double totalannucertproduction = 0;																	
		for (PowerPlant PP : allPowerPlants_copy) { 																//All operational PP. Notice that this is the original "allPowerPlants".
			if (PP.getendyear() >= (currentyear+i) ) {																	//Only count certificates when the plant is eligable. 
			totalannucertproduction = totalannucertproduction + PP.getestimannualprod();} 								//Starting at year i. Later method returns the calculated normal year production.
			}
		
		certificatebalance = certificatebalance - totalannudemand + totalannucertproduction;							//Give the current balance and hence how many new project needs to be realized.

		if (certificatebalance < 0) {											//Just to save time. No need to create LRMC of the balance is positive.
			
			for (PowerPlant PP : allendogenousprojects) {							//All endogenous projects
				if (PP.getearlieststartyear() <= currentyear+i) {					//If they can earliest be finished in time for this year.
			tempendogenousprojects.add(PP);	}}									//Add all relevant endogenous projects to this list.
		
		//What if there is a shortcumming and there are no certificate plants available? Needs to be handle
			if (tempendogenousprojects.size() < 1) {throw new Error("There is no projects that can be finished in order to meet demand");}
			
			LRMCCurve yearcurve = new LRMCCurve(currentyear, currentyear+i);		//Calculates the certpriceneeded for all objects in the list and calculates the equilibrium price. 
			yearcurve.calculatelrmccurve(tempendogenousprojects, certificatebalance); 
		
			for (PowerPlant PP : tempendogenousprojects) {
				if (PP.getcertpriceneeded() < yearcurve.getequilibriumprice()) {
					allendogenousprojects.remove(PP); 									//NOT COMPLETLE SURE THIS WOULD WORK; BUT I THINK SO! ASK Gavin
					PP.setendyear(currentyear+i+14);									//Just setting the endyear is sufficient for calculating the future certproduction of this plant. 
					allPowerPlants_copy.add(PP);										//Adds the same powerplant to list of powerplants in production. 
					//Remove fom 
					}}
			equilibriumpricesyearsahead.add(yearcurve.getequilibriumprice());			//Stores just the equilibrium price from the LRMC curve. Could be an idea to store the object itself, or the curvepair. 
			}
		else { //Positive certificate balance.
			equilibriumpricesyearsahead.add(0.0);}	//In case there are no shortcommings, the price is set to 0.
		
	}
//Ended iteration over endouengous years for all i`s. Store the LRMC curves? 
}
	//End of iteration-year iteration.
	//THe final operation of FMA: Setting the MPE and LPE. 
	int b = 2;
	
	int MPEcount = 3;
	while (equilibriumpricesyearsahead.get(MPEcount) < 0.1 && MPEcount < 10) {
		MPE = equilibriumpricesyearsahead.get(MPEcount);
		MPEcount++;
	}
	//Select the Long term fundamental price
	int LPEcount = 10;
	while (equilibriumpricesyearsahead.get(LPEcount) < 0.1 && LPEcount < numberofyears-1) {
		LPE = equilibriumpricesyearsahead.get(LPEcount);
		LPEcount++;	
	}

	}

	public static double getMPE() {
		return MPE;
	}
	public static double getLPE() {
		return LPE;
	}
		
}
		
	
