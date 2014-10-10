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
	private double annualproductionnewplants;																		//Annual production from plants not with status=1 at iteration start.
	
	//In order to preform a fundamental market analysis (with perfect foresight) for all years ahead. A copy of all projects are needed.
	//public static ArrayList<PowerPlant> projectsunderconstruction_copy = new ArrayList<PowerPlant>();				//PowerPlants currently under construction. Not needed as they are already decided.
	public static ArrayList<PowerPlant> projectsawaitinginvestmentdecision_copy = new ArrayList<PowerPlant>();		//PowerPlants projects awaiting investment decision
	public static ArrayList<PowerPlant> projectinprocess_copy = new ArrayList<PowerPlant>();						//All powerplant in process
	public static ArrayList<PowerPlant> projectsidentifyed_copy = new ArrayList<PowerPlant>();						//All projects identifyed
	public static ArrayList<PowerPlant> potentialprojects_copy = new ArrayList<PowerPlant>();						//Auto-generated potential projects
	
	FundamentalMarketAnalysis() {};													
	
	
	public void runfundamentalmarketanalysis() {
		//The ArrayLists must be clear as the Collection.copy method would risk keeping some of the original values in the List. 
		projectsawaitinginvestmentdecision_copy.clear();
		projectinprocess_copy.clear();
		projectsidentifyed_copy.clear();
		potentialprojects_copy.clear();
		
		//Then create the copies based on the current year. 
		Collections.copy(projectsawaitinginvestmentdecision_copy, TheEnvironment.projectsawaitinginvestmentdecision);
		Collections.copy(projectinprocess_copy, TheEnvironment.projectinprocess);
		Collections.copy(projectsidentifyed_copy, TheEnvironment.projectsidentifyed);
		Collections.copy(potentialprojects_copy, TheEnvironment.potentialprojects);
		
		//Calcuations begin
		int currenttick = TheEnvironment.theCalendar.getCurrentTick();
		int currentyear = 2012 /*currentyear */;
		int numberofyears = 2035 - currentyear + 1;
		int numberofticksinyear = TheEnvironment.theCalendar.getNumTradePdsInYear();
		
	
		certificatebalance = TheEnvironment.GlobalValues.totalmarketphysicalposition; 							//Based on balance from previous year.
		annualproductionnewplants = 0;																			//Based simply on LF and Capacity
		
		for (int i = 0; i < numberofyears; ++i ) {
		
		//For the years before the endogenous projects can be build. Only nesissary due to correct certificatebalane at year = current year + 3.
			if (i < 3) {
		//Get total demand for given year. 
		double totalannudemand = 0;
		for (Region R : TheEnvironment.allRegions) {															//All regions
			for (int j = 0; j < numberofticksinyear; j++) { 													//All ticks in a year			
			totalannudemand = totalannudemand + R.getMyDemand().getCertDemand(currenttick+(numberofticksinyear*i)+j); } //TheEnvironment.theCalendar.getCurrentTick()+1;
		}
		//Get total production from plants i operation at start, for this year.
			double totalannucertproduction = 0;																	
		for (PowerPlant PP : TheEnvironment.allPowerPlants) { 													//All operational PP
			for (int j = 0; j < numberofticksinyear; j++) { 													//All ticks in a year
				totalannucertproduction = totalannucertproduction + PP.getProduction(currenttick+(numberofticksinyear*i)+j);} //Starting at year i. 
			}
		
		//Get and updates the annual production from the plants in process. Notie that LF * Capacity (hence normal year) is used. The same should might be used for existing plants.
		for (PowerPlant PP : TheEnvironment.projectinprocess) {
			if (PP.getstartyear() == currentyear+i) {
				annualproductionnewplants = annualproductionnewplants + (PP.getLoadfactor() * PP.getCapacity());
			}
		}
		//Calculate the certificatebalance before new investments are made. 
		certificatebalance = certificatebalance + totalannucertproduction - totalannudemand + annualproductionnewplants;
			}
		//No the years when LRMCCurve is calculated. 
			else {
		
		
			
		}
		//So we have this comming years demand and production
		
		
				}
	}
		
	}
		
	
