/*
 * Version info:
 *      File defining a generic tactic for buystrategy1
 *     
 *     Last altered data: 20140811
 *     Made by: Karan Kathuria
 */
package nemmstmstrategiestactics;

import java.util.ArrayList;
import repast.simphony.engine.environment.RunEnvironment;
import nemmagents.ParentAgent;
import nemmstmstrategiestactics.BuyOffer;
import nemmstmstrategiestactics.SellOffer;


public class BuyStrategy1Tactic {
	 
	private double shareboughtatdiscount;
	private double discount;
	private BuyOffer buyofferone;
	private BuyOffer buyoffertwo;
	private double tacticutilityscore;
	private ArrayList<BuyOffer> tacticbuyoffers = new ArrayList<BuyOffer>();
	private ArrayList<HistoricTacticValues> historictacticvalues = new ArrayList<HistoricTacticValues>();
	
	
	private class HistoricTacticValues {
		 
			private ArrayList<BuyOffer> tacticsbuyoffers; //with fixed length given as a parameter. 
			private int month;
	 }

	BuyStrategy1Tactic() {
		shareboughtatdiscount = 0;
		discount = 0;
		}
	//Used constructor
	BuyStrategy1Tactic(double sbd, double d) {
		shareboughtatdiscount = sbd;
		discount = d;	
	}
	
	private BuyOffer creatBuyOfferone(double expectedprice, int physicalposition) {
		BuyOffer ret = new BuyOffer();
		ret.numberofcert = (int) (shareboughtatdiscount*(-physicalposition)); //-As the phisical position of buyer would in most cases be negative, but as the offer only has positive numbers. 
		ret.price = (1-discount)*expectedprice;
		return ret;
		}
	

	private BuyOffer creatBuyOffertwo(double expectedprice, int physicalposition) {
		BuyOffer ret = new BuyOffer();
		ret.numberofcert = (-physicalposition) -( (int) (shareboughtatdiscount*(-physicalposition))); //rest of the monthly production bought at expected price.
		ret.price = expectedprice;
		return ret;
		}
	
	public void updatetacticbuyoffers(double expectedprice, int physicalposition) {
		if (physicalposition > 0){
			physicalposition = 0;} //To ensure that we dont get crazy bids.  
		tacticbuyoffers.clear();
		buyofferone = creatBuyOfferone(expectedprice,physicalposition);
		buyoffertwo = creatBuyOffertwo(expectedprice,physicalposition);
		tacticbuyoffers.add(buyofferone);
		tacticbuyoffers.add(buyoffertwo);
		addtactichistory();
	}
	
	public void addtactichistory() {
		HistoricTacticValues a = new HistoricTacticValues();
		a.tacticsbuyoffers = tacticbuyoffers;
		a.month = (int) RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
	}
	
	public BuyOffer getbuyofferone() {
		return buyofferone;}
	
	public BuyOffer getbuyoffertwo() {
		return buyoffertwo;}
	
	public ArrayList<BuyOffer> gettacticbuyoffers() {
		return tacticbuyoffers;}
	
	public ArrayList<HistoricTacticValues> gethistorictacticvalues() {
		return historictacticvalues;}
	public void settacticutilityscore(double s) {
		tacticutilityscore = s;
	}
	
}
