/*
 * Version info:
 *     Set of methods for calculating utilities, both for tactics and strategies. These utilities are agents specific. 
 *     
 *     Last altered data: 20140822
 *     Made by: Karan Kathuria
 */
 
package nemmstrategy_shortterm;

import java.util.ArrayList;
import java.util.Collections;
import repast.simphony.random.RandomHelper;
import nemmcommons.AllVariables;
import nemmcommons.CommonMethods;
import nemmprocesses.UpdatePhysicalPosition;


//Class definition
public class OPAUtilityMethod extends GenericUtilityMethod{

	// Class level variables
	private int flagUtilityFunction;
	// Specific variables used for utility 2 (expected return)
	private double alpha;
	private int flagFirstPd; // the first period has special rules for setting expectedProfit and expectedActivation
	
	// Constructor
	public OPAUtilityMethod(int functionFlag) {
		if (functionFlag < 1 || functionFlag > 2){
			throw new IllegalArgumentException("DEBUG: Illegal flagUtilityFunction in OPAUtilityMethod. Val = " + functionFlag);
		}
		flagUtilityFunction = functionFlag;
		// Using the second utility function approach requires special treatment in the first tick
		switch (flagUtilityFunction){
		case 1:
			// No specific initialisation required
			break;
		case 2:
			double rndUniform = RandomHelper.nextDoubleFromTo(0, 1);
			alpha = AllVariables.tacticMaxUtilityAlphaOP*rndUniform + 
				(1-rndUniform)*AllVariables.tacticMinUtilityAlphaOP;
			flagFirstPd = 1;
			break;
		}
	}
	
	public ArrayList<double[]> CalcUtilityWithHistory(double marketprice, ArrayList<BidOffer> s, double shareofmarginaltoffersold) {
		
		ArrayList<double[]> retList = new ArrayList<double[]>();
		double retVal = 0;
		switch (flagUtilityFunction){
		case 1:
			retVal = UtilFctn_VolOnly( marketprice, s, shareofmarginaltoffersold);
			double[] retArray = new double[1];
			retArray[0] = retVal;
			for(int i=0;i<s.size();i++){
				// Just for convenience we save the utility in the first (must sell) offer
				// As the utilities for each offer step are added up in the tactic to get a total
				// utility, this is fine
				if(i==0) {
					retList.add(retArray);
				}
				else {
					retList.add(null);
				}
			}			
			break;
		case 2:
			retList = UtilFctn_ExpReturn( marketprice, s, shareofmarginaltoffersold);	
			break;
		}
		return retList;
	}		
	
