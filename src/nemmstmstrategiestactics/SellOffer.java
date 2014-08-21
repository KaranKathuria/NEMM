/*
 * Version info:
 *     Object SellOffer
 *     
 *     Last altered data: 20140811
 *     Made by: Karan Kathuria
 */

package nemmstmstrategiestactics;

public class SellOffer {
	
	public int numberofcert;
	public double price;
	
	//when created the class instance is constructed with zero-values.
	public SellOffer() {
		numberofcert = 0;
		price = 1.0;
	}
	public Double getSellOfferprice() {
		return price;
	}
	public int getnumberofcert() {
		return numberofcert;
	}
}
