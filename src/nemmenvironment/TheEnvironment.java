package nemmenvironment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.random.RandomHelper;
import nemmagents.CompanyAgent;
import nemmagents.CompanyAgent.ActiveAgent;
import nemmcommons.AllVariables;
import nemmcommons.CommonMethods;
import nemmcommons.ParameterWrapper;
import nemmcommons.TickArray;
import nemmprocesses.ShortTermMarket;
import inputreader.ReadExcel;

import java.util.Comparator;

import nemmtime.NemmCalendar;

public final class TheEnvironment {

	// This class is used to hold all the stuff in the environment
	public static ArrayList<PowerPlant> allPowerPlants; 					//allPowerPlants referes to all power plants that have been or are in operation. Before an object PowerPlant is in operation, its reffered to as a project.
	public static ArrayList<PowerPlant> projectsunderconstruction;			//PowerPlants currently under construction (status = 2)
	public static ArrayList<PowerPlant> projectsawaitinginvestmentdecision;	//PowerPlants projects awaiting investment decision (status = 3)
	public static ArrayList<PowerPlant> projectinprocess;					//All powerplant in process of getting concession.  (status = 4)
	public static ArrayList<PowerPlant> projectsidentifyed;					//All projects identifyed							(status = 5)
	public static ArrayList<PowerPlant> potentialprojects;					//Auto-generated potential projects. Note distributed among development agents. (status = 6).
	public static ArrayList<PowerPlant> trashedprojects;					//Arraylist of projects not receiving concession (status = 0).
	
	public static ArrayList<Region> allRegions;
	public static ArrayList<CompanyAgent> allCompanies;
	
	public static ArrayList<ProjectRRR> alladjustedRRR;						//List containing alle the adjusted RRR. Used by Projects to look up their specific RRR.
	public static NemmCalendar theCalendar;
	
	
	private TheEnvironment() {}
	
	// Initialise the environment ------------------------------------------------------------
	public static void InitEnvironment(){
		// Create & set up the time calendar and create the lists to hold plants, projects and regions.
		inputreader.ReadExcel.InitReadExcel();
		inputreader.ReadExcel.ReadCreateTime();
		GlobalValues.initglobalvalues();
		allPowerPlants = new ArrayList<PowerPlant>() ;
		potentialprojects = new ArrayList<PowerPlant>() ;
		projectsidentifyed = new ArrayList<PowerPlant>() ;
		projectinprocess = new ArrayList<PowerPlant>() ;
		projectsawaitinginvestmentdecision = new ArrayList<PowerPlant>() ;
		projectsunderconstruction = new ArrayList<PowerPlant>() ;
		allRegions = new ArrayList<Region>() ;	
		alladjustedRRR = new ArrayList<ProjectRRR>();
	}
	
	// Populate the Environment ------------------------------------------------------------
	
	public static void PopulateEnvironment(){
		inputreader.ReadExcel.ReadRegions();
		inputreader.ReadExcel.ReadRRR();
		inputreader.ReadExcel.ReadPowerPlants();

	}
		
	
	public static class GlobalValues {
		
		public static TickArray certificateprice;
		public static double currentmarketprice;
		public static double currentinterestrate;   //risk free interest rate
		public static double RRR; 					//Required rate of return for RE investments. 
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
		public static double bestbuyoffer1;
		public static double bestbuyoffer2;
		public static double bestselloffer1;
		public static double bestselloffer2;
		
		public GlobalValues() {
			currentmarketprice = ParameterWrapper.getpriceexpectation();
		}
		public static void initglobalvalues() {
			
			//Initiating globale values consisting of public market information
			certificateprice = new TickArray();
			currentmarketprice = ParameterWrapper.getpriceexpectation();
			currentinterestrate = ParameterWrapper.getinitialinterestrate();
			RRR = AllVariables.RRR;
			
			producersphysicalposition = 20000;					//Must be set to the sum of all agents startingposition. Just used for graph values.
			tradersphysicalposition = 0;						//Must be set to the sum of all agents startingposition. Just used for graph values.
			obligatedpurchasersphysiclaposition = -20000;		//Must be set to the sum of all agents startingposition. Just used for graph values.
			totalmarketphysicalposition = 0;					//Must be set to the sum of all agents startingposition. Just used for graph values.
			}
		
		public static void marketshock() {
			//Modelling the effect of resetting the market price
			currentmarketprice = ParameterWrapper.getpriceexpectation();
			}
		
