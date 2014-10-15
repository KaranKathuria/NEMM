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

import nemmagents.MarketAnalysisAgent;
import nemmagents.ParentAgent;
import nemmprocesses.ShortTermMarket;
import nemmstrategy_shortterm.GenericUtilityMethod;
import nemmstrategy_shortterm.OPAUtilityMethod;
import nemmstrategy_shortterm.PAUtilityMethod;
import nemmstrategy_shortterm.TAUtilityMethod;
import nemmstrategy_shortterm.BuyStrategy1;
import nemmstrategy_shortterm.GenericStrategy;
import nemmstrategy_shortterm.SellStrategy1;
import nemmstrategy_shortterm.TradeStrategy1;
import nemmcommons.VolumePrognosis;
import nemmenvironment.PowerPlant;
import nemmenvironment.Region;
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
		private double riskadjustedinterestrate; //Used to calculate the level of risk averness in current trade price in relation to future expected price. 
		private double portfoliocapital;
		private double RAR;						 //Company specific risk adjusted rate used to discount the future price of certificates. 
		
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
		}
		
		//Get methods for the ActiveAgent
		public double getphysicalnetposition() {
			return physicalnetposition;
			}
		public double getRAR() {
			return RAR;
		}
		public double getlasttickproduction() {
			return this.lasttickproduction;
		}
		public double getlasttickdemand() {
			return this.lasttickdemand;
		}
		public GenericStrategy getbeststrategy() {
			return beststrategy;
			}
		public void setbeststrategy(GenericStrategy s) {
			beststrategy = s;
			}
		public ArrayList<GenericStrategy> getallstrategies() {
			return allstrategies;
		}
		
		public void poststmupdate(double certificatessold, double certificatesbought) {
			//Updates the portfoliocapital before the physical position. Also thes method requires that ShortTermMarket is ran, but not Globalvalues.
			portfoliocapital = portfoliocapital + (physicalnetposition * (GlobalValues.currentmarketprice - ShortTermMarket.getcurrentmarketprice())); //is ran before global values are updated, hence global values current market price has the old market price
			physicalnetposition = physicalnetposition + certificatesbought - certificatessold; //Certificates bought and sold are positive numbers.
		}
		//takes in either production (positiv) or demand (negative) amount of certs and updates "lasttickproduction" "lasttickdemand" and physicalposition
		public void addtophysicalposition(double prodordemand) {
			physicalnetposition = physicalnetposition + prodordemand;
			if (prodordemand > 0) { //Implies production
				lasttickproduction = prodordemand;
			}
			else {lasttickdemand = prodordemand;}
		}
		
		public void setphysicalnetposition(double a) {
			physicalnetposition = a;
			}
		public CompanyAnalysisAgent getagentcompanyanalysisagent() {
			return companyanalysisagent;
			}
		
		public GenericUtilityMethod getutilitymethod() {
			return utilitymethod;
		}
		public ArrayList<PowerPlant> getmypowerplants() {
			return myPowerPlants;
		}
		public void addpowerplant(PowerPlant pp) {
			myPowerPlants.add(pp);	
		}
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
		
	//Back to CompanyAgent documentation
	private String companyname;
	private ActiveAgent produceragent;
	private ActiveAgent obligatedpurchaseragent;
	private ActiveAgent traderagent;
	private CompanyAnalysisAgent companyanalysisagent;
	private ArrayList<CompanyDemandShare> myDemandShares = new ArrayList<CompanyDemandShare>();
	private ArrayList<PowerPlant> myPowerPlants = new ArrayList<PowerPlant>();
	
	private double WACC; //Company specific cost of capital
	
	
	//default constructor not in use.
	public CompanyAgent() {
		companyname = "Company " + this.getID();
		produceragent = null;
		obligatedpurchaseragent = null;
		traderagent = null;
		companyanalysisagent = null;}
	
	
	public CompanyAgent(boolean p, boolean op, boolean t) {
		if (p==true) {
			produceragent = new ActiveAgent(1);}
		if (op==true) {
			obligatedpurchaseragent = new ActiveAgent(2);}
		if (t==true) {
			traderagent = new ActiveAgent(3);}
		
		companyname = "Company " + this.getID();
		companyanalysisagent = new CompanyAnalysisAgent();	
		WACC = 0.10;
		
		
		}	
	//CompanyAgents methods
	public ActiveAgent getproduceragent() {
		return produceragent;
	}
	public ActiveAgent getobligatedpurchaseragent() {
		return obligatedpurchaseragent;
	}
	public ActiveAgent gettraderagent() {
		return traderagent;
	}
	public CompanyAnalysisAgent getcompanyanalysisagent() {
		return companyanalysisagent;}
	
	public ArrayList<PowerPlant> getmypowerplants() {
		return myPowerPlants;
	}
	public ArrayList<CompanyDemandShare> getMyDemandShares() {
		return myDemandShares;
	}	
	
}
	
