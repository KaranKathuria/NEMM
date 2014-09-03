package nemmenvironment;

import java.util.ArrayList;

import repast.simphony.random.RandomHelper;
import nemmagents.CompanyAgent;
import nemmagents.CompanyAgent.ActiveAgent;
import nemmcommons.CommonMethods;
import nemmcommons.ParameterWrapper;
import nemmprocesses.ShortTermMarket;
import nemmtime.NemmCalendar;

public final class TheEnvironment {

	// This class is used to hold all the stuff in the environment
	public static ArrayList<PowerPlant> allPowerPlants;
	public static ArrayList<Region> allRegions;
	public static ArrayList<CompanyAgent> allCompanies;
	public static NemmCalendar theCalendar;
	
	private TheEnvironment() {}
	
	// Initialise the environment ------------------------------------------------------------
	public static void InitEnvironment(){
		// Create & set up the time calendar and create the lists
		// to hold the plants, companies, and regions
		ReadCreateTime();
		allPowerPlants = new ArrayList<PowerPlant>() ;
		allRegions = new ArrayList<Region>() ;	
		
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
		PopulatePowerPrices();
		PopulateMarketDemands();
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
			Region newRegion = new Region(regionNames[i]); //Currently this also creates default power prices, power demand and certplikt.
			allRegions.add(newRegion);
		}		

		// Population -----------------
		// Populate the market demand and power price objects with data
		//PopulateMarketDemands();
		//PopulatePowerPrices();
	}
	
	private static void ReadCreatePowerPlants() {
		// Reads in the power plant data and creates
		// power plant objects, populates these, and 
		// stores them in the power plant list for the
		// passed region
		
		// TEST VERSION: creates the plants randomly (i.e. they
		// are not read in from anywhere
		
		int numplants = 10;
		for (int i = 0; i < numplants; ++i) {
			// randomly create capacity and load factor for the 
			// new plant
			int newcap = 10000; //CommonMethods.randInt(50, 150);
			double newlf = 0.1; //CommonMethods.randInt(20, 35)/100;
			// randomly choose the region
			int selectedRegion = CommonMethods.randInt(0, allRegions.size()-1);
			// Create the plant and store it in the list
			PowerPlant newplant = new PowerPlant(newcap, newlf, allRegions.get(selectedRegion));
			double[] defProd = new double[1];
			defProd[0] = 1000;
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
			newDem[0] = 50000;
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
	
	public static class GlobalValues {
		
		public static double currentmarketprice;
		public static double currentinterestrate;
		public static int numberofbuyoffersstm;
		public static int numberofselloffersstm;
		// Future cert prices
		public static double endofyearpluss1;
		public static double endofyearpluss2;
		public static double endofyearpluss3;
		public static double endofyearpluss4;
		public static double endofyearpluss5;
		// Power prices (pp), current and future. 
		public static double powerprice;
		public static double ppendofyearpluss1;
		public static double ppendofyearpluss2;
		public static double ppendofyearpluss3;
		public static double ppendofyearpluss4;
		public static double ppendofyearpluss5;
		
		public static double producersphysicalposition = 0;
		public static double tradersphysicalposition = 0;
		public static double obligatedpurchasersphysiclaposition = 0;
		public static double totalmarketphysicalposition = 0;
		
		public GlobalValues() {
			currentmarketprice = ParameterWrapper.getpriceexpectation();
		}
		public static void initglobalvalues() {
			//Initially the global market price is set to that of the price expectations
			currentmarketprice = ParameterWrapper.getpriceexpectation();
			currentinterestrate = ParameterWrapper.getinitialinterestrate();
			producersphysicalposition = 10000;
			tradersphysicalposition = 0;
			obligatedpurchasersphysiclaposition = -10000;
			totalmarketphysicalposition = 0;
			}
		public static void marketshock() {
			//Modelling the effect of resetting the market price
			currentmarketprice = ParameterWrapper.getpriceexpectation();
			}
		// Monthly update of current global values
		public static void monthlyglobalvalueupdate() {
			currentmarketprice = ShortTermMarket.getcurrentmarketprice();
			currentinterestrate = currentinterestrate + RandomHelper.nextDoubleFromTo(-0.002, 0.002);
			numberofbuyoffersstm = ShortTermMarket.getnumberofbuyoffers();
			numberofselloffersstm = ShortTermMarket.getnumberofselloffers();
			
			producersphysicalposition = 0;
			tradersphysicalposition = 0;
			obligatedpurchasersphysiclaposition = 0;
			totalmarketphysicalposition = 0;
			
			for (ActiveAgent pa: CommonMethods.getPAgentList()){
				producersphysicalposition = producersphysicalposition + pa.getphysicalnetposition();	
			}
			for (ActiveAgent opa: CommonMethods.getOPAgentList()){
				obligatedpurchasersphysiclaposition = obligatedpurchasersphysiclaposition + opa.getphysicalnetposition();	
			}
			for (ActiveAgent ta: CommonMethods.getTAgentList()){
				tradersphysicalposition = tradersphysicalposition + ta.getphysicalnetposition();	
			}
			totalmarketphysicalposition = tradersphysicalposition + obligatedpurchasersphysiclaposition + producersphysicalposition;
		}
		// Annual update of annual chaning global values
		public static void annualglobalvalueupdate() {
			endofyearpluss1 = currentmarketprice*(1+currentinterestrate);
			endofyearpluss2 = currentmarketprice*Math.pow((1+currentinterestrate), 2);
			endofyearpluss3 = currentmarketprice*Math.pow((1+currentinterestrate), 3);
			endofyearpluss4 = currentmarketprice*Math.pow((1+currentinterestrate), 4);
			endofyearpluss5 = currentmarketprice*Math.pow((1+currentinterestrate), 5);
		}
	}
	
	
}
