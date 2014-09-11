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
	public double averageprice; 	//Not the average price per say as the price would be the marketprice for all (uniform bidding market).
	public double notacceptavrprice; //The avr bid price of the bids that did not get acceptet (lower then market price)
	
 
	// Nevertheless this is usefull as the average price of offers bid that where accepted indicates how "close" the bidding is and is futher
	//used by the utility methods for OPA.
	
	//when created the class instance is constructed with zero-values.
	public BoughtInSTM() {
		numberofcert = 0;
		averageprice = 0.0;
		notacceptavrprice = 0.0;
		
	}
	public double getBoughtInSTMprice() {
		return averageprice;}
	public double getBoughtInSTMcert() {
		return numberofcert;}
	public double getBoughtInSTMnotaccepted() {
		return notacceptavrprice;}
	

	//public double getBoughtInSTMvalue() { This method does not make sense as the capture price for certificates is the market price. 
	//return numberofcert*averageprice;
//}
}