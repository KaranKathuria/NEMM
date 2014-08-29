/*
 * Version info:
 *     Object BuyOffer
 *     
 *     Last altered data: 20140811
 *     Made by: Karan Kathuria
 */
 
package nemmstrategy_shortterm;

public class BoughtInSTM {
	
	public double numberofcert; //Needs to be public so this class can be reused.
	public double averageprice; //Not the average price per say as the price would be the marketprice for all (uniform bidding market). 
	// Nevertheless this is usefull as the average price of offers bid that where accepted indicates how "close" the bidding is and is futher
	//used by the utility methods for OPA.
	
	//when created the class instance is constructed with zero-values.
	public BoughtInSTM() {
		numberofcert = 0;
		averageprice = 1.0;
	}
	public Double getBoughInSTMprice() {
		return averageprice;
	}
	public double getBoughtInSTMcert() {
		return numberofcert;}

	//public double getBoughtInSTMvalue() { This method does not make sense as the capture price for certificates is the market price. 
	//return numberofcert*averageprice;
//}
}