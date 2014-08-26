/*
 * Version info:
 *     Object BuyOffer
 *     
 *     Last altered data: 20140811
 *     Made by: Karan Kathuria
 */

package nemmstrategy_shortterm;

public class BuyOffer {
	
	public int numberofcert; //Needs to be public so this class can be reused.
	public double price;
	
	//when created the class instance is constructed with zero-values.
	public BuyOffer() {
		numberofcert = 0;
		price = 0;
	}
	public Double getBuyOfferprice() {
		return price;
	}
	public int getnumberofcert() {
		return numberofcert;
	}
	}