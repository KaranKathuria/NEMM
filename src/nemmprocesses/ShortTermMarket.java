/*
 * Version info:
 *     File defining the short term (within-year) market for trading certificates. This market is cleared by a double auction logic.
 *     Last altered data: 20140722
 *     Made by: Karan Kathuria
 */

package nemmprocesses;

import java.util.ArrayList;
import java.util.Collections;

import nemmagents.CompanyAgent.ActiveAgent;
import nemmcommons.CommonMethods;
import nemmstrategy_shortterm.BidOffer;
import nemmstrategy_shortterm.BuyOffer;
import nemmstrategy_shortterm.GenericStrategy;
import nemmstrategy_shortterm.SellOffer;


//To be implemntetd Should in short take inn all bids, find the price which maximates traded volume. excetute trades. And set the price! (and do somethings smart with the imbalance of trade).
// Take inn bids of differen type. Use them to clear the market and set price. Inputt agents based bids, output price. as well as a methods that updates agents salse volume maybe)
public class ShortTermMarket {
	
	//Static means that the market price exist without the object short term market. Hence we can get the market price without having to refer or create a object short term market.
	private static double currentmarketprice;
	private static double marketsupply;
	private static double marketdemand;
	private static double shareofmarginalbuyofferbought;
	private static double shareofmarignalselloffersold;
	private static double tradedvolume;
	private static double pricestep;
	private static double initLow;
	private static double initHigh;
	private static ArrayList<BidOffer> Allbuyoffers = new ArrayList<BidOffer>();
	private static ArrayList<BidOffer> Allselloffers = new ArrayList<BidOffer>();
	
	// For display purposes
	private static double buyoffer1;
	private static double[] buyoffer2;
	private static double bestbuyoffer2;
	private static double selloffer1;
	private static double[] selloffer2;
	private static double bestselloffer2;
	private static double floor;
	private static double roof;

// ---- GJB Added
	// ---- Class for storing the "active" bid and offer in the 
	// market clearing code
	protected class ActiveBidOffer {
		double price; // the offer/bid price
		double maxVol; // the offer/bid volume
		double tradedVol; // the volume sold/bought so far
		
		public ActiveBidOffer() {
			price = 0.0;
			maxVol = 0.0;
			tradedVol = 0.0;
		}
	}
// ---- end GJB Added
	
	public ShortTermMarket() {}

	// GJB This needs to be moved into ACTIVE AGENT
	// it is here temporarily
	public static void updateAllBidsAndOffers() {
		for (final ActiveAgent agent : CommonMethods.getAAgentList()) {
			for (GenericStrategy strategy : agent.getallstrategies()) {
				// Update all the bids and offers for the strategy
				// Note: we are assuming here that the strategy will update all B&O for each
				// tactic. This is OK - we can assume that it is the strategy's responsibility
				// to take care of its own tactics
				strategy.updateBidsAndOffers();
			}
		}
	}
	// ===========================================
	
