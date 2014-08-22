/*
 * Version info:
 *     File defining a generic tactic for sellstrategy1
 *     
 *     Last altered data: 20140811
 *     Made by: Karan Kathuria
 */
package nemmstmstrategiestactics;

import java.util.ArrayList;
import repast.simphony.engine.environment.RunEnvironment;
import nemmagents.ParentAgent;
import nemmstmstrategiestactics.SellOffer;
import nemmstmstrategiestactics.SellOffer;


public class SellStrategy1Tactic {
	 
	private double sharesoldtatdiscount;
	private double discount;
	private SellOffer sellofferone;
	private SellOffer selloffertwo;
	private double tacticscore;
	private ArrayList<SellOffer> tacticselloffers = new ArrayList<SellOffer>(); //This tactics selloffers. 
	private ArrayList<HistoricTacticValues> historictacticvalues = new ArrayList<HistoricTacticValues>();  //An array of historic values. 
	
	
	private class HistoricTacticValues {
		 
			private ArrayList<SellOffer> tacticselloffers; //with fixed length given as a parameter. 
			private int month;
	 }

	SellStrategy1Tactic() {
		sharesoldtatdiscount = 0;
		discount = 0;
		sellofferone = new SellOffer();
		selloffertwo = new SellOffer();
		}
	//Used constructor
	SellStrategy1Tactic(double sbd, double d) {
		sharesoldtatdiscount = sbd;
		discount = d;	
	}
	
	private SellOffer creatSellOfferone(double expectedprice, int physicalposition) {
		SellOffer ret = new SellOffer();
		ret.numberofcert = (int) (sharesoldtatdiscount*physicalposition); //
		ret.price = (1-discount)*expectedprice; 
		return ret;
		}
	private SellOffer creatSellOffertwo(double expectedprice, int physicalposition) {
		SellOffer ret = new SellOffer();
		ret.numberofcert = physicalposition - ((int) (sharesoldtatdiscount*physicalposition)); //rest of the monthly production sold at expected price.
		ret.price = expectedprice;
		return ret;
		}
	
	public void updatetacticselloffers(double expectedprice, int physicalposition) {
		if (physicalposition < 0){
			physicalposition = 0;} //To not get crazy selloffers
		tacticselloffers.clear();
		sellofferone = creatSellOfferone(expectedprice,physicalposition);
		selloffertwo = creatSellOffertwo(expectedprice,physicalposition);
		tacticselloffers.add(sellofferone);
		tacticselloffers.add(selloffertwo);
		addtactichistory();
	}
	
	public void addtactichistory() {
		HistoricTacticValues a = new HistoricTacticValues();
		a.tacticselloffers = tacticselloffers;
		a.month = (int) RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
	}
	public void settacticscore(double ts) {
		tacticscore = ts;
	}
	
	public SellOffer getsellofferone() {
		return sellofferone;}
	
	public SellOffer getselloffertwo() {
		return selloffertwo;}
	
	public ArrayList<SellOffer> gettacticselloffers() {
		return tacticselloffers;}
	
	public ArrayList<HistoricTacticValues> gethistorictacticvalues() {
		return historictacticvalues;}
	
	
}
