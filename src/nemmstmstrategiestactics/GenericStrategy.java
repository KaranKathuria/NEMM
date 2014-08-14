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

import nemmstmstrategiestactics.BuyOffer;
import nemmstmstrategiestactics.SellOffer;

// 
public abstract class GenericStrategy {
	
	//Variables. Notice that the generic strategy only contains the array of buy and sell offers and not the buy or sell offers itself because the number of these vary with the strategy. 
	protected ArrayList<BuyOffer> agentsbuyoffers = new ArrayList<BuyOffer>();
	protected ArrayList<SellOffer> agentsselloffers = new ArrayList<SellOffer>();
	protected int numberoftactics;
	protected double strategyutility;
	protected double[] historicstrategyutility;
	protected String strategyname; 
	

	//Constructor for parant class. Not sure about this. This construction will note be used as this class is abstract. 
	public GenericStrategy() {}
			
	public ArrayList<BuyOffer> getAgentsBuyOffers() {
		return agentsbuyoffers;
	}
	
	public ArrayList<SellOffer> getAgentsSellOffers() {
		return agentsselloffers;
	}
	public void updatealloffers(double expectedprice, int physicaldemand) {
		//should be overritten in subclass
	}
	
	}	
	