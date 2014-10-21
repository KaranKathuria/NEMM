/*
 * Version info:
 *     Class containing methods distributing PowerPlants and projects among the CompanyAgents having an ActiveAgent of type Producer Agent. Note that list of power plants lies at the CompanyAgent. 
 *     Last altered data: 20140829
 *     Made by: Karan Kathuria
 */
package nemmprocesses;
import java.util.ArrayList;

import nemmcommons.CommonMethods;
import nemmenvironment.PowerPlant;
import nemmenvironment.TheEnvironment;

public class DistributeProjectsandPowerPlants {
	
	public DistributeProjectsandPowerPlants() {}
	
	//Method distributing all operational powerplants. Thats all 
	public static void distributeallpowerplants() {
		int numberofPA = CommonMethods.getPAgentList().size();
		if (numberofPA <= 0){
			throw new IllegalArgumentException("Error: Zero CompanyAgents with production");}
		int i = 0;
		
		//Distributes all powerplants one by one two the agents, hence if the number of powerplants are increased, the order of the existing ones are the same!
		for (PowerPlant pp : TheEnvironment.allPowerPlants){
			pp.setMyCompany(CommonMethods.getPAgentList().get(i).getmycompany()); //Sets the powerplants company to the first company
			CommonMethods.getPAgentList().get(i).addpowerplant(pp); //Adds the power plant to this agents list. 
			i = i+1;
			if (i == (numberofPA) ){
				i = 0;}
			}
	}
	
	//Rather den distributin the powerplants in operation from the environmental list "allPowerPlants" the following methods distribuutes projects among developmentagents. 
	public static void distributeprojectsunderconstruction() {
	//TBD
	}
	
	public static void distributeprojectsawaitinginvestmentdecision() {
		
	}
	
	public static void distributeprojectinprocess() {

	}
	
	
}
