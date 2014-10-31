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
	private BidOffer sellofferone;
	private BidOffer selloffertwo;
	private BidOffer buyofferone;
	private BidOffer buyoffertwo;
	private ArrayList<BidOffer> tacticselloffers = new ArrayList<BidOffer>(); //This tactics selloffers. 
	private ArrayList<BidOffer> tacticbuyoffers = new ArrayList<BidOffer>(); // This tactics buyoffers.

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
	private BidOffer creatSellOfferone(double expectedprice, double physicalposition) {
		BidOffer ret = new BidOffer();
		ret.setPrice((1+premium)*expectedprice); 
		ret.setCertVolume(400.0);//For now, they always buy or sell 1000 units, but they cannot go short. 
		if (physicalposition < maxshortpos+(400)) {
			ret.setCertVolume(0.0);;
		}

		return ret;
		}
	
	private BidOffer creatSellOffertwo(double expectedprice, double physicalposition) {
		BidOffer ret = new BidOffer();
		ret.setPrice((1+(premium/2))*expectedprice); 
		ret.setCertVolume(400);//For now, they always buy or sell 200 units, but they cannot go short. 
		if (physicalposition < maxshortpos+(800)) { //As there are two selloffers.
			ret.setCertVolume(0.0);;
		}
		return ret;
		}
	
	private BidOffer creatBuyOfferone(double expectedprice, double physicalposition) {
		BidOffer ret = new BidOffer();
		ret.setCertVolume(400); //int in order to only sell integer values of certs.
		ret.setPrice((1-discount)*expectedprice); 
		if (physicalposition > maxlongpos-(400)) {
		ret.setCertVolume(0.0);; //If the physical position is larger than maximum physical position than you cannot by more. 
		} 
		return ret;
		}
	
	private BidOffer creatBuyOffertwo(double expectedprice, double physicalposition) {
		BidOffer ret = new BidOffer();
		ret.setCertVolume(400); //int in order to only sell integer values of certs.
		ret.setPrice((1-(discount/2))*expectedprice); 
		if (physicalposition > maxlongpos-(800)) { //400 as its two.
		ret.setCertVolume(0.0); //If the physical position is larger than maximum physical position than you cannot by more. 
		} return ret;
		}
	
	public void updatetactictradeoffers() {
		double physicalposition = this.getmyStrategy().getmyAgent().getphysicalnetposition();
		double expectedprice = this.getmyStrategy().getmyAgent().getagentcompanyanalysisagent().getmarketanalysisagent().getmarketprognosis().getstpriceexpectation();
		double portfoliocapital = this.getmyStrategy().getmyAgent().getportfoliocapital();
		
		if (portfoliocapital<=0) { //Currently just thowing an expception, but this would have to be handled another way such as creating a new trader.
			throw new IllegalArgumentException("The Trader is bankrupt!");}
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
	
	public void addTacticValuesToHistory() {
		HistoricTacticValue a = new HistoricTacticValue();
		a.tacticsbuyoffers = tacticbuyoffers;
		a.tacticselloffers = tacticselloffers;
		a.tacticutilityscore = tacticutilityscore;
		a.tickID = TheEnvironment.theCalendar.getCurrentTick();
		historictacticvalues.add(a);
	}
	
	// GJB Added 6oct14
	public void calcUtilityForCurrentTick() {
	//Use the agents utilitymethod to calculate each tactics utility
		double temputilityscore = myStrategy.myAgent.getutilitymethod().calculateutility(ShortTermMarket.getcurrentmarketprice(), gettacticbuyoffers(), gettacticselloffers(), ShortTermMarket.getshareofmarignaloffersold(), ShortTermMarket.getshareofmarignalofferbought());
	//Updates that tactics utility
		setUtilityScore(temputilityscore);
	}
	// --End GJB Added

	
	public BidOffer getsellofferone() {
		return sellofferone;}
	
	public BidOffer getselloffertwo() {
		return selloffertwo;}
	
	public BidOffer getbuyofferone() {
		return buyofferone;}
	
	public BidOffer getbuyoffertwo() {
		return buyoffertwo;}
	
	public ArrayList<BidOffer> gettacticselloffers() {
		return tacticselloffers;}
	
	public ArrayList<BidOffer> gettacticbuyoffers() {
		return tacticbuyoffers;}
	
	
}
