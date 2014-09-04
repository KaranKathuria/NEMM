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
import nemmstrategy_shortterm.BoughtInSTM;
import nemmstrategy_shortterm.SoldInSTM;

//Class definition
public class OPAUtilityMethod extends GenericUtilityMethod{

	public OPAUtilityMethod() {
	}
	
	//Takes in the given values and calculates the producers utility just based on what maximizes value of certificates sold. 
	public Double calculateutility(double marketprice, ArrayList<BuyOffer> b, ArrayList<SellOffer> s, double physicalposition, double shareofmarginaltoffersold, double shareofmarginalofferbought) {
		double boughtcerts;
		double avrbidprice;
		double ret;
		boughtcerts = UpdatePhysicalPosition.returnboughtvolume(b, marketprice, shareofmarginalofferbought).getBoughtInSTMcert();
		avrbidprice = UpdatePhysicalPosition.returnboughtvolume(b, marketprice, shareofmarginalofferbought).getBoughInSTMprice();
		ret = avrbidprice*boughtcerts; //Buy as many as possible but with an average bid price as close to market price as possible. (Avrprice is the avr price for the offers bids that where accepted)
		return ret;}

}
	
	
