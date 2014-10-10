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
		int numberofyears = 2035 - /*currentyear */ + 1;
		int numberofticksinyear = TheEnvironment.theCalendar.getNumTradePdsInYear();
		
	for (int i = 0; i < numberofyears; ++i ) {
		
		
		
		
		
		
		//for each year. Calculate total demand. Take total bank + total production from existing plants and production from plants 
		double totaldemand = 0;
		for (Region R : TheEnvironment.allRegions) {															//All regions
			for (int j = 0; j < numberofticksinyear; j++) { 													//All ticks in a year			
			totaldemand = totaldemand + R.getMyDemand().getCertDemand(currenttick+(numberofticksinyear*i)+j); } //TheEnvironment.theCalendar.getCurrentTick()+1;
		}
			double totalbankandcertproduction = TheEnvironment.GlobalValues.totalmarketphysicalposition; 			//This provides the totalmarket minus or pluss at end of previous tick.
		for (PowerPlant PP : TheEnvironment.allPowerPlants) { 													//All operational PP
			for (int j = 0; j < numberofticksinyear; j++) { 													//All ticks in a year
			totalbankandcertproduction = totalbankandcertproduction + PP.getProduction(currenttick+(numberofticksinyear*i)+j);} //Starting at year i. 
			}
		
		//So we have this comming years demand and production
		
		
				}
	}
		
	}
		
	
	
}
