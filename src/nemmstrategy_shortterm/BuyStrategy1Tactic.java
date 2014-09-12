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
import nemmcommons.CommonMethods;
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
	// GJB LEARNING
	private double paramOLDRestVoldiscount;
	private double paramOLDUtilityScore;
	private static final double MAXRESTVOLDISCOUNT = 2;
	private static final double MINRESTVOLDISCOUNT = 0.25;
	private static final double PRICEMULTDELTASTEP = 0.05;

	//Default constructor. Not in use. 
	BuyStrategy1Tactic() {}
	
	//Used constructor
	BuyStrategy1Tactic(double sbd, double d) {
		shareboughtatdiscount = sbd;
		discount = 1-d;
		paramOLDRestVoldiscount = 1; // GJB LEARNING - Need a better way to set this. Random could work. What is this?
		paramOLDUtilityScore = 0; // GJB LEARNING - This can be set another way // The learning method needs to be set here also. Now defaults to 0.
		paramLearningMethod = 1; // Default learning method ID is 0 (= no learning)
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
		ret.setbuyofferprice((discount)*expectedprice); //Most likely that the second offer is at at discount. Hence they buy what they dont must, at a discount.
		if (physicalposition == 0) {
			ret = null;
		}
		return ret;
		}
	
	public void updatetacticbuyoffers() {
		double physicalposition = this.getmyStrategy().getmyAgent().getphysicalnetposition();
		double expectedprice = this.getmyStrategy().getmyAgent().getagentcompanyanalysisagent().getmarketanalysisagent().getpriceprognosis().getstpriceexpectation();
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
		// Update the % of expected price for the rest volume
		// Improvement in utility - we adjust price multiplier delta in the same direction as last time
		// A decline in utility - we adjust the price multiplier delta in the opposite direction as last time
 		
		int diffmultUtility; //Retning på utility
		int diffmultDelta;	//Retning på pris fra forrige forrige gang til forrige gang. 
		double priceMultDelta;
		// Utility comparison		
		if (tacticutilityscore-paramOLDUtilityScore >= 0) {
			// Utility has improved
			diffmultUtility = 1;
		}
		else {
			diffmultUtility = -1;
		}
		// -ve if the mult is less than the previous, positive otherwise
		diffmultDelta = CommonMethods.signDbl(discount-paramOLDRestVoldiscount); //Om prisen har gått ned får vi negativt tall
		if (diffmultDelta == 0) {diffmultDelta = 1;} // tie breaker
		// set the new multiplier delta
		priceMultDelta = diffmultUtility * diffmultDelta * PRICEMULTDELTASTEP;
		// Update the history parameters
		paramOLDRestVoldiscount = discount;
		paramOLDUtilityScore = tacticutilityscore;
		// Ensure not out of bounds. Note minus sign in difference to sellstrategy1tactiv1 learning 1
		discount = Math.min(MAXRESTVOLDISCOUNT, Math.max(discount+priceMultDelta,MINRESTVOLDISCOUNT));

	

	}
	private void learningMethod2() {
		// here we write the learning method code
	}		
}
