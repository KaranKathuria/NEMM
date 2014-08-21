/**
 * 
 */
package nemmenvironment;

import static nemmcommons.ParameterWrapper.getobligatedpurchaseragentsnumber;
import java.util.ArrayList;
import nemmcommons.CommonMethods;
import nemmstmstrategiestactics.GenericStrategy;


 //@author Gavin

public class World {

	// The physical and market environment - plants, demand, prices, regions
	
	private ArrayList<Region> myRegions;
	private IOCreate worldCreator;
	
	
	public World() {
		this.myRegions = new ArrayList<Region>();
		this.worldCreator = new IOCreate();
	}
	
	public void SetUpWorld(){
		worldCreator.ReadCreateRegions();
	}
	
	 public class IOCreate{
		// encapculates all the IO for setting up the world
		// The code here will be completely replaced when we move to reading data
		// from a data source. However, for now this is in place so that the model
		// will run and can be tested
		
		public IOCreate(){
			
		}
		
		public void ReadCreateRegions() {
			// this will read in all the region data, including
			// market demand and power plants, and create the corresponding
			// objects. The regions will be saved in the World's myRegions list.
			
			// Currently we just hard code the creation of Norway and Sweden
			int numRegions = 2;
			String[] regionNames = new String[2];
			regionNames[0]="Norway";
			regionNames[1]="Sweden";
			
			for (int i = 0; i < numRegions; ++i) {
				Region newRegion = new Region(regionNames[i]);
				this.PopulatePowerPlants(newRegion);
				this.PopulateMarketDemand(newRegion);
				// add it to the World's region list
				myRegions.add(newRegion);
			}			
		}
		
		private void PopulatePowerPlants(Region curRegion) {
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
				// Create the plant and store it in the list
				PowerPlant newplant = new PowerPlant(newcap, newlf, curRegion);
				curRegion.getallpowerplants().add(newplant);
			}
		}
		
		private void PopulateMarketDemand(Region curRegion){
			// Reads in the demand data and 
			// populates the MarketDemand object for the 
			// passed region
			
		}
		
		
		
	} // IOCreate
	
} // World class
