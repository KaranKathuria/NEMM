/*
 * Version info:
 *     File defining a generic tactic for sellstrategy1
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
import nemmstrategy_shortterm.SellOffer;
import nemmtime.NemmCalendar;

// STRATEGY 1 TACTIC 1
//
// This tactic sets the two sell offers as follows:
//
// A must sell volume set at a % of the agent's physical position, at a price set at a small % of the expected price
// The remaining volume, set at a % of the expected price for the tick
//
// Learning is focused on one of the following:
// - updating the % of the expected price for the remaining volume (2 methods for this)
// - updating the must-sell volume
//
// Other learning possibilities could be updating the must sell price discount


public class SellStrategy1Tactic extends GenericTactic {
	
	// The tactic parameters
	private double paramMustSellShare;
	private double paramRestVolPriceMult;
	private double paramOLDRestVolPriceMult;
	private double paramOLDUtilityScore;
	private SellOffer offerMustSellVol;
	private SellOffer offerRestVol;
	private ArrayList<SellOffer> tacticselloffers = new ArrayList<SellOffer>(); //This tactics selloffers. 	
	// GJB LEARNING
	private static final double MAXRESTVOLPRICEMULT = 2;
	private static final double MINRESTVOLPRICEMULT = 0.25;
	private static final double PRICEMULTDELTASTEP = 0.05;
	
    //Default constructor.
	SellStrategy1Tactic() {}
	
	//Used constructor
	SellStrategy1Tactic(double sbd, double d) {
		// These are set in the constructor only
		paramMustSellShare = sbd;
		paramRestVolPriceMult = 1+d;
		paramOLDRestVolPriceMult = 1; // GJB LEARNING - Need a better way to set this. Random could work. This means some try higher som try lower price. 
		//positive number over means that it tried to increase price last time. 
		paramOLDUtilityScore = 0; // GJB LEARNING - This can be set another way // The learning method needs to be set here also. Now defaults to 0.
		paramLearningMethod = 1; // Default learning method ID is 0 (= no learning)
		NUMLEARNINGMETHODS = 4; //  Learning method IDs are 0 thru 3
	}
	
	private SellOffer createMustSellVolOffer(double expectedprice, double physicalposition, double ...capitalbase) {
		SellOffer ret = new SellOffer();
		ret.setselloffervol((paramMustSellShare*physicalposition)); //
		ret.setsellofferprice((1-AllVariables.PAgentmustselldiscount)*expectedprice);
		if (physicalposition == 0) {
			ret = null;
		}
		return ret;
		}
	
	private SellOffer createRestVolOffer(double expectedprice, double physicalposition, double ...capitalbase) {
		SellOffer ret = new SellOffer();
		ret.setselloffervol(physicalposition - ((paramMustSellShare*physicalposition))); //rest of the monthly production sold at expected price.
		ret.setsellofferprice(expectedprice*(paramRestVolPriceMult)); //Prices unsymetrically around expected price with must of the volume tried sold at at premium (1+discount)*expt.
		if (physicalposition == 0) {
			ret = null;
		}
		return ret;
		}
	
	public void updatetacticselloffers() {
		double physicalposition = this.getmyStrategy().getmyAgent().getphysicalnetposition();
		double expectedprice = this.getmyStrategy().getmyAgent().getagentcompanyanalysisagent().getmarketanalysisagent().getpriceprognosis().getstpriceexpectation();
		if (physicalposition <= 0){
			physicalposition = 0.0;} //To not get crazy selloffers
		// GJB LEARNING - adjust the parameters and then create new offers.
		parameterLearning();
		tacticselloffers.clear();
		offerMustSellVol = createMustSellVolOffer(expectedprice,physicalposition);
		offerRestVol = createRestVolOffer(expectedprice,physicalposition);
		tacticselloffers.add(offerMustSellVol);
		tacticselloffers.add(offerRestVol);
	}
	
	public void addtactichistory() {
		HistoricTacticValue a = new HistoricTacticValue();
		a.tacticsbuyoffers = null;
		a.tacticselloffers = tacticselloffers;
		a.tacticutilityscore = tacticutilityscore;
		a.tickID = TheEnvironment.theCalendar.getCurrentTick();
		historictacticvalues.add(a);}
	
	public SellOffer getsellofferone() {
		return offerMustSellVol;}
	
	public SellOffer getselloffertwo() {
		return offerRestVol;}
	
	public ArrayList<SellOffer> gettacticselloffers() {
		return tacticselloffers;}

	
	private void parameterLearning() {
		// Call the appropriate learning method
		// if no learning, the method is 0 and nothing is called
		if (paramLearningMethod == 1) {
			learningMethod1();
		} else if (paramLearningMethod == 2) {
			learningMethod2();
		}
		else if (paramLearningMethod == 3) {
			learningMethod3();
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
		diffmultDelta = CommonMethods.signDbl(paramRestVolPriceMult-paramOLDRestVolPriceMult); //Prisendringen er gitt prisendring forrige forrige gang og forrige gang.
		if (diffmultDelta == 0) {diffmultDelta = 1;} // tie breaker
		// set the new multiplier delta
		priceMultDelta = diffmultUtility * diffmultDelta * PRICEMULTDELTASTEP;
		// Update the history parameters
		paramOLDRestVolPriceMult = paramRestVolPriceMult;
		paramOLDUtilityScore = tacticutilityscore;
		// Ensure not out of bounds
		paramRestVolPriceMult = Math.min(MAXRESTVOLPRICEMULT, Math.max(paramRestVolPriceMult+priceMultDelta,MINRESTVOLPRICEMULT));

	}
	private void learningMethod2() {
		// here we write the learning method code
		// Volume change - this determines the volume of the non "must sell" offer 
		// (I cant remember if that is offer 1 or offer 2...). 
		// This will adjust the volume % for the offer
		// e.g. if utility(t) > utility (t-1) then diffmult = 1 else diffmult = -1
		// 
		// buy_vol_delta = diffmult * sign(buy_vol_perc(t)-buy_vol_perc(t-1)) * rand(0.05,0.1) * buy_vol_perc(t)
		// buy_vol_perc(t+1) = min(1,max(0,buy_vol(t) + buy_vol_delta)); // ensure not bigger or smaller than 0% or 100%
	
	
	}	
	private void learningMethod3() {
		// Update the % of expected price for the rest volume
		// Improvement in utility - keep the same multiplier
		// A decline in utility - change the multiplier to be on the other "side" of 1

		double priceMult;
		// Utility comparison		
		if (tacticutilityscore-paramOLDUtilityScore >= 0) {
			// Utility has improved
			priceMult = paramRestVolPriceMult;
		}
		else {
			priceMult = 1-CommonMethods.signDbl(paramRestVolPriceMult-1);
		}
		// Update the history parameters
		paramOLDRestVolPriceMult = paramRestVolPriceMult;
		paramOLDUtilityScore = tacticutilityscore;
		// Ensure not out of bounds
		paramRestVolPriceMult = priceMult;
	}
	
}
