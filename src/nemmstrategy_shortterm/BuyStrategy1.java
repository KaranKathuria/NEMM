/*
 * Version info:
 *     File defining a buy strategy. A buy strategy takes price expectations and physical position as input and produced a list of buy offers, by using its best tactic. 
 *     This strategy uses double expectedprice, int certdemand but could in future take physical position object as input. 
 *     
 *     Last altered data: 20140811
 *     Made by: Karan Kathuria
 */
package nemmstrategy_shortterm;

import java.util.ArrayList;
import java.util.Random;

import repast.simphony.random.RandomHelper;
import nemmcommons.AllVariables;
import nemmcommons.RandomWrapper;



public class BuyStrategy1 extends GenericStrategy {
	
	private BuyOffer buyofferone;
	private BuyOffer buyoffertwo;
	
	//Constructor for OPABidstrategy adding the four offers to arraylist of offers.
	public BuyStrategy1() {
		
		strategyname = "BuyStrategy1";
		strategyutilityscore.add(0.0);
		numberoftactics = AllVariables.numberoftacticsBuyStrategy1;
		numberofmonthsmaxpp = 12; //This means that the maximum pp equalt the next twelve months expected demand.
		floorroofpricemultiplier = 1; //Indicates that this strategy uses floor/roof price
		
		//Adds four tactics with differen values of sbd and discount, and stores them in alltactics
		for (int i = 0; i < numberoftactics; ++i) {
			double randomshareboughtatdiscount = AllVariables.OPAgentmustsellshare;//(tacticstream.nextDouble());
			double randomdiscount = (RandomHelper.nextDouble()- 0.25); // between -0.25 and 0.75 starting point for the variable offer
			BuyStrategy1Tactic tactic = new BuyStrategy1Tactic(randomshareboughtatdiscount, randomdiscount);
			tactic.setdeltapricemultiplier(0.025+(i/50)); //Sets this to 0.025 in case only one tactic, but with multiple tactics we have tactics with bigger pricesteps
			tactic.setmyStrategy(BuyStrategy1.this);
			alltactics.add(tactic);
		}
		
		besttactic = alltactics.get(RandomHelper.nextIntFromTo(0,numberoftactics-1));
		
		buyofferone = new BuyOffer();
		buyoffertwo = new BuyOffer();
		agentsbuyoffers.add(buyofferone);
		agentsbuyoffers.add(buyoffertwo);

	}	

	// Clears strategies buyoffers, updates offers from all tactics. set strategys buyoffers to those of the best tactic. 
	public void updateBidsAndOffers() {
		//Updates all tactics
		for (int i = 0; i < numberoftactics; ++i) {
			alltactics.get(i).updatetacticbuyoffers();}

		agentsbuyoffers.clear();
		agentsselloffers.clear();
		buyofferone = besttactic.getbuyofferone();
		buyoffertwo = besttactic.getbuyoffertwo();
		agentsbuyoffers.add(buyofferone);
		agentsbuyoffers.add(buyoffertwo);
		
	}
	
} // END CLASS
	
	

	