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
	private double pricemultiplier;
	private BuyOffer buyofferone;
	private BuyOffer buyoffertwo;
	private ArrayList<BuyOffer> tacticbuyoffers = new ArrayList<BuyOffer>();
	private static final double deltapricemultiplier = 0.05;

	//Default constructor. Not in use. 
	BuyStrategy1Tactic() {}
	
	//Used constructor
	BuyStrategy1Tactic(double sbd, double d) {
		shareboughtatdiscount = sbd;
		pricemultiplier = 1-d; // gives pricemultiplier = (1.25 - 0.25)
		paramLearningMethod = 1; // Default learning method ID is 0 (= no learning)
		numberoflearningmethods = 3; //  Learning method IDs are 0, 1 & 2
		tacticutilityscore = 0.5;

	}
	
	private BuyOffer creatBuyOfferone(double expectedprice, double physicalposition) {
		BuyOffer ret = new BuyOffer();
		ret.setbuyoffervol((shareboughtatdiscount*(-physicalposition))); //-As the phisical position of buyer would in most cases be negative, but as the offer only has positive numbers. 
		ret.setbuyofferprice((1+AllVariables.OPAgentmustbuypremium)*expectedprice); //Given must buy volume price. 
		if (physicalposition == 0) {
			ret = null;
		}
		return ret;
		}
	

	private BuyOffer creatBuyOffertwo(double expectedprice, double physicalposition) {
		BuyOffer ret = new BuyOffer();
		ret.setbuyoffervol((-physicalposition) -( (shareboughtatdiscount*(-physicalposition)))); //rest of the monthly production bought at expected price.
		ret.setbuyofferprice((pricemultiplier)*expectedprice); //Most likely that the second offer is at at pricemultiplier. Hence they buy what they dont must, at a pricemultiplier.
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
		// Utility = 1 - All variable offers where expeted thus try to bid a lower price
		// Utility = 0 - None of the variable offers where bought, hence reduce increase buyoffer next time
		// Utility <0,1> - Some where expeted, some where not. Try same again.
		
		if (tacticutilityscore == 1)
			pricemultiplier = pricemultiplier - deltapricemultiplier; //reduce price
		else if (tacticutilityscore == 0) {
			pricemultiplier = pricemultiplier + deltapricemultiplier; //increase price
		}
		else {
			//Unchanged
		}
	}
		//Something to caputure the roof price. The variable price cannot be 
		
	private void learningMethod2() {
		// here we write the learning method code
	}		
}


// OLD Learning 1

//GJB LEARNING
//paramOLDRestVoldiscount = 0; // GJB LEARNING - Need a better way to set this. Random could work. What is this?
//paramOLDUtilityScore = 0; // GJB LEARNING - This can be set another way // The learning method needs to be set here also. Now defaults to 0.
//private double paramOLDRestVoldiscount;
//private double paramOLDUtilityScore;

/*
int diffmultUtility; //Retning på utility
int diffmultDelta;	//Retning på pris fra forrige forrige gang til forrige gang. 
double deltapricemultipier;
// Utility comparison		
if (tacticutilityscore-paramOLDUtilityScore >= 0) { //Unchanged utility is positive change. 
	// Utility has improved
	diffmultUtility = 1;
}
else {
	diffmultUtility = -1;
}
// -ve if the mult is less than the previous, positive otherwise
diffmultDelta = CommonMethods.signDbl(pricemultiplier-paramOLDRestVoldiscount); //Unchanged price is positive (increase) price. 
if (diffmultDelta == 0) {diffmultDelta = 1;} // tie breaker
// set the new multiplier delta
deltapricemultipier = diffmultUtility * diffmultDelta * PRICEMULTDELTASTEP;
// Update the history parameters
paramOLDRestVoldiscount = pricemultiplier;
paramOLDUtilityScore = tacticutilityscore;
// Ensure not out of bounds. Note minus sign in difference to sellstrategy1tactiv1 learning 1
pricemultiplier = Math.min(MAXRESTVOLDISCOUNT, Math.max(pricemultiplier+deltapricemultipier,MINRESTVOLDISCOUNT));
//Says if positive change (utilttychange and pricechange) increase price.
*/
