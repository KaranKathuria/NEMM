/*
 * Version info:
 *     Object soldInSTM, returns what is sold in the market. 
 *     
 *     Last altered data: 20140811
 *     Made by: Karan Kathuria
 */

package nemmstrategy_shortterm;

public class SoldInSTM {
	
	public double numberofcert; //Needs to be public so this class can be reused.
	public double averageprice; //Not the average price per say as the price would be the marketprice for all (uniform bidding market). 
	public double notacceptavrprice; //The avr bid price of the bids that did not get acceptet (lower then market price)

	// Nevertheless this is usefull as the average price of offers bid that where accepted indicates how "close" the bidding is and is futher
	//used by the utility methods for OPA.
	
	//when created the class instance is constructed with zero-values.
	public SoldInSTM() {
		numberofcert = 0;
		averageprice = 0.0;
		notacceptavrprice = 0.0;
	}
	public double getSoldInSTMprice() {
		return averageprice;}
	public double getSoldInSTMcert() {
		return numberofcert;}
	public double getSoldInSTMnotaccepted() {
		return notacceptavrprice;}
	

	}