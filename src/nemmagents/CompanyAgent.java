/*
 * Version info:
 *     File defining the Company Agent, which consist of one or none of the following active agents: Producer, Obligated Purchaser or Trader. And a companyanalsysagent. 
 *      *     File defining the Active Agents. These agents are a agents of type Producer, Obligated Purchaser or Trader. 
 *     
 *     Last altered data: 20140826
 *     Made by: Karan Kathuria
 */
package nemmagents;

//Import section for other methods
import java.util.ArrayList;
import java.util.Random;

import nemmagents.MarketAnalysisAgent;
import nemmagents.ParentAgent;
import nemmprocesses.ShortTermMarket;
import nemmstrategy_shortterm.BidOffer;
import nemmstrategy_shortterm.GenericUtilityMethod;
import nemmstrategy_shortterm.OPAUtilityMethod;
import nemmstrategy_shortterm.PAUtilityMethod;
import nemmstrategy_shortterm.TAUtilityMethod;
import nemmstrategy_shortterm.BuyStrategy1;
import nemmstrategy_shortterm.GenericStrategy;
import nemmstrategy_shortterm.SellStrategy1;
import nemmstrategy_shortterm.TradeStrategy1;
import nemmcommons.AllVariables;
import nemmcommons.VolumePrognosis;
import nemmenvironment.PowerPlant;
import nemmenvironment.Region;
import nemmenvironment.TheEnvironment;
import nemmenvironment.TheEnvironment.GlobalValues;

// Class definition
public class CompanyAgent extends ParentAgent {
	
	//ActiveAgent defined as inner class
	public class ActiveAgent extends ParentAgent {

		private CompanyAgent companyagent;
		private String activeagenttypename;
		private int activeagenttypecode;		 //1 = ProducerAgent, 2 = ObligatedPurchaserAgent, 3 = TraderAgent
		private ArrayList<GenericStrategy> allstrategies = new ArrayList<GenericStrategy>();
		private int numberofstrategies;
		private GenericStrategy beststrategy = null;
		private GenericUtilityMethod utilitymethod;
		private double physicalnetposition;
		private double lasttickproduction;
		private double lasttickdemand;
		private double portfoliocapital;
		private int sizecode;			//Code value indicationg if the AA has few=1, normal=2 og alot=3 of activities (powerplants or demandshares) in the regions the company of that AA participates in
		private double RAR;				//Agentspecific risk adjusted rate used to discount the future price of certificates to floor/roof prices in STM bidding. Not to be confused with the company specific RRR which is the companies investmentcriteria.
		// ---- GJB Added
		private int numTicksToEmptyPosition; // number of ticks the agent will try to empty its position over
		// ---- end GJB Added
		
		// Null constructor for ActiveAgent. Should not be used as this does not specify type of agent.
		public ActiveAgent() {
			activeagenttypename = "Unreal ActiveAgent";
			activeagenttypecode = 0;
			//Return exeption?
		}
		public ActiveAgent(int type) {
			activeagenttypecode = type;
			
			if (type == 1) {
				activeagenttypename = "ProducerAgent";
				physicalnetposition = 1000;
				lasttickproduction = 1000;
				// here we need a way to have different utilities
				// currently hard coded to the default
				utilitymethod = new PAUtilityMethod(nemmcommons.AllVariables.utilityDefault_PA);
				SellStrategy1 sellstrategy = new SellStrategy1();
				sellstrategy.setmyAgent(ActiveAgent.this);
				allstrategies.add(sellstrategy);
				portfoliocapital = 0;
				// ---- GJB Added
				// Goal is to set some agents who try to exit their physical position 
				// quickly, others who can hold it for a while, and others who can hold for a long time
				// Specify the type and get the number of ticks
				Random generator = new Random(); 
				double dPhysRnd = generator.nextDouble();
				for (int stratID=AllVariables.numPAExitStrategies-1; stratID>=0; stratID--){
					if (dPhysRnd <= AllVariables.cutoffPAExit[stratID]) {
						numTicksToEmptyPosition = AllVariables.numTicksPAExit[stratID];
					}
				}

				// ---- end GJB Added
												
			} else if (type == 2) {
				activeagenttypename = "ObligatedPurchaserAgent";
				physicalnetposition = -1000;
				lasttickdemand = -1000;
				// here we need a way to have different utilities
				// currently hard coded to the default
				utilitymethod = new OPAUtilityMethod(nemmcommons.AllVariables.utilityDefault_OP);
				BuyStrategy1 buystrategy = new BuyStrategy1();
				buystrategy.setmyAgent(ActiveAgent.this);
				allstrategies.add(buystrategy);
				portfoliocapital = 0;
				
				// ---- GJB Added
				// Goal is to set some agents who try to exit their physical position 
				// quickly, others who can hold it for a while, and others who can hold for a long time
				// Specify the type and get the number of ticks
				Random generator = new Random(); 
				double dPhysRnd = generator.nextDouble();
				for (int stratID=AllVariables.numOPExitStrategies-1; stratID>=0; stratID--){
					if (dPhysRnd <= AllVariables.cutoffOPExit[stratID]) {
						numTicksToEmptyPosition = AllVariables.numTicksOPExit[stratID];
					}
				}
				
				// ---- end GJB Added
				
			} else { //Notice that else is all other added as Trader agents. This is okey for now but should call an expetion later. 
				activeagenttypename = "TraderAgent";
				physicalnetposition = 0;
				// here we need a way to have different utilities
				// currently hard coded to the default
				utilitymethod = new TAUtilityMethod(nemmcommons.AllVariables.utilityDefault_TR);
				TradeStrategy1 tradestrategy = new TradeStrategy1();
				tradestrategy.setmyAgent(ActiveAgent.this);
				allstrategies.add(tradestrategy);
				portfoliocapital = 1000000; 
			} 
			RAR = 0.07; //quite risk avers
			companyagent = CompanyAgent.this;
			this.utilitymethod.setmyAgent(ActiveAgent.this);
			beststrategy = allstrategies.get(0); // Choose the first one initially 
			numberofstrategies = allstrategies.size();
			sizecode = 2;
		}
		
