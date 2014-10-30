/*
 * Version info:
 *     Object SellOffer
 *     
 *     Last altered data: 20140811
 *     Made by: Karan Kathuria
 */

package nemmstrategy_shortterm;

public class SellOffer implements Comparable<SellOffer>{
	
	private double numberofcert;
	private double price;
	private double[] offerUtility; // used if required to remember the utility values for the sell offer
	
	//when created the class instance is constructed with zero-values.
	public SellOffer() {
		numberofcert = 0;
		price = 0;
	}
	
	// ---- Comparator
	
	@Override
	public int compareTo(SellOffer o) {
		// Compare based on utility
		int lastCmp;
		if (price == o.getSellOfferprice()) {lastCmp = 0;}
		else if (price < o.getSellOfferprice()) {lastCmp = -1;}
		else {lastCmp = 1;}
		return lastCmp;
	}
	
	// ---- Gets & Sets
	public Double getSellOfferprice() {
		return price;
	}
	public double getnumberofcert() {
		return numberofcert;
	}
	public void setsellofferprice(double d){
		price = d;
	}
	public void setselloffervol(double d){
		numberofcert = d;
	}
	public double[] getOfferUtility() {
		return offerUtility;
	}
	public void setOfferUtility(double[] offerUtility) {
		this.offerUtility = offerUtility;
	}
}
