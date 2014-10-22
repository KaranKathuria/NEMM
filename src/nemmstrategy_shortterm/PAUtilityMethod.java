/*
 * Version info:
 *     Set of methods for calculating utilities, both for tactics and strategies. These utilities are agent specific. 
 *     
 *     Last altered data: 20140822
 *     Made by: Karan Kathuria
 */

package nemmstrategy_shortterm;

import java.util.ArrayList;
import java.util.Collections;

import nemmcommons.CommonMethods;
import nemmprocesses.UpdatePhysicalPosition;

//Class definition
public class PAUtilityMethod extends GenericUtilityMethod{

	// Class level variables
	private int flagUtilityFunction;
	
	// Specific variables used for utility 2 (expected return)
	private double alpha;

	private int flagFirstPd; // the first period has special rules for setting expectedProfit and expectedActivation

	// Constructor
	public PAUtilityMethod(int functionFlag) {
		if (functionFlag < 1 || functionFlag > 2){
			throw new IllegalArgumentException("DEBUG: Illegal flagUtilityFunction in PAUtilityMethod. Val = " + functionFlag);
		}
		flagUtilityFunction = functionFlag;
		switch (flagUtilityFunction){
			case 1:
				// No specific initialisation required
			case 2:
				alpha = 0.5;
				flagFirstPd = 1;
		}
	}

	public ArrayList<double[]> CalcUtilityWithHistory(double marketprice, ArrayList<SellOffer> s, double shareofmarginaltoffersold) {
		
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

		case 2:
			retList = UtilFctn_ExpReturn( marketprice, s, shareofmarginaltoffersold);	
		}
		return retList;
	}	
	
	
	public Double calculateutility(double marketprice, ArrayList<BuyOffer> b, ArrayList<SellOffer> s, double shareofmarginaltoffersold, double shareofmarginalofferbought) {
	
		double retVal = 0;
		switch (flagUtilityFunction){
		case 1:
			retVal = UtilFctn_VolOnly( marketprice, s, shareofmarginaltoffersold);
		case 2:
			retVal = 0.0; // should not be using this function if you want the expected return utility
			
		}
		return retVal;
	}
	
	private ArrayList<double[]> UtilFctn_ExpReturn(double priceSpot, ArrayList<SellOffer> s, double shareofmarginaltoffersold) {

		double curProfit = 0;
		double curActivation = 0;
		double curOfferVol = 0;
		double curOfferPrice = 0;
		double curOfferSold = 0;
		
		ArrayList<double[]> retList = new ArrayList<double[]>();
		double[] tmpArray;
		int i;
		
		// Initialise the return array list with null objects.
		for ( i=0;i<s.size();i++) {
			retList.add(null);
		}
		
		for ( i=0;i<s.size();i++) {
			if(s.get(i) != null) {
				// There is a sell offer, so we need to calculate the new utility measures
				
				// Initialise the utilities to be the previous values
				tmpArray = null;
				if(s.get(i).getOfferUtility()!=null){
					tmpArray = s.get(i).getOfferUtility().clone();
				}				
				// Calculate the new utility if there was an offer made.
				// If not offer was made, we just keep the previous utility
				curOfferVol = s.get(i).getnumberofcert(); 
				curOfferPrice = s.get(i).getSellOfferprice();
				if (curOfferVol > 0) {
					// Calculate the volume sold from the offer if an offer was made (vol >0)
					if (curOfferPrice<priceSpot){
						curOfferSold = curOfferVol;
					}
					else if (curOfferPrice == priceSpot) {
						curOfferSold = curOfferVol*shareofmarginaltoffersold;
					}
					else {
						curOfferSold = 0;
					}
					curProfit = curOfferSold*curOfferPrice; // Profit
					curActivation = curOfferSold/curOfferVol; // Activation
					// Exponential smoothing
					if (tmpArray != null) {
						tmpArray[1] = tmpArray[1] + alpha*(curProfit-tmpArray[1]); // Profit
						tmpArray[2] = tmpArray[2] + alpha*(curActivation-tmpArray[2]); // Activation
						tmpArray[0] = tmpArray[1]*tmpArray[2]; // Return						
					}
					else {
						// This will occur if there has been no utility set for this sell offer as yet
						// In this case, we will simply set the utilities to be the current offer's profit
						// and activation and return
						tmpArray = new double[3];
						tmpArray[1] = curProfit;
						tmpArray[2] = curActivation;
						tmpArray[0] = tmpArray[1]*tmpArray[2];
					}
						

				}
				// Add a copy of the result to the return ArrayList. We use a copy here because
				// we will re-use the tmpArray object
				retList.set(i,tmpArray.clone());
			}
				
		}
		return retList;
		
	}
	
	//Takes in the given values and calculates the producers utility just based on how much of the variable sell offers are accepted in the short term market.
	private Double UtilFctn_VolOnly(double marketprice, ArrayList<SellOffer> s, double shareofmarginaltoffersold) {
		
		double variableoffervolume = 0; //Offered volume at variable price. This is the sum of all offers but not the must sell offer (lowest priced offer)
		double totalsoldcerts = 0;	//Total number of certs sold
		double variablesoldtcerts = 0; //Total number of certs sold at variable price (not including must buy offer)
		//double avrbidprice = 0;

		s.removeAll(Collections.singleton(null)); //removes null bids (should not be the case, but...)
		
		if (s.size() < 2) { //In this case there is no variable bid or there is no bids at all (which could be because the PA has no volume to offer) Then utility is <0,1> so prices are unchanged
			return 0.5;} //Just any number between 0 and 1 is fine as this would imply that the tactic is unchanged.
			
		Collections.sort(s, new CommonMethods.customselloffercomparator()); //Sorts offers form lowest to highest price. The first bid is hence the must sell bid
		
		for (int i = 1; i < s.size(); ++i) { //Skips the first bid as this is must sell bid
			variableoffervolume = variableoffervolume + s.get(i).getnumberofcert(); //Volume of variable offers
		}
		
		totalsoldcerts = UpdatePhysicalPosition.returnsoldvolume(s, marketprice, shareofmarginaltoffersold).getSoldInSTMcert();
		variablesoldtcerts = totalsoldcerts - s.get(0).getnumberofcert(); //Minus the must buy which would always be the first offer to be bought

		double ret = (variablesoldtcerts/variableoffervolume); //Should always be a number [0,1]
		if (ret > 1 && ret < 0) {
			throw new IllegalArgumentException("Something is wrong with the volume offered");
		}
		return ret;
		
	}	
}


