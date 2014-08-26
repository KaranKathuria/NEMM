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
public class OPAUtilityMethod extends GenericUtilityMethod{

	public OPAUtilityMethod() {
	}
	
	//Takes in the given values and calculates the producers utility just based on what maximizes value of certificates sold. 
	public Double calculateutility(double marketprice, ArrayList<BuyOffer> b, ArrayList<SellOffer> s, int physicalposition, double shareofmarginaltoffersold, double shareofmarginalofferbought) {
		int soldcert;
		double avrbidprice;
		double ret;
		soldcert = GenericStrategy.returnboughtvolume(b, marketprice, shareofmarginalofferbought).getBoughtInSTMcert();
		avrbidprice = GenericStrategy.returnboughtvolume(b, marketprice, shareofmarginalofferbought).getBoughInSTMprice();
		ret = ( 0.7 * soldcert ) - (soldcert * 0.5 * (avrbidprice/marketprice)); //Buy as many as possible but with an average bid price as low as possible.
		return ret;}

}
	
	
