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
public class PAUtilityMethod extends GenericUtilityMethod{

	public PAUtilityMethod() {
	}
	
	//Takes in the given values and calculates the producers utility just based on what maximizes value of certificates sold. 
	public Double calculateutility(double marketprice, ArrayList<BuyOffer> b, ArrayList<SellOffer> s, double physicalposition, double shareofmarginaltoffersold, double shareofmarginalofferbought) {
		double averageprice;
		double soldcerts;
		soldcerts = UpdatePhysicalPosition.returnsoldvolume(s, marketprice, shareofmarginaltoffersold).getSoldInSTMcert();
		averageprice = UpdatePhysicalPosition.returnsoldvolume(s, marketprice, shareofmarginaltoffersold).getSoldInSTMprice();
		return soldcerts*averageprice;
		}
}
	//Change to sum of vilume wieghted bid price for bids under market pruce 
	
