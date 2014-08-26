/*
 * Version info:
 *     File defining the Company Agent, which consist of one or none of the following active agents: Producer, Obligated Purchaser or Trader. And a companyanalsysagent. 
 *      *     File defining the Active Agents. These agents are a agents of type Producer, Obligated Purchaser or Trader. 
 *     
 *     Last altered data: 20140819
 *     Made by: Karan Kathuria
 */
package nemmagents;

//Import section for other methods
import java.util.ArrayList;
import java.util.List;

import nemmagents.MarketAnalysisAgent;
import nemmagents.ParentAgent;
import nemmstmstrategiestactics.BuyStrategy1;
import nemmstmstrategiestactics.GenericStrategy;
import nemmstmstrategiestactics.GenericUtilityMethod;
import nemmstmstrategiestactics.OPAUtilityMethod;
import nemmstmstrategiestactics.PAUtilityMethod;
import nemmstmstrategiestactics.SellStrategy1;
import nemmstmstrategiestactics.TAUtilityMethod;
import nemmstmstrategiestactics.TradeStrategy1;
import nemmcommons.ParameterWrapper;
import nemmcommons.VolumePrognosis;
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
		private int physicalnetposition;
		
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
				physicalnetposition = 10;
				utilitymethod = new PAUtilityMethod();
				SellStrategy1 sellstrategy = new SellStrategy1();
				allstrategies.add(sellstrategy);
									
			} else if (type == 2) {
				activeagenttypename = "ObligatedPurchaserAgent";
				physicalnetposition = -10;
				utilitymethod = new OPAUtilityMethod();
				BuyStrategy1 buystrategy = new BuyStrategy1();
				allstrategies.add(buystrategy);
				
			} else { //Notice that else is all other added as Trader agents. This is okey for now but should call an expetion later. 
				activeagenttypename = "TraderAgent";
				physicalnetposition = 0;
				utilitymethod = new TAUtilityMethod();
				TradeStrategy1 tradestrategy = new TradeStrategy1();
				allstrategies.add(tradestrategy);
			} 
			beststrategy = allstrategies.get(0); // Choose the first one initially 
		}
		
		//Get methods for the ActiveAgent
		public int getphysicalnetposition() {
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
		
		public void poststmupdate(int certificatessold, int certificatesbought) {
			physicalnetposition = physicalnetposition + certificatesbought - certificatessold; //Certificates bought and sold are positive numbers.
			//totalsold_cp = totalsold_cp + certificatessold;
			}
		public void setphysicalnetposition(int a) {
			physicalnetposition = a;
			}
		public CompanyAnalysisAgent getagentcompanyanalysisagent() {
			return companyanalysisagent;
			}
		
		public GenericUtilityMethod getutilitymethod() {
			return utilitymethod;
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
	private String companyname = "The Company";
	private ActiveAgent produceragent;
	private ActiveAgent obligatedpurchaseragent;
	private ActiveAgent traderagent;
	private CompanyAnalysisAgent companyanalysisagent;
	private ArrayList<CompanyDemandShare> myDemandShares;
	
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
	
	public ArrayList<CompanyDemandShare> getMyDemandShares() {
	return this.myDemandShares;
	}	
	public void AddNewDemandShare(double defaultShare, Region demRegion){
	CompanyDemandShare tempDS = new CompanyDemandShare(defaultShare, demRegion);
	this.myDemandShares.add(tempDS);
	}
	
}
	
