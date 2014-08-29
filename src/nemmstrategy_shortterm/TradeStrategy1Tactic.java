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
import nemmstrategy_shortterm.GenericTactic.HistoricTacticValue;
import nemmstrategy_shortterm.SellOffer;
import nemmtime.NemmCalendar;


public class TradeStrategy1Tactic extends GenericTactic {
	 
	private double premium;
	private double discount;
	private SellOffer sellofferone;
	private BuyOffer buyofferone;
	private ArrayList<SellOffer> tacticselloffers = new ArrayList<SellOffer>(); //This tactics selloffers. 
	private ArrayList<BuyOffer> tacticbuyoffers = new ArrayList<BuyOffer>(); // This tactics buyoffers.


	TradeStrategy1Tactic() {
		premium = 0;
		discount = 0;
		}
	//Used constructor
	TradeStrategy1Tactic(double p, double d) {
		premium = p;
		discount = d;	
	}
	
	//The buy and sell offer of these tactics are extreamly simple and NOT dependent on the capital base of the trader. This must be changed. 
	private SellOffer creatSellOfferone(double expectedprice, double physicalposition, double capitalbase) {
		SellOffer ret = new SellOffer();
		ret.setsellofferprice((1+premium)*expectedprice); 
		ret.setselloffervol(1000);//For now, they always buy or sell 1000 units, but they cannot go short. 
		if (physicalposition < 1000) {
			ret.setselloffervol(physicalposition);
		}
		return ret;
		}
	private BuyOffer creatBuyOfferone(double expectedprice, double physicalposition, double capitalbase) {
		BuyOffer ret = new BuyOffer();
		ret.setbuyoffervol(1000); //int in order to only sell integer values of certs.
		ret.setbuyofferprice((1-discount)*expectedprice); 
		if (capitalbase < 1000*((1+premium)*expectedprice)) {
		ret.setbuyoffervol(capitalbase/(((1-discount)*expectedprice)));} //If the capitalbase is less than can afford 1000 certs at expected price offered, then reduced the amount to what 
		return ret;
		}
	
	public void updatetactictradeoffers(double expectedprice, double physicalposition, double capitalbase) {
		tacticselloffers.clear();
		tacticbuyoffers.clear();
		sellofferone = creatSellOfferone(expectedprice,physicalposition, capitalbase);
		buyofferone = creatBuyOfferone(expectedprice,physicalposition, capitalbase);
		tacticselloffers.add(sellofferone);
		tacticbuyoffers.add(buyofferone);
	}
	
	public void addtactichistory() {
		HistoricTacticValue a = new HistoricTacticValue();
		a.tacticsbuyoffers = tacticbuyoffers;
		a.tacticselloffers = tacticselloffers;
		a.tacticutilityscore = tacticutilityscore;
		a.tickID = NemmCalendar.getCurrentTick();
		historictacticvalues.add(a);
	}
	
	public SellOffer getsellofferone() {
		return sellofferone;}
	
	public BuyOffer getbuyofferone() {
		return buyofferone;}
	
	public ArrayList<SellOffer> gettacticselloffers() {
		return tacticselloffers;}
	
	public ArrayList<BuyOffer> gettacticbuyoffers() {
		return tacticbuyoffers;}
	
	
}