	public static void runshorttermmarket() {
		shareofmarginalbuyofferbought = 1;
		shareofmarignalselloffersold = 1;
		marketdemand = 0;
		marketsupply = 0;
		tradedvolume = 0;
		pricestep = 0.25;
		
		// For display purposes
		int counts = 0;
		int countb = 0;
		buyoffer2 = new double[20];
		selloffer2 = new double[20];
		
// GJB This needs to be moved into ACTIVE AGENT
// it is here temporarily 
		updateAllBidsAndOffers();
// ==============================================

		Allselloffers.clear();
		Allbuyoffers.clear();
		//Update all analysisagents price expectations		
		//Update and add all sell- and buy-offers.  
		for (final ActiveAgent agent : CommonMethods.getPAgentList()) {
/* GJB -- REMOVE --
			//update their phusical net position
			agent.getbeststrategy().updateBidsAndOffers(); 
*/
			//Updates all bids for all agents
/* NOTE: The following should be hidden inside the agent. That is, the agent should be asked
 * for its sell offers. It is up to it to get the best offer.
 */
			Allselloffers.addAll(agent.getbeststrategy().getAgentsSellOffers());
			//Allbuyoffers.addAll(agent.getbeststrategy().getAgentsBuyOffers()); For the time being the producer does not have buyoffers.
			
			//For displaypurposes
			if (agent.getbeststrategy().getalltactics().get(0).getsellofferone() == null) {selloffer1 = 0;} else{ 		  //For handling null-offers from agents without prod.
			selloffer1 = agent.getbeststrategy().getalltactics().get(0).getsellofferone().getPrice();}
			if (agent.getbeststrategy().getalltactics().get(0).getselloffertwo() == null) {selloffer2[counts] = 0;} else{ //For handling null-offers from agents without prod.
			selloffer2[counts] = agent.getbeststrategy().getalltactics().get(0).getselloffertwo().getPrice();}
			if (agent.getbeststrategy().getbesttactic().getselloffertwo() == null) {bestselloffer2 = 0;} else{ 			  //For handling null-offers from agents without prod.
			bestselloffer2 = agent.getbeststrategy().getbesttactic().getselloffertwo().getPrice();}
			floor = agent.getbeststrategy().getalltactics().get(0).getfloorroofprice();
			counts++;
			
		}
		
		for (final ActiveAgent agent : CommonMethods.getOPAgentList()) {
			/* GJB -- REMOVE --
			agent.getbeststrategy().updateBidsAndOffers();
			*/
			//Updates all bids for all agents
			/* NOTE: The following should be hidden inside the agent. That is, the agent should be asked
			 * for its sell offers. It is up to it to get the best offer.
			 */
			Allbuyoffers.addAll(agent.getbeststrategy().getAgentsBuyOffers());

			if (agent.getbeststrategy().getalltactics().get(0).getbuyofferone() == null) {buyoffer1 = 0;} else{  //For handling null-offers from agents without demand.
			buyoffer1 = agent.getbeststrategy().getalltactics().get(0).getbuyofferone().getPrice(); }
			if (agent.getbeststrategy().getalltactics().get(0).getbuyoffertwo() == null) {buyoffer2[countb] = 0;} else{  //For handling null-offers from agents without demand.
			buyoffer2[countb] = agent.getbeststrategy().getalltactics().get(0).getbuyoffertwo().getPrice();}
			if (agent.getbeststrategy().getbesttactic().getbuyoffertwo() == null) {bestbuyoffer2 = 0;} else{			 //For handling null-offers from agents without prod.
			bestbuyoffer2 = agent.getbeststrategy().getbesttactic().getbuyoffertwo().getPrice();}
			roof = agent.getbeststrategy().getalltactics().get(0).getfloorroofprice();
			countb++;
		}
		for (final ActiveAgent agent : CommonMethods.getTAgentList()) {
			/* GJB -- REMOVE --
			agent.getbeststrategy().updateBidsAndOffers(); //Updates all bids for all agents
			*/
			/* NOTE: The following should be hidden inside the agent. That is, the agent should be asked
			 * for its sell offers. It is up to it to get the best offer.
			 */
			Allselloffers.addAll(agent.getbeststrategy().getAgentsSellOffers());
			Allbuyoffers.addAll(agent.getbeststrategy().getAgentsBuyOffers());
		}
		
		//For errorcheking purposes
		ArrayList<BidOffer> tempbuy = new ArrayList<BidOffer>();
		ArrayList<BidOffer> tempsell = new ArrayList<BidOffer>();
		
		
		Allbuyoffers.removeAll(Collections.singleton(null));
		Allselloffers.removeAll(Collections.singleton(null));

		// Exit if there are no non-null bids or offers
		int numberofbuyoffers = Allbuyoffers.size(); 
		int numberofselloffers = Allselloffers.size();
		if (numberofbuyoffers == 0 || numberofselloffers == 0){ //Sets price to zero, and stops the execution in case one side of the market is null.
			currentmarketprice = 0;
			return;}
		
		// Sort the buy and sell offers from lowest to highest price 
		// Sort from lowest to highest price. 

		Collections.sort(Allselloffers);
		Collections.sort(Allbuyoffers);
		
		// Initialise the active bid and offer
		
		int indexBid = Allbuyoffers.size()-1; // start at highest price bid
		int indexOffer = 0; // Start at cheapest offer
		ActiveBidOffer curBid = ExtractBidOfferData(Allbuyoffers.get(indexBid));
		ActiveBidOffer curOffer = ExtractBidOfferData(Allselloffers.get(indexOffer));
		tempbuy = Allbuyoffers;
		tempsell = Allselloffers;
		// Ensure all bids and offers are marked as not cleared
		for (BidOffer m : Allbuyoffers) {
			m.setShareCleared(0.0);
		}
		for (BidOffer m : Allselloffers) {
			m.setShareCleared(0.0);
		}
		// Set the cleared market flag and market variables
		int flagMarketCleared = 0; // market not yet cleared
		double clearedVolume = 0; // cumulative volume cleared
		double tradedVolume = 0; // volume traded in the current trade
		double bidAvailVol = 0;
		double offerAvailVol = 0;
		double certPrice = 0;
		
		while( flagMarketCleared == 0) {
			// This is the market clearing code
			
			// Clear the bid and offer pair as long as the bid price is not smaller than the offer price
			// (if the opposite is true, the remaining bids and offers will not clear)
			
			// Note: bids or offers with zero volume will go through this process and be "cleared"
			// They may even be the marginal bid/offer (that's OK - it still all works)
			
			if (curBid.price>=curOffer.price) {
				
				// Calculate the traded volume from the current bid and offer pair
				bidAvailVol = curBid.maxVol-curBid.tradedVol;
				if (bidAvailVol<0.0) {
					throw new IllegalArgumentException("DEBUG: ShortTermMarket: Negative bidAvailVol");
				}
				offerAvailVol = curOffer.maxVol - curOffer.tradedVol;
				if (offerAvailVol<0.0) {
					throw new IllegalArgumentException("DEBUG: ShortTermMarket: Negative bidAvailVol");
				}
				
				tradedVolume = Math.min(bidAvailVol, offerAvailVol);
				// Add the traded volume to the cumulative volume
				curBid.tradedVol += tradedVolume;
				curOffer.tradedVol += tradedVolume;
				clearedVolume += tradedVolume;
				
				// One or both of the bid and offer will have been "used up"
				// Replace them if so
				if (curBid.tradedVol>=curBid.maxVol){
					indexBid--;
					// Stop if we have run out of bids, otherwise take the next
					// cheapest bid
					if (indexBid <0) {
						flagMarketCleared = 1;
					}
					else{
						curBid = ExtractBidOfferData(Allbuyoffers.get(indexBid));
					}	
				}
				if (curOffer.tradedVol>=curOffer.maxVol){
					indexOffer++;
					// Stop if we have run out of bids, otherwise take the next
					// cheapest bid
					if (indexOffer >=Allselloffers.size()) {
						flagMarketCleared = 1;
					}
					else{
						curOffer = ExtractBidOfferData(Allselloffers.get(indexOffer));
					}	
				}
			}
			else {
				// the market is now cleared (the remaining bids and offers will not clear)
				flagMarketCleared = 1;
			}			
		}
		
		// Now we need to set the price
		// if no more sell offers, price is set equal to the last cleared buy offer
		// if no more buy offers, price is set equal to the last cleared sell offer
		// if the buy offer traded volume is 0, then price is set at the last cleared sell offer price
		// if the sell offer traded volume is 0, then the price is set to the last cleared buy offer price
		// if both the sell and buy offer traded volume is 0, the price is set to be half-way between the
		// new buy offer price and the new sell offer price
		// With this structure, we should never get the situation where we have an uncleared sell volume
		// with an offer price less than the cleared market price, and an uncleared buy volume with a bid
		// price greater than the cleared market price
		
		if (indexOffer >=Allselloffers.size()) {
			certPrice = curBid.price;
		}
		else if (indexBid < 0) {
			certPrice = curOffer.price;
		}
		else if (curBid.tradedVol == 0 & curOffer.tradedVol == 0) {
			certPrice = 0.5*(curOffer.price+curBid.price);
		}
		else if (curBid.tradedVol == 0) {
			certPrice = curOffer.price;
		}
		else if (curOffer.tradedVol == 0) {
			certPrice = curBid.price;
		}
		else {
			certPrice = -1;
		}
		tradedvolume = clearedVolume;
		marketsupply = clearedVolume;
		marketdemand = clearedVolume;
		
		int i;
		// set all offers cheaper than the marginal offer to be fully cleared
		for(i=0;i<indexOffer;i++) {
			Allselloffers.get(i).setShareCleared(1.0);
		}
		// set all bids more expensive than the marginal bid to be fully cleared
		for(i=Allbuyoffers.size()-1;i>indexBid;i--) {
			Allbuyoffers.get(i).setShareCleared(1.0);
		}		
		// calculate the amount of the marginal offers cleared
		if(curOffer.maxVol > 0) {
			Allselloffers.get(indexOffer).setShareCleared(curOffer.tradedVol/curOffer.maxVol);	
		}
		else {
			Allselloffers.get(indexOffer).setShareCleared(1.0);
		}
		if(curBid.maxVol > 0) {
			Allbuyoffers.get(indexBid).setShareCleared(curBid.tradedVol/curBid.maxVol);	
		}
		else {
			Allbuyoffers.get(indexBid).setShareCleared(1.0);
		}	
		
		// Set the share of the marginal offer sold and the share of the marginal offer purchased
		// GJB - remove this once checked that it is no longer used
		if (curBid.tradedVol == 0 || curBid.maxVol == 0) {
			shareofmarginalbuyofferbought = 1;
		} 
		else {
			shareofmarginalbuyofferbought = curBid.tradedVol/curBid.maxVol;
		}
		if (curOffer.tradedVol == 0 || curOffer.maxVol == 0) {
			shareofmarignalselloffersold = 1;
		} 
		else {
			shareofmarignalselloffersold = curOffer.tradedVol/curOffer.maxVol;
		}	
		
/* GJB - OLD CLEARING CODE - TO REMOVE		
		//Should be changed to something smarter. 
		//currentmarketprice = Allbuyoffers.get(0).getPrice();
		
		initLow = Math.min(Allbuyoffers.get(0).getPrice(), Allselloffers.get(0).getPrice()); 
		initHigh = Math.max(Allbuyoffers.get(numberofbuyoffers-1).getPrice(), Allselloffers.get(numberofselloffers-1).getPrice());
		//For debug pruposes
		double low = initLow;
		double high = initHigh;

		double mindiff = 2000000000;
		
		
		//Finds market price. Iterates thorugh all steps, calculates diffs in demand and supply and set price where diff is minimal
		//I situasjoner hvor krysset har ulike salgs og kj�psvillighet. Alts� at i den prisen hvor volume maksimeres, s� er selgeren villig til � selge billigere enn alle kj�pere er villig til � kj�pe. Dette er en lik situsajon som at to markedspriser gir samme maksimert handlet volum. 
		//Hva blir prisen da? Forel�pig satt til det laveste. Dette er ok, siden det ikke vil skje s� ofte n�r budene er mange. 
		for (int i = 0, n = (int) (((initHigh-initLow)/pricestep)+0.5); i <= n; ++i) { //For all price steps
			//Get supply
			// Get price with lowes abs diff in s and d
			double tempprice;
			double diff;
			 //TBD: Find a smarter initialiation for mindiff
			tempprice = initLow + (i*pricestep);
			double demand = 0;
			double supply = 0;
			
			for (BidOffer b : Allbuyoffers) { //Sums all demand for that price (lowest to highest).
				if (b.getPrice()>=tempprice){
					demand = demand + b.getCertVolume();}
			}
			for (BidOffer s : Allselloffers) { //Sums all supply for that price (lowest to highest).
				if (s.getPrice()<=tempprice) {
					supply = supply + s.getCertVolume();}
			}
			diff = Math.abs(demand-supply);
			if (diff<mindiff && supply>0 && demand>0) { //setting < or <= has quite an effect as this implies that prices will be the lowest when if there are several cases with same diff.Nevertheless, when running with "a lot" of agents, this will have a minor effect. 
				//marketsupply = supply;
				mindiff = diff;
				tradedvolume = Math.min(supply, demand);
				marketsupply = supply;
				marketdemand = demand;
				certprice = tempprice;}//certprice is when the diff is minimum. 	
		}
		
		// The following code takes care of the access demand or supply which is only partly accepted in the market. The code calculate the share of the marginal offer that is accepted. 
		// Note that if the marginal offer is not larger (volumewise) that the access offered, the share is set to zero, and nothing more is done. 
		if (marketsupply != marketdemand){
			double marginalsupply = 0;
			double marginaldemand = 0;
			if (marketsupply>marketdemand) {
				double accessupply = marketsupply - marketdemand;
				for (BidOffer s : Allselloffers) { //Sums all supply for that price (lowest to highest).
					if (s.getPrice()>certprice-pricestep && s.getPrice()<=certprice) {
						marginalsupply = marginalsupply + s.getCertVolume();}}
				if (marginalsupply<accessupply) {
					shareofmarignalselloffersold = 0;}
				else {
				shareofmarignalselloffersold = (marginalsupply - accessupply)/marginalsupply;
				}
			}
			else {
				double accessdemand = marketdemand - marketsupply;
				for (BidOffer b : Allbuyoffers) { //Sums all supply for that price (lowest to highest).
					if (b.getPrice()>=(certprice) && b.getPrice()<(certprice+pricestep)) {
						marginaldemand = marginaldemand + b.getCertVolume();}}
				if (marginaldemand<accessdemand) {
					shareofmarginalbuyofferbought = 0;}
				else {
					shareofmarginalbuyofferbought = (marginaldemand - accessdemand)/marginaldemand;
				}
			}
		}
		double tempshb = shareofmarginalbuyofferbought;
		double tempshs = shareofmarignalselloffersold;
		*/
		currentmarketprice = certPrice;
	}

// ---- Methods used in the clearing code
	
