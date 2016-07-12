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
import nemmprocesses.ShortTermMarket;
import nemmtime.NemmCalendar;


public class TraderBuyStrategy1Tactic extends GenericTactic {
	 
	private double paramMustBuyShare;
	private double paramRestVolPriceMult;
	private double paramMustBuyPriceMult;
	private double ceilingExtraDiscountRate;
	private BidOffer bidOne;
	private BidOffer bidTwo;
	private ArrayList<BidOffer> tacticbuyoffers = new ArrayList<BidOffer>();
	private ArrayList<double[]> curUtility; // stores the utilities for the latest offers

	//Default constructor. Not in use. 
	TraderBuyStrategy1Tactic() {}
	
	//Used constructor
	TraderBuyStrategy1Tactic(double multBidVol, double sbd, double multMustBuyPrice, double multRestPrice, int learnID) {
		// This should not really be here...
		numLearningMethods = 4; //  Learning method IDs are 0 thru 3
		// These are set in the constructor only
		paramMustBuyShare = sbd;
		paramMustBuyPriceMult = multMustBuyPrice;
		paramRestVolPriceMult = multRestPrice;
		paramLearningMethod = learnID; // Default learning method ID is 1
		if (learnID < 0 || learnID > numLearningMethods-1) {
			paramLearningMethod = 0; // default is no learning
		}
		ceilingExtraDiscountRate = AllVariables.tacticExtraCeilingDiscountRate;
		tacticutilityscore = 0.5; //To ensure no change the first tick
		maxBidOfferVolumeMultiplier = multBidVol; //Indicates how much the maximum offer volume compared is to last ticks demand.
		// Create the curUtility array list to store utilities
		// The first item is the utility info for the must sell
		// The second item is the utility info for the rest volume
		// Two null utilities are added - this is just temporary as they will
		// be replaced by the utility function if they are required
		curUtility = new ArrayList<double[]>();
		curUtility.add(null);
		curUtility.add(null);
	}

	// ---- UPDATE THE TACTIC BUY BIDS
	
	private BidOffer creatBidOne(double expectedprice, double physicalposition, double lasttickdemand) { 
		// Note that physicalposition and lasttickdemand are negative numbers.
		BidOffer ret = new BidOffer();
		if (physicalposition > AllVariables.tradermaximumlongpos) {
			ret.setCertVolume(0);
		}
		else {
		//Simplyfied for the trader as physical position is not important in the same manner.
		ret.setCertVolume(AllVariables.tradermustbuyvol);
		}
		ret.setPrice(paramMustBuyPriceMult*expectedprice); //Given must buy volume price. 
		return ret;
	}
	
	private BidOffer creatBidTwo(double expectedprice, double physicalposition,  double lasttickdemand, double mustbuy) {////physicalpos and lasttickdem are negatie number. 
		BidOffer ret = new BidOffer();
		if (physicalposition > AllVariables.tradermaximumlongpos) {
		ret.setCertVolume(0);
		}
		else {
		ret.setCertVolume(AllVariables.traderrestbuyvol);
		}
		ret.setPrice(expectedprice*paramRestVolPriceMult);//Math.min(expectedprice*paramRestVolPriceMult, floorroofprice)); //Most likely that the second offer is at at pricemultiplier. Hence they buy what they dont must, at a pricemultiplier.
		return ret;
	}
	
	public void updateinputvalues() { //Calculates and updates the floorroofprice based on the agents risk adjusted rate and the risk free rate and the market prognosis future pric
		double twoyearahead;
		double tempdisc;
		twoyearahead = this.getmyStrategy().getmyAgent().getagentcompanyanalysisagent().getmarketanalysisagent().getmarketprognosis().getmedumrundpriceexpectations();
		tempdisc = TheEnvironment.GlobalValues.currentinterestrate - this.getmyStrategy().getmyAgent().getRAR() - ceilingExtraDiscountRate; //For Sellers/Producers this is added + (instead of minus)
		
		if(TheEnvironment.theCalendar.getCurrentTick()<AllVariables.firstrealtick) {
			floorroofprice = AllVariables.maxpricecerts;
		} else {			
			floorroofprice = twoyearahead/Math.pow(tempdisc + 1, 2); //Hence this equals the discounted future expected cert price. Discounted with a risk free rate and a risk rate //In other words, the seller will not sell the variable part unless the sell price is better than the discounted future price expectations. In that case he would hold the certificates in two years.
		}	
			//		floorroofprice = AllVariables.certMaxPrice;

	}
		
	
	public void updatetacticbuyoffers() {
		double physicalposition = this.getmyStrategy().getmyAgent().getphysicalnetposition();
		double expectedprice = this.getmyStrategy().getmyAgent().getagentcompanyanalysisagent().getmarketanalysisagent().getmarketprognosis().getstpriceexpectation();
		//double lasttickdemand = this.getmyStrategy().getmyAgent().getlasttickdemand();
		
		updateinputvalues();
		
		tacticbuyoffers.clear();
		bidOne = creatBidOne(expectedprice,physicalposition, 10);	//these other numbers are not in use.
		double mustbuyvol = bidOne.getCertVolume();
		bidTwo = creatBidTwo(expectedprice,physicalposition, 10000, mustbuyvol);	//these other numbers are not in use.
		tacticbuyoffers.add(bidOne);
		tacticbuyoffers.add(bidTwo);
		if (tacticbuyoffers.size()==1) {
			throw new IllegalArgumentException("DEBUG: Should not have only one buy offer");
		}

	}

	// ---- TACTIC MEMORY	
	
