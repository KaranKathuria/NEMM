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
	
	public void parameterLearning() {
		// Call the appropriate learning method
		if (paramLearningMethod == 1) {
			learningMethod1();
		} else if (paramLearningMethod == 2) {
			learningMethod2();
		}
	}
	public void learningMethod1() {
		// here we write the learning method code
		// Price based change
		// if utility(t) > utility (t-1) then diffmult = 1 else diffmult = -1
		// 
		// buy_price_delta = diffmult * sign(buy_price(t)-buy_price(t-1)) * rand(0.05,0.1) * buy_price(t)
		// buy_price(t+1) = buy_price(t) + buy_price_delta;
	}
	public void learningMethod2() {
		// here we write the learning method code
		// Volume change - this determines the volume of the non "must sell" offer 
		// (I cant remember if that is offer 1 or offer 2...). 
		// This will adjust the volume % for the offer
		// e.g. if utility(t) > utility (t-1) then diffmult = 1 else diffmult = -1
		// 
		// buy_vol_delta = diffmult * sign(buy_vol_perc(t)-buy_vol_perc(t-1)) * rand(0.05,0.1) * buy_vol_perc(t)
		// buy_vol_perc(t+1) = min(1,max(0,buy_vol(t) + buy_vol_delta)); // ensure not bigger or smaller than 0% or 100%
	}		
}
