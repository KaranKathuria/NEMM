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
public class GenericUtilityMethod {
	
	public GenericUtilityMethod() {}
	
	//Method for scoring utility to be overwritten by subclass methods with same name. 
	public Double calculateutility(double marketprice, ArrayList<BuyOffer> b, ArrayList<SellOffer> s, int physicalposition, double shareofmarginaltoffersold, double shareofmarginalofferbought) {
		double ret = 1000;
		return ret;
	}

}
	
	
