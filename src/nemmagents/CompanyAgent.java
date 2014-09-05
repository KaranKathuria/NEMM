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

// Class definition
public class CompanyAgent extends ParentAgent {
	
	//ActiveAgent defined as inner class
	public class ActiveAgent extends ParentAgent {

		private CompanyAgent companyagent;
		private String activeagenttypename;
		private int activeagenttypecode; //1 = ProducerAgent, 2 = ObligatedPurchaserAgent, 3 = TraderAgent
		private ArrayList<GenericStrategy> allstrategies = new ArrayList<GenericStrategy>();
		private int numberofstrategies;
		private GenericStrategy beststrategy = null;
		private GenericUtilityMethod utilitymethod;
		private double physicalnetposition;
		private double capitalbase;
		
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
				utilitymethod = new PAUtilityMethod();
				SellStrategy1 sellstrategy = new SellStrategy1();
				allstrategies.add(sellstrategy);
				capitalbase = 0;
												
			} else if (type == 2) {
				activeagenttypename = "ObligatedPurchaserAgent";
				physicalnetposition = -1000;
				utilitymethod = new OPAUtilityMethod();
				BuyStrategy1 buystrategy = new BuyStrategy1();
				allstrategies.add(buystrategy);
				capitalbase = 0;
				
			} else { //Notice that else is all other added as Trader agents. This is okey for now but should call an expetion later. 
				activeagenttypename = "TraderAgent";
				physicalnetposition = 0;
				utilitymethod = new TAUtilityMethod();
				TradeStrategy1 tradestrategy = new TradeStrategy1();
				allstrategies.add(tradestrategy);
				capitalbase = 100000; 
			} 
			companyagent = CompanyAgent.this;
			beststrategy = allstrategies.get(0); // Choose the first one initially 
			numberofstrategies = allstrategies.size();
		}
		
		//Get methods for the ActiveAgent
		public double getphysicalnetposition() {
			return physicalnetposition;
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
		
		public void poststmupdate(double certificatessold, double certificatesbought, double ...capitalused) {
			physicalnetposition = physicalnetposition + certificatesbought - certificatessold; //Certificates bought and sold are positive numbers.
			if (capitalused.length > 0) {
			capitalbase = capitalbase + capitalused[0]; }
			}
		
		public void addtophysicalposition(double prodordemand) {
			physicalnetposition = physicalnetposition + prodordemand;
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
		public double getcapitalbase() {
			return capitalbase;
		}
	}
		
		public class CompanyAnalysisAgent extends ParentAgent {
			
			//Nested inner class 
			public class VolumeAnalysisAgent extends ParentAgent {	
				private VolumePrognosis volumeprognosis;
					
				VolumeAnalysisAgent() {
					volumeprognosis = new VolumePrognosis();
				}
				public VolumePrognosis getvolumeprognosis() {
					return volumeprognosis;
				}
			}
			
			private MarketAnalysisAgent marketanalysisagent;
			private VolumeAnalysisAgent volumeanalysisagent;

			CompanyAnalysisAgent() {
				marketanalysisagent = new MarketAnalysisAgent();
				volumeanalysisagent = new VolumeAnalysisAgent();
			}
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
	
	public CompanyAgent() {
		companyname = "zeroagent";
		produceragent = null;
		obligatedpurchaseragent = null;
		traderagent = null;
		companyanalysisagent = null;
	}
	
	public CompanyAgent(boolean p, boolean op, boolean t) {
		if (p==true) {
			produceragent = new ActiveAgent(1);}
//		if (p==false) {
//			produceragent = null;}
		if (op==true) {
			obligatedpurchaseragent = new ActiveAgent(2);}
//		if (op==false) {
//			obligatedpurchaseragent = null;}
		if (t==true) {
			traderagent = new ActiveAgent(3);}
//		if (t==false) {
//			traderagent = null;}
		companyname = "Company " + this.getID();
		companyanalysisagent = new CompanyAnalysisAgent();	
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
	
}
	
