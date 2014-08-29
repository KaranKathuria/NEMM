/*
 * Version info:
 *     File defining a generic abstract (parent) class strategies. A Strategy is a way of forming buy and sell offers based on you physical position. 
 *     The parent agent 
 *     
 *     Last altered data: 20140811
 *     Made by: Karan Kathuria
 */
package nemmstrategy_shortterm;

import java.util.ArrayList;

import nemmagents.CompanyAgent.ActiveAgent;
import nemmcommons.CommonMethods;
import nemmstrategy_shortterm.BuyOffer;
import nemmstrategy_shortterm.SellOffer;

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
	public void updatealloffers(double expectedprice, double physicalposition) {}
	
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

	