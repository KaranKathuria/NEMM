/*
 * Version info:
 *     File defining a sell strategy. A sell strategy takes price expectations and physical position as input and produced a list of buy offers, by using its best tactic. 
 *     This strategy uses double expectedprice, int certdemand but could in future take physical position object as input. 
 *     
 *     Last altered data: 20140813
 *     Made by: Karan Kathuria
 */
package nemmstrategy_shortterm;

import java.util.ArrayList;
import java.util.Random;

import repast.simphony.random.RandomHelper;
import nemmcommons.AllVariables;
import nemmcommons.RandomWrapper;
import nemmstrategy_shortterm.GenericStrategy.*;


public class TradeStrategy1 extends GenericStrategy {
	
	//A simple buy strategy creating one sell offer and one buy offer. Buying at a discount and selling at a premium around the expected price. 
	
	
	private SellOffer sellofferone;
	private SellOffer selloffertwo;
	private BuyOffer buyofferone;
	private BuyOffer buyoffertwo;
	private double maximumshortpos; //Number of certs a trader using this strategy can be short
	private double maximumlongpos;	//Number of certs a trader using this strategy can be long
	
	//Constructor for TradeStrategy1
	public TradeStrategy1() {
		
		strategyname = "TradeStrategy1";
		strategyutilityscore.add(0.0);
		numberoftactics = AllVariables.numberoftacticsTraderStrategy1;
		maximumshortpos = AllVariables.tradermaximumshortpos;
		maximumlongpos = AllVariables.tradermaximumlongpos;
		
		//Adds four tactics with differen values of discount and premium. This is less sophisticated. 
		for (int i = 0; i < numberoftactics; ++i) {
			double randompremium = ((RandomHelper.nextDouble())-0.3)/2; // Gives a random number between -0.1.5 and 0.35
			double randomdiscount = ((RandomHelper.nextDouble())-0.3)/2; //Gives a random number between -0.1.5 and 0.35
			//Special case ensuring that a trader does not offers a sellprice lower than buy price
			while ((1+randompremium) < (1-randomdiscount)) {
				randompremium = randompremium + 0.01;
				randomdiscount = randomdiscount + 0.01; 
			}
			TradeStrategy1Tactic tactic = new TradeStrategy1Tactic(randompremium, randomdiscount, maximumshortpos, maximumlongpos);
			tactic.setmyStrategy(TradeStrategy1.this);
			alltactics.add(tactic);
		}
		
		besttactic = alltactics.get(RandomHelper.nextIntFromTo(0, numberoftactics-1)); //Randomly selects the initial best strategy.
		
		sellofferone = new SellOffer();
		selloffertwo = new SellOffer();
		buyofferone = new BuyOffer();
		buyoffertwo = new BuyOffer();
		agentsselloffers.add(sellofferone);
		agentsselloffers.add(selloffertwo);
		agentsbuyoffers.add(buyofferone);
		agentsbuyoffers.add(buyoffertwo);

	}	

	// Clears strategies buyoffers, updates offers from all tactics. set strategys buyoffers to those of the best tactic. 
	public void updateBidsAndOffers() {
		//Updates all tactics
		for (int i = 0; i < numberoftactics; ++i) {
			alltactics.get(i).updatetactictradeoffers();}
		// Updates this strategies buyoffers and selloffers based on the best tactic (which in turn is given from the previous round).
		agentsbuyoffers.clear();
		agentsselloffers.clear();
		sellofferone = besttactic.getsellofferone();
		selloffertwo = besttactic.getselloffertwo();
		buyofferone = besttactic.getbuyofferone();
		buyoffertwo = besttactic.getbuyoffertwo();
		agentsselloffers.add(sellofferone);
		agentsselloffers.add(selloffertwo);
		agentsbuyoffers.add(buyoffertwo);
		agentsbuyoffers.add(buyofferone);
	
	}
	

} // END CLASS


	

	