		// Monthly update of current global values
		public static void monthlyglobalvalueupdate() {
			currentmarketprice = ShortTermMarket.getcurrentmarketprice();
			certificateprice.setElement(ShortTermMarket.getcurrentmarketprice(), theCalendar.getCurrentTick()); //Adds certPrice to history.
			
			//currentinterestrate = currentinterestrate + RandomHelper.nextDoubleFromTo(-0.002, 0.002); //Randomness in risk free interest rate.
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
		/*
		// Annual update of annual chaning global values
		public static void annualglobalvalueupdate() {
			endofyearpluss1 = currentmarketprice*(1+currentinterestrate);
			endofyearpluss2 = currentmarketprice*Math.pow((1+currentinterestrate), 2);
			endofyearpluss3 = currentmarketprice*Math.pow((1+currentinterestrate), 3);
			endofyearpluss4 = currentmarketprice*Math.pow((1+currentinterestrate), 4);
			endofyearpluss5 = currentmarketprice*Math.pow((1+currentinterestrate), 5);
		}
		*/
	}
	
	
	
}

//NEMMCALENDAR START ========================================================	
	/*public static class NemmCalendar {
		
		private int startYear;
		private int endYear;
		private int numYears;
		private int numObligatedPdsInYear;
		private int numTradePdsInObligatedPd;
		private int numTradePdsInYear;
		private int numTicks;
		private int currentTick;
		private ArrayList<NemmTime> timeBlocks;
		
		
		public NemmCalendar(int startYear, int endYear, int numObligatedPdsInYear,
				int numTradePdsInObligatedPd) {
			// should throw errors if start year later than end year, other vals <=0 etc
			this.currentTick=0;
			this.startYear = startYear;
			this.endYear = endYear;
			this.numObligatedPdsInYear = numObligatedPdsInYear;
			this.numTradePdsInObligatedPd = numTradePdsInObligatedPd;
			this.numYears = this.endYear - this.startYear + 1;
			this.numTradePdsInYear = this.numObligatedPdsInYear*this.numTradePdsInObligatedPd;
			this.numTicks = this.numYears * this.numTradePdsInYear;
			timeBlocks = new ArrayList<NemmTime>();
			
			int curTick = 0;
			for (int y = 0; y < numYears; ++y){
				for (int b = 0; b < this.numObligatedPdsInYear; ++b){
					for (int t = 0; t < this.numTradePdsInObligatedPd; ++t){
						NemmTime newBlock = new NemmTime(y,b,t, curTick);
						timeBlocks.add(newBlock);
						curTick = curTick+1;
					}
				}
			}
			Collections.sort(timeBlocks, new NemmTimeCompare());
		}
		
		public int getCurrentTick() {
			// will grab this from repast
			return (int) RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		}


		public NemmTime getTimeBlock(int tickID){
			return timeBlocks.get(tickID);
		}

		public int getStartYear() {
			return startYear;
		}

		public int getEndYear() {
			return endYear;
		}

		public int getNumYears() {
			return numYears;
		}

		public int getNumObligatedPdsInYear() {
			return numObligatedPdsInYear;
		}

		public int getNumTradePdsInObligatedPd() {
			return numTradePdsInObligatedPd;
		}

		public int getNumTradePdsInYear() {
			return numTradePdsInYear;
		}

		public int getNumTicks() {
			return numTicks;
		}
		// NEMMTIME START ===================================================
		public static class NemmTime {

			// This is a structure to concisely hold NEMM time info
			
			public int year;
			public int obligationpdID;
			public int tradepdID;
			public Calendar startDate;
			public Calendar endDate;
			public int tickIndex;
			
			public NemmTime(int yearPd, int oblPd, int trdPd, int tickID) {
				year = yearPd;
				obligationpdID = oblPd;
				tradepdID = trdPd;
				tickIndex = tickID;
			}
				
			
		} 
		// NEMMTIME END ===================================================
		// NEMMTIMECOMPARE START ===================================================
		class NemmTimeCompare implements Comparator<NemmTime> {

		    @Override
		    public int compare(NemmTime t1, NemmTime t2) {
		        // comparison logic based on tick index
		    	int compareTo = t2.tickIndex > t1.tickIndex ? 1 : (t2.tickIndex < t1.tickIndex ? -1 : 0);
		        return compareTo;
		    }
		}
		// NEMMTIMECOMPARE END ===================================================

	}
	// NEMMCALENDAR END ========================================================	
	*/
