/*
 * Version info:
 *     Set of methods for calculating utilities, both for tactics and strategies. These utilities are agents specific. 
 *     
 *     Last altered data: 20140822
 *     Made by: Karan Kathuria
 */

package nemmstrategy_shortterm;

import java.util.ArrayList;

import nemmstrategy_shortterm.BoughtInSTM;
import nemmstrategy_shortterm.SoldInSTM;

//Class definition
public class PAUtilityMethod extends GenericUtilityMethod{

	public PAUtilityMethod() {
	}
	
	//Takes in the given values and calculates the producers utility just based on what maximizes value of certificates sold. 
	public Double calculateutility(double marketprice, ArrayList<BuyOffer> b, ArrayList<SellOffer> s, int physicalposition, double shareofmarginaltoffersold, double shareofmarginalofferbought) {
		double ret;
		ret = GenericStrategy.returnsoldvolume(s, marketprice, shareofmarginaltoffersold).getSoldInSTMcert() * marketprice;
		return ret;
		}
}
	
	