	private ArrayList<double[]> UtilFctn_ExpReturn(double priceSpot, ArrayList<BidOffer> s, double shareofmarginaltoffersold) {

		double curProfit = 0;
		double curActivation = 0;
		double curReturn = 0.0;
		double curBidVol = 0;
		double curBidPrice = 0;
		double curBidPurchased = 0;
		double curBidShareCleared = 0;
		double[] certValue;
		double curRestVal;
		
		ArrayList<double[]> retList = new ArrayList<double[]>();
		double[] tmpArray;
		int i;
		
		// Initialise the return array list with null objects.
		for ( i=0;i<s.size();i++) {
			retList.add(null);
		}
		
		// Calculate the penalties
		// Note: this is a certificate value (like a water value), and represents the chance and cost of being short
		// and of paying more in the future for something you could buy today
		// These will be set to:
		// index 0 = expected shortfall price (as you need to buy the certificates today)
		// all others = expected price of the next tick
		// They should be obtained from the analysis agent
		certValue = new double[s.size()];
		certValue[0]=Math.max(AllVariables.valueCertShortfall, priceSpot*AllVariables.penaltyratio); // I have made this bigger than the "penalty" to reflect risk aversion to being short 
		for ( i=1;i<s.size();i++) {
		//	certValue[i]=priceSpot; // for now, take the market price as the best guess of the certificate value
			certValue[i]=myAgent.getagentcompanyanalysisagent().getmarketanalysisagent().getCertValuePurchaser();
		}
		
		for ( i=0;i<s.size();i++) {
			if(s.get(i) != null) {
				// There is a bid, so we need to calculate the new utility measures
				// Note: there should be bids, and the first bid should be the "must buy" bid
				
				// Initialise the utilities to be the previous values
				tmpArray = null;
				if(s.get(i).getUtility()!=null){
					tmpArray = s.get(i).getUtility().clone();
				}				
				// Calculate the new utility if there was an offer made.
				// If no offer was made, we just keep the previous utility
				// Note - if the previous utility is null, this still works
				curBidVol = s.get(i).getCertVolume(); 
				curBidPrice = s.get(i).getPrice();
//				curBidShareCleared = s.get(i).getShareCleared();
				curBidShareCleared = 1;
				if (curBidVol > 0) {
					// Calculate the volume sold from the offer if an offer was made (vol >0)
					if (curBidPrice>priceSpot){
						curBidPurchased = curBidVol;
					}
					else if (curBidPrice == priceSpot) {
						curBidPurchased = curBidVol*curBidShareCleared;
					}
					else {
						curBidPurchased = 0;
					}			
					curProfit = -curBidVol*curBidPrice; // Profit (cost as -ve) if all was purchased
					curActivation = curBidPurchased/curBidVol; // Activation (prob. of purchase)
					curRestVal = -curBidVol*certValue[i];
//					curReturn = -curBidVol*(certValue[i]*(1-curActivation)+curBidPrice*curActivation); // Return (Utility)
					// Exponential smoothing
					if (tmpArray != null) {
						tmpArray[1] = tmpArray[1] + alpha*(curProfit-tmpArray[1]); // Profit (cost)
						tmpArray[2] = tmpArray[2] + alpha*(curActivation-tmpArray[2]); // Activation
//						tmpArray[0] = tmpArray[0] + alpha*(curReturn-tmpArray[0]); // Return	
						tmpArray[0] = tmpArray[1]*tmpArray[2]+(1-tmpArray[2])*curRestVal;
					}
					else {
						// This will occur if there has been no utility set for this bid as yet
						// In this case, we will simply set the utilities to be the current offer's profit
						// and activation and return
						tmpArray = new double[3];
						tmpArray[1] = curProfit;
						tmpArray[2] = curActivation;
						tmpArray[0] = tmpArray[1]*tmpArray[2]+(1-tmpArray[2])*curRestVal;
					}
						

				}
				// Add a copy of the result to the return ArrayList. We use a copy here because
				// we will re-use the tmpArray object

				// A null value means there has been no utility set as yet
				if(tmpArray != null) {
				retList.set(i,tmpArray.clone());
				}
				else {
					retList.set(i,null);
				}
			}
			else {
				throw new IllegalArgumentException("OPAUtilityMethod: Null Bid encountered!");
			}
				
		}
		return retList;
		
	}
	
	//Takes in the given values and calculates the producers utility just based on how much of the variable sell offers are accepted in the short term market.
		private Double UtilFctn_VolOnly(double marketprice, ArrayList<BidOffer> s, double shareofmarginaltoffersold) {
			double ret = 0.5;
			double variableBidVolume = 0; //Offered volume at variable price. This is the sum of all offers but not the must sell offer (lowest priced offer)
			double totalPurchasedCerts = 0;	//Total number of certs sold
			double variablePurchasedCerts = 0; //Total number of certs sold at variable price (not including must buy offer)
			//double avrbidprice = 0;
			// s.removeAll(Collections.singleton(null)); //removes null bids (should not be the case, but...)
			
//			if (s.size() < 2) { //In this case there is no variable bid or there is no bids at all (which could be because the PA has no volume to offer) Then utility is <0,1> so prices are unchanged
//				return ret;} //Just any number between 0 and 1 is fine as this would imply that the tactic is unchanged.
				
//			Collections.sort(s, new CommonMethods.customselloffercomparator()); //Sorts offers form lowest to highest price. The first bid is hence the must sell bid
			Collections.sort(s); //Sorts offers form lowest to highest price. The first bid is hence the must sell bid
			
			for (int i = 1; i < s.size(); ++i) { //Skips the first bid as this is must sell bid
				variableBidVolume = variableBidVolume + s.get(i).getCertVolume(); //Volume of variable offers
			}
			totalPurchasedCerts = this.getmyAgent().getBoughtVolume();
//			totalsoldcerts = UpdatePhysicalPosition.returnsoldvolume(s, marketprice, shareofmarginaltoffersold).getSoldInSTMcert();
			variablePurchasedCerts = totalPurchasedCerts - s.get(0).getCertVolume(); //Minus the must buy which would always be the first offer to be bought
			if (variableBidVolume>0){ 
				ret = (variablePurchasedCerts/variableBidVolume); //Should always be a number [0,1]
				if (ret > 1 && ret < 0) {
					throw new IllegalArgumentException("Something is wrong with the volume offered");
				}
			}
			else {
				ret = 0.5; // No variable offer, so return a "no change" result
			}
			
			return ret;
			
		}	
	
/*	//Takes in the given values and calculates the producers utility based on how much of the offered volume at variable price (all bids without must buy bid) he managed to sell.
	public Double UtilFctn_VolOnly(double marketprice, ArrayList<BidOffer> b, ArrayList<BidOffer> s, double shareofmarginaltoffersold, double shareofmarginalofferbought) {
		
		double variableoffervolume = 0; //Offered volume at variable price. This is the sum of all offers but not the must buy offer (highest offer)
		double totalboughtcerts = 0;	//Total number of certs bought
		double variableboughtcerts = 0; //Total number of certs bought at variable price (not including must buy offer)
		//double avrbidprice = 0;
		
		b.removeAll(Collections.singleton(null)); //removes null bids (should not be the case, but...)
		
		if (b.size() < 2) { //In this case there is no variable bid or there is no bids at all (which could be because the OPA has no volume to offer) Then utility is <0,1> so prices are unchanged
			return 0.5;} //Just any number between 0 and 1 is fine as this would imply that the tactic is unchanged.
			
//		Collections.sort(b, new CommonMethods.custombuyoffercomparator()); //Sorts offers form lowest to highest price. The last bid is hence the must buy bid
		Collections.sort(b); //Sorts offers form lowest to highest price. The last bid is hence the must buy bid

		int lastindx = b.size()-1;
		
		for (int i = 0; i < lastindx; ++i) {
			variableoffervolume = variableoffervolume + b.get(i).getCertVolume(); //Volume of variable offers
		}
		
//		totalboughtcerts = UpdatePhysicalPosition.returnboughtvolume(b, marketprice, shareofmarginalofferbought).getBoughtInSTMcert();
		totalboughtcerts = this.getmyAgent().getBoughtVolume();
		variableboughtcerts = totalboughtcerts - b.get(lastindx).getCertVolume(); //Minus the must buy which would always be the first offer to be bought

		double ret = (variableboughtcerts/variableoffervolume); //Should always be a number [0,1]
		if (ret > 1 && ret < 0) {
			throw new IllegalArgumentException("Something is wrong with the volume offered");
		}
		return ret;
		

	} */
	
