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
import nemmcommons.AllVariables;
import nemmcommons.CommonMethods;
import nemmenvironment.PowerPlant;
import nemmenvironment.TheEnvironment;

public class ProjectDevelopment {
	//Variables
	
	//Defualt NULL constructor
	public ProjectDevelopment() {};
	
	//Method finishind projects that are under construction (alter from status=2 to status=1) and has a start year smaller or equal to current year. 
	//FOr projects finishing the year, it adds the project to correct lists, set as starttick and updates status.
	public static void finalizeprojects() {
		
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
						PP.setStarttick(currenttick + RandomHelper.nextIntFromTo(0, TheEnvironment.theCalendar.getNumTradePdsInYear()-1));	//Randoml set starttick between now and 12 tick ahead.
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
	
	
	//Method moving projects that fulfill investment criterieas from status=3 to status=2. Some projects are just unaltered.
	//Method iterating through all projects awaiting investment decision and deciding which to invest investment decison. SHould be ran after process are finished, and after construction are finished.
	public static void startconstrucion() {
		//Should: Set start year. Should select the best projects according to certprice needed, limit and maxcap developed.
		//for (PowerPlant PP : TheEnvironment.projectsawaitinginvestmentdecision) {
		
		int currenttick = TheEnvironment.theCalendar.getCurrentTick();
		int currentyear = TheEnvironment.theCalendar.getTimeBlock(currenttick).year + TheEnvironment.theCalendar.getStartYear();	//Gets the current year.
		
		for (DeveloperAgent DA : CommonMethods.getDAgentList()) {
			int temp_projectsunderconstruct = 0;
			ArrayList<PowerPlant> templist = new ArrayList<PowerPlant>();
			
			double usedRRR = DA.getmycompany().getInvestmentRRR();					//Company specific RRR.
			
			//Collecting the projects that are awaiting investmetn decision (status = 3) and counting projects currently under construction.
			for (PowerPlant PP : DA.getmyprojects()) {
				if (PP.getstatus() == 3) {														//3=Awaiting investment decision.
					PP.addyearsincurrentstatus(1);												//Increasing number of years with this status with one.
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
			
			//The critical investment decision.
			while ((constructionproject_counter < maxnumberofconstrucprojects) && (capacitydeveloped_counter < maxcapacitydeveloped) &&  (projects_pointer <= potentialprojects )) {
				if (templist.get(projects_pointer).getcertpriceneeded() <= cutoffcertprice) {	//Starting with the best, if its worth investing...startconstruction.
					
					PowerPlant thisplant = templist.get(projects_pointer);
					capacitydeveloped_counter = capacitydeveloped_counter + thisplant.getCapacity();
					constructionproject_counter = constructionproject_counter + 1;
					
					thisplant.setstatus(2);														//Changing status for the project (from 3=awaitingid to 2=underconstruction).
					thisplant.setyearsincurrentstatus(0);  										//Setting this for consistency for project reaching new stag. This value is note used in later stages.
					thisplant.setstartyear(currentyear + thisplant.getminconstructionyears());	//Adding a startdate. Notice that this is done here rather than in the finalizeprojects.
					TheEnvironment.projectinprocess.add(thisplant);								//Add to the Environment list of projects in process.
					TheEnvironment.projectsawaitinginvestmentdecision.remove(thisplant);		//Removing from Environment list of awaitinginvestmentsdecisions
					
					//No need for updating the developer number of projects as this is done in another method after this.
					projects_pointer++;
					}
				else {break;}																	//If the current project is not wort investing, the following are not either.
			}			
					
		}
}
	
	//Method updating projects to receive concession from status 4=in process to 3=awaiting investment decision. 
	//Currently randomized such that project can receive concession after a given number of years until a max. After that they are trash if they have not got concession.
	public static void receivingconcession() {
		
		int currenttick = TheEnvironment.theCalendar.getCurrentTick();
		int currentyear = TheEnvironment.theCalendar.getTimeBlock(currenttick).year + TheEnvironment.theCalendar.getStartYear();	//Get current year.
		
		for (PowerPlant PP : TheEnvironment.projectinprocess) {
			PP.addyearsincurrentstatus(1);													//Increase number of years by one for all projects.
			
			//If trashable
			if (PP.getyearsincurrentstatus() >= AllVariables.maxyearsinconcessionqueue) {	//Have not received concession in so many years, it will nevere get it.
				
				PP.setstatus(0);															//The project is trashed, hence status is 0.
				PP.setyearsincurrentstatus(0); 												//Updating status means clearing years with this status.
				TheEnvironment.trashedprojects.add(PP);										//Add to all operations powerplants
				//PP.getMyCompany().getmyprojects().remove(PP);								//NOT Remving these from company`s list of project! Keeping these eases the controll and counting.
				
			    //To work around the problem of concurring operations, the removement of the project from the Underconstruction list must be done in another operation below.
				for (int i = 0; i < TheEnvironment.projectinprocess.size(); i++) {
					if (TheEnvironment.projectinprocess.get(i).getstatus() == 0) {
					TheEnvironment.projectsunderconstruction.remove(i);}}
			}
			
			//If in the lottery for getting concession.
			if ((PP.getyearsincurrentstatus() >= PP.getminyearinprocess()) && (PP.getyearsincurrentstatus() < AllVariables.maxyearsinconcessionqueue)) {
				int rand = RandomHelper.nextIntFromTo(1, 10);
				if (rand <= AllVariables.annualpropforreceivingconcession*10)	{ 			//Tricksy way of having a given chance for receiving concession. If concession:
					
					PP.setstatus(3);														//The project is trashed, hence status is 0.
					PP.setyearsincurrentstatus(0); 											//Updating status means clearing years with this status.
					TheEnvironment.projectsawaitinginvestmentdecision.add(PP);				//Add to all operations powerplants
					
					//To work around the problem of concurring operations, the removement of the project from the Underconstruction list must be done in another operation below.
					for (int i = 0; i < TheEnvironment.projectinprocess.size(); i++) {
						if (TheEnvironment.projectinprocess.get(i).getstatus() == 0) {
						TheEnvironment.projectsunderconstruction.remove(i);}}
				}
				//No else
			}
			//No else
		}
					
	
	}
			
	//An important issue to be decided is whether the status vairbale should define the project stage, the list its contained in, or both.What is the master. 
		//All methods updates the lists and status. They also initially check for lists and status.
	
	//Method moving project from potential to identifyed, that means distribution of "made up" projects.
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
	//Method moving projects from identifyed to apply for concession. THis is in part equal to the method of start construcion, but with an premimum RRR and a different CAPEX due to learning.
	//HENCE the currentyer for the LERMC should be current + expected concession period +1 or +2.
	public static void applyforconcession() {
		
	//TOBEFICED!!!
		int currenttick = TheEnvironment.theCalendar.getCurrentTick();
		int currentyear = TheEnvironment.theCalendar.getTimeBlock(currenttick).year + TheEnvironment.theCalendar.getStartYear();	//Gets the current year.
		
		for (DeveloperAgent DA : CommonMethods.getDAgentList()) {
			int temp_projectsunderconstruct = 0;
			ArrayList<PowerPlant> templist = new ArrayList<PowerPlant>();
			
			double usedRRR = DA.getmycompany().getInvestmentRRR();					//Company specific RRR.
			
			//Collecting the projects that are awaiting investmetn decision (status = 3) and counting projects currently under construction.
			for (PowerPlant PP : DA.getmyprojects()) {
				if (PP.getstatus() == 3) {														//3=Awaiting investment decision.
					PP.addyearsincurrentstatus(1);												//Increasing number of years with this status with one.
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
			
			//The critical investment decision.
			while ((constructionproject_counter < maxnumberofconstrucprojects) && (capacitydeveloped_counter < maxcapacitydeveloped) &&  (projects_pointer <= potentialprojects )) {
				if (templist.get(projects_pointer).getcertpriceneeded() <= cutoffcertprice) {	//Starting with the best, if its worth investing...startconstruction.
					
					PowerPlant thisplant = templist.get(projects_pointer);
					capacitydeveloped_counter = capacitydeveloped_counter + thisplant.getCapacity();
					constructionproject_counter = constructionproject_counter + 1;
					
					thisplant.setstatus(2);														//Changing status for the project (from 3=awaitingid to 2=underconstruction).
					thisplant.setyearsincurrentstatus(0);  										//Setting this for consistency for project reaching new stag. This value is note used in later stages.
					thisplant.setstartyear(currentyear + thisplant.getminconstructionyears());	//Adding a startdate. Notice that this is done here rather than in the finalizeprojects.
					TheEnvironment.projectinprocess.add(thisplant);								//Add to the Environment list of projects in process.
					TheEnvironment.projectsawaitinginvestmentdecision.remove(thisplant);		//Removing from Environment list of awaitinginvestmentsdecisions
					
					//No need for updating the developer number of projects as this is done in another method after this.
					projects_pointer++;
					}
				else {break;}																	//If the current project is not wort investing, the following are not either.
	}
		}}
	
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
