/*
 * Version info:
 *     Public and static class containing the annual operation done to simulate the investmentprocess. All stages of the new project process should have separate methods.
 *     
 *     Last altered data: 20141020
 *     Made by: Karan Kathuria
 */

package nemmprocesses;

import java.util.ArrayList;
import java.util.Collections;

import repast.simphony.random.RandomHelper;
import nemmagents.CompanyAgent.DeveloperAgent;
import nemmcommons.CommonMethods;
import nemmenvironment.PowerPlant;
import nemmenvironment.TheEnvironment;

public class ProjectDevelopment {
	//Variables
	
	//Defualt NULL constructor
	public ProjectDevelopment() {};
	
	//Method finishind projects that are under construction and has a start year smaller or equal to current year. 
	//FOr projects finishing the year, it adds the project to corrects lists, set as starttick and updates status.
	
	public static void finalizeprojects() {
		//Should: Change status, distribute onwnership, set end-date
		
		int currenttick = TheEnvironment.theCalendar.getCurrentTick();
		int currentyear = TheEnvironment.theCalendar.getTimeBlock(currenttick).year + TheEnvironment.theCalendar.getStartYear();	//Get current year.
		
		//Updating information for projects finished this year.
		for (PowerPlant PP : TheEnvironment.projectsunderconstruction) {
			if (PP.getstartyear() <= currentyear) {
				PP.setstatus(1);
					if (!PP.getMyRegion().getcertificatespost2020flag() && currentyear > 2020) {	//Takes care of projecs post 2020 in regions without certs post 2020.
						PP.setendyear(2020);}
					else {
						PP.setendyear(Math.min(PP.getlifetime()+currentyear-1, currentyear+14));
						PP.setStarttick(currenttick + RandomHelper.nextIntFromTo(0, TheEnvironment.theCalendar.getNumTradePdsInYear()-1));	//Randoml set starttick.
					}
				
				TheEnvironment.allPowerPlants.add(PP);			//Add to all operations powerplants
				PP.getMyCompany().getmypowerplants().add(PP);	//Add to company`s list of powerplants
				//PP.getMyCompany().getmyprojects().remove(PP);	//NOT Remving these from company`s list of project! Keeping these eases the controll and counting.
			}
		}
	    //To work around the problem of concurring operations, the removement of the project from the Underconstruction list must be done in another operation below.
		for (int i = 0; i < TheEnvironment.projectsunderconstruction.size(); i++) {
			if (TheEnvironment.projectsunderconstruction.get(i).getstatus() == 1) {
			TheEnvironment.projectsunderconstruction.remove(i);}}
	}
	
	//Method iterating through all projects awaiting investment decision and deciding which to invest investment decison. SHould be ran after process are finished, and after construction are finished.
	public static void startconstrucion() {
		//Should: Set start year. Should select the best projects according to certprice needed, limit and 
		//for (PowerPlant PP : TheEnvironment.projectsawaitinginvestmentdecision) {
		
		int currenttick = TheEnvironment.theCalendar.getCurrentTick();
		int currentyear = TheEnvironment.theCalendar.getTimeBlock(currenttick).year + TheEnvironment.theCalendar.getStartYear();	//Gets the current year.
		
		for (DeveloperAgent DA : CommonMethods.getDAgentList()) {
			int temp_projectsunderconstruct = 0;
			ArrayList<PowerPlant> templist = new ArrayList<PowerPlant>();
			
			double usedRRR = DA.getmycompany().getInvestmentRRR();					//Company specific RRR.
			
			//Collecting the projects that are awaiting investmetn decision (status = 3) and counting projects currently under construction.
			for (PowerPlant PP : DA.getmyprojects()) {
				if (PP.getstatus() == 3) {
					templist.add(PP);															//Adds all the projects, regardsless of having a cert price needed to high or low.
					PP.calculateLRMCandcertpriceneeded(currentyear, usedRRR, 3);				//Using the market forward power price in that given reigon.
				}

			}
			
			//For each DeveloperAgent For all the relevant projects. Do the following:			
			Collections.sort(templist, new CommonMethods.customprojectcomparator());			//Sorting the of a DAs project awaiting from lowest certprie needed to highest cert price needed
			
			//All the cirteria variables for the investment decision
			double cutoffcertprice = DA.getmycompany().getcompanyanalysisagent().getmarketanalysisagent().getmarketprognosis().getlongrunpriceexpectatations(); //Should be discussed.
			int maxnumberofconstrucprojects = DA.getconstructionlimit();
			int constructionproject_counter = DA.getnumprojectsunderconstr();					//Newly updated values.
			double maxcapacitydeveloped		= DA.gettotalcapacitylimit();
			double capacitydeveloped_counter = DA.getcapacitydevorundrconstr();
			int potentialprojects = templist.size();
			int projects_pointer = 0;															//To ensure that the loop is not longer than number of objects.
			
			while ((constructionproject_counter < maxnumberofconstrucprojects) && (capacitydeveloped_counter < maxcapacitydeveloped) &&  (projects_pointer <= potentialprojects )) {
				if (templist.get(projects_pointer).getcertpriceneeded() <= cutoffcertprice) {	//Starting with the best, if its worth investing...startconstruction.
					
					capacitydeveloped_counter = capacitydeveloped_counter + templist.get(projects_pointer).getCapacity();
					constructionproject_counter = constructionproject_counter + 1;
					
					templist.get(projects_pointer).setstatus(2);												//Changing status for the project it is refering to.
					TheEnvironment.projectinprocess.add(templist.get(projects_pointer));						//Add to the Environment list of projects in process.
					TheEnvironment.projectsawaitinginvestmentdecision.remove(templist.get(projects_pointer));	//Removing form Environment list in 
											//More.
					projects_pointer++;
					}
				else {break;}																	//If the current project is not wort investing, the following are not either.
				
				
			}
				//IF good enough and available construction resources --> put in construction.
				
					
		}
}
	
