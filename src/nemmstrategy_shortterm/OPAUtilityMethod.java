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
	public Double calculateutility(double marketprice, ArrayList<BuyOffer> b, ArrayList<SellOffer> s, double physicalposition, double shareofmarginaltoffersold, double shareofmarginalofferbought) {
		double boughtcerts;
		double avrbidprice;
		double averagepricenotaccepted;

		boughtcerts = UpdatePhysicalPosition.returnboughtvolume(b, marketprice, shareofmarginalofferbought).getBoughtInSTMcert();
		avrbidprice = UpdatePhysicalPosition.returnboughtvolume(b, marketprice, shareofmarginalofferbought).getBoughtInSTMprice();
		if (avrbidprice == 0) {return 0.0;} //Should not be the case
		averagepricenotaccepted = UpdatePhysicalPosition.returnboughtvolume(b, marketprice, shareofmarginalofferbought).getBoughtInSTMnotaccepted();
		return ((1/avrbidprice)*boughtcerts) + ((averagepricenotaccepted - marketprice)/(avrbidprice*10)); //Buy as many as possible but with an average bid price as close to market price as possible. (Avrprice is the avr price for the offers bids that where accepted)
		//Trenger ikke siste del siden utilitien �kes n�r prisene �kes. 
	}

}
	
	