		//Get methods for the ActiveAgent
		public double getphysicalnetposition() {return physicalnetposition;}
		public double getRAR() {return RAR;}
		public int getsizecode() {return sizecode;}
		public int getregionpartcode() {return this.companyagent.regionpartcode;}
		public double getlasttickproduction() {return this.lasttickproduction;}
		public double getlasttickdemand() {return this.lasttickdemand;}
		public GenericStrategy getbeststrategy() {return beststrategy;}
		public void setbeststrategy(GenericStrategy s) {beststrategy = s;}
		public ArrayList<GenericStrategy> getallstrategies() {return allstrategies;}
		
		// ---- GJB Added
		public int getNumTicksToEmptyPosition() {
			return numTicksToEmptyPosition;
		}
		// ---- end GJB Added
		
		public void poststmupdate(double certificatessold, double certificatesbought) {
			//Updates the portfoliocapital before the physical position. Also thes method requires that ShortTermMarket is ran, but not Globalvalues.
			portfoliocapital = portfoliocapital + (physicalnetposition * (GlobalValues.currentmarketprice - ShortTermMarket.getcurrentmarketprice())); //is ran before global values are updated, hence global values current market price has the old market price
			physicalnetposition = physicalnetposition + certificatesbought - certificatessold; //Certificates bought and sold are positive numbers.
		}
		//takes in either production (positiv) or demand (negative) amount of certs and updates "lasttickproduction" "lasttickdemand" and physicalposition
		public void addtophysicalposition(double prodordemand) {
			physicalnetposition = physicalnetposition + prodordemand;
			if (prodordemand >= 0) { //Implies production as produciont cannot be negative.
				lasttickproduction = prodordemand;
			}
			if (prodordemand <= 0) { //Implies demand as demand cannot be postive.
				lasttickdemand = prodordemand;}
		}
		
		public void setphysicalnetposition(double a) {physicalnetposition = a;}
		public CompanyAnalysisAgent getagentcompanyanalysisagent() {return companyanalysisagent;}
		public GenericUtilityMethod getutilitymethod() {return utilitymethod;}
		public ArrayList<PowerPlant> getmypowerplants() {return myPowerPlants;}
		public void addpowerplant(PowerPlant pp) {myPowerPlants.add(pp);}
		public void AddDemandShare(double defaultShare, Region demRegion){
			CompanyDemandShare tempDS = new CompanyDemandShare(defaultShare, demRegion);
			myDemandShares.add(tempDS);
		}
		public ArrayList<CompanyDemandShare> getMyDemandShares() {
			return myDemandShares;
		}	
		public CompanyAgent getmycompany() {
			return this.companyagent;
		}
		public double getportfoliocapital() {
			return portfoliocapital;
		}
		// ---- GJB ADDED
		private void updatePortfolioCapital(double mktPriceDelta) {
			// Update the portfolio's M2M value
			portfoliocapital = portfoliocapital + (physicalnetposition * mktPriceDelta);			
		}
		
