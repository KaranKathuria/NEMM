/*
 * Version info:
 *     Set of methods for calculating utilities, both for tactics and strategies. These utilities are agents specific. 
 *     
 *     Last altered data: 20140822
 *     Made by: Karan Kathuria
 */
 
package nemmstrategy_shortterm;

import java.util.ArrayList;
import nemmprocesses.UpdatePhysicalPosition;


//Class definition
public class OPAUtilityMethod extends GenericUtilityMethod{

	public OPAUtilityMethod() {
	}
	
	//Takes in the given values and calculates the producers utility just based on what maximizes value of certificates sold. 
	public Double calculateutility(double marketprice, ArrayList<BuyOffer> b, ArrayList<SellOffer> s, double shareofmarginaltoffersold, double shareofmarginalofferbought) {
		double boughtcerts;
		double avrbidprice;
		//double averagepricenotaccepted;
		double phys = Math.abs(this.getmyAgent().getphysicalnetposition());
		double ret;
		if (phys == 0) { //If this is the case, then nothing is buy because it no demand.
			return 1.0;}

		boughtcerts = UpdatePhysicalPosition.returnboughtvolume(b, marketprice, shareofmarginalofferbought).getBoughtInSTMcert();
		avrbidprice = UpdatePhysicalPosition.returnboughtvolume(b, marketprice, shareofmarginalofferbought).getBoughtInSTMprice();

		if ((boughtcerts/phys) < 1) { 
			ret = (boughtcerts/phys); //If we did not manage to buy all demand. Than utility is given by volume. Unchanged volume ensures that the tactic increase price.
		// Could ad something to ensure utility in right direction when variable price is below market price
		} else { //Only if everything is bought
			ret = 1 + (1/avrbidprice); //Adds a factor that increases the lower the avrbidprice (the average price of the offers that where accepted).
		}
		return ret;
		

	}

}
	
	
//OLD

// if (avrbidprice == 0 || this.getmyAgent().getphysicalnetposition() == 0 ) {return 0.0;} //Should not be the case
//averagepricenotaccepted = UpdatePhysicalPosition.returnboughtvolume(b, marketprice, shareofmarginalofferbought).getBoughtInSTMnotaccepted();
//double ret = ((1/avrbidprice)*(boughtcerts/(Math.abs(this.getmyAgent().getphysicalnetposition())))) + ((averagepricenotaccepted - marketprice)/(avrbidprice*100*(Math.abs(this.getmyAgent().getphysicalnetposition())))); //Buy as many as possible but with an average bid price as close to market price as possible. 
//return ret; // (Avrprice is the avr price for the offers bids that where accepted)

//The first term is for the bids being sold. This ensures the buyer to reduce his price as long as the offers are accepted. Furhter the devision on the agents physical position is to ensure 
//that fluctuating demand does not change the the utility. The utility an learnign should be independent of the demand from the market. 
//The second term for ensuring offers far below market price are increase towards market price. THis term also needs division so that this term is not 
//wheighting more than the first term. (abs() on physcal posistion as the physical position of the OPA is negative).