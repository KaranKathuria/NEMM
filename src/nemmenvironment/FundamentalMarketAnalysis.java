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
	private ArrayList<Double> equilibriumpricesyearsahead = new ArrayList<Double>();
	private double certificatebalance;																				//Certbalance at end of given year.
	
	//In order to preform a fundamental market analysis (with perfect foresight) for all years ahead. A copy of all projects are needed.
	
	public ArrayList<PowerPlant> allPowerPlants_copy = new ArrayList<PowerPlant>();
	public ArrayList<PowerPlant> projectsunderconstruction_copy = new ArrayList<PowerPlant>();				//PowerPlants currently under construction. Not needed as they are already decided.
	public ArrayList<PowerPlant> projectsawaitinginvestmentdecision_copy = new ArrayList<PowerPlant>();		//PowerPlants projects awaiting investment decision
	public ArrayList<PowerPlant> projectinprocess_copy = new ArrayList<PowerPlant>();						//All powerplant in process
	public ArrayList<PowerPlant> projectsidentifyed_copy = new ArrayList<PowerPlant>();						//All projects identifyed
	public ArrayList<PowerPlant> potentialprojects_copy = new ArrayList<PowerPlant>();						//Auto-generated potential projects
	public ArrayList<PowerPlant> allendogenousprojects = new ArrayList<PowerPlant>();	
	
	ArrayList<PowerPlant> tempendogenousprojects = new ArrayList<PowerPlant>();
	
	//For storing the result from historic fundamental market analysis
	public class HistoricFundamentalMarketAnalysis {
		private ArrayList<Double> equilibriumpricesyearsahead = new ArrayList<Double>();
		private int yearran;
		}
	
	FundamentalMarketAnalysis() {};	
	
	public void runfundamentalmarketanalysis() {
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
		
		//Adding to allPowerPlants from the plants in process that will be finished.. Notie that LF * Capacity (hence normal year) is used and that this is done BEFORE the sum annual production to include the ones finished this year.
		for (PowerPlant PP : this.projectsunderconstruction_copy) {
			if (PP.getstartyear() == currentyear+i) {									 //Currentyear + i is the iterated year. By definition projects are set in operation 1.1 for the start year.
			PP.setendyear(currentyear+i+14);											 //Setting endyear in order to not count the certificates after 15 years.
			this.projectsunderconstruction_copy.remove(PP);
			this.allPowerPlants_copy.add(PP);
			}
		}
		
		//Get total production from plants i operation at start, for iterated year.
		double totalannucertproduction = 0;																	
		for (PowerPlant PP : this.allPowerPlants_copy) { 																//All operational PP. Notice that this is the original "allPowerPlants".
			if (PP.getendyear() >= (currentyear+i) ) {																	//Only count certificates when the plant is eligable. 
			totalannucertproduction = totalannucertproduction + (PP.getLoadfactor() * PP.getCapacity() * 8760);} 		//Starting at year i. 
			}
		
		//Calculate the certificatebalance before new investments are made. 
		certificatebalance = certificatebalance + totalannucertproduction - totalannudemand;
		//Do something with price?????? For instane if balance is negative, priecs are 1.5 * current price?!
			}
			
		//Now the years when LRMCCurve is calculated, as there are no endogenous projects in the next three years.
			else  {
				
		double totalannudemand = 0;																						//Demand this year (current + i (>3))
		for (Region R : TheEnvironment.allRegions) {																	//All regions
			for (int j = 0; j < numberofticksinyear; j++) { 															//All ticks in a year			
			totalannudemand = totalannudemand + R.getMyDemand().getCertDemand(currenttick+(numberofticksinyear*i)+j);}} //TheEnvironment.theCalendar.getCurrentTick()+1;
		
		certificatebalance = certificatebalance - totalannudemand;														//Give the current balance and hence how many new project needs to be realized.

							
		for (PowerPlant PP : this.allendogenousprojects) {							//All endogenous projects
			if (PP.getearlieststartyear() == currentyear+i) {						//If they can earliest be finished in time
			tempendogenousprojects.add(PP);	}										//Add all endogenous projects to this list.
				LRMCCurve yearcurve = new LRMCCurve(currentyear, currentyear+i);
				yearcurve.calculatelrmccurve(tempendogenousprojects, certificatebalance);
					
				}
				
				
			
			
		
		
			
		}
		//So we have this comming years demand and production
		
		
				}
	}
		
	}
		
	
