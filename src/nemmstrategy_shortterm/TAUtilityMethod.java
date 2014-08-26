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
public class TAUtilityMethod extends GenericUtilityMethod{

	public TAUtilityMethod() {
		
	}
	
	//Calculates the traders tactic utilities. Overrides the GenericUtilityMethods methods. 
	public Double calculateutility(double marketprice, ArrayList<BuyOffer> b, ArrayList<SellOffer> s, int physicalposition, double shareofmarginaltoffersold, double shareofmarginalofferbought) {
		double ret;
		int soldcert;
		int boughtcert;
		soldcert = UpdatePhysicalPosition.returnsoldvolume(s, marketprice, shareofmarginaltoffersold).getSoldInSTMcert();
		boughtcert = UpdatePhysicalPosition.returnboughtvolume(b, marketprice, shareofmarginalofferbought).getBoughtInSTMcert();
		ret = boughtcert + soldcert; //Maximize volume.
		return ret;}
}
	
	