		private void updatePhysicalNetPosition(double volBought, double volSold, double volProd, double volDemand) {
			physicalnetposition = physicalnetposition + volBought + volProd - volSold - volDemand;
			lasttickproduction = volProd;
			lasttickdemand = -volDemand; // it expects demand to be negative
		}
		public double getSoldVolume() {
			// Calculates and returns the volume sold in the last market round
			double volSold = 0.0; 
			if (beststrategy.getAgentsSellOffers() != null) {
				 for (BidOffer m : beststrategy.getAgentsSellOffers()) {
					 volSold = volSold + m.getCertVolume()*m.getShareCleared();
				 }	 
			 }
			return volSold;
		}
		public double getBoughtVolume() {
			// Calculates and returns the volume bought in the last market round
			double volBought = 0.0;
			 if (beststrategy.getAgentsBuyOffers() != null) {
				 for (BidOffer m : beststrategy.getAgentsBuyOffers()) {
					 volBought = volBought + m.getCertVolume()*m.getShareCleared();
				 }
			 } 
			 return volBought;
		}
		public double getProductionVolume() {
			double volProd = 0.0;
			int curTick = TheEnvironment.theCalendar.getCurrentTick();
			if (myPowerPlants != null) {
				for (PowerPlant PP : myPowerPlants) { 
					// Add in the power plant's production for the current tick	
					if(PP.getStartTick() <= curTick) {
						volProd = volProd + PP.getProduction();
					}
				}
			}
			return volProd;
		}
		public double getDemandVolume() {
			double volDemand = 0.0;
			if (myDemandShares != null) {
				for (CompanyDemandShare CDS : myDemandShares) { 
					//Go through all demandshares (which consists of a region and a share. 
					//Sum the product of that regions demandshare with that regions demand, for current tick.
					volDemand = volDemand + (CDS.getMyRegion().getMyDemand().getCertDemand() * CDS.getDemandShare());  
				}
			}
			return volDemand;
		}
		 public void updateAgentPositions() {
			 // Updates following market transactions, plant production and power sales
			 // This occurs at the end of the tick (as the certificates do not accrue
			 // until the production is reported). This is not quite accurate, but ok for now
			 double volBought = 0.0;
			 double volSold = 0.0;
			 double volProd = 0.0;
			 double volDemand = 0.0;
			 double priceDelta = 0.0;
			 // Market transactions
			 volSold = getSoldVolume();
			 volBought = getBoughtVolume();
			 // Physical production and demand
			 volProd = getProductionVolume();
			 volDemand = getDemandVolume();
			
			// Update the portfolio value 
			priceDelta = GlobalValues.currentmarketprice - ShortTermMarket.getcurrentmarketprice();
			updatePortfolioCapital(priceDelta);
			// Update the agent's net physical position
			updatePhysicalNetPosition(volBought, volSold, volProd, volDemand);
			@SuppressWarnings("unused")
			int tmp = 1;
		 }
		// ---- end GJB ADDED
	}
	
	////DeveloperAgent defined as inner class of CompanyAgent in order to have access to company specific data.
	public class DeveloperAgent extends ParentAgent {
		
		private CompanyAgent companyagent;
		private int developmentcriteriaflag; 			//Flag indicationg the development criteria for the DA. 1 = The project must fullfill RRR with prognosed price 2 = The project must fullfill RRR with todays price. 3 = The project must fullfill RRR with both price.
		private double totalcapacitylimit;				//Politically given roof for how much installed REC capacity is developed (Applicable for Swedish municipalities).
		private int constructionlimit;					//Number of projects it can have under construction simultaniasly.
		private int projectprocessandidylimit;			//The agents maximum number of projects that can be identifyed or in concession/preconstruct each year. Thats from the potential list.
		private int sizecode;							//Code value indicationg if the DA has few=1, normal=2 og alot=3 of activities (projects) in the regions the company of that AA participates in
		
		//Endogenous variables 
		private double capacitydevorundrconstr;
		private int numprojectstrashed;
		private int numprojectsfinished;
		private int numprojectsunderconstr;
		private int numprojectsawaitingid;
		private int numprojectsinprocess;
		private int numprojectsidentyfied;
		
		public DeveloperAgent() {
			companyagent = this.companyagent;
			sizecode = 2;								//By default all developmentagents have normal amount of activety.
			projectprocessandidylimit = 5;				//Max number of project getting identifyed or getting in process.
			constructionlimit = 2;						//Max number of projects getting in from moving to construction. 
		}
		
		public void updateDAnumbers(double cpdorconstr, int numpt, int numpf, int numpuc, int numpaid, int numpip, int numpid) {
			capacitydevorundrconstr = cpdorconstr;
			numprojectstrashed = numpt;
			numprojectsfinished = numpf;
			numprojectsunderconstr = numpuc;
			numprojectsawaitingid = numpaid;
			numprojectsinprocess = numpip;
			numprojectsidentyfied = numpid;
		}
		
