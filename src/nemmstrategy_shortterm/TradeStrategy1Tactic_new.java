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
import nemmtime.NemmCalendar;


public class TradeStrategy1Tactic_new extends GenericTactic {
	 
	private double premium;
	private double discount;
	private double maxlongpos;
	private double maxshortpos;
	private BidOffer sellofferone;
	private BidOffer buyofferone;
	//Added 2015. Borrowed from Buystrategy1tactics
	private double paramRestVolPriceMult;

	private ArrayList<BidOffer> tacticselloffers = new ArrayList<BidOffer>(); //This tactics selloffers. 
	private ArrayList<BidOffer> tacticbuyoffers = new ArrayList<BidOffer>(); // This tactics buyoffers.

	//Default constructor
	TradeStrategy1Tactic_new() {}
	//Used constructor
	TradeStrategy1Tactic_new(double p, double d, double maxshort, double maxlong) {
		
		premium = p;
		discount = d;
		maxshortpos = maxshort;
		maxlongpos = maxlong;
	}
	
	
	//Updated 20150630 KK:
	private BidOffer creatbuyofferone(double expectedprice) {
		BidOffer ret = new BidOffer();
		
		//Simple size of the offer is "hardcoded"
		ret.setCertVolume(5000); //rest of the monthly production sold at expected price.			
		ret.setPrice(Math.min(expectedprice*paramRestVolPriceMult, floorroofprice)); //Most likely that the second offer is at at pricemultiplier. Hence they buy what they dont must, at a pricemultiplier.
		return ret;
	}
	
	private BidOffer createRestVolOffer(double expectedprice) {
		BidOffer ret = new BidOffer();
			
			ret.setCertVolume(5000); 
		ret.setPrice(Math.max(expectedprice*paramRestVolPriceMult, floorroofprice)); 
		return ret;
	}
	
	
	public void updatetactictradeoffers() {
		double expectedprice = this.getmyStrategy().getmyAgent().getagentcompanyanalysisagent().getmarketanalysisagent().getmarketprognosis().getstpriceexpectation();
		

		tacticselloffers.clear();
		tacticbuyoffers.clear();
		sellofferone = createRestVolOffer(expectedprice);
		buyofferone = creatbuyofferone(expectedprice);
		
		tacticselloffers.add(sellofferone);
		tacticbuyoffers.add(buyofferone);
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
	
	public BidOffer getbuyofferone() {
		return buyofferone;}
	
	public ArrayList<BidOffer> gettacticselloffers() {
		return tacticselloffers;}
	
	public ArrayList<BidOffer> gettacticbuyoffers() {
		return tacticbuyoffers;}
	
	
}
