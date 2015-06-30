/*
 * Version info:
 *     File defining the simplifyed market for projects, where endougenous are divided around
 *     Last altered data: 20150622
 *     Made by: Karan Kathuria
 */

package nemmprocesses;

import java.util.ArrayList;
import java.util.Collections;

import repast.simphony.random.RandomHelper;
import nemmagents.CompanyAgent;
import nemmagents.CompanyAgent.ActiveAgent;
import nemmagents.CompanyAgent.DeveloperAgent;
import nemmcommons.AllVariables;
import nemmcommons.CommonMethods;
import nemmcommons.ParameterWrapper;
import nemmenvironment.PowerPlant;
import nemmenvironment.TheEnvironment;


public class ProjectMarket {
	
	private static ArrayList<DeveloperAgent> sortedDeveloperAgents_norway = new ArrayList<DeveloperAgent>();	//List of developers having actitivis in Norway, sorted by wacc
	private static ArrayList<DeveloperAgent> sortedDeveloperAgents_sweden = new ArrayList<DeveloperAgent>();	//List of developers having actitivis in Sweden, sorted by wacc
	private static int numberofDAnorway;
	private static int numberofDAsweden;
	private static int numberofownershipchange = 0;
	private static ArrayList<PowerPlant> projectsforredistribution = new ArrayList<PowerPlant>();			//Helplist needed to determine the percentile best projects.
	private static double marginalcertpriceneeded = 0.0;

	
	
//Variebles needed for running the projectmarket method.	
	public ProjectMarket() {};	//Default constructor not used for much.
	
	//The annual projectmarket used all years expect the intial year (as non projects have been activly not taken forward)
	public static void simplifyedprojectmarket() {
		
		//Make sure that flags are updated
		for (PowerPlant PP : TheEnvironment.allPowerPlantsandProjects) {	//Loops thorugh all, hence the logic is in the Power Plant method.
			PP.updatacriteriaflag_standard();}
		
		//Update rank and exchange projects
		updaterelativerank();												//updates all ranks and populate developer lists for each country.
		exchangeprojects();													//Exchange projects
		
	}
	
	public static void initialprojectmarket() {
		
		//First, the benchmark for determining what good or bad projects are must be determined.
		setmarginalcertpriceneeded();
		//Make sure that flags are updated
		for (PowerPlant PP : TheEnvironment.allPowerPlantsandProjects) {	//Loops thorugh all, hence the logic is in the Power Plant method.
			PP.updatacriteriaflag_initial();}
		
		//Update rank and exchange projects
		updaterelativerank();												//updates all ranks and populate developer lists for each country.
		exchangeprojects();													//Exchange projects
		}
	
	
	
	//Calculates the percentile for redustribution
	public static void setmarginalcertpriceneeded() {
		projectsforredistribution.clear();
		for (PowerPlant PP : TheEnvironment.allPowerPlantsandProjects) {
			if (PP.getstatus() > 2 && PP.getstatus() < 6) {
				
				//Currently just the X-percentage of all projects (status 3,4,5) have a chance for redistribution. 
				//Maybe those that cannot be finished in time (for the deadline) should not be included.
				PP.calculateLRMCandcertpriceneeded(2018, PP.getspecificRRR(), 3);
				projectsforredistribution.add(PP);
			}
		}
		Collections.sort(projectsforredistribution, new CommonMethods.customprojectcomparator());			//Sorting the of a DAs project awaiting from lowest certprieneeded to highest certpriceneeded
		int marginalindeks = (int) (AllVariables.initialowenershipchangepercentile * projectsforredistribution.size()) - 1;
		marginalcertpriceneeded = projectsforredistribution.get(marginalindeks).getcertpriceneeded();
	}
	
	
	//Method for doing the actual exchange. Loops thorugh all projets and takes the ones that are marked with positive candidate flag. 
	public static void exchangeprojects() {
		numberofownershipchange = 0;
		for (PowerPlant PP : TheEnvironment.allPowerPlantsandProjects) {
		
			if (PP.getprojectmarketcandidateflag() == 1) {
				
				if (PP.getstatus() < 3) {	throw new IllegalArgumentException("Project Market does not handle powerplants, only projects");}
				else {
					numberofownershipchange = numberofownershipchange +1;
				//Norway
			if (PP.getMyRegion() == TheEnvironment.allRegions.get(0)) {
				
				//Getting a uniform distributed random indeks of new owner that is "better" than current owner
				int tempmax = PP.getMyCompany().getdeveloperagent().getrelativerank_norway() - 1;
				int newownerindeks = RandomHelper.nextIntFromTo(0, tempmax);
				//Change ownership
				CompanyAgent oldowner = PP.getMyCompany();
				DeveloperAgent newowner = sortedDeveloperAgents_norway.get(newownerindeks);
				newowner.addproject(PP);
				oldowner.getmyprojects().remove(PP);	
				PP.setMyCompany(newowner.getmycompany());
				}
				//Sweden
			else {
				int tempmax = PP.getMyCompany().getdeveloperagent().getrelativerank_sweden() - 1;
				int newownerindeks = RandomHelper.nextIntFromTo(0, tempmax);
				//Change ownership
				CompanyAgent oldowner = PP.getMyCompany();
				DeveloperAgent newowner = sortedDeveloperAgents_norway.get(newownerindeks);
				newowner.addproject(PP);
				oldowner.getmyprojects().remove(PP);	
				PP.setMyCompany(newowner.getmycompany());				
				}
			}}
		}
	}
	
	public static void updaterelativerank() {
		//Sorts and rank relevent developers 
		if (sortedDeveloperAgents_norway.isEmpty() == false) {
		sortedDeveloperAgents_norway.clear();}
		
		if (sortedDeveloperAgents_sweden.isEmpty() == false) {
			sortedDeveloperAgents_sweden.clear();}

		//Add to all the relevant lists
		for (DeveloperAgent DA : CommonMethods.getDAgentList()) {
			if (DA.getregionpartcode() == 0) {
				sortedDeveloperAgents_norway.add(DA);}
			if (DA.getregionpartcode() == 1) {
				sortedDeveloperAgents_sweden.add(DA);}
			if (DA.getregionpartcode() == 2) {
				sortedDeveloperAgents_norway.add(DA);
				sortedDeveloperAgents_sweden.add(DA);
			}
		}
		Collections.sort(sortedDeveloperAgents_norway, new CommonMethods.customdeveloperagentcomparator());
		Collections.sort(sortedDeveloperAgents_sweden, new CommonMethods.customdeveloperagentcomparator());
		
		numberofDAnorway = sortedDeveloperAgents_norway.size();
		numberofDAsweden = sortedDeveloperAgents_sweden.size();

		for (int i = 0; i < numberofDAnorway; i++) {
			sortedDeveloperAgents_norway.get(i).setrelativerank_norway(i+1);}
		for (int i = 0; i < numberofDAsweden; i++) {
			sortedDeveloperAgents_sweden.get(i).setrelativerank_sweden(i+1);} 
	}
	public static int getnumberofownershipchange() {return numberofownershipchange;}
	public static double getmarginalcertpriceneeded() {return marginalcertpriceneeded;}		
}

