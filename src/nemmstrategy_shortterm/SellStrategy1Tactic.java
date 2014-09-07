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
// This tactic sets the two buy and sell offers as follows:
//
// A must sell volume set at a % of the agent's physical position, at a price set at a small % of the expected price
// The remaining volume, set at a % of the expected price for the tick
//
// Learning is focused on one of the following:
// - updating the % of the expected price for the remaining volume
// - updating the must-sell volume
//
// Other learning possibilities could be updating the must sell price discount


public class SellStrategy1Tactic extends GenericTactic {
	
	// GJB LEARNING
	// The tactic parameters
	private double paramMustSellShare;
	private double paramRestVolPriceMult;
	private double paramOLDRestVolPriceMult;
	private SellOffer offerMustSellVol;
	private SellOffer offerRestVol;
	private ArrayList<SellOffer> tacticselloffers = new ArrayList<SellOffer>(); //This tactics selloffers. 	
	// GJB LEARNING
	private static final double MAXRESTVOLPRICEMULT = 2.0;
	private static final double MINRESTVOLPRICEMULT = 0.25;
	private static final double PRICEMULTSTEP = 0.05;
	

	SellStrategy1Tactic() {
		paramMustSellShare = 0;
		paramRestVolPriceMult = 0;
		paramOLDRestVolPriceMult = 0;
		offerMustSellVol = new SellOffer();
		offerRestVol = new SellOffer();
		paramLearningMethod = 0; // GJB LEARNING
		 // Default learning method ID is 0 (= no learning)
		NUMLEARNINGMETHODS = 3; //  Learning method IDs are 0, 1 & 2
		}
	//Used constructor
	SellStrategy1Tactic(double sbd, double d) {
		// These are set in the constructor only
		paramMustSellShare = sbd;
		// GJB LEARNING - added 1+
		paramRestVolPriceMult = 1+d;
		paramOLDRestVolPriceMult = 1; // GJB LEARNING - Need a better way to set this. Random?
		// The learning method needs to be set here also. Now defaults to 0.
		paramLearningMethod = 0; // GJB LEARNING
		 // Default learning method ID is 0 (= no learning)
		NUMLEARNINGMETHODS = 3; //  Learning method IDs are 0, 1 & 2

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
		// GJB LEARNING - removed 1+
		ret.setsellofferprice(expectedprice*(paramRestVolPriceMult)); //Prices unsymetrically around expected price with must of the volume tried sold at at premium (1+discount)*expt.
		if (physicalposition == 0) {
			ret = null;
		}
		return ret;
		}
	
	public void updatetacticselloffers(double expectedprice, double physicalposition, double ...capitalbase) {
		if (physicalposition <= 0){
			physicalposition = 0.0;} //To not get crazy selloffers
		// GJB LEARNING - adjust the parameters
		parameterLearning();
		// GJB LEARNING - create new offers with the new parameters and save
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
		// Price based change
		// if utility(t) > utility (t-1) then diffmult = 1 else diffmult = -1
		// 
		// buy_price_delta = diffmult * sign(buy_price(t)-buy_price(t-1)) * rand(0.05,0.1) * buy_price(t)
		// buy_price(t+1) = buy_price(t) + buy_price_delta;
		double[][] recentUtilities; 		
		int diffMult;
		double priceMultDelta;
		int nowTickID = TheEnvironment.theCalendar.getCurrentTick();
		// Utility comparison
		if (nowTickID > 0) {// need two ticks for this to work
			recentUtilities = gettacticutilityscore(2);
			if (recentUtilities[0][1]-recentUtilities[1][1] >= 0) {
				// Utility has improved
				diffMult = 1;
			}
			else {
				diffMult = -1;
			}
			priceMultDelta = diffMult * CommonMethods.signDbl(paramRestVolPriceMult-paramOLDRestVolPriceMult) * PRICEMULTSTEP;
			paramRestVolPriceMult=paramRestVolPriceMult+priceMultDelta;
			// Ensure not out of bounds
			paramRestVolPriceMult = Math.min(MAXRESTVOLPRICEMULT, Math.max(paramRestVolPriceMult,MINRESTVOLPRICEMULT));
		}
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
	
}
