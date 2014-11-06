/*
 * Version info:
 *     Class containing methods distributing PowerPlants and projects among the CompanyAgents having an ActiveAgent of type Producer Agent. Note that list of power plants lies at the CompanyAgent. 
 *     Last altered data: 20140829
 *     Made by: Karan Kathuria
 */
package nemmprocesses;
import java.util.ArrayList;

import com.lowagie.text.pdf.hyphenation.TernaryTree.Iterator;

import repast.simphony.random.RandomHelper;
import nemmagents.CompanyAgent.ActiveAgent;
import nemmagents.CompanyAgent.DeveloperAgent;
import nemmcommons.CommonMethods;
import nemmenvironment.PowerPlant;
import nemmenvironment.TheEnvironment;

public class DistributeProjectsandPowerPlants {
	
	public DistributeProjectsandPowerPlants() {}
	
	//Method distributing all operational powerplants. The method takes in a distributionflag telling who the probabilitydistribution is (1=uniform, 2=None-uniform according to sizeflag, 3=custom none-uniform).
		public static void distributeallpowerplants(int distcode) {

		int numberofPA = CommonMethods.getPAgentList().size();
				if (numberofPA <= 0){
				throw new IllegalArgumentException("Error: Zero CompanyAgents with production");}
				
		int[] distribution = new int[3]; //Only works with two regions.
		
		//Assignes the probabilitydistribution used among the PAagents with various sizecodes.
		switch (distcode) {
			case 1: distribution[0] = 1; distribution[1] = 1; distribution[2] = 1;
			break;
			case 2: distribution[0] = 1; distribution[1] = 2; distribution[2] = 3;
			break;
			case 3: distribution[0] = 2; distribution[1] = 3; distribution[2] = 6;
			break;
			}
		
		ArrayList<ActiveAgent> probadjustedagentlistNorway = new ArrayList<ActiveAgent>();
		ArrayList<ActiveAgent> probadjustedagentlistSweden = new ArrayList<ActiveAgent>();
		
		//Fills the two arraylists with PA´s having apperance in that region and with copies according to probabilitydistribution;
		for (ActiveAgent PA : CommonMethods.getPAgentList())	{
			if (PA.getregionpartcode() > 3 || PA.getsizecode() > 3) {throw new IllegalArgumentException("Error: Regionrepcode or sizecode of PAgent not accepted");}
			if (PA.getregionpartcode() < 3) {				//Thats Norway and (Sweden and Norway)
				for (int i = 1; i <= distribution[PA.getsizecode()-1]; i++) {
					probadjustedagentlistNorway.add(PA);	//Adding the number of copies corresponding to the probability distirbution
					}
				}
			if (PA.getregionpartcode() > 1) {			//Thats not just Norway (or Sweden and Sweden and Norway if you like)
				for (int i = 1; i <= distribution[PA.getsizecode()-1]; i++) {
					probadjustedagentlistSweden.add(PA);
							}
				}
			}
		int randintervalNorway = probadjustedagentlistNorway.size() -1;   
		int randintervalSweden = probadjustedagentlistSweden.size() -1;
		
		if (randintervalNorway < 0 || randintervalSweden < 0)  {throw new IllegalArgumentException("Error: No producers in either Norway or Sweden. How strange.");}
		
		//Asignes the powerplants to agents according to the region and the respective arraylist. Notice the random uniform number used.
		for (PowerPlant PP : TheEnvironment.allPowerPlants)	{
			if (PP.getMyRegion() == TheEnvironment.allRegions.get(0)) {					 //Norway
				int assign = RandomHelper.nextIntFromTo(0,randintervalNorway);
				PP.setMyCompany(probadjustedagentlistNorway.get(assign).getmycompany()); //Sets the powerplants to a random company in the relevant arraylist of Norway.
				probadjustedagentlistNorway.get(assign).addpowerplant(PP);				 //The same PowerPlant is also added to that specific agents production list.
			}	
			else { 																		 //Sweden
				int assign = RandomHelper.nextIntFromTo(0,randintervalSweden);
				PP.setMyCompany(probadjustedagentlistSweden.get(assign).getmycompany()); //Sets the powerplants to a random company in the relevant arraylist of Sweden.
				probadjustedagentlistNorway.get(assign).addpowerplant(PP);				 //The same PowerPlant is also added to that specific agents production list.
			}
		}
			
		}
		