	public void addTacticValuesToHistory() {
		HistoricTacticValue a = new HistoricTacticValue();
		a.tacticsbuyoffers = tacticbuyoffers;
		a.tacticselloffers = null;
		a.tacticutilityscore = tacticutilityscore;
		a.tickID = TheEnvironment.theCalendar.getCurrentTick();
		historictacticvalues.add(a);
	}

	// ---- UTILITY CALCULATIONS
	
/*	// GJB Added 6oct14
	public void calcUtilityForCurrentTick() {
	//Use the agents utilitymethod to calculate each tactics utility
		double temputilityscore = myStrategy.myAgent.getutilitymethod().calculateutility(ShortTermMarket.getcurrentmarketprice(), gettacticbuyoffers(), gettacticselloffers(), ShortTermMarket.getshareofmarignaloffersold(), ShortTermMarket.getshareofmarignalofferbought());
	//Updates that tactics utility
		setUtilityScore(temputilityscore);
	}
	// --End GJB Added	*/
	
	public void calcUtilityForCurrentTick() {
		
		ArrayList<double[]> retUtility;
		int i;
		int numBids;
		int a = curUtility.size();
		int b = 3;
		
		// Check that the tacticselloffers and the utility arraylists are the same size
		if(tacticbuyoffers.size()!=curUtility.size()) {
			throw new IllegalArgumentException("DEBUG(calcUtilityForCurrentTick: tacticbuyoffers and "
					+ "curUtility are different sizes.");
		}
		numBids = tacticbuyoffers.size();
		// Append the (previous tick's) utility to the current sell offers
		// Reason: in case the utility function needs to use the previous utility
		// values in calculating the utility for the current tick
		// Am using clone so that we get a copy, not a reference
		// If there is no sell offer (it is null) then we skip this
		addUtilitiesToBids(numBids);
		// Calculate the utility for the current tick, using the utility function for the agent
		
		 retUtility = myStrategy.myAgent.getutilitymethod().CalcUtilityWithHistory(ShortTermMarket.getcurrentmarketprice(), tacticbuyoffers, ShortTermMarket.getshareofmarignaloffersold());
		 // Check that retUtility is returned with size = numBids
		if(retUtility.size()!=numBids) {
				throw new IllegalArgumentException("DEBUG(calcUtilityForCurrentTick: Returned utility not expected size");
		}
		 
		// Store the returned utility if there is an offer and a non-null utility returned, otherwise just keep the 
		// existing utility	
		for(i = 0; i<numBids; i++) {
			if(tacticbuyoffers.get(i) != null) {
				if( retUtility.get(i) != null) {
					curUtility.set(i, retUtility.get(i).clone());
				}
			}
			else {
				throw new IllegalArgumentException("DEBUG(calcUtilityForCurrentTick: null buy offers");
			}
				
			
		}
		
		// Add the calculated utility (that is, the utility for this tick) to each of the tick's offers
		// This will replace the previous tick's utility (stored above)
		addUtilitiesToBids(numBids);
		
		// Calculate the total utility score for the set of offers (use the first item in the utility arrays)
		// Note: the default if all utilities are null (this can occur if there have been no bids so far) is 0
		double calcUtil = 0.0;
		for(i = 0; i<numBids; i++) {
			if(curUtility.get(i)!=null) {
				calcUtil = calcUtil + curUtility.get(i)[0];
			}		
		}
		// Update to tactic's current total utility score
		setUtilityScore(calcUtil);
	}
	
	private void addUtilitiesToBids(int numBids) {
		int i;
		for(i = 0; i<numBids; i++) {
			if(tacticbuyoffers.get(i) != null) {
				if(curUtility.get(i)!=null){
					tacticbuyoffers.get(i).setUtility(curUtility.get(i).clone());
				}
				else {
					tacticbuyoffers.get(i).setUtility(null);
				}
					
			}
		}
	}
	
	// ---- GETS & SETS
	
	// TO DO: Rename the "buyoffer" part of the function names to "bid"
	public BidOffer getbuyofferone() {
		return bidOne;}
	
	public BidOffer getbuyoffertwo() {
		return bidTwo;}
	
	public ArrayList<BidOffer> gettacticbuyoffers() {
		return tacticbuyoffers;}
	
	public ArrayList<HistoricTacticValue> gethistorictacticvalues() {
		return historictacticvalues;}

	// ---- PARAMETER LEARNING	
	
	public void learnParameters() {
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
		
		//Learning the adjustment of price multiplier
		double deltapricemup;
		if (TheEnvironment.theCalendar.getCurrentTick() > 0 && (Math.abs(bidTwo.getPrice() - ShortTermMarket.getcurrentmarketprice())/(ShortTermMarket.getcurrentmarketprice())) > 0.11) {
		 deltapricemup = 0.1;
		}
		else {
		 deltapricemup = deltapricemultiplier;
		}
		
		
		
		//If I reduce price with one step based on the offer price on last tick and the prices I get is lower than floor --> do the opposit.
		if (TheEnvironment.theCalendar.getCurrentTick() > 0 && bidTwo.getPrice() + (deltapricemup*bidTwo.getPrice()) >= floorroofprice) {
			paramRestVolPriceMult = paramRestVolPriceMult - deltapricemup;
		}
		else {
			if (tacticutilityscore == 1)
				paramRestVolPriceMult = paramRestVolPriceMult - deltapricemup; //reduce price
			else if (tacticutilityscore == 0) {
				paramRestVolPriceMult = paramRestVolPriceMult + deltapricemup; //increase price
			}
			else {
				//Unchanged
			}
		}
		if (myStrategy.myAgent.getID() == 127) {
			@SuppressWarnings("unused")
			int tmpA = 1;
		}
	}
		
	private void learningMethod2() {
		// here we write the learning method code
	}		
}

