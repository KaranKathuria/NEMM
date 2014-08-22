/*
 * Version info:
 *     File defining a Trader tactic used buy tradestrategy1.
 *     
 *     Last altered data: 20140813
 *     Made by: Karan Kathuria
 */
package nemmstmstrategiestactics;

import java.util.ArrayList;

import repast.simphony.engine.environment.RunEnvironment;
import nemmagents.ParentAgent;
import nemmstmstrategiestactics.SellOffer;
import nemmstmstrategiestactics.SellOffer;


public class TradeStrategy1Tactic {
	 
	private double premium;
	private double discount;
	private SellOffer sellofferone;
	private BuyOffer buyofferone;
	private double tacticscore;
	private ArrayList<SellOffer> tacticselloffers = new ArrayList<SellOffer>(); //This tactics selloffers. 
	private ArrayList<BuyOffer> tacticbuyoffers = new ArrayList<BuyOffer>(); // This tactics buyoffers.
	private ArrayList<HistoricTacticValues> historictacticvalues = new ArrayList<HistoricTacticValues>();//An array of historic values. 
	
	//Object for storing av tradetactics historical values. Differs form those of buy or sell strategies, as they have just buy or sell. 
	private class HistoricTacticValues {
		 
			private ArrayList<SellOffer> tacticselloffers;
			private ArrayList<BuyOffer>  tacticbuyoffers;
			private int month;
	 }

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
	private SellOffer creatSellOfferone(double expectedprice, int physicalposition) {
		SellOffer ret = new SellOffer();
		ret.numberofcert = 10; //For now, they always buy or sell 10 units, hence physical position is unused. 
		ret.price = (1+premium)*expectedprice; 
		return ret;
		}
	private BuyOffer creatBuyOfferone(double expectedprice, int physicalposition) {
		BuyOffer ret = new BuyOffer();
		ret.numberofcert = 10; //int in order to only sell integer values of certs.
		ret.price = (1-discount)*expectedprice; 
		return ret;
		}
	
	public void updatetactictradeoffers(double expectedprice, int physicalposition) {
		tacticselloffers.clear();
		tacticbuyoffers.clear();
		sellofferone = creatSellOfferone(expectedprice,physicalposition);
		buyofferone = creatBuyOfferone(expectedprice,physicalposition);
		tacticselloffers.add(sellofferone);
		tacticbuyoffers.add(buyofferone);
		addtactichistory(); //Updates the historic values. 
	}
	
	public void addtactichistory() {
		HistoricTacticValues a = new HistoricTacticValues();
		a.tacticselloffers = tacticselloffers;
		a.tacticbuyoffers = tacticbuyoffers;
		a.month = (int) RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
	}
	
	public SellOffer getsellofferone() {
		return sellofferone;}
	
	public BuyOffer getbuyofferone() {
		return buyofferone;}
	
	public void settacticscore(double ts) {
		tacticscore = ts;
	}
	public ArrayList<HistoricTacticValues> gethistorictacticvalues() {
		return historictacticvalues;}
	
	
}
