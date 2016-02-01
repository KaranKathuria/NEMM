/*
 * Version info:
 *     File defining a generic tactic for sellstrategy1
 *     
 *     Last altered data: 20140811
 *     Made by: Karan Kathuria
 */
package nemmstrategy_shortterm;

import java.util.ArrayList;


import nemmcommons.AllVariables;
import nemmenvironment.TheEnvironment;
import nemmprocesses.ShortTermMarket;


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
	private double paramMustSellPriceMult;
	private double paramRestVolVolMult;
	private double floorExtraDiscountRate;
	private BidOffer offerMustSellVol;
	private BidOffer offerRestVol;
	private ArrayList<BidOffer> tacticselloffers = new ArrayList<BidOffer>(); //This tactics selloffers. 	
	private ArrayList<double[]> curUtility; // stores the utilities for the latest offers

// ---- CONSTRUCTORS
	
    //Default constructor.
	SellStrategy1Tactic() {}
	
	//Used constructor
	SellStrategy1Tactic(double multOfferVol, double sbd, double multMustSellPrice, double multRestPrice, int learnID) {
		// This should not really be here...
		numLearningMethods = 4; //  Learning method IDs are 0 thru 3
		// These are set in the constructor only
		paramMustSellShare = sbd;
		paramMustSellPriceMult = multMustSellPrice;
		paramRestVolPriceMult = multRestPrice;
		paramLearningMethod = learnID; // Default learning method ID is 1
		if (learnID < 0 || learnID > numLearningMethods-1) {
			paramLearningMethod = 0; // default is no learning
		}
		floorExtraDiscountRate = AllVariables.tacticExtraFloorDiscountRate;
		tacticutilityscore = 0.5; //To ensure no change the first tick
		maxBidOfferVolumeMultiplier = multOfferVol; //Indicates how much the maximum offer volume compared is to last ticks production.
		// Create the curUtility array list to store utilities
		// The first item is the utility info for the must sell
		// The second item is the utility info for the rest volume
		// Two null utilities are added - this is just temporary as they will
		// be replaced by the utility function if they are required
		curUtility = new ArrayList<double[]>();
		curUtility.add(null);
		curUtility.add(null);
	}
	
// ---- GETS & SETS	
	
	public BidOffer getsellofferone() {
		return offerMustSellVol;}
	
	public BidOffer getselloffertwo() {
		return offerRestVol;}
	
	public ArrayList<BidOffer> gettacticselloffers() {
		return tacticselloffers;}
	
