/*
 * Version info:
 *     Public and static class containing the annual operation done to simulate the investmentprocess. All stages of the new project process should have separate methods.
 *     
 *     Last altered data: 20141020
 *     Made by: Karan Kathuria
 */

package nemmprocesses;

import java.util.ArrayList;

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
				PP.getMyCompany().getmyprojects().remove(PP);	//Remove from company`s list of project
			}
		}
	    //To work around the problem of concurring operations, the removement of the project from the Underconstruction list must be done in another operation below.
		for (int i = 0; i < TheEnvironment.projectsunderconstruction.size(); i++) {
			if (TheEnvironment.projectsunderconstruction.get(i).getstatus() == 1) {
			TheEnvironment.projectsunderconstruction.remove(i);}}
	}
	
	//Method iterating through all projects awaiting investment decision and deciding which to invest investment decison.
	public static void startconstrucion() {
		//Should: Set start year. Should select the best projects according to certprice needed, limit and 
		//for (PowerPlant PP : TheEnvironment.projectsawaitinginvestmentdecision) {
		for (DeveloperAgent DA : CommonMethods.getDAgentList()) {
			int temp_projectsunderconstruct = 0;
			ArrayList<PowerPlant> templist = new ArrayList<PowerPlant>();
			
			//Collecting the projects that are awaiting investmetn decision (status = 3) and counting projects currently under construction.
			for (PowerPlant PP : DA.getmyprojects()) {
				if (PP.getstatus() == 3) {
					templist.add(PP);}
				if (PP.getstatus() == 2) {
					temp_projectsunderconstruct = temp_projectsunderconstruct +1;
				}
			}
			
			//For all the relevant projects. Do the following:
			for (PowerPlant PP : templist) {
				
					
				}
			}
			
		
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

}

/*
 * 	public static ArrayList<PowerPlant> projectsunderconstruction;			//PowerPlants currently under construction
	public static ArrayList<PowerPlant> projectsawaitinginvestmentdecision;	//PowerPlants projects awaiting investment decision
	public static ArrayList<PowerPlant> projectinprocess;					//All powerplant in process of getting concession.
	public static ArrayList<PowerPlant> projectsidentifyed;					//All projects identifyed
	public static ArrayList<PowerPlant> potentialprojects;					//Auto-generated potential projects. Note distributed among development agents. 
		public static ArrayList<PowerPlant> allPowerPlants; 

	*/