		//Add, set and get methods for DeveloperAgent
		public int getsizecode() {return sizecode;}
		public int getregionpartcode() {return regionpartcode;}
		public CompanyAgent getmycompany() {return this.companyagent;}
		public void addproject(PowerPlant PP) {myProjects.add(PP);	}
		public ArrayList<PowerPlant> getmyprojects() {return myProjects;}
		public double gettotalcapacitylimit() {return totalcapacitylimit;}
		public int getconstructionlimit() {return constructionlimit;}
		public int getprojectprocessandidylimit() {return projectprocessandidylimit;}
		public double getcapacitydevorundrconstr() {return capacitydevorundrconstr;}
		public int getnumprojectsunderconstr() {return numprojectsunderconstr;}
		public int getnumprojectsinprocess() {return numprojectsinprocess;}

		
	}
	
	//Inner class CompanyAnalysisAgent. This agent consist of two other objects. MarketAnalysisAgent and VolumeAnalysisAgent. THe latter is an inner subclass.
	public class CompanyAnalysisAgent extends ParentAgent {
			
			//Nested inner class VolumeAnalysisAgent
		public class VolumeAnalysisAgent extends ParentAgent {	
			private VolumePrognosis volumeprognosis;
					
			VolumeAnalysisAgent() {
				volumeprognosis = new VolumePrognosis();
				volumeprognosis.setmyVAA(VolumeAnalysisAgent.this);
			}
			public VolumePrognosis getvolumeprognosis() {
				return volumeprognosis;
			}
			public CompanyAgent getmyCompany() {
				return CompanyAgent.this;
			}
			}
			
	private MarketAnalysisAgent marketanalysisagent;
	private VolumeAnalysisAgent volumeanalysisagent;

	//Default constructor calling the respective objects default constructors
	CompanyAnalysisAgent() {
		marketanalysisagent = new MarketAnalysisAgent();
		volumeanalysisagent = new VolumeAnalysisAgent();
		}
		
	//Sets and gets
		public MarketAnalysisAgent getmarketanalysisagent() {
			return marketanalysisagent;
		}
		public VolumeAnalysisAgent getvolumeanalysisagent() {
			return volumeanalysisagent;
		}
	}
		
	//CompanyAgent variables
	private String companyname;
	private ActiveAgent produceragent;
	private ActiveAgent obligatedpurchaseragent;
	private ActiveAgent traderagent;
	private DeveloperAgent developeragent;
	private CompanyAnalysisAgent companyanalysisagent;
	private ArrayList<CompanyDemandShare> myDemandShares = new ArrayList<CompanyDemandShare>();
	private ArrayList<PowerPlant> myPowerPlants = new ArrayList<PowerPlant>();						//List of PowerPlants owned by the company regardless of project stage.
	private ArrayList<PowerPlant> myProjects = new ArrayList<PowerPlant>();							//List of all projects owned by the company regardless of project stage.
	private int regionpartcode;																		//Code indication which region the Company is active in. 1=Norway (region1), 2=Norway and Sweden, 3=Sweden. This number has a connection with AA sizecode, hence a company with big size and reigonpartcode=2 is large in both regions.
	private double investmentRRR; 																	//Company specific cost of capital. Used to evaluate investment decisions. Defined before tax.
	private double earlystageRRR;																	//Required rate off return for project cut-off on earl-stage projects. Defined before tax. Higher than InvestmentRRR as the risk is higher.
	
	//default constructor not in use.
	public CompanyAgent() {
		companyname = "Company " + this.getID();
		produceragent = null;
		obligatedpurchaseragent = null;
		traderagent = null;
		companyanalysisagent = null;}
	
	
	public CompanyAgent(boolean p, boolean op, boolean t) {
		if (p==true) {
			produceragent = new ActiveAgent(1);
			developeragent = new DeveloperAgent();}													//By default all and just all companies with PA have a DA.
		if (op==true) {
			obligatedpurchaseragent = new ActiveAgent(2);}
		if (t==true) {
			traderagent = new ActiveAgent(3);}
		
		companyname = "Company " + this.getID();
		companyanalysisagent = new CompanyAnalysisAgent();	
		investmentRRR = 0.06;
		earlystageRRR = 0.08;
		regionpartcode = 2;																			//By default, all companies are active in both countries. 
		
		}	
	
	//CompanyAgents get methods
	public ActiveAgent getproduceragent() {return produceragent;}
	public ActiveAgent getobligatedpurchaseragent() {return obligatedpurchaseragent;}
	public ActiveAgent gettraderagent() {return traderagent;}
	public DeveloperAgent getdeveloperagent() {return developeragent;}
	public CompanyAnalysisAgent getcompanyanalysisagent() {return companyanalysisagent;}
	public ArrayList<PowerPlant> getmypowerplants() {return myPowerPlants;}
	public ArrayList<PowerPlant> getmyprojects() {return myPowerPlants;}
	public ArrayList<CompanyDemandShare> getMyDemandShares() {return myDemandShares;}
	public double getInvestmentRRR() {return investmentRRR;}
	public double getearlystageRRR() {return earlystageRRR;}
	
}
	
