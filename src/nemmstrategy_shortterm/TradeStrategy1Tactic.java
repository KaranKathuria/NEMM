/*
 * Version info:
 *     File defining a Trader tactic used buy tradestrategy1.
 *     
 *     Last altered data: 20140813
 *     Made by: Karan Kathuria
 */
package nemmstrategy_shortterm;

import java.util.ArrayList;

import repast.simphony.engine.environment.RunEnvironment;
import nemmagents.ParentAgent;
import nemmcommons.AllVariables;
import nemmenvironment.TheEnvironment;
import nemmprocesses.ShortTermMarket;
import nemmstrategy_shortterm.GenericTactic.HistoricTacticValue;
import nemmstrategy_shortterm.SellOffer;
import nemmtime.NemmCalendar;


public class TradeStrategy1Tactic extends GenericTactic {
	 
	private double premium;
	private double discount;
	private double maxlongpos;
	private double maxshortpos;
	private double porfoliocapitalexitlimit;
	private SellOffer sellofferone;
	private SellOffer selloffertwo;
	private BuyOffer buyofferone;
	private BuyOffer buyoffertwo;
	private ArrayList<SellOffer> tacticselloffers = new ArrayList<SellOffer>(); //This tactics selloffers. 
	private ArrayList<BuyOffer> tacticbuyoffers = new ArrayList<BuyOffer>(); // This tactics buyoffers.

	//Default constructor
	TradeStrategy1Tactic() {}
	//Used constructor
	TradeStrategy1Tactic(double p, double d, double maxshort, double maxlong) {
		
		premium = p;
		discount = d;
		maxshortpos = maxshort;
		maxlongpos = maxlong;
		porfoliocapitalexitlimit = AllVariables.portfoliocapitalexitlimit;
	}
	
	//The buy and sell offer of these tactics are simply creating two buyoffers and two selloffers according to a random discount, premium and a share. 
	private SellOffer creatSellOfferone(double expectedprice, double physicalposition) {
		SellOffer ret = new SellOffer();
		ret.setsellofferprice((1+premium)*expectedprice); 
		ret.setselloffervol(400);//For now, they always buy or sell 1000 units, but they cannot go short. 
		if (physicalposition < maxshortpos+(400)) {
			ret = null;
		}

		return ret;
		}
	
	private SellOffer creatSellOffertwo(double expectedprice, double physicalposition) {
		SellOffer ret = new SellOffer();
		ret.setsellofferprice((1+(premium/2))*expectedprice); 
		ret.setselloffervol(400);//For now, they always buy or sell 200 units, but they cannot go short. 
		if (physicalposition < maxshortpos+(800)) { //As there are two selloffers.
			ret = null;
		}
		return ret;
		}
	
	private BuyOffer creatBuyOfferone(double expectedprice, double physicalposition) {
		BuyOffer ret = new BuyOffer();
		ret.setbuyoffervol(400); //int in order to only sell integer values of certs.
		ret.setbuyofferprice((1-discount)*expectedprice); 
		if (physicalposition > maxlongpos-(400)) {
		ret = null; //If the physical position is larger than maximum physical position than you cannot by more. 
		} 
		return ret;
		}
	
	private BuyOffer creatBuyOffertwo(double expectedprice, double physicalposition) {
		BuyOffer ret = new BuyOffer();
		ret.setbuyoffervol(400); //int in order to only sell integer values of certs.
		ret.setbuyofferprice((1-(discount/2))*expectedprice); 
		if (physicalposition > maxlongpos-(800)) { //400 as its two.
		ret = null; //If the physical position is larger than maximum physical position than you cannot by more. 
		} return ret;
		}
	
	public void updatetactictradeoffers() {
		double physicalposition = this.getmyStrategy().getmyAgent().getphysicalnetposition();
		double expectedprice = this.getmyStrategy().getmyAgent().getagentcompanyanalysisagent().getmarketanalysisagent().getmarketprognosis().getstpriceexpectation();
		double portfoliocapital = this.getmyStrategy().getmyAgent().getportfoliocapital();
		
		if (portfoliocapital<=0) { //Currently just thowing an expception, but this would have to be handled another way such as creating a new trader.
			throw new IllegalArgumentException("The Trader is bankcrupt!");}
		tacticselloffers.clear();
		tacticbuyoffers.clear();
		sellofferone = creatSellOfferone(expectedprice,physicalposition);
		selloffertwo = creatSellOffertwo(expectedprice,physicalposition);
		buyofferone = creatBuyOfferone(expectedprice,physicalposition);
		buyoffertwo = creatBuyOffertwo(expectedprice,physicalposition);
		
		//Small if that triggers an exit behavor if the traders are about to go bust. 
		if (this.getmyStrategy().getmyAgent().getportfoliocapital() < porfoliocapitalexitlimit) {
			if (physicalposition < 0) {
				sellofferone = null;
				selloffertwo = null;
			}
			else {
				buyofferone = null;
				buyoffertwo = null;
			}
		}
		
		tacticselloffers.add(sellofferone);
		tacticselloffers.add(selloffertwo);
		tacticbuyoffers.add(buyofferone);
		tacticbuyoffers.add(buyoffertwo);
	}
	
	public void addtactichistory() {
		HistoricTacticValue a = new HistoricTacticValue();
		a.tacticsbuyoffers = tacticbuyoffers;
		a.tacticselloffers = tacticselloffers;
		a.tacticutilityscore = tacticutilityscore;
		a.tickID = TheEnvironment.theCalendar.getCurrentTick();
		historictacticvalues.add(a);
	}
	
	// GJB Added 6oct14
	public void UpdateUtilityScore() {
	//Use the agents utilitymethod to calculate each tactics utility
		double temputilityscore = myStrategy.myAgent.getutilitymethod().calculateutility(ShortTermMarket.getcurrentmarketprice(), gettacticbuyoffers(), gettacticselloffers(), ShortTermMarket.getshareofmarignaloffersold(), ShortTermMarket.getshareofmarignalofferbought());
	//Updates that tactics utility
		updatetacticutilityscore(temputilityscore);
	}
	// --End GJB Added

	
	public SellOffer getsellofferone() {
		return sellofferone;}
	
	public SellOffer getselloffertwo() {
		return selloffertwo;}
	
	public BuyOffer getbuyofferone() {
		return buyofferone;}
	
	public BuyOffer getbuyoffertwo() {
		return buyoffertwo;}
	
	public ArrayList<SellOffer> gettacticselloffers() {
		return tacticselloffers;}
	
	public ArrayList<BuyOffer> gettacticbuyoffers() {
		return tacticbuyoffers;}
	
	
}