	//Method updating projects to receive concession.
	public static void receivingconcession() {
		
		
	}
			
	//An important issue to be decided is whether the status vairbale should define the project stage, the list its contained in, or both.What is the master. 
	//For efficiency and cleanness it seems nice to have separate lists. At least for the LMCA. --> Best forslag: Lets keep status as the master, but also have lists for practial pruposes.
	//Wheather to udate all lists at the same time at de end based on status, or to move them from lists can be decided.
	
	//These methods are moving projects from the different stages (or not). Hence the names corresponds to the stages between differen project statuses.
	
	public static void projectidentification() {
		//TBD: Methods taking all projects not assign to a DA (Development Agent), assigning it, calculating LRMC and Certpriceneeded for that DAs early stageRRR.
		//Distribution new potential projects among the DAs companies. Notice that there is a maximum level of projects that can be identifyed due to limited resources. 
		//Should also only be distributed projects for their region.
		
		/*
		For each developer/for all projects in the potentialproject-list.
			Input: The distribution of the number of new projects sourced per year by the developer, and the Norway/Sweden split. Projects in "potential projects".
			This may be a function of the number of projects the developer has in the new build process and/or in operation
			
			Calculate: Randomly select x Norway projects and y Sweden projects from the Potential Projects collection and allocate these to the developer. 
			The random selection follows the input distribution. 
			
			Calculate: Add these projects to the developer’s Identified Projects collection and change the statuses to 
			
			Projects into identifyed projects.

		 */
		
	}
	
	public static void applyforconcession() {
		//This method takes project that are indetifyed an set the status for 
	}
	
	//Simply updateing all endogenous variables for the DAagents.
	public static void updateDAgentsnumber() {
		
		double capacitydevorundrconstr=0;
		int numprojectsfinished=0;
		int numprojectsunderconstr =0;
		int numprojectsawaitingid=0;
		int numprojectsinprocess=0;
		int numprojectsidentyfied=0;
		
		for (DeveloperAgent DA : CommonMethods.getDAgentList()) {
			for (PowerPlant PP : DA.getmyprojects()) {
				if (PP.getstatus() == 1) {
					numprojectsfinished = numprojectsfinished +1;
					capacitydevorundrconstr = capacitydevorundrconstr + PP.getCapacity();}
				if (PP.getstatus() == 2) {
					numprojectsunderconstr = numprojectsunderconstr +1;
					capacitydevorundrconstr = capacitydevorundrconstr + PP.getCapacity();}
				if (PP.getstatus() == 3) {
					numprojectsawaitingid = numprojectsawaitingid +1;}
				if (PP.getstatus() == 4) {
					numprojectsinprocess = numprojectsinprocess +1;}
				if (PP.getstatus() == 5) {
					numprojectsidentyfied = numprojectsidentyfied +1;}
			}
			DA.updateDAnumbers(capacitydevorundrconstr, numprojectsfinished, numprojectsunderconstr, numprojectsawaitingid, numprojectsinprocess, numprojectsidentyfied);
		}
	}
		

}

/*
 * 	public static ArrayList<PowerPlant> projectsunderconstruction;			//PowerPlants currently under construction
	public static ArrayList<PowerPlant> projectsawaitinginvestmentdecision;	//PowerPlants projects awaiting investment decision
	public static ArrayList<PowerPlant> projectinprocess;					//All powerplant in process of getting concession.
	public static ArrayList<PowerPlant> projectsidentifyed;					//All projects identifyed
	public static ArrayList<PowerPlant> potentialprojects;					//Auto-generated potential projects. Note distributed among development agents. 
		public static ArrayList<PowerPlant> allPowerPlants; 

	*/
