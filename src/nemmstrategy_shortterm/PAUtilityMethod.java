/*
 * Version info:
 *     Set of methods for calculating utilities, both for tactics and strategies. These utilities are agents specific. 
 *     
 *     Last altered data: 20140822
 *     Made by: Karan Kathuria
 */

package nemmstrategy_shortterm;

import java.util.ArrayList;

import nemmagents.CompanyAgent.ActiveAgent;
import nemmprocesses.UpdatePhysicalPosition;

//Class definition
public class PAUtilityMethod extends GenericUtilityMethod{
	
	public PAUtilityMethod() {}
	
	//Takes in the given values and calculates the producers utility just based on what maximizes value of certificates sold. 
	public Double calculateutility(double marketprice, ArrayList<BuyOffer> b, ArrayList<SellOffer> s, double shareofmarginaltoffersold, double shareofmarginalofferbought) {
		double averageprice;
		double soldcerts;
		//double averagepricenotaccepted;
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
			ret = 1 + (averageprice/marketprice); //Adds a factor that increases the lower the avrbidprice (the average price of the offers that where accepted).
		}
		
		return ret;
		
	}	
}

//OLD
//double ret = ((averageprice*soldcerts/Math.abs(this.getmyAgent().getphysicalnetposition()))) - ((averagepricenotaccepted - marketprice)/(Math.abs(this.getmyAgent().getphysicalnetposition()*10))); 
//Division on abs(physicalnetposition) to make utililty independent on the production and the physical volume. Its the volume sold per possible sold that should determine the utility. 
//Last term for occurences where the variable bids is not sold, hence the utility function must give incentives to reduce price. 
	
