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

public class NewFundamentalMarketAnalysis {
	
	//List containing all the fundamental price for the years ahead.
	private static double MPE;
	private static double LPE;
	private static ArrayList<Double> equilibriumpricesyearsahead = new ArrayList<Double>();
	private static double certificatebalance;				//Certbalance at end of given year.

	
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
		ArrayList<PowerPlant> tempremoval = new ArrayList<PowerPlant>();
		
		//Calculation for all future LRMC curves begins. i stands for iterated yer. for each i there is a aar from i to end, that to sum up future production from i.
		for (int i = 0; i < numberofyears; ++i ) {
			tempendogenousprojects.clear();												//Important to clear so that the same endog project is not buildt twice.
			double totalannufuturedemand = 0;											//All futuredemand from i and to the end
			double totalannufuturecertproduction = 0;									//All futuresupply form i and to the end
			double allfuturecertificatebalance = 0;										//All future balance, that is bank at i + allfuturesupply - allfuture demand
			double totalannudemand = 0;													//Demand at the year i. Needed to calculate the bank at i.
			double totalannucertproduction = 0;											//Supply at the year i. Needed to calculate the bank at i.
			tempremoval.clear();  														//Clear the temporeral removal list.
			int xyears = AllVariables.yearsbuildout;									//Variale for FMA that determines what years of future certificate balance to build
			int xyearsused = Math.min(xyears, numberofyears);							//Cannot take the shortcomings of years after 2035.			
			double xyearfuturecertdemand = 0;
			double xyearfuturecertproduction = 0;
			double xyearfuturecertbalance = 0;
						
	//Note that this does not take account for projects beeing realised and NOT reciving certs. KK 17.11.2014
		//Adding to allPowerPlants from the plants in process that will be finished.
		for (PowerPlant PP : projectsunderconstruction_copy) {
			if (PP.getstartyear() == currentyear+i) {									 //Currentyear + i is the iterated year. Hence if they start this year --> Move.
			PP.setendyear(Math.min(PP.getlifetime()+currentyear+i-1, currentyear+i+14)); //Setting endyear in order to not count the certificates after 15 years. And take care of projects in overgangsperioden with lifetime = 1. Does not take care of Norway after 2020. this is a weakness, but arguably no projects will be realized in Norway after 2020 anyways, and this stage is not setting the Investment Deceison but only conting certs correctl. Hence no it sort of supports both situations (certs and nocerts post2020 in Norway).
			tempremoval.add(PP);
			allPowerPlants_copy.add(PP);
			}}
			projectsunderconstruction_copy.removeAll(tempremoval);						//workaround to move the completed plants from the list.
		
			
		//First get annual demand this year and all the following years. And the current year. 
		for (int aar = i; aar < numberofyears; ++aar) {
		for (Region R : TheEnvironment.allRegions) {																	
			for (int j = 0; j < numberofticksinyear; j++) { 																	
				totalannufuturedemand = totalannufuturedemand + R.getMyDemand().getCertDemand(currenttick+j+(numberofticksinyear*aar));  	//j runs from 0-11. Currentick is the starttick. i is the iterated year.
				if (aar < (i+xyearsused)) {
				xyearfuturecertdemand = xyearfuturecertdemand + R.getMyDemand().getCertDemand(currenttick+j+(numberofticksinyear*aar));	//For the x futuer years
				}
				if (aar == i) {
				totalannudemand = totalannudemand + R.getMyDemand().getCertDemand(currenttick+j+(numberofticksinyear*aar));
				}
			}
		}}
		
		
	//Get total production from plants i operation from the iterated and years to come. Notice the use of getestimannualprod() and not the exact or expected production. 
		//For production this year
		for (PowerPlant PP : allPowerPlants_copy) { 																		//All operational PP.
			if (PP.getstartyear() == (currentyear+i) && PP.getstartyear() != TheEnvironment.theCalendar.getStartYear()) {	//Special rule if the plant startet "this" iteration-year. But not for start year as projects in operation at start should be counted at start and not by *0.5
				totalannucertproduction = totalannucertproduction + (PP.getestimannualprod() * 0.5);}						//This is sexy! On average the projects finished this year is estimated to start in june.
			else {
				if (PP.getendyear() >= (currentyear+i) ) {																	//If started earlier, only count certs for eligable years (recall that Norway post2020 might be mistakenly counted if here, but, they would noe be invested inn) 
			totalannucertproduction = totalannucertproduction + PP.getestimannualprod();} }									//Starting at year i. Later method returns the calculated normal year production.
			
			if (PP.getendyear()+1 == (currentyear+i) && PP.getstartyear() != TheEnvironment.theCalendar.getStartYear()) {	//Extreamly sexy. For counting the years at end, we need to add the 50 % that where cut in the start year. 
			totalannucertproduction = totalannucertproduction + (PP.getestimannualprod() * 0.5);} 
			}
		
		//For production in xfuture year
		for (int aar = i; aar < (i+xyearsused); ++aar) {															
			for (PowerPlant PP : allPowerPlants_copy) { 																		
				if (PP.getstartyear() == (currentyear+aar) && PP.getstartyear() != TheEnvironment.theCalendar.getStartYear()) {	
					xyearfuturecertproduction = xyearfuturecertproduction + (PP.getestimannualprod() * 0.5);}			
				else {
					if (PP.getendyear() >= (currentyear+aar) ) {																 
						xyearfuturecertproduction = xyearfuturecertproduction + PP.getestimannualprod();} }				
				
				if (PP.getendyear()+1 == (currentyear+aar) && PP.getstartyear() != TheEnvironment.theCalendar.getStartYear()) {	 
					xyearfuturecertproduction = xyearfuturecertproduction + (PP.getestimannualprod() * 0.5);} 
				}}
		
		//For production in all future years
		for (int aar = i; aar < numberofyears; ++aar) {															
		for (PowerPlant PP : allPowerPlants_copy) { 																		
			if (PP.getstartyear() == (currentyear+aar) && PP.getstartyear() != TheEnvironment.theCalendar.getStartYear()) {	
				totalannufuturecertproduction = totalannufuturecertproduction + (PP.getestimannualprod() * 0.5);}			
			else {
				if (PP.getendyear() >= (currentyear+aar) ) {																 
					totalannufuturecertproduction = totalannufuturecertproduction + PP.getestimannualprod();} }				
			
			if (PP.getendyear()+1 == (currentyear+aar) && PP.getstartyear() != TheEnvironment.theCalendar.getStartYear()) {	 
				totalannufuturecertproduction = totalannufuturecertproduction + (PP.getestimannualprod() * 0.5);} 
			}}	
		
		//At last, count the production from plants under construction not finished on year i.
		for (PowerPlant PP : projectsunderconstruction_copy) {
			if (PP.getstartyear() > 2020 && !PP.getMyRegion().getcertificatespost2020flag()) { //If post 2020 and in regions without certs, do nothing.
			}
			else {
				totalannufuturecertproduction = totalannufuturecertproduction + (PP.getestimannualprod()*Math.min(15, 2036-PP.getstartyear()));
				if (PP.getstartyear() < (currentyear+i+xyearsused)) {
					xyearfuturecertproduction = xyearfuturecertproduction + (PP.getestimannualprod()*((currentyear+i+xyearsused)-PP.getstartyear())) - (0.5*PP.getestimannualprod()); //To take account for the midyear start.
			}}
		}
		
		//Calculate the certificatebalance before new investments are made. 
		xyearfuturecertbalance = certificatebalance + xyearfuturecertproduction - xyearfuturecertdemand;
		allfuturecertificatebalance = certificatebalance + totalannufuturecertproduction - totalannufuturedemand;	//For all years from i and to end (including i).
		certificatebalance = certificatebalance + totalannucertproduction - totalannudemand;						//For the year i.

		
	if (i < AllVariables.yearstoendogprojects) {																	//Seperate rule for the end-projects.
			double temp = 0.0;
			if (certificatebalance >= 0){																	//Will there be shortfall in the future of the market? Which years shortfall?
				temp = 0.0;}																					
			else {
				temp = TheEnvironment.GlobalValues.currentmarketprice * AllVariables.penaltyratio;}		 			
			equilibriumpricesyearsahead.add(temp);
	}	
	else {

		//Following is the key different between the original and current FMA.  
		if (xyearfuturecertbalance < 0) {											//Should be the three year.That is which shortcoming should it build on?
				
				for (PowerPlant PP : allendogenousprojects) {						//All endogenous projects. Pooling together all projects in another stage than under construction.
					if ((PP.getearlieststartyear()+1) <= (currentyear+i)) {			//If they can earliest be finished in time for this year. Added +1 as its highly unlikely that all are finished in "best case" time.
				tempendogenousprojects.add(PP);	}}									//Add all relevant endogenous projects to this list.
			
			//What if there is a shortcoming and there are no certificate plants available? Needs to be handle
				if (tempendogenousprojects.size() < 1) {throw new Error("There is no projects that can be finished in order to meet demand");}
				
				LRMCCurve yearcurve = new LRMCCurve(currentyear, currentyear+i);		  //Calculates the certpriceneeded for all objects in the list and calculates the equilibrium price. 
				yearcurve.calculatelrmccurve(tempendogenousprojects, xyearfuturecertbalance, xyearsused); //Which certificate balance to fulfill. 
				int debugtest = tempendogenousprojects.size();
				for (PowerPlant PP : tempendogenousprojects) {
					if (PP.getcertpriceneeded() <= yearcurve.getequilibriumprice()) {
						allendogenousprojects.remove(PP); 									
						PP.setendyear(currentyear+i+14);									//Just setting the endyear is sufficient for calculating the future certproduction of this plant. 
						allPowerPlants_copy.add(PP);										//Adds the same powerplant to list of powerplants in production. 
						}
					}
				equilibriumpricesyearsahead.add(yearcurve.getequilibriumprice());			//Stores just the equilibrium price from the LRMC curve. Could be an idea to store the object itself, or the curvepair. 
				}
			else { //Positive certificate balance.
				equilibriumpricesyearsahead.add(0.0);}	//In case there are no shortcommings, the price is set to 0.	
		}
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
		
		
}
		
	
