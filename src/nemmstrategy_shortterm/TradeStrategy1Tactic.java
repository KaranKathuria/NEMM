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
	private SellOffer creatSellOfferone(double expectedprice, double physicalposition) {
		SellOffer ret = new SellOffer();
		ret.numberofcert = 10; //For now, they always buy or sell 10 units, hence physical position is unused. 
		ret.price = (1+premium)*expectedprice; 
		return ret;
		}
	private BuyOffer creatBuyOfferone(double expectedprice, double physicalposition) {
		BuyOffer ret = new BuyOffer();
		ret.numberofcert = 10; //int in order to only sell integer values of certs.
		ret.price = (1-discount)*expectedprice; 
		return ret;
		}
	
	public void updatetactictradeoffers(double expectedprice, double physicalposition) {
		tacticselloffers.clear();
		tacticbuyoffers.clear();
		sellofferone = creatSellOfferone(expectedprice,physicalposition);
		buyofferone = creatBuyOfferone(expectedprice,physicalposition);
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
