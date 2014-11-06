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
		ArrayList<PowerPlant> tempremoval = new ArrayList<PowerPlant>();															//Templist for removing projects.
		//Updating information for projects finished this year.
		for (PowerPlant PP : TheEnvironment.projectsunderconstruction) {
			if (PP.getstartyear() == currentyear) {
				PP.setstatus(1);
					if (!PP.getMyRegion().getcertificatespost2020flag() && currentyear > 2020) {	//Takes care of projecs post 2020 in regions without certs post 2020.
						PP.setendyear(2020);
						PP.setStarttick(currenttick+1);
						PP.setendtick(currenttick);}												//Setting this back in time, hence these certs are never produced.
					else {
						PP.setendyear(Math.min(PP.getlifetime()+currentyear-1, currentyear+14));	//Takes care of projects "in overgangsordningen" with 1 year lifetime.
						int temp = currenttick + RandomHelper.nextIntFromTo(0, TheEnvironment.theCalendar.getNumTradePdsInYear()-1);
						PP.setStarttick(temp);	//Randoml set starttick between now and 12 tick ahead.
						PP.setendtick(temp+(TheEnvironment.theCalendar.getNumTradePdsInYear()*Math.min(PP.getlifetime(), 15)));
					}
				
				TheEnvironment.allPowerPlants.add(PP);			//Add to all operations powerplants
				PP.getMyCompany().getmypowerplants().add(PP);	//Add to company`s list of powerplants
				tempremoval.add(PP);
				//PP.getMyCompany().getmyprojects().remove(PP);	//NOT Remving these from company`s list of project! Keeping these eases the controll and counting.
			}
		}
	    //To work around the problem of concurring operations, the removement of the project from the Underconstruction list must be done in another operation below.
			TheEnvironment.projectsunderconstruction.removeAll(tempremoval);
	}
	
	
	//Method moving projects that fulfill investment criterieas from status=3 to status=2. Some projects are just unaltered.
	//Method iterating through all projects awaiting investment decision and deciding which to invest investment decison. SHould be ran after process are finished, and after construction are finished.
	public static void startconstrucion() {
		
		int currenttick = TheEnvironment.theCalendar.getCurrentTick();
		int currentyear = TheEnvironment.theCalendar.getTimeBlock(currenttick).year + TheEnvironment.theCalendar.getStartYear();	//Gets the current year.
		
		for (DeveloperAgent DA : CommonMethods.getDAgentList()) {
			
			ArrayList<PowerPlant> templist = new ArrayList<PowerPlant>();
			
			//Collecting the projects that are awaiting investmetn decision (status = 3) and counting projects currently under construction.
			for (PowerPlant PP : DA.getmyprojects()) {
			double usedRRR = DA.getmycompany().getInvestmentRRR()*PP.getspecificRRR();			//Company specific RRR multiplied with company`s investment RRR.

				if (PP.getstatus() == 3) {														//3=Awaiting investment decision.
					PP.addyearsincurrentstatus(1);												//Increasing number of years with this status with one.
					templist.add(PP);															//Adds all the projects, regardsless of having a cert price needed to high or low.
					PP.calculateLRMCandcertpriceneeded(currentyear, usedRRR, 1);				//Using the market forward power price in that given reigon. Notice that this is calculated for when the year the project can be invested in, not the year it can be finished!!
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
					TheEnvironment.projectsunderconstruction.add(thisplant);								//Add to the Environment list of projects in process.
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
		ArrayList<PowerPlant> tempremoval = new ArrayList<PowerPlant>();															//Templist for removing projects.
		
		for (PowerPlant PP : TheEnvironment.projectinprocess) {
			PP.addyearsincurrentstatus(1);														//Increase number of years by one for all projects.
			
			//If trashable due to no concession in 7 years.
			int maxyearsinconcessionqueue = PP.getminyearinprocess() + AllVariables.maxyearsinconcessionqueue;     //Max years depends on the project estimated minconcession years.
			if (PP.getyearsincurrentstatus() >= maxyearsinconcessionqueue) { //Have not received concession in so many years, it will nevere get it.
				
				PP.setstatus(0);																//The project is trashed, hence status is 0.
				PP.setyearsincurrentstatus(0); 													//Updating status means clearing years with this status.
				TheEnvironment.trashedprojects.add(PP);											//Add to all operations powerplants
				tempremoval.add(PP);															//Add to removal list.
				

			}
			
			//If in the lottery for getting concession.
			if ((PP.getyearsincurrentstatus() >= PP.getminyearinprocess()) && (PP.getyearsincurrentstatus() < maxyearsinconcessionqueue)) {
				int rand = RandomHelper.nextIntFromTo(1, 10);
				if (rand <= AllVariables.annualprobforreceivingconcession*10)	{ 				//Tricksy way of having a given chance for receiving concession. If concession:
					
					PP.setstatus(3);															//The project is granted concession, hence status = 3.
					PP.setyearsincurrentstatus(0); 												//Updating status means clearing years with this status.
					TheEnvironment.projectsawaitinginvestmentdecision.add(PP);					//Add to all operations powerplants
					tempremoval.add(PP);

				}
				//No else. Nothing is done if the yearswithcurrent status does not fulfill the above criteria.
			}
			//No else
		}
		TheEnvironment.projectinprocess.removeAll(tempremoval);
		
	}
	
	
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
	public static void startpreprojectandapplication() {
		
		int currenttick = TheEnvironment.theCalendar.getCurrentTick();
		int currentyear = TheEnvironment.theCalendar.getTimeBlock(currenttick).year + TheEnvironment.theCalendar.getStartYear(); //Get current year.
		
		for (DeveloperAgent DA : CommonMethods.getDAgentList()) {

			ArrayList<PowerPlant> templist = new ArrayList<PowerPlant>();
			
			//Collecting the projects that the DA have identrifyed
			for (PowerPlant PP : DA.getmyprojects()) {
				double usedRRR = DA.getmycompany().getearlystageRRR() * PP.getspecificRRR();									 //Company specific multipled with earlistage corrctor.
				if (PP.getstatus() == 5) {																					     //5=Identyfied project.
					PP.addyearsincurrentstatus(1);																				 //Increasing number of years with this status.
					templist.add(PP);															
					PP.calculateLRMCandcertpriceneeded((currentyear+AllVariables.expectedyersinconcession), usedRRR, 1);		 //3=Using the forward power price in the region of the project.
					//Notice the "currentyear+AllVariables.expectedyersinconcession". The LRMC takes account for the years of construcion deciding the eligability for certifiates. Whereas the input here
					//takes account for the years in concessions. Adding this up the DA then takes account for both the years in concess
				}
				//No else. Projects with other statuses should not be keept.
			}
	
			//For each DeveloperAgent For all the relevant projects. Do the following:			
			Collections.sort(templist, new CommonMethods.customprojectcomparator());			//Sorting the of a DAs project awaiting from lowest certprie needed to highest cert price needed
			
			//All the cirteria variables for the investment decision
			double cutoffcertprice = DA.getmycompany().getcompanyanalysisagent().getmarketanalysisagent().getmarketprognosis().getlongrunpriceexpectatations(); //Should be discussed.
			int maxnumberofprocess = DA.getprojectprocessandidylimit();
			int processprojects_counter = DA.getnumprojectsinprocess();							//Not newly updated required. Just that the Concession process is updated for this tick.
			int potentialprojects = templist.size();
			int projects_pointer = 0;															//To ensure that the loop is not longer than number of objects.
			
			//The critical investment decision.
			while ((processprojects_counter < maxnumberofprocess) &&  (projects_pointer <= potentialprojects )) {
				if (templist.get(projects_pointer).getcertpriceneeded() <= cutoffcertprice) {	//Starting with the best, if its worth investing...apply for concession.
					
					PowerPlant thisplant = templist.get(projects_pointer);
					processprojects_counter = processprojects_counter + 1;						//Temporary (local) counter for projects in concessionqueue for the Developer.
					
					thisplant.setstatus(4);														//Changing status for the project (from 5=identyfied to 4=preconstruction&process).
					thisplant.setyearsincurrentstatus(0);  										//Setting this for consistency for project reaching new stag. This value is note used in later stages.
					TheEnvironment.projectinprocess.add(thisplant);								//Add to the Environment list of projects in process.
					TheEnvironment.projectsidentifyed.remove(thisplant);						//Removing from Environment list of awaitinginvestmentsdecisions
					projects_pointer++;
					}
				else {break;}																	//If the current project is not wort investing, the following are not either.
			}
		}}
	
	//Method updateing all endogenous variables for the DAagents.
	public static void updateDAgentsnumber() {
		
		double capacitydevorundrconstr=0;
		int numprojectstrashed=0;
		int numprojectsfinished=0;
		int numprojectsunderconstr =0;
		int numprojectsawaitingid=0;
		int numprojectsinprocess=0;
		int numprojectsidentyfied=0;
		
		for (DeveloperAgent DA : CommonMethods.getDAgentList()) {
			for (PowerPlant PP : DA.getmyprojects()) {
				if (PP.getstatus() == 0) {
					numprojectstrashed = numprojectstrashed +1;}
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
			DA.updateDAnumbers(capacitydevorundrconstr, numprojectstrashed, numprojectsfinished, numprojectsunderconstr, numprojectsawaitingid, numprojectsinprocess, numprojectsidentyfied);
		}
	}
		

}

