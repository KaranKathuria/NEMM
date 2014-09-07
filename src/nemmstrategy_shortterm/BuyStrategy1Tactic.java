/*
 * Version info:
 *      File defining a generic tactic for buystrategy1
 *     
 *     Last altered data: 20140811
 *     Made by: Karan Kathuria
 */
package nemmstrategy_shortterm;

import java.util.ArrayList;

import repast.simphony.engine.environment.RunEnvironment;
import nemmagents.ParentAgent;
import nemmcommons.AllVariables;
import nemmenvironment.TheEnvironment;
import nemmstrategy_shortterm.BuyOffer;
import nemmstrategy_shortterm.SellOffer;
import nemmtime.NemmCalendar;


public class BuyStrategy1Tactic extends GenericTactic {
	 
	private double shareboughtatdiscount;
	private double discount;
	private BuyOffer buyofferone;
	private BuyOffer buyoffertwo;
	private ArrayList<BuyOffer> tacticbuyoffers = new ArrayList<BuyOffer>();	


	BuyStrategy1Tactic() {
		shareboughtatdiscount = 0;
		discount = 0;
		paramLearningMethod = 0; // GJB LEARNING
								 // Default learning method ID is 0 (= no learning)
		NUMLEARNINGMETHODS = 3; //  Learning method IDs are 0, 1 & 2
		}
	//Used constructor
	BuyStrategy1Tactic(double sbd, double d) {
		shareboughtatdiscount = sbd;
		discount = d;	
		paramLearningMethod = 0; // GJB LEARNING
		 // Default learning method ID is 0 (= no learning)
		NUMLEARNINGMETHODS = 3; //  Learning method IDs are 0, 1 & 2

	}
	
	private BuyOffer creatBuyOfferone(double expectedprice, double physicalposition, double ...capitalbase) {
		BuyOffer ret = new BuyOffer();
		ret.setbuyoffervol((shareboughtatdiscount*(-physicalposition))); //-As the phisical position of buyer would in most cases be negative, but as the offer only has positive numbers. 
		ret.setbuyofferprice((1+AllVariables.OPAgentmustbuypremium)*expectedprice); //Given must buy volume price. 
		if (physicalposition == 0) {
			ret = null;
		}
		return ret;
		}
	

	private BuyOffer creatBuyOffertwo(double expectedprice, double physicalposition, double ...capitalbase) {
		BuyOffer ret = new BuyOffer();
		ret.setbuyoffervol((-physicalposition) -( (shareboughtatdiscount*(-physicalposition)))); //rest of the monthly production bought at expected price.
		ret.setbuyofferprice((1-discount)*expectedprice); //Most likely that the second offer is at at discount. Hence they buy what they dont must, at a discount.
		if (physicalposition == 0) {
			ret = null;
		}
		return ret;
		}
	
	public void updatetacticbuyoffers(double expectedprice, double physicalposition, double ...capitalbase) {
		if (physicalposition >= 0){
			physicalposition = -0.0;} //To ensure that we dont get crazy bids.  
		parameterLearning(); // GJB LEARNING
		tacticbuyoffers.clear();
		buyofferone = creatBuyOfferone(expectedprice,physicalposition);
		buyoffertwo = creatBuyOffertwo(expectedprice,physicalposition);
		tacticbuyoffers.add(buyofferone);
		tacticbuyoffers.add(buyoffertwo);
	}
	
	public void addtactichistory() {
		HistoricTacticValue a = new HistoricTacticValue();
		a.tacticsbuyoffers = tacticbuyoffers;
		a.tacticselloffers = null;
		a.tacticutilityscore = tacticutilityscore;
		a.tickID = TheEnvironment.theCalendar.getCurrentTick();
		historictacticvalues.add(a);
	}
	
	public BuyOffer getbuyofferone() {
		return buyofferone;}
	
	public BuyOffer getbuyoffertwo() {
		return buyoffertwo;}
	
	public ArrayList<BuyOffer> gettacticbuyoffers() {
		return tacticbuyoffers;}
	
	public ArrayList<HistoricTacticValue> gethistorictacticvalues() {
		return historictacticvalues;}
	
	// GJB LEARNING
	
	private void parameterLearning() {
		// Call the appropriate learning method
		if (paramLearningMethod == 1) {
			learningMethod1();
		} else if (paramLearningMethod == 2) {
			learningMethod2();
		}
	}
	private void learningMethod1() {
		// here we write the learning method code

	}
	private void learningMethod2() {
		// here we write the learning method code
	}		
}