// ---- UPDATE THE TACTIC SELL OFFERS	
	
	public void updatetacticselloffers() {
		double physicalnetposition = this.getmyStrategy().getmyAgent().getphysicalnetposition();
		double expectedprice = this.getmyStrategy().getmyAgent().getagentcompanyanalysisagent().getmarketanalysisagent().getmarketprognosis().getstpriceexpectation();
		double lasttickproduction = this.getmyStrategy().getmyAgent().getlasttickproduction();
		
		if (physicalnetposition <= 0){
			physicalnetposition = 0.0;} //To not get crazy selloffers

		updateinputvalues();
		
		// Delete the previous sell offers stored in the tacticselloffers arraylist
		tacticselloffers.clear();
		// Calculate the sell offers
		offerMustSellVol = createMustSellVolOffer(expectedprice,physicalnetposition,lasttickproduction);
		double mustsellvol = offerMustSellVol.getCertVolume();
		offerRestVol = createRestVolOffer(expectedprice,physicalnetposition, lasttickproduction, mustsellvol);
		// Store the sell offers in the tacticselloffers arraylist
		tacticselloffers.add(offerMustSellVol);
		tacticselloffers.add(offerRestVol);
		if (tacticselloffers.size()==1) {
			throw new IllegalArgumentException("DEBUG: Should not have only one sell offer");
		}
	}
	
	public void updateinputvalues() { //Calculates and updates the floorroofprice based on the agents risk adjusted rate and the risk free rate and the market prognosis future pric
		int numTicksAhead;
		double progPrice;
		double probSale;
		double twoyearahead;
		double tempdisc;
		numTicksAhead = this.getmyStrategy().getmyAgent().getNumTicksToEmptyPosition();
/*		if (tacticselloffers.get(1).getUtility()!=null) {
			// set the probability of sale equal to the activation for the "rest" offer
			probSale = tacticselloffers.get(1).getUtility()[1];
			progPrice = this.getmyStrategy().getmyAgent().getagentcompanyanalysisagent().getmarketanalysisagent().getCertificateValue(numTicksAhead, probSale);
			tempdisc = TheEnvironment.GlobalValues.currentinterestrate + this.getmyStrategy().getmyAgent().getRAR(); //For Sellers/Producers this is added + (instead of minus)			
			floorroofprice = progPrice/Math.pow(tempdisc + 1, 2); //Hence this equals the discounted future expected cert price. Discounted with a risk free rate and a risk rate //In other words, the seller will not sell the variable part unless the sell price is better than the discounted future price expectations. In that case he would hold the certificates in two years.
			floorroofprice = 0.0; 
		}
		else {
			floorroofprice = 0.0; // i.e. not binding floor
		}
*/		
		twoyearahead = this.getmyStrategy().getmyAgent().getagentcompanyanalysisagent().getmarketanalysisagent().getmarketprognosis().getmedumrundpriceexpectations();
		tempdisc = TheEnvironment.GlobalValues.currentinterestrate + this.getmyStrategy().getmyAgent().getRAR() + floorExtraDiscountRate; //For Sellers/Producers this is added + (instead of minus)		
		if(TheEnvironment.theCalendar.getCurrentTick()<AllVariables.firstrealtick) {
			floorroofprice = 0.0;
		} else {
			//will not sell rest below this price
			floorroofprice = twoyearahead/Math.pow(tempdisc + 1, 2); //Hence this equals the discounted future expected cert price. Discounted with a risk free rate and a risk rate //In other words, the seller will not sell the variable part unless the sell price is better than the discounted future price expectations. In that case he would hold the certificates in two years.
		}
//		floorroofprice = 0; // test no floor price
		//maxBidOfferVolume = maxBidOfferVolumeMultiplier * this.getmyStrategy().getmyAgent().getlasttickproduction(); // * //What i produced the last tick
		
//		maxppvolume = this.getmyStrategy().getmyAgent().getagentcompanyanalysisagent().getvolumeanalysisagent().getvolumeprognosis().getCurObPdCertProduction(); //The max pp volume is equal to the expected production of the twelve next ticks. This value itself is produced in the volumeanalysis agent.
		
	}
	
	private BidOffer createMustSellVolOffer(double expectedprice, double physicalposition,double lasttickproduction) {
		BidOffer ret = new BidOffer();
		// Must sell is set to the maximum of the must sell share * last ticks production, and the difference between
		// the physical position and the desired/target max physical position
		if (lasttickproduction == 0) {
			ret.setCertVolume(0.0);
		}
		else {
//			ret.setCertVolume(Math.max(paramMustSellShare*lasttickproduction,physicalposition-maxppvolume)); //equals must sell	
//			int tmpTicks = myStrategy.getmyAgent().getNumTicksToEmptyPosition();
//			ret.setCertVolume(Math.max(paramMustSellShare*lasttickproduction,Math.max(0.0, physicalposition/myStrategy.getmyAgent().getNumTicksToEmptyPosition())));
//			ret.setCertVolume(Math.max(0.0, physicalposition/myStrategy.getmyAgent().getNumTicksToEmptyPosition()));

			// The must sell share is the largest of the physical position divided by the number of ticks that physical
			// position should be exited over, and the physical position * the must sell share.  
			ret.setCertVolume(Math.max(0.0, Math.max(physicalposition*paramMustSellShare, 
					physicalposition/myStrategy.getmyAgent().getNumTicksToEmptyPosition())));

		}
		ret.setPrice(paramMustSellPriceMult*expectedprice);
		
		if (TheEnvironment.theCalendar.getCurrentTick() == 50) {
			int temp=0;
			temp = 1;
		}
		
		return ret;
		}
		
		//In case of no last production the must sell volume is zero, but we still have a variable volume!
	private BidOffer createRestVolOffer(double expectedprice, double physicalposition,double lasttickproduction, double mustsell) {
		// Should replace this with using the already calculated must sell
		BidOffer ret = new BidOffer();
		//20151129 KK: Added to controll the total tilfall of certs to the market.
		maxBidOfferVolumeMultiplier = TheEnvironment.GlobalValues.updated_tacticMaxPhysPosSellShare_PASellStrategy1;
		if (physicalposition == 0) {
			ret.setCertVolume(0.0);
		} 
		else {
//			ret.setCertVolume(Math.max(0.0,Math.min(maxBidOfferVolume-mustsell,physicalposition-mustsell))); //rest of the monthly production sold at expected price.			
			ret.setCertVolume(Math.max(0.0,Math.min(1.0, maxBidOfferVolumeMultiplier)*(physicalposition-mustsell))); //rest of the monthly production sold at expected price.		KK 20151129 Interesting. why not the above?	
		}
		//Added max.max 20150519
		ret.setPrice(Math.max(expectedprice*paramRestVolPriceMult, floorroofprice)); //Prices not symmetric around expected price with must of the volume tried sold at at premium (1+discount)*expt.
		//ret.setPrice(Math.min(Math.max(expectedprice*paramRestVolPriceMult, floorroofprice), AllVariables.certMaxPrice));
		
		return ret;
		}
	
