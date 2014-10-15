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
	
	static ArrayList<PowerPlant> tempendogenousprojects = new ArrayList<PowerPlant>();
	
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
		
		//Then create the copies based on the current year. Only the lists of projects which are endougneous are kept. 
		Collections.copy(allPowerPlants_copy, TheEnvironment.allPowerPlants);
		Collections.copy(projectsunderconstruction_copy, TheEnvironment.projectsunderconstruction);
		Collections.copy(projectsawaitinginvestmentdecision_copy, TheEnvironment.projectsawaitinginvestmentdecision);	//Can be build in 3 years earliest
		Collections.copy(projectinprocess_copy, TheEnvironment.projectinprocess);										//4 years earliest
		Collections.copy(projectsidentifyed_copy, TheEnvironment.projectsidentifyed);									
		Collections.copy(potentialprojects_copy, TheEnvironment.potentialprojects);
		allendogenousprojects.addAll(projectsawaitinginvestmentdecision_copy);
		allendogenousprojects.addAll(projectinprocess_copy);
		allendogenousprojects.addAll(projectsidentifyed_copy);
		allendogenousprojects.addAll(potentialprojects_copy);
		
		//Calcuations begin
		int currenttick = TheEnvironment.theCalendar.getCurrentTick();
		int currentyear = TheEnvironment.theCalendar.getTimeBlock(currenttick).year;							//Gets the current year.
		int numberofyears = 2035 - currentyear + 1;
		int numberofticksinyear = TheEnvironment.theCalendar.getNumTradePdsInYear();
		
		certificatebalance = TheEnvironment.GlobalValues.totalmarketphysicalposition; 							//Based on balance from previous year.
				
		for (int i = 0; i < numberofyears; ++i ) {
			tempendogenousprojects.clear();
		
		//For the years before the endogenous projects can be build. In order to  have correct certificatebalane at year = current year + 3.
			if (i < 3) {
				
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
			projectsunderconstruction_copy.remove(PP);
			allPowerPlants_copy.add(PP);
			}
		}
		
		//Get total production from plants i operation, for iterated year. Notie the use of getestimannualprod() and not the exact or expected production. 
		double totalannucertproduction = 0;																	
		for (PowerPlant PP : allPowerPlants_copy) { 																//All operational PP. Notice that this is the original "allPowerPlants".
			if (PP.getendyear() >= (currentyear+i) ) {																	//Only count certificates when the plant is eligable. 
			totalannucertproduction = totalannucertproduction + PP.getestimannualprod();} 								//Starting at year i. Later method returns the calculated normal year production.
			}
		
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

		if (certificatebalance < 0) {												//Just to save time. 
			
		for (PowerPlant PP : allendogenousprojects) {							//All endogenous projects
			if (PP.getearlieststartyear() == currentyear+i) {						//If they can earliest be finished in time for this year.
			tempendogenousprojects.add(PP);	}}										//Add all relevant endogenous projects to this list.
			
		LRMCCurve yearcurve = new LRMCCurve(currentyear, currentyear+i);			//Calculates the certpriceneeded for all objects in the list and calculates the equilibrium price. 
		yearcurve.calculatelrmccurve(tempendogenousprojects, certificatebalance); 
		
		for (PowerPlant PP : tempendogenousprojects) {
			if (PP.getcertpriceneeded() < yearcurve.getequilibriumprice()) {
				allendogenousprojects.remove(PP); 									//NOT COMPLETLE SURE THIS WOULD WORK; BUT I THINK SO! ASK Gavin
				PP.setendyear(currentyear+i+14);									//Just setting the endyear is sufficient for calculating the future certproduction of this plant. 
				allPowerPlants_copy.add(PP);										//Adds the same powerplant to list of powerplants in production. 
			}}
		equilibriumpricesyearsahead.add(yearcurve.getequilibriumprice());			//Stores just the equilibrium price from the LRMC curve. Could be an idea to store the object itself, or the curvepair. 
			}
		else {
		equilibriumpricesyearsahead.add(0.0);	//In case there are no shortcommings, the price is set to 0.
		}	
		//End of iteration over endouengous years. Could be usefull to store the LRMCCurves. 
	}			
	//End of iteration-year iteration.	
}
	//THe final operation of FMA: Setting the MPE and LPE. 
	MPE = Math.max(equilibriumpricesyearsahead.get(3), equilibriumpricesyearsahead.get(4)); //Set it to the highest price of the 3. or 4. year ahead.
	LPE = Math.max(equilibriumpricesyearsahead.get(10), equilibriumpricesyearsahead.get(11)); //Set it to the highest price of the 3. or 4. year ahead.
	}
	
	public static double getMPE() {
		return MPE;
	}
	public static double getLPE() {
		return LPE;
	}
		
}
		
	