//OLD

/*
double ret;
double phys = Math.abs(this.getmyAgent().getphysicalnetposition());
if (phys == 0) { //If this is the case, then nothing is sold because it is nothing to sell. 
	return 1.0;}
		
soldcerts = UpdatePhysicalPosition.returnsoldvolume(s, marketprice, shareofmarginaltoffersold).getSoldInSTMcert();
averageprice = UpdatePhysicalPosition.returnsoldvolume(s, marketprice, shareofmarginaltoffersold).getSoldInSTMprice();
//averagepricenotaccepted = UpdatePhysicalPosition.returnsoldvolume(s, marketprice, shareofmarginaltoffersold).getSoldInSTMnotaccepted();
if ((soldcerts/phys) < 1) { 
	ret = (soldcerts/phys); //If we did NOT manage to sell all production. Then utility is given by how much we sold. Unchanged volume ensures that the tactic decrease prices.
} else { //Only if everything is sold, then the best is where the sellbid is close to marketprice
	ret = 1 + ((averageprice/marketprice)*(1/100)); //Adds a factor that increases the lower the avrbidprice (the average price of the offers that where accepted).
//The 1/100 factor is added to ensure that the "st�rrelsessorden" of the access term is not large compared to the vloume sold term which is much more importan.
// This because the best tactic is based on a number of historic utilities and a high increase in due to the latter term would make the tacticselection "wrong".
}
*/


//OLD
//double ret = ((averageprice*soldcerts/Math.abs(this.getmyAgent().getphysicalnetposition()))) - ((averagepricenotaccepted - marketprice)/(Math.abs(this.getmyAgent().getphysicalnetposition()*10))); 
//Division on abs(physicalnetposition) to make utililty independent on the production and the physical volume. Its the volume sold per possible sold that should determine the utility. 
//Last term for occurences where the variable bids is not sold, hence the utility function must give incentives to reduce price. 
	
