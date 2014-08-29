/*
 * Version info:
 *     Object SellOffer
 *     
 *     Last altered data: 20140811
 *     Made by: Karan Kathuria
 */

package nemmstrategy_shortterm;

public class SellOffer {
	
	public double numberofcert;
	public double price;
	
	//when created the class instance is constructed with zero-values.
	public SellOffer() {
		numberofcert = 0;
		price = 0;
	}
	public Double getSellOfferprice() {
		return price;
	}
	public double getnumberofcert() {
		return numberofcert;
	}
}
