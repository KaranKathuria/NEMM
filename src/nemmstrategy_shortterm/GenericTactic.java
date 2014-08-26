/*
 * Version info:
 *    
 *     
 *     Last altered data: 20140823
 *     Made by: Karan Kathuria
 */
package nemmstrategy_shortterm;

import java.util.ArrayList;

import repast.simphony.engine.environment.RunEnvironment;
import nemmagents.ParentAgent;


public class GenericTactic {
	
	protected double tacticutilityscore;
	protected ArrayList<HistoricTacticValue> historictacticvalues = new ArrayList<HistoricTacticValue>();
	//This class could have had all the selloffers and buyoffers form the respective tactics...
	
	
	protected class HistoricTacticValue {
		 
			protected ArrayList<BuyOffer> tacticsbuyoffers;
			protected ArrayList<SellOffer> tacticselloffers; //with fixed length given as a parameter. 
			protected double tacticutilityscore;
			protected int month;
	 }
	 
	public GenericTactic() {};
	
	public BuyOffer getbuyofferone() {return null;} //All these methods are overridden by the respective subtactics hence they do return something
	public BuyOffer getbuyoffertwo() {return null;}

	public SellOffer getsellofferone() {return null;}
	public SellOffer getselloffertwo() {return null;}
	
	public ArrayList<BuyOffer> gettacticbuyoffers() {return null;}
	public ArrayList<SellOffer> gettacticselloffers() {return null;}
	
	public void updatetacticselloffers(double expectedprice, int physicalposition) {};
	public void updatetacticbuyoffers(double expectedprice, int physicalposition) {};
	public void updatetactictradeoffers(double expectedprice, int physicalposition) {};
	public void updatetacticutilityscore(double t) {tacticutilityscore = t;};
	public void addtactichistory() {};
	public double gettacticutilityscore() {return tacticutilityscore;}
	public ArrayList<HistoricTacticValue> gethistorictacticvalues() {
		return historictacticvalues;}

	
}
