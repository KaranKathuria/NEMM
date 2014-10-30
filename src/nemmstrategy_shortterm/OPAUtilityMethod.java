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

import nemmcommons.CommonMethods;
import nemmprocesses.UpdatePhysicalPosition;


//Class definition
public class OPAUtilityMethod extends GenericUtilityMethod{

	// Class level variables
	private int flagUtilityFunction;
	
	
	// Constructor
	public OPAUtilityMethod(int functionFlag) {
		if (functionFlag < 1 || functionFlag > 2){
			throw new IllegalArgumentException("DEBUG: Illegal flagUtilityFunction in OPAUtilityMethod. Val = " + functionFlag);
		}
		flagUtilityFunction = functionFlag;

	}
	
	//Takes in the given values and calculates the producers utility based on how much of the offered volume at variable price (all bids without must buy bid) he managed to sell.
	public Double calculateutility(double marketprice, ArrayList<BuyOffer> b, ArrayList<SellOffer> s, double shareofmarginaltoffersold, double shareofmarginalofferbought) {
		
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
			variableoffervolume = variableoffervolume + b.get(i).getnumberofcert(); //Volume of variable offers
		}
		
		totalboughtcerts = UpdatePhysicalPosition.returnboughtvolume(b, marketprice, shareofmarginalofferbought).getBoughtInSTMcert();
		variableboughtcerts = totalboughtcerts - b.get(lastindx).getnumberofcert(); //Minus the must buy which would always be the first offer to be bought

		double ret = (variableboughtcerts/variableoffervolume); //Should always be a number [0,1]
		if (ret > 1 && ret < 0) {
			throw new IllegalArgumentException("Something is wrong with the volume offered");
		}
		return ret;
		

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