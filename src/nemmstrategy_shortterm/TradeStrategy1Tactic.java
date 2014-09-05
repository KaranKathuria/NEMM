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
import nemmenvironment.TheEnvironment;
import nemmstrategy_shortterm.GenericTactic.HistoricTacticValue;
import nemmstrategy_shortterm.SellOffer;
import nemmtime.NemmCalendar;


public class TradeStrategy1Tactic extends GenericTactic {
	 
	private double premium;
	private double discount;
	private double maxlongpos;
	private double maxshortpos;
	private SellOffer sellofferone;
	private SellOffer selloffertwo;
	private BuyOffer buyofferone;
	private BuyOffer buyoffertwo;
	private ArrayList<SellOffer> tacticselloffers = new ArrayList<SellOffer>(); //This tactics selloffers. 
	private ArrayList<BuyOffer> tacticbuyoffers = new ArrayList<BuyOffer>(); // This tactics buyoffers.


	TradeStrategy1Tactic() {
		premium = 0;
		discount = 0;
		maxlongpos = 0;
		maxshortpos = 0;
		}
	//Used constructor
	TradeStrategy1Tactic(double p, double d, double maxshort, double maxlong) {
		
		premium = p;
		discount = d;
		maxshortpos = maxshort;
		maxlongpos = maxlong;
	}
	
	//The buy and sell offer of these tactics are simply creating two buyoffers and two selloffers according to a random discount, premium and a share. 
	private SellOffer creatSellOfferone(double expectedprice, double physicalposition, double capitalbase) {
		SellOffer ret = new SellOffer();
		ret.setsellofferprice((1+premium)*expectedprice); 
		ret.setselloffervol(100);//For now, they always buy or sell 1000 units, but they cannot go short. 
		if (physicalposition < maxshortpos+(100)) {
			ret = null;
		}

		return ret;
		}
	
	private SellOffer creatSellOffertwo(double expectedprice, double physicalposition, double capitalbase) {
		SellOffer ret = new SellOffer();
		ret.setsellofferprice((1+(premium/2))*expectedprice); 
		ret.setselloffervol(100);//For now, they always buy or sell 1000 units, but they cannot go short. 
		if (physicalposition < maxshortpos+(200)) { //As there are two selloffers.
			ret = null;
		}
		return ret;
		}
	
	private BuyOffer creatBuyOfferone(double expectedprice, double physicalposition, double capitalbase) {
		BuyOffer ret = new BuyOffer();
		ret.setbuyoffervol(100); //int in order to only sell integer values of certs.
		ret.setbuyofferprice((1-discount)*expectedprice); 
		if (physicalposition > maxlongpos-(100)) {
		ret = null; //If the physical position is larger than maximum physical position than you cannot by more. 
		} 
		return ret;
		}
	
	private BuyOffer creatBuyOffertwo(double expectedprice, double physicalposition, double capitalbase) {
		BuyOffer ret = new BuyOffer();
		ret.setbuyoffervol(100); //int in order to only sell integer values of certs.
		ret.setbuyofferprice((1-discount)*expectedprice); 
		if (physicalposition > maxlongpos-(200)) { //200 as its two.
		ret = null; //If the physical position is larger than maximum physical position than you cannot by more. 
		} return ret;
		}
	
	public void updatetactictradeoffers(double expectedprice, double physicalposition, double ...capitalbase) {
		if (capitalbase.length<1) {
			throw new IllegalArgumentException("Now given capitalbase for tradingstrategy");}
		tacticselloffers.clear();
		tacticbuyoffers.clear();
		sellofferone = creatSellOfferone(expectedprice,physicalposition, capitalbase[0]);
		selloffertwo = creatSellOffertwo(expectedprice,physicalposition, capitalbase[0]);
		buyofferone = creatBuyOfferone(expectedprice,physicalposition, capitalbase[0]);
		buyoffertwo = creatBuyOffertwo(expectedprice,physicalposition, capitalbase[0]);
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
