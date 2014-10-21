/*
 * Version info:
 *     Public and static class containing the annual operation done to simulate the investmentprocess. All stages of the new project process should have separate methods.
 *     
 *     Last altered data: 20141020
 *     Made by: Karan Kathuria
 */

package nemmprocesses;

import java.util.ArrayList;

import nemmenvironment.PowerPlant;

public class ProjectDevelopment {
	//Variables
	
	//Defualt NULL constructor
	public ProjectDevelopment() {};
	
	
	//An important issue to be decided is whether the status vairbale should define the project stage, the list its contained in, or both.What is the master. 
	//For efficiency and cleanness it seems nice to have separate lists. At least for the LMCA. --> Best forslag: Lets keep status as the master, but also have lists for practial pruposes.
	//Wheather to udate all lists at the same time at de end based on status, or to move them from lists can be decided.
	
	//These methods are moving projects from the different stages (or not). Hence the names corresponds to the stages between differen project statuses.
	
	public static void projectidentification() {
		//TBD: Methods taking all projects not assign to a DA (Development Agent), assigning it, calculating LRMC and Certpriceneeded for that DAs RRR.
		
		
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
