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
	
	public PAUtilityMethod() {}
	
	//Takes in the given values and calculates the producers utility just based on how much of the variable sell offers are accepted in the short term market.
	public Double calculateutility(double marketprice, ArrayList<BuyOffer> b, ArrayList<SellOffer> s, double shareofmarginaltoffersold, double shareofmarginalofferbought) {
		
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
//The 1/100 factor is added to ensure that the "størrelsessorden" of the access term is not large compared to the vloume sold term which is much more importan.
// This because the best tactic is based on a number of historic utilities and a high increase in due to the latter term would make the tacticselection "wrong".
}
*/


//OLD
//double ret = ((averageprice*soldcerts/Math.abs(this.getmyAgent().getphysicalnetposition()))) - ((averagepricenotaccepted - marketprice)/(Math.abs(this.getmyAgent().getphysicalnetposition()*10))); 
//Division on abs(physicalnetposition) to make utililty independent on the production and the physical volume. Its the volume sold per possible sold that should determine the utility. 
//Last term for occurences where the variable bids is not sold, hence the utility function must give incentives to reduce price. 
	
