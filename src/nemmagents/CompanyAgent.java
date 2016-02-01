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
// opmment
import java.util.ArrayList;
import repast.simphony.random.RandomHelper;
import nemmagents.MarketAnalysisAgent;
import nemmagents.ParentAgent;
import nemmprocesses.ShortTermMarket;
import nemmstrategy_shortterm.BidOffer;
import nemmstrategy_shortterm.GenericUtilityMethod;
import nemmstrategy_shortterm.OPAUtilityMethod;
import nemmstrategy_shortterm.PAUtilityMethod;
import nemmstrategy_shortterm.BuyStrategy1;
import nemmstrategy_shortterm.GenericStrategy;
import nemmstrategy_shortterm.SellStrategy1;
import nemmstrategy_shortterm.TradeStrategy1_new;
import nemmstrategy_shortterm.TraderBuyUtilityMethod;
import nemmstrategy_shortterm.TraderSellUtilityMethod;
import nemmcommons.AllVariables;
import nemmcommons.ParameterWrapper;
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
		private GenericUtilityMethod utilitymethod_additional = null;	//Used for the sell side of traders (2015)
		private double physicalnetposition;
		private double lasttickproduction;
		private double lasttickdemand;
		private double portfoliocapital;
		private int sizecode;			//Code value indicationg if the AA has few=1, normal=2 og alot=3 of activities (powerplants or demandshares) in the regions the company of that AA participates in
		private double RAR;				//Agentspecific risk adjusted rate used to discount the future price of certificates to floor/roof prices in STM bidding. Not to be confused with the company specific RRR which is the companies investmentcriteria.
		// ---- GJB Added
		private int numTicksToEmptyPosition; // number of ticks the agent will try to empty its position over
		
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
				lasttickproduction = 0;
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
				
				double temp = 0;
				TheEnvironment.produceragentscounter = TheEnvironment.produceragentscounter +1;
				temp = TheEnvironment.produceragentscounter;
				double dPhysRnd = temp/ParameterWrapper.getproduceragentsnumber(); //RandomHelper.nextDouble();
				int a = 3;
				
				for (int stratID=AllVariables.numPAExitStrategies-1; stratID>=0; stratID--){
					if (dPhysRnd <= AllVariables.cutoffPAExit[stratID]) {
						numTicksToEmptyPosition = AllVariables.numTicksPAExit[stratID];
					}
				}
				physicalnetposition = 10*numTicksToEmptyPosition*numTicksToEmptyPosition;				//20151126 KK: Multiplied with HH to distrribute bank to thos with long HH. this is more like the real world. Times number of agents this equals initial bank, but as this is scaled anyhow it matters little.
				sizecode = 2;	//For all


				// ---- end GJB Added
												
			} if (type == 2) {
				activeagenttypename = "ObligatedPurchaserAgent";
				lasttickdemand = 0;
				// here we need a way to have different utilities
				// currently hard coded to the default
				utilitymethod = new OPAUtilityMethod(nemmcommons.AllVariables.utilityDefault_OP);
				BuyStrategy1 buystrategy = new BuyStrategy1();
				buystrategy.setmyAgent(ActiveAgent.this);
				allstrategies.add(buystrategy);
				portfoliocapital = 0;
				
				//KK Added 20151201
				double temp = 0;
				TheEnvironment.obligatedpurchaseragentcounter = TheEnvironment.obligatedpurchaseragentcounter +1;
				temp = TheEnvironment.obligatedpurchaseragentcounter;
				double dPhysRnd = temp/ParameterWrapper.getobligatedpurchaseragentsnumber(); 

				for (int stratID=AllVariables.numOPExitStrategies-1; stratID>=0; stratID--){
					if (dPhysRnd <= AllVariables.cutoffOPExit[stratID]) {
						numTicksToEmptyPosition = AllVariables.numTicksOPExit[stratID];
					}
				}
				physicalnetposition = 1000;				//20151126 KK:Times number of agents this equals initial bank, but as this is scaled anyhow it matters little. Unequal PA, this volume is not distributed to those buying less (rather the opposit).
				sizecode = 2;	//For all

				// ---- end GJB Added
				
			} if (type == 3) { //Notice that else is all other added as Trader agents. This is okey for now but should call an expetion later. 
				activeagenttypename = "TraderAgent";
				physicalnetposition = 0;
				// here we need a way to have different utilities
				// currently hard coded to the default
				utilitymethod = new TraderBuyUtilityMethod(nemmcommons.AllVariables.utilityDefault_OP);
				utilitymethod_additional = new TraderSellUtilityMethod(nemmcommons.AllVariables.utilityDefault_PA);
				this.utilitymethod_additional.setmyAgent(ActiveAgent.this);
				
				TradeStrategy1_new tradestrategy = new TradeStrategy1_new();
				tradestrategy.setmyAgent(ActiveAgent.this);
				allstrategies.add(tradestrategy);
				
				
				double dPhysRnd = RandomHelper.nextDouble();
				for (int stratID=AllVariables.numTExitStrategies-1; stratID>=0; stratID--){
					if (dPhysRnd <= AllVariables.cutoffTExit[stratID]) {
						numTicksToEmptyPosition = AllVariables.numTicksTExit[stratID];
					}
				}
				sizecode = 2;	//For all

						
			} 
			if (type == 4) {	//20160128 KK Pensionfund producer
				activeagenttypename = "PensionProducerAgent";
				lasttickproduction = 0;
				// here we need a way to have different utilities
				// currently hard coded to the default
				utilitymethod = new PAUtilityMethod(nemmcommons.AllVariables.utilityDefault_PA);
				SellStrategy1 sellstrategy = new SellStrategy1();
				sellstrategy.setmyAgent(ActiveAgent.this);
				allstrategies.add(sellstrategy);
				portfoliocapital = 0;

				double temp = 0;
				TheEnvironment.produceragentscounter = TheEnvironment.produceragentscounter +1;
				numTicksToEmptyPosition = AllVariables.holdinghorizontpensionproducer;
				
				physicalnetposition = 0;				
				sizecode = 1;	//Indication that this producer does NOT get any powerplants initially.
				
			}
			
			RAR = RandomHelper.nextDoubleFromTo(AllVariables.RAR[0], AllVariables.RAR[1]);	

			companyagent = CompanyAgent.this;
			this.utilitymethod.setmyAgent(ActiveAgent.this);
			beststrategy = allstrategies.get(0); // Choose the first one initially 
			numberofstrategies = allstrategies.size();
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
		
		public void scalephysicalposition(double scaleratio) {
			physicalnetposition = physicalnetposition*scaleratio;
		}
		public void setphysicalnetposition(double a) {physicalnetposition = a;}
		public CompanyAnalysisAgent getagentcompanyanalysisagent() {return companyanalysisagent;}
		public GenericUtilityMethod getutilitymethod() {return utilitymethod;}
		public GenericUtilityMethod getutilitymethod_additional() {return utilitymethod_additional;}

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
			int a = 3;
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
					 if (m==null) {
						 int tmp = 1;
						 tmp = 2;
								 
					 }
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

					if((PP.getStartTick() <= curTick) && (PP.getendtick() > curTick)) {
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
		private int investmentdecisiontype;				//Code defining what kind of certprice the agent uses to evaluate the investment decision. (1,2,3,4)
		private double fundamentaleasefactor;			//for Fundamental agents, this is 1, FOr price agents this is either large or varies between 1-2.
		private double priceeasefactor;					//for price agents, this is 1, For fundamental agents this is either large or varies between 1-2.
		private int relativerank_norway;						//This is the relative rank of the developer i terms of the owner Company Agents wacc. 1 is thus implying that the developer has the lowest wacc of all.
		private int relativerank_sweden;						//This is the relative rank of the developer i terms of the owner Company Agents wacc. 1 is thus implying that the developer has the lowest wacc of all.
		private int cvvaluehorizont; // number of ticks the agent will try to empty its position over

		
		//Endogenous variables 
		private double capacitydevorundrconstr;
		private int numprojectstrashed;
		private int numprojectsfinished;
		private int numprojectsunderconstr;
		private int numprojectsawaitingid;
		private int numprojectsinprocess;
		private int numprojectsidentyfied;
		
		public DeveloperAgent(int type) {
			if (type==1) {
			companyagent = CompanyAgent.this;
			sizecode = 2;								//By default all developmentagents have normal amount of activety. (1= few, 2=normal, 3=alot) used in intial distribution of projects.
			projectprocessandidylimit = sizecode*AllVariables.preprojectandidentifyconstraint;	//func of sizecode: = 3*sizecode. Problem as sizecode only defines size in one region. whereas //Max number of project getting identifyed or in proecss.
			constructionlimit = AllVariables.constructionconstraints;					//Max number of projects getting in from moving to construction. 
			totalcapacitylimit = 100000000;
			cvvaluehorizont = AllVariables.cvvaluehorizont;
			if (AllVariables.isbacktest) {
				constructionlimit = constructionlimit*AllVariables.backtesteaseconstruction+2;
				totalcapacitylimit = totalcapacitylimit*AllVariables.backtesteaseconstruction;
			}
			if (this.getregionpartcode() == 1) { //Norway
			//1=invest based on long term price of certs (pure Fundamental based), 2=Invest on pure fundamental and some price, 3=Invest based on curren cert price and some fundamental, 4=Invest based on current cert price for two years
			double investdecrand = RandomHelper.nextDoubleFromTo(0.0, 1.0);
			double a = investdecrand;
					if (investdecrand <= AllVariables.developerinvestmenttypedistribution_Norway[0]) {
						investmentdecisiontype = 1;
						fundamentaleasefactor = AllVariables.fundamentalfundamentaleasefactordistribution_Norway;
						priceeasefactor = 5000;
						}
					if (investdecrand > AllVariables.developerinvestmenttypedistribution_Norway[0] && investdecrand <= AllVariables.developerinvestmenttypedistribution_Norway[1]) {
						investmentdecisiontype = 2;
						fundamentaleasefactor = AllVariables.fundamentalfundamentaleasefactordistribution_Norway;
						priceeasefactor = RandomHelper.nextDoubleFromTo(AllVariables.developerinvestmentpriceeasefactordistribution_Norway[0], AllVariables.developerinvestmentpriceeasefactordistribution_Norway[1]);
						}
					if (investdecrand > AllVariables.developerinvestmenttypedistribution_Norway[1] && investdecrand <= AllVariables.developerinvestmenttypedistribution_Norway[2]) {
						investmentdecisiontype = 3;
						fundamentaleasefactor = RandomHelper.nextDoubleFromTo(AllVariables.developerinvestmentfundamentaleasefactordistribution[0], AllVariables.developerinvestmentfundamentaleasefactordistribution[1]);
						priceeasefactor = AllVariables.pricedeveloperspriceeasefactordistribution;
						}
					if (investdecrand > AllVariables.developerinvestmenttypedistribution_Norway[2]){
						investmentdecisiontype = 4;
						priceeasefactor = AllVariables.pricedeveloperspriceeasefactordistribution;			
						fundamentaleasefactor = 5000;		
						}
			}
			if (this.getregionpartcode() == 3) { //Sweden
			//1=invest based on long term price of certs (pure Fundamental based), 2=Invest on pure fundamental and some price, 3=Invest based on curren cert price and some fundamental, 4=Invest based on current cert price for two years
			double investdecrand = RandomHelper.nextDoubleFromTo(0.0, 1.0);
			double a = investdecrand;
					if (investdecrand <= AllVariables.developerinvestmenttypedistribution_Sweden[0]) {
						investmentdecisiontype = 1;
						fundamentaleasefactor = AllVariables.fundamentalfundamentaleasefactordistribution_Sweden;
						priceeasefactor = 5000;
						}
					if (investdecrand > AllVariables.developerinvestmenttypedistribution_Sweden[0] && investdecrand <= AllVariables.developerinvestmenttypedistribution_Sweden[1]) {
						investmentdecisiontype = 2;
						fundamentaleasefactor = AllVariables.fundamentalfundamentaleasefactordistribution_Sweden;
						priceeasefactor = RandomHelper.nextDoubleFromTo(AllVariables.developerinvestmentpriceeasefactordistribution_Sweden[0], AllVariables.developerinvestmentpriceeasefactordistribution_Sweden[1]);
						}
					if (investdecrand > AllVariables.developerinvestmenttypedistribution_Sweden[1] && investdecrand <= AllVariables.developerinvestmenttypedistribution_Sweden[2]) {
						investmentdecisiontype = 3;
						fundamentaleasefactor = RandomHelper.nextDoubleFromTo(AllVariables.developerinvestmentfundamentaleasefactordistribution[0], AllVariables.developerinvestmentfundamentaleasefactordistribution[1]);
						priceeasefactor = 1;
						}
					if (investdecrand > AllVariables.developerinvestmenttypedistribution_Sweden[2]){
						investmentdecisiontype = 4;
						priceeasefactor = 1;			
						fundamentaleasefactor = 5000;		
						}
			}
			if (this.getregionpartcode() == 2) { //both countries
					//1=invest based on long term price of certs (pure Fundamental based), 2=Invest on pure fundamental and some price, 3=Invest based on curren cert price and some fundamental, 4=Invest based on current cert price for two years
					double investdecrand = RandomHelper.nextDoubleFromTo(0.0, AllVariables.developerinvestmenttypedistribution[2]);
					double a = investdecrand;
							if (investdecrand <= AllVariables.developerinvestmenttypedistribution[0]) {
								investmentdecisiontype = 1;
								fundamentaleasefactor = AllVariables.fundamentalfundamentaleasefactordistribution;
								priceeasefactor = 5000;
								}
							if (investdecrand > AllVariables.developerinvestmenttypedistribution[0] && investdecrand <= AllVariables.developerinvestmenttypedistribution[1]) {
								investmentdecisiontype = 2;
								fundamentaleasefactor = AllVariables.fundamentalfundamentaleasefactordistribution;
								priceeasefactor = RandomHelper.nextDoubleFromTo(AllVariables.developerinvestmentpriceeasefactordistribution[0], AllVariables.developerinvestmentpriceeasefactordistribution[1]);
								}
							if (investdecrand > AllVariables.developerinvestmenttypedistribution[1] && investdecrand <= AllVariables.developerinvestmenttypedistribution[2]) {
								investmentdecisiontype = 3;
								fundamentaleasefactor = RandomHelper.nextDoubleFromTo(AllVariables.developerinvestmentfundamentaleasefactordistribution[0], AllVariables.developerinvestmentfundamentaleasefactordistribution[1]);
								priceeasefactor = 1;
								}
							if (investdecrand > AllVariables.developerinvestmenttypedistribution[2]){
								investmentdecisiontype = 4;
								priceeasefactor = 1;			
								fundamentaleasefactor = 5000;		
								}
					}
						
			}
			if (type==2) { //Pension developer 
				companyagent = CompanyAgent.this;
				sizecode = 1;							//0 initial projects
				projectprocessandidylimit = sizecode*AllVariables.preprojectandidentifyconstraint;	//func of sizecode: = 3*sizecode. Problem as sizecode only defines size in one region. whereas //Max number of project getting identifyed or in proecss.
				constructionlimit = AllVariables.pfconstructionconstraints;					//Max number of projects getting in from moving to construction. 
				totalcapacitylimit = 100000000;
				cvvaluehorizont = AllVariables.cvvaluehorizont;
				investmentdecisiontype = 3;
				fundamentaleasefactor = RandomHelper.nextDoubleFromTo(AllVariables.developerinvestmentfundamentaleasefactordistribution[0], AllVariables.developerinvestmentfundamentaleasefactordistribution[1]);
				priceeasefactor = 1;
				
			}
				
			if (this.getregionpartcode()>3 || this.getregionpartcode() < 1) {
				throw new IllegalArgumentException("THis is no region");}
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
		
		public boolean iscvvaluesufficient(double cutoffcertprice_price, double certpriceneeded) {
			boolean ret = false;
			double tempcurrentprice = (cutoffcertprice_price/this.priceeasefactor); //Backengineerd to get current price
			
			double temp = this.getmycompany().getcompanyanalysisagent().getmarketanalysisagent().getCertValueDeveloper();
			if (tempcurrentprice < (this.getmycompany().getcompanyanalysisagent().getmarketanalysisagent().getCertValueDeveloper()*this.priceeasefactor)
					|| certpriceneeded < (this.getmycompany().getcompanyanalysisagent().getmarketanalysisagent().getCertValueDeveloper() ) ) {	// *this.priceeasefactor) Only true if CV value is larger than curren price. Kan vurdere å inkludere priceeasen her.
				ret = true;
			}
			int a = 3;
			return ret;
		}
		
		//Add, set and get methods for DeveloperAgent
		public int getcvvaluehorizont() {return cvvaluehorizont;}
		public int getsizecode() {return sizecode;}
		public int getregionpartcode() {return this.companyagent.regionpartcode;}
		public CompanyAgent getmycompany() {return this.companyagent;}
		public void addproject(PowerPlant PP) {myProjects.add(PP);	}
		public ArrayList<PowerPlant> getmyprojects() {return myProjects;}
		public double gettotalcapacitylimit() {return totalcapacitylimit;}
		public int getconstructionlimit() {return constructionlimit;}
		public int getprojectprocessandidylimit() {return projectprocessandidylimit;}
		public double getcapacitydevorundrconstr() {return capacitydevorundrconstr;}
		public int getnumprojectsunderconstr() {return numprojectsunderconstr;}
		public int getnumprojectsinprocess() {return numprojectsinprocess;}
		public int getinvestmentdecisiontype() {return investmentdecisiontype;}
		public double getfundamentaleasefactor() {return fundamentaleasefactor;}
		public double getpriceeasefactor() {return priceeasefactor;}
		public int getrelativerank_norway() {return relativerank_norway;}
		public void setrelativerank_norway(int a) {this.relativerank_norway = a;}
		public int getrelativerank_sweden() {return relativerank_sweden;}
		public void setrelativerank_sweden(int a) {this.relativerank_sweden = a;}
		
		
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
		private CompanyAgent myCompany;
	
		//Default constructor calling the respective objects default constructors
		CompanyAnalysisAgent() {
			marketanalysisagent = new MarketAnalysisAgent();
			marketanalysisagent.setMyCAAgent(this);
			volumeanalysisagent = new VolumeAnalysisAgent();
		}
		
	//Sets and gets
		public MarketAnalysisAgent getmarketanalysisagent() {
			return marketanalysisagent;
		}
		public VolumeAnalysisAgent getvolumeanalysisagent() {
			return volumeanalysisagent;
		}
		public void setMyCompany(CompanyAgent bigDaddy) {
			myCompany = bigDaddy;
		}
		public CompanyAgent getMyCompany() {
			return myCompany;
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
	private ArrayList<PowerPlant> myProjects = new ArrayList<PowerPlant>();							//List of all projects owned by the company regardless of project stage. Trashed projects are not included.
	private int regionpartcode;																		//Code indication which region the Company is active in. 1=Norway (region1), 2=Norway and Sweden, 3=Sweden. This number has a connection with AA sizecode, hence a company with big size and reigonpartcode=2 is large in both regions.
	private double investmentRRR; 									//Correct name should be investmentRRR corrector. This factor is mulitplied with the specificRRR. Company specific cost of capital adjuster (this times the project specific is the cost of capital) Defined before tax. 
																	//Note that this is NEVER used as the Fundamental builders use project specific RRR to determine buildout, but by price based investors as their investment decision depends on their individual wacc (RRR). 
	private double earlystageRRR;																	//Required rate off return for project cut-off on earl-stage projects. Defined before tax. Higher than InvestmentRRR as the risk is higher.
	
	//default constructor not in use.
	public CompanyAgent() {
		companyname = "Company " + this.getID();
		produceragent = null;
		obligatedpurchaseragent = null;
		traderagent = null;
		companyanalysisagent = null;
		developeragent = null;}
	
	
	public CompanyAgent(boolean p, boolean op, boolean t) {
		
		//20151117 Add something that makes a split if the agent has a developer agent. This should only be either in sweden or norway.1 = Norway 3 = Sweden, 2 = both
		double regionpartrand = RandomHelper.nextDoubleFromTo(0.0, AllVariables.companyregiondistribution[2]);
		int a =2;
				if (regionpartrand <= AllVariables.companyregiondistribution[0]) {
					regionpartcode = 1;
					}
				if (regionpartrand > AllVariables.companyregiondistribution[0] && regionpartrand <= AllVariables.companyregiondistribution[1]) {
					regionpartcode = 3;
					}
				if (regionpartrand > AllVariables.companyregiondistribution[1] && regionpartrand <= AllVariables.companyregiondistribution[2]) {
					regionpartcode = 2;
					}
		
		if (p==true) {
			produceragent = new ActiveAgent(1);
			developeragent = new DeveloperAgent(1);}													//By default all and just all companies with PA have a DA.
		if (op==true) {
			regionpartcode = 2; //set this agent to both conutries as for the demand sake country dividing is not neede.
			obligatedpurchaseragent = new ActiveAgent(2);}
		if (t==true) {
			regionpartcode = 2; //set this agent to both conutries as for the demand sake country dividing is not neede.
			traderagent = new ActiveAgent(3);}
		
		companyname = "Company " + this.getID();													
		companyanalysisagent = new CompanyAnalysisAgent();	
		companyanalysisagent.setMyCompany(this);
		investmentRRR = RandomHelper.nextDoubleFromTo(AllVariables.minInvestRRRAdjustFactor, AllVariables.maxInvestRRRAdjustFactor);				//Correct name should be investmentRRR corrector. This factor is mulitplied with the specificRRR.
		earlystageRRR = investmentRRR + AllVariables.earlystageInvestRRRAdjustFactor;																//Correct name should be earlystageRRR corrector. This factor is mulitplied with the specificRRR.
		//regionpartcode = 2;		
	}	//By default, all companies are active in both countries. (1=Norway, 3 = Sweden, 2 = both)
	
		public CompanyAgent(double RRR) { //pensionfund
			regionpartcode = AllVariables.pfregioncode;		//1 = norway, 2 = both, 3 = sweden
			produceragent = new ActiveAgent(4);
			developeragent = new DeveloperAgent(2);	
			companyname = "Company " + this.getID();													
			companyanalysisagent = new CompanyAnalysisAgent();	
			companyanalysisagent.setMyCompany(this);
			investmentRRR = RRR;			//Correct name should be investmentRRR corrector. This factor is mulitplied with the specificRRR.
			earlystageRRR = investmentRRR;
			
			//By default all and just all companies with PA have a DA.
			
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
	public Double getInvestmentRRR() {return investmentRRR;}
	public double getearlystageRRR() {return earlystageRRR;}
	public double getphysicalnetposition() {double temp = 0;
	if (produceragent != null) {
		temp = temp + produceragent.physicalnetposition;}
	if (obligatedpurchaseragent != null) {
		temp = temp + obligatedpurchaseragent.physicalnetposition;}
	if (traderagent != null) {
		temp = temp + traderagent.physicalnetposition;}
	return temp;}
	public int getnumberofpowerplants() {return myPowerPlants.size();}
	public int getnumberofprojects() {return myProjects.size();}
	public String getname() {return this.companyname;}
	}
			