	private static ActiveBidOffer ExtractBidOfferData(BidOffer tmpBidOffer) {
		ActiveBidOffer ret = new ShortTermMarket().new ActiveBidOffer();
		
		ret.price = tmpBidOffer.getPrice();
		ret.maxVol = tmpBidOffer.getCertVolume();
		
		return ret;
	}
	
	
// ---- GETS & SETS	
public static double getshareofmarignaloffersold() {
	return shareofmarignalselloffersold;
}
public static double getshareofmarignalofferbought() {
	return shareofmarginalbuyofferbought;
}
public static double getcurrentmarketprice() {
	return currentmarketprice;}

public static double getmarketsupply() {
	return marketsupply;
}
public static double getmarketdemand() {
	return marketdemand;
}
public static double gettradedvolume() {
	return tradedvolume;
}
public static double getpricestep() {
	return pricestep;
}

public static int getnumberofbuyoffers() {
	return Allbuyoffers.size();
}

public static int getnumberofselloffers() {
	return Allselloffers.size();
}
public static double getfloor() {
	return floor;
}
public static double getroof() {
	return roof;
}

//DisplayVariables
//The beststrategy and tactis offers for a selecter PA and OPA
public static double getbuyoffer1() {
return buyoffer1;
}
public static double[] getbuyoffer2() {
return buyoffer2;
}
public static double getbestbuyoffer2() {
return bestbuyoffer2;
}
public static double getselloffer1() {
return selloffer1;
}
public static double[] getselloffer2() {
return selloffer2;}
public static double getbestselloffer2() {
return bestselloffer2;
}

}

