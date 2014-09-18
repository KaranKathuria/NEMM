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
	private SellOffer offerMustSellVol;
	private SellOffer offerRestVol;
	private ArrayList<SellOffer> tacticselloffers = new ArrayList<SellOffer>(); //This tactics selloffers. 	
	private static final double deltapricemultiplier = 0.05;
	
    //Default constructor.
	SellStrategy1Tactic() {}
	
	//Used constructor
	SellStrategy1Tactic(double sbd, double d) {
		// These are set in the constructor only
		paramMustSellShare = sbd;
		paramRestVolPriceMult = 1+d;
		paramLearningMethod = 1; // Default learning method ID is 1
		numberoflearningmethods = 4; //  Learning method IDs are 0 thru 3
		tacticutilityscore = 0.5; //To ensure no change the frist tick
		maxoffervolumemultiplier = 2; //Indicates how much the maximum offer volume compared is to last ticks production.
	}
	
	private SellOffer createMustSellVolOffer(double expectedprice, double physicalposition,double lasttickproduction) {
		SellOffer ret = new SellOffer();
		ret.setselloffervol(Math.max(paramMustSellShare*lasttickproduction,physicalposition-maxppvolume)); //equals must sell
		ret.setsellofferprice((1-AllVariables.PAgentmustselldiscount)*expectedprice);
		if (lasttickproduction == 0) {
			ret = null;
		}
		return ret;
		}
		
		//In case of no last production the must sell volume is zero, but we still have a variable volume!
	private SellOffer createRestVolOffer(double expectedprice, double physicalposition,double lasttickproduction) {
		double mustsell = (Math.max(paramMustSellShare*lasttickproduction, physicalposition-maxppvolume));
		SellOffer ret = new SellOffer();
		ret.setselloffervol(Math.max(0.0,Math.min(maxoffervolume-mustsell,physicalposition-mustsell))); //rest of the monthly production sold at expected price.
		ret.setsellofferprice(Math.max(expectedprice*paramRestVolPriceMult, floorroofprice)); //Prices unsymetrically around expected price with must of the volume tried sold at at premium (1+discount)*expt.
		if (physicalposition == 0) {
			ret = null;
		}
		return ret;
		}
	
	public void updateinputvalues() { //Calculates and updates the floorroofprice based on the agents risk adjusted rate and the risk free rate and the market prognosis future pric
		double twoyearahead;
		double tempdisc;
		twoyearahead = this.getmyStrategy().getmyAgent().getagentcompanyanalysisagent().getmarketanalysisagent().getmarketprognosis().getmedumrundpriceexpectations();
		tempdisc = TheEnvironment.GlobalValues.currentinterestrate + this.getmyStrategy().getmyAgent().getRAR(); //For Sellers/Producers this is added + (instead of minus)
		
		floorroofprice = twoyearahead/Math.pow(tempdisc + 1, 2); //Hence this equals the discounted future expected cert price. Discounted with a risk free rate and a risk rate //In other words, the seller will not sell the variable part unless the sell price is better than the discounted future price expectations. In that case he would hold the certificates in two years.
		maxoffervolume = maxoffervolumemultiplier * this.getmyStrategy().getmyAgent().getlasttickproduction(); // * //What i produced the last tick
		maxppvolume = this.getmyStrategy().getmyAgent().getagentcompanyanalysisagent().getvolumeanalysisagent().getvolumeprognosis().getnexttwelvetickscertproduction(); //The max pp volume is equal to the expected production of the twelve next ticks. This value itself is produced in the volumeanalysis agent.
		
	}
	
	public void updatetacticselloffers() {
		double physicalnetposition = this.getmyStrategy().getmyAgent().getphysicalnetposition();
		double expectedprice = this.getmyStrategy().getmyAgent().getagentcompanyanalysisagent().getmarketanalysisagent().getmarketprognosis().getstpriceexpectation();
		double lasttickproduction = this.getmyStrategy().getmyAgent().getlasttickproduction();
		
		if (physicalnetposition <= 0){
			physicalnetposition = 0.0;} //To not get crazy selloffers
		
		
		parameterLearning(); //Takes bids and utilities form last time and alters the price of the variable bid
		updateinputvalues();
		
		tacticselloffers.clear();
		offerMustSellVol = createMustSellVolOffer(expectedprice,physicalnetposition,lasttickproduction);
		offerRestVol = createRestVolOffer(expectedprice,physicalnetposition, lasttickproduction);
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
		// Utility = 1 - All variable offers where expeted thus try to sell at a higher price
		// Utility = 0 - None of the variable offers where sold, thus try to sell at a lower price
		// Utility <0,1> - Some where expeted, some where not. Try same again.
		
		//Exeption if the price offered last time is lower than close to the floor-price. 
		if (TheEnvironment.theCalendar.getCurrentTick() > 0 && offerRestVol.getSellOfferprice() - (deltapricemultiplier*offerRestVol.getSellOfferprice()) <= floorroofprice) {
			paramRestVolPriceMult = paramRestVolPriceMult + deltapricemultiplier;
		} //Could cause failure if the price start below floorroofprice
		else {
		if (tacticutilityscore == 1)
			paramRestVolPriceMult = paramRestVolPriceMult + deltapricemultiplier; //increase variable sell price
		else if (tacticutilityscore == 0) {
			paramRestVolPriceMult = paramRestVolPriceMult - deltapricemultiplier; //reduce variable sell price
		}
		else {
			//Unchanged
		}
			
			
		}
	}
		//Something to caputure the roof price. The variable price cannot be 

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
		/* Update the % of expected price for the rest volume
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
		*/
	}
	
}


//OLD

//GB Learning
//	private double paramOLDRestVolPriceMult;
//	private double paramOLDUtilityScore;
//private static final double MAXRESTVOLPRICEMULT = 2;
//private static final double MINRESTVOLPRICEMULT = 0.25;

//paramOLDRestVolPriceMult = 2;  GJB LEARNING - Need a better way to set this. Random could work. This means some try higher som try lower price. 
//paramOLDUtilityScore = 0; // GJB LEARNING - This can be set another way // The learning method needs to be set here also. Now defaults to 0.

/* Update the % of expected price for the rest volume
// Improvement in utility - we adjust price multiplier delta in the same direction as last time
// A decline in utility - we adjust the price multiplier delta in the opposite direction as last time
//Unchanged utility --> Do the same as last time, which to start wiht is to reduce price.
	
int diffmultUtility; //Retning på utility
int diffmultDelta;	//Retning på pris fra forrige forrige gang til forrige gang. 
double priceMultDelta;
// Utility comparison		
if (tacticutilityscore-paramOLDUtilityScore >= 0) {
	// Utility has improved or is unchanged.
	diffmultUtility = 1; 
}
else {
	diffmultUtility = -1;
}
// -ve if the mult is less than the previous, positive otherwise
diffmultDelta = CommonMethods.signDbl(paramRestVolPriceMult-paramOLDRestVolPriceMult); // Increased price gives positive number
if (diffmultDelta == 0) {diffmultDelta = 1;} // tie breaker
// set the new multiplier delta
priceMultDelta = diffmultUtility * diffmultDelta * deltapricemultiplier; //Hence increasd price and unchange utility --> incresed price (wrong)
// Update the history parameters
paramOLDRestVolPriceMult = paramRestVolPriceMult;
paramOLDUtilityScore = tacticutilityscore;
// Ensure not out of bounds
paramRestVolPriceMult = Math.min(MAXRESTVOLPRICEMULT, Math.max(paramRestVolPriceMult+priceMultDelta,MINRESTVOLPRICEMULT));
*/