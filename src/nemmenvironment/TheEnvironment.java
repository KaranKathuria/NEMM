package nemmenvironment;

import java.util.ArrayList;

import nemmcommons.CommonMethods;
import nemmtime.NemmCalendar;

public final class TheEnvironment {

	// This class is used to hold all the stuff in the environment
	private ArrayList<PowerPlant> allPowerPlants;
	private ArrayList<Region> allRegions;
	public static NemmCalendar theCalendar;
	
	
	
	/**
	 * 
	 */
	public TheEnvironment() {
		allPowerPlants = new ArrayList<PowerPlant>() ;
		allRegions = new ArrayList<Region>() ;
	}
	
	// Gets and Sets --------------------------------------------------------------

	public ArrayList<PowerPlant> getAllPowerPlants() {
		return allPowerPlants;
	}
	public void setAllPowerPlants(ArrayList<PowerPlant> allPowerPlants) {
		this.allPowerPlants = allPowerPlants;
	}
	public ArrayList<Region> getAllRegions() {
		return allRegions;
	}
	public void setAllRegions(ArrayList<Region> allRegions) {
		this.allRegions = allRegions;
	}
	
	// Creation methods ------------------------------------------------------------
	
	public void SetUpEnvironment(){
		ReadCreateRegions();
	}
	
	public void ReadCreateRegions() {
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
		this.PopulateMarketDemands();
		this.PopulatePowerPrices();
	}
	
	private void ReadCreatePowerPlants() {
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
			int newcap = CommonMethods.randInt(50, 150);
			double newlf = CommonMethods.randInt(20, 35)/100;
			// randomly choose the region
			int selectedRegion = CommonMethods.randInt(0, allRegions.size()-1);
			// Create the plant and store it in the list
			PowerPlant newplant = new PowerPlant(newcap, newlf, allRegions.get(selectedRegion));
			this.allPowerPlants.add(newplant);
		}
	}
	
	
	private void PopulateMarketDemands(){
		for (Region curRegion: allRegions){
			// code to come here to set up the regions power
			// demand and certificate obligation
		}
	}
	
	private void PopulatePowerPrices(){
		for (Region curRegion: allRegions){
			// code to come here to set up the regions power
			// prices
			// currently these are just fixed at 200 whatevers
			double newPrice = 200;
			curRegion.getMyPowerPrice().setPrice(newPrice, null);
		}		
	}	
	
	
/*	public void ReadCreateCompanies() {
		// this will read in the company info, including
		// initial demand shares, agent structure and so on
		
		// Currently we just hard code the creation of companies
		int numComps = 10;
		String baseName = "Company ";
		String compName;
		
		for (int i = 0; i < numComps; ++i) {
			compName = baseName + numComps;
			GenericCompany newCompany = new GenericCompany(compName);
			allCompanies.add(newCompany);
		}			
	} */
	
}
