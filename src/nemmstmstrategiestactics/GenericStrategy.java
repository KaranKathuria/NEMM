/*
 * Version info:
 *     File defining a generic abstract (parent) class strategies. A Strategy is a way of forming buy and sell offers based on you physical position. 
 *     The parent agent 
 *     
 *     Last altered data: 20140811
 *     Made by: Karan Kathuria
 */
package nemmstmstrategiestactics;

import java.util.ArrayList;

import nemmagents.CompanyAgent.ActiveAgent;
import nemmcommons.CommonMethods;
import nemmstmstrategiestactics.BuyOffer;
import nemmstmstrategiestactics.SellOffer;

// 
public abstract class GenericStrategy {
	
	//Variables. Notice that the generic strategy only contains the array of buy and sell offers and not the buy or sell offers itself because the number of these vary with the strategy. 
	protected ArrayList<BuyOffer> agentsbuyoffers = new ArrayList<BuyOffer>();
	protected ArrayList<SellOffer> agentsselloffers = new ArrayList<SellOffer>();
	protected int numberoftactics;
	protected ArrayList<Double> strategyutilityscore = new ArrayList<Double>();
	protected String strategyname; 
	protected ArrayList<GenericTactic> alltactics = new ArrayList<GenericTactic>();
	protected GenericTactic besttactic = null;

	

	//Constructor for parant class. Not sure about this. This construction will note be used as this class is abstract. 
	public GenericStrategy() {}
			
	public ArrayList<BuyOffer> getAgentsBuyOffers() {
		return agentsbuyoffers;
	}
	
	public ArrayList<SellOffer> getAgentsSellOffers() {
		return agentsselloffers;
	}
	public void updatealloffers(double expectedprice, int physicalposition) {
		//should be overritten in subclass
	}
	// The following to methods estimates the result of a buy or sell offer array in the market. Hence it takes inn the offers, the outcome price and a "shareoflastoffer sold/bought. THe latter
	//is for splitting the volume sold or bought which cannot be bought all because it not enough on the other side of the market. The only problem below are the cases where the access volume
	// (the volume which has a price that means accepted, but the volume on the other side is limited) is larger than the volume bought or sold at the market price, so that dropping a share of the
	// highest priced bid on the access side is not enough to balance the market. For now this is not taken care of. 
	public static SoldInSTM returnsoldvolume(ArrayList<SellOffer> aso, double marketprice, double shareoflastoffersold) { //Method calculation the outcome of a selloffers array offered in a STM market
		SoldInSTM ret = new SoldInSTM();
		int soldcerts = 0;
		double averageprice = 0;
		for (SellOffer s : aso) {
			if (s.getSellOfferprice() < marketprice) { //In this case the offers was accepted in the market
			averageprice = ((averageprice*soldcerts)+(s.getnumberofcert()*s.getSellOfferprice()))/(soldcerts+s.getnumberofcert()); //The new average price
			soldcerts = soldcerts + s.getnumberofcert();	//Total number of certs sold is updated
			}
			if (s.getSellOfferprice() == marketprice) {
				averageprice = ((averageprice*soldcerts)+(s.getnumberofcert()*s.getSellOfferprice()*shareoflastoffersold))/(soldcerts+(s.getnumberofcert()*shareoflastoffersold)); //The new average price
				soldcerts = soldcerts + s.getnumberofcert();	//Total number of certs sold is updated
			}}
			ret.averageprice = averageprice;
			ret.numberofcert = soldcerts;
		return ret;
	}
	
	public static BoughtInSTM returnboughtvolume(ArrayList<BuyOffer> abo, double marketprice, double shareoflastofferbought) { //Method calculation the outcome of a selloffers array offered in a STM market
		BoughtInSTM ret = new BoughtInSTM();
		int boughtcerts = 0;
		double averageprice = 0;
		for (BuyOffer b : abo) {
			if (b.getBuyOfferprice() > marketprice) { //In this case the offers was accepted in the market
			averageprice = ((averageprice*boughtcerts)+(b.getnumberofcert()*b.getBuyOfferprice()))/(boughtcerts+b.getnumberofcert()); //The new average price
			boughtcerts = boughtcerts + b.getnumberofcert();	//Total number of certs sold is updated
			}
			if (b.getBuyOfferprice() == marketprice) {
				averageprice = ((averageprice*boughtcerts)+(b.getnumberofcert()*b.getBuyOfferprice()*shareoflastofferbought))/(boughtcerts+(b.getnumberofcert()*shareoflastofferbought)); //The new average price
				boughtcerts = boughtcerts + b.getnumberofcert();	//Total number of certs sold is updated
			}}
			ret.averageprice = averageprice;
			ret.numberofcert = boughtcerts;
		return ret;
	}
	
	//Used when adding/updating to the strategyutility ArrayList with the new score. 
	public void addstrategyutilityscore(double t) {
		strategyutilityscore.add(t);}
	
	public int getnumberoftactics() {
		return numberoftactics;}
	
	public GenericTactic getbesttactic() {return besttactic;}
	
	public ArrayList<GenericTactic> getalltactics() {
		return alltactics;}
	
    public void updatebesttactic(GenericTactic t) {
    	besttactic = t;}
	
	public double getsumofstrategyutility() {
		double ret = 0;
		for (int i = 0; i < strategyutilityscore.size(); ++i) {
			ret = ret + strategyutilityscore.get(i);
		}
		return ret;}
	
}

	