	// This is here for legacy purposes. This should be removed eventually once it is no longer used.
	public Double calculateutility(double marketprice, ArrayList<BidOffer> b, ArrayList<BidOffer> s, double shareofmarginaltoffersold, double shareofmarginalofferbought) {
		
		double retVal = 0;
		switch (flagUtilityFunction){
		case 1:
			retVal = UtilFctn_VolOnly( marketprice, s, shareofmarginaltoffersold);
			break;
		case 2:
			retVal = 0.0; // should not be using this function if you want the expected return utility
			break;
			
		}
		return retVal;
	}

}
	
	
//OLD


/*
double phys = Math.abs(this.getmyAgent().getphysicalnetposition());
double ret;
if (phys == 0) { //If this is the case, then nothing is buy because it no demand.
	return 1.0;}


avrbidprice = UpdatePhysicalPosition.returnboughtvolume(b, marketprice, shareofmarginalofferbought).getBoughtInSTMprice();

if ((boughtcerts/phys) < 1) { 
	ret = (boughtcerts/phys); //If we did not manage to buy all demand. Than utility is given by volume. Unchanged volume ensures that the tactic increase price.
// Could ad something to ensure utility in right direction when variable price is below market price
} else { //Only if everything is bought
	ret = 1 + (1/avrbidprice); //Adds a factor that increases the variable bid price by rewarding bids with lower average bid price (average price of the offers that where accepted).
}
*/

//OLDER

// if (avrbidprice == 0 || this.getmyAgent().getphysicalnetposition() == 0 ) {return 0.0;} //Should not be the case
//averagepricenotaccepted = UpdatePhysicalPosition.returnboughtvolume(b, marketprice, shareofmarginalofferbought).getBoughtInSTMnotaccepted();
//double ret = ((1/avrbidprice)*(boughtcerts/(Math.abs(this.getmyAgent().getphysicalnetposition())))) + ((averagepricenotaccepted - marketprice)/(avrbidprice*100*(Math.abs(this.getmyAgent().getphysicalnetposition())))); //Buy as many as possible but with an average bid price as close to market price as possible. 
//return ret; // (Avrprice is the avr price for the offers bids that where accepted)

//The first term is for the bids being sold. This ensures the buyer to reduce his price as long as the offers are accepted. Furhter the devision on the agents physical position is to ensure 
//that fluctuating demand does not change the the utility. The utility an learnign should be independent of the demand from the market. 
//The second term for ensuring offers far below market price are increase towards market price. THis term also needs division so that this term is not 
//wheighting more than the first term. (abs() on physcal posistion as the physical position of the OPA is negative).
