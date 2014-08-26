/*
 * Version info:
 *     Object BuyOffer
 *     
 *     Last altered data: 20140811
 *     Made by: Karan Kathuria
 */

package nemmstrategy_shortterm;

public class SoldInSTM {
	
	public int numberofcert; //Needs to be public so this class can be reused.
	public double averageprice; //Not the average price per say as the price would be the marketprice for all (uniform bidding market). 
	// Nevertheless this is usefull as the average price of offers bid that where accepted indicates how "close" the bidding is and is futher
	//used by the utility methods for OPA.
	
	//when created the class instance is constructed with zero-values.
	public SoldInSTM() {
		numberofcert = 0;
		averageprice = 1.0;
	}
	public Double getSoldInSTMprice() {
		return averageprice;
	}
	public int getSoldInSTMcert() {
		return numberofcert;}
	
	//public double getSoldInSTMvalue() {
	//	return numberofcert*averageprice;
	//}
	
		}