		//This methods distributes all projects among developmentagents. Alle projects regardless of stage are distributed.
		public static void distributeprojects(int distcode) {
		
			int numberofDA = CommonMethods.getDAgentList().size();
			if (numberofDA <= 0){
				//throw new Exception("Error: Zero CompanyAgents with developmentagents");		//Noe need for exception because this is actually okey.
				return;}
			
			else {
		
			int[] distribution = new int[3]; //Only works with two regions.

			//Assignes the probabilitydistribution used among the DAagents with various sizecodes.
			switch (distcode) {
			case 1: distribution[0] = 1; distribution[1] = 1; distribution[2] = 1;
			break;
			case 2: distribution[0] = 1; distribution[1] = 2; distribution[2] = 3;
			break;
			case 3: distribution[0] = 2; distribution[1] = 3; distribution[2] = 6;
			break;
			}

		ArrayList<DeveloperAgent> probadjustedagentlistNorway = new ArrayList<DeveloperAgent>();
		ArrayList<DeveloperAgent> probadjustedagentlistSweden = new ArrayList<DeveloperAgent>();

		//Fills the two arraylists with DA´s having apperance in that region.
		for (DeveloperAgent DA : CommonMethods.getDAgentList())	{
			if (DA.getregionpartcode() > 3 || DA.getsizecode() > 3) {throw new IllegalArgumentException("Error: Regionrepcode or sizecode of DAgent not accepted");}
			if (DA.getregionpartcode() < 3) {				//Thats Norway or Sweden and Norway
				for (int i = 1; i <= distribution[DA.getsizecode()-1]; i++) {
					probadjustedagentlistNorway.add(DA);	//Adding the number of copies corresponding to the probability distirbution
			}
		}
			if (DA.getregionpartcode() > 1) {			//Thats not just Norway (or Sweden and Sweden and Norway if you like)
				for (int i = 1; i <= distribution[DA.getsizecode()-1]; i++) {
					probadjustedagentlistSweden.add(DA);
					}
		}
	}

		int randintervalNorway = probadjustedagentlistNorway.size() -1;
		int randintervalSweden = probadjustedagentlistSweden.size() -1;
		
		//Creates one big iterable list of projects that are in a process-stage, that all stages not including in operationg and potential. 
		ArrayList<PowerPlant> temp = new ArrayList<PowerPlant>();
		temp.addAll(TheEnvironment.projectsidentifyed); temp.addAll(TheEnvironment.projectinprocess); 
		temp.addAll(TheEnvironment.projectsawaitinginvestmentdecision);temp.addAll(TheEnvironment.projectsunderconstruction);
		//int breakpointtest = temp.size();
		
		//Assign the powerplants to agents according to the region and the respective arraylist. Notice the random uniform number used.
		for (PowerPlant PP : temp)	{
			if (PP.getMyRegion() == TheEnvironment.allRegions.get(0)) {					 //Norway
				int assign = RandomHelper.nextIntFromTo(0,randintervalNorway);
				PP.setMyCompany(probadjustedagentlistNorway.get(assign).getmycompany()); //Sets the project to a random company in the relevant arraylist of Norway.
				probadjustedagentlistNorway.get(assign).addproject(PP);   				 //Adds the same project to the CompanyAgents list myProjects.
			}
			else { 																		//Sweden
				int assign = RandomHelper.nextIntFromTo(0,randintervalSweden);
				PP.setMyCompany(probadjustedagentlistSweden.get(assign).getmycompany()); //Sets the project to a random company in the relevant arraylist of Sweden.
				probadjustedagentlistNorway.get(assign).addproject(PP);   				 //Adds the same project to the CompanyAgents list myProjects.

			}
		}
	
		}
		}
	

}
	
	