// ---- TACTIC MEMORY	
	
	public void addTacticValuesToHistory() {
		// Store the sell offers for each tick
		HistoricTacticValue a = new HistoricTacticValue();
		a.tacticsbuyoffers = null;
		a.tacticselloffers = tacticselloffers;
		a.tacticutilityscore = tacticutilityscore;
		a.tickID = TheEnvironment.theCalendar.getCurrentTick();
		historictacticvalues.add(a);}

// ---- UTILITY CALCULATIONS
	
	public void calcUtilityForCurrentTick() {
		
		ArrayList<double[]> retUtility;
		int i;
		int numOffers;
		
		// Check that the tacticselloffers and the utility arraylists are the same size
		if(tacticselloffers.size()!=curUtility.size()) {
			throw new IllegalArgumentException("DEBUG(calcUtilityForCurrentTick: tacticseloffers and curUtility are different sizes.");
		}
		numOffers = tacticselloffers.size();
		// Append the (previous tick's) utility to the current sell offers
		// Reason: in case the utility function needs to use the previous utility
		// values in calculating the utility for the current tick
		// Am using clone so that we get a copy, not a reference
		// If there is no sell offer (it is null) then we skip this
		addUtilitiesToOffers(numOffers);
		// Calculate the utility for the current tick, using the utility function for the agent
		
		 retUtility = myStrategy.myAgent.getutilitymethod().CalcUtilityWithHistory(ShortTermMarket.getcurrentmarketprice(), tacticselloffers, ShortTermMarket.getshareofmarignaloffersold());
		 // Check that retUtility is returned with size = numBids
		if(retUtility.size()!=numOffers) {
				throw new IllegalArgumentException("DEBUG(calcUtilityForCurrentTick: Returned utility not expected size");
		}
		 
		// Store the returned utility if there is an offer and a non-null utility returned, otherwise just keep the 
		// existing utility	
		for(i = 0; i<numOffers; i++) {
			if(tacticselloffers.get(i) != null && retUtility.get(i) != null) {
				curUtility.set(i, retUtility.get(i).clone());
			}
		}
		
		// Add the calculated utility (that is, the utility for this tick) to each of the tick's offers
		// This will replace the previous tick's utility (stored above)
		addUtilitiesToOffers(numOffers);
		
		// Calculate the total utility score for the set of offers (use the first item in the utility arrays)
		// Note: the default if all utilities are null (this can occur if there have been no bids so far) is 0
		double calcUtil = 0.0;
		for(i = 0; i<numOffers; i++) {
			if(curUtility.get(i)!=null) {
				calcUtil = calcUtil + curUtility.get(i)[0];
			}		
		}
		// Update to tactic's current total utility score
		setUtilityScore(calcUtil);
	}
	
	private void addUtilitiesToOffers(int numOffers) {
		int i;
		for(i = 0; i<numOffers; i++) {
			if(tacticselloffers.get(i) != null) {
				if(curUtility.get(i)!=null){
					tacticselloffers.get(i).setUtility(curUtility.get(i).clone());
				}
				else {
					tacticselloffers.get(i).setUtility(null);
				}
					
			}
		}
	}
	
// ---- PARAMETER LEARNING
	
	public void learnParameters() {
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
		// NOTE: This learning method only makes sense for [0,1] utility functions
		// Update the % of expected price for the rest volume
		// Utility = 1 - All variable offers where expeted thus try to sell at a higher price
		// Utility = 0 - None of the variable offers where sold, thus try to sell at a lower price
		// Utility <0,1> - Some where expeted, some where not. Try same again.
		
		//Learning the adjustment of price multiplier
		double deltapricemup;
		if (TheEnvironment.theCalendar.getCurrentTick() > 0 && (Math.abs(offerRestVol.getPrice() - ShortTermMarket.getcurrentmarketprice())/(ShortTermMarket.getcurrentmarketprice())) > 0.11) {
			 deltapricemup = 0.1;
			}
			else {
			 deltapricemup = deltapricemultiplier;
			}
		
		
		//Exeption if the price offered last time is lower than close to the floor-price. 
		if (TheEnvironment.theCalendar.getCurrentTick() > 0 && offerRestVol.getPrice() - (deltapricemup*offerRestVol.getPrice()) <= floorroofprice) {
			paramRestVolPriceMult = paramRestVolPriceMult + deltapricemup;
		} //Could cause failure if the price start below floorroofprice
		else {
		if (tacticutilityscore == 1)
			paramRestVolPriceMult = paramRestVolPriceMult + deltapricemup; //increase variable sell price
		else if (tacticutilityscore == 0) {
			paramRestVolPriceMult = paramRestVolPriceMult - deltapricemup; //reduce variable sell price
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
