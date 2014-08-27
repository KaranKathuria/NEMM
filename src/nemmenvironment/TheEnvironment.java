package nemmenvironment;

import java.util.ArrayList;

import nemmagents.CompanyAgent;
import nemmcommons.CommonMethods;
import nemmtime.NemmCalendar;

public final class TheEnvironment {

	// This class is used to hold all the stuff in the environment
	public static ArrayList<PowerPlant> allPowerPlants;
	public static ArrayList<Region> allRegions;
	public static ArrayList<CompanyAgent> allCompanies;
	public static NemmCalendar theCalendar;
	
	
	
	/**
	 * 
	 */
	private TheEnvironment() {

	}
	
	// Initialise the environment ------------------------------------------------------------
	
	
	public static void InitEnvironment(){
		// Create & set up the time calendar and create the lists
		// to hold the plants, companies, and regions
		ReadCreateTime();
		allPowerPlants = new ArrayList<PowerPlant>() ;
		allRegions = new ArrayList<Region>() ;
		allCompanies = new ArrayList<CompanyAgent>();		
	}

	public static void ReadCreateTime(){
		int startYear = 2012;
		int endYear = 2014;
		int numObPdsInYear = 1;
		int numTradePdsInObPd = 12;
		theCalendar = new NemmCalendar(startYear, endYear, numObPdsInYear, numTradePdsInObPd);
	}
	
	// Populate the Environment ------------------------------------------------------------
	
	public static void PopulateEnvironment(){
		
		// This could be added to the constructor, or can be run immediately after
		ReadCreateRegions();
		ReadCreatePowerPlants();
		ReadCreateCompanies();
	}
	

	
	public static void ReadCreateRegions() {
		// this will read in the region info and create the corresponding
		// objects and populate them with data. 
		// The regions will be saved in the environments allRegions list.
		
		// Creation -----------------
		// Currently we just hard code the creation of Norway and Sweden
		int numRegions = 2;
		String[] regionNames = new String[2];
		regionNames[0]="Norway";
		regionNames[1]="Sweden";
		
		for (int i = 0; i < numRegions; ++i) {
			Region newRegion = new Region(regionNames[i]);
			allRegions.add(newRegion);
		}		

		// Population -----------------
		// Populate the market demand and power price objects with data
		PopulateMarketDemands();
		PopulatePowerPrices();
	}
	
	private static void ReadCreatePowerPlants() {
		// Reads in the power plant data and creates
		// power plant objects, populates these, and 
		// stores them in the power plant list for the
		// passed region
		
		// TEST VERSION: creates the plants randomly (i.e. they
		// are not read in from anywhere
		
		int numplants = 40;
		for (int i = 0; i < numplants; ++i) {
			// randomly create capacity and load factor for the 
			// new plant
			// REPLACE random method with repast random stream
			int newcap = CommonMethods.randInt(50, 150);
			double newlf = CommonMethods.randInt(20, 35)/100;
			// randomly choose the region
			int selectedRegion = CommonMethods.randInt(0, allRegions.size()-1);
			// Create the plant and store it in the list
			PowerPlant newplant = new PowerPlant(newcap, newlf, allRegions.get(selectedRegion));
			double[] defProd = new double[0];
			defProd[0] = 50;
			newplant.setAllProduction(defProd); //  production in each tick set to a default
			allPowerPlants.add(newplant);
		}
	}
	
	
	private static void PopulateMarketDemands(){
		for (Region curRegion: allRegions){
			// code to come here to set up the regions power
			// demand and certificate obligation
			double[] newDem = new double[1];
			double[] newObl = new double[1];
			newDem[0] = 2000;
			newObl[0] = 0.10;
			curRegion.getMyDemand().initMarketDemand(newDem, newObl);			
		}
	}
	
	private static void PopulatePowerPrices(){
		for (Region curRegion: allRegions){
			// code to come here to set up the regions power
			// prices
			// currently these are just fixed at 200 whatevers
			double[] newPrice = new double[1];
			newPrice[0] = 200;
			curRegion.getMyPowerPrice().initMarketSeries(newPrice);
		}		
	}	
	
	
	public static void ReadCreateCompanies() {
		// this will read in the company info, including
		// initial demand shares, agent structure and so on
		
		// Currently we just hard code the creation of companies
		int numComps = 10;
		
		for (int i = 0; i < numComps; ++i) {
			CompanyAgent newCompany = new CompanyAgent();
			allCompanies.add(newCompany);
		}			
	} 
	
}
