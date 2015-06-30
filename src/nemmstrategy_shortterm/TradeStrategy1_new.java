/*
 * Version info:
 *     File defining a sell strategy. A sell strategy takes price expectations and physical position as input and produced a list of buy offers, by using its best tactic. 
 *     This strategy uses double expectedprice, int certdemand but could in future take physical position object as input. 
 *     
 *     Update 20150630: Added some simplifyed logic. Fixed buy and sell volumes.
 *     
 *     Last altered data: 20140813
 *     Made by: Karan Kathuria
 */
package nemmstrategy_shortterm;

import java.util.ArrayList;

import repast.simphony.random.RandomHelper;
import nemmcommons.AllVariables;
import nemmstrategy_shortterm.GenericStrategy.*;


public class TradeStrategy1_new extends GenericStrategy {
	
	//A simple buy strategy creating one sell offer and one buy offer. Buying at a discount and selling at a premium around the expected price. 
	
	
	private BidOffer sellofferone;
	private BidOffer buyofferone;
	private double maximumshortpos; //Number of certs a trader using this strategy can be short
	private double maximumlongpos;	//Number of certs a trader using this strategy can be long

	
	//Sellstrategy variables borrowed from PA
	
	
	//Constructor for TradeStrategy1
	public TradeStrategy1_new() {
		// Run the super constructor
		super();
		
		//Buystrategy_variables borrowed from OPA
		double randomMultStep;
		double multRestPrice;
		double multRestPriceStep;
		
		int numRestSteps = AllVariables.numberoftacticsTraderStrategy1;
		int numRestSteps_additional = AllVariables.numberoftacticsTraderStrategy1;
		numberoftactics = numRestSteps;
		
		strategyname = "TradeStrategy1";
		strategyutilityscore.add(0.0);
		//Not in use
		maximumshortpos = AllVariables.tradermaximumshortpos;
		maximumlongpos = AllVariables.tradermaximumlongpos;
		//not in use end.
		
		for (int j=0; j < numRestSteps;j++) {
			
			// Determine the volume and price multipliers for the Rest offer
			// This is dependent on whether tactic-level learning is used
			// If yes, we set the initial multiplier values to be random
			// If not, we distribute the multiplier values uniformly over the min to max range
			if (AllVariables.tacticDefaultLearn_OPBuyStrategy1 > 0) {
				randomMultStep = RandomHelper.nextDouble()*(AllVariables.tacticMaxRestPriceMult_OPBuyStrategy1-
						AllVariables.tacticMinRestPriceMult_OPBuyStrategy1); // between (-0.25 - 0.75) //Initial starting point for the variable bid price. Is changed in the tactic later
				multRestPrice = AllVariables.tacticMinRestPriceMult_OPBuyStrategy1+randomMultStep;				
			}
			else if (numRestSteps==1) {
				// Set the multiplier to be halfway between the min & max
				multRestPrice = 0.5*(AllVariables.tacticMaxRestPriceMult_OPBuyStrategy1+
						AllVariables.tacticMinRestPriceMult_OPBuyStrategy1);
			}
			else {
				multRestPrice = AllVariables.tacticMinRestPriceMult_OPBuyStrategy1 + 
						j*(AllVariables.tacticMaxRestPriceMult_OPBuyStrategy1-
						AllVariables.tacticMinRestPriceMult_OPBuyStrategy1)/(numRestSteps-1);
			}
			
			// A trick workaround, sepcially for the first inputvariabel, the maxbuyshare. Since the traders does not nessacery have that!.
			BuyStrategy1Tactic tactic = new BuyStrategy1Tactic(1, 0, 0, multRestPrice, AllVariables.tacticDefaultLearn_OPBuyStrategy1);
			
			// Set the step size for the rest price multiplier (used if learning turned on) 
			multRestPriceStep = RandomHelper.nextDouble()*(AllVariables.tacticMaxRestPriceStep_OPBuyStrategy1 - 
					AllVariables.tacticMinRestPriceStep_OPBuyStrategy1) + AllVariables.tacticMinRestPriceStep_OPBuyStrategy1;
			tactic.setdeltapricemultiplier(multRestPriceStep); //Sets this to 0.025 in case only one tactic, but with multiple tactics we have tactics with bigger pricesteps
			
			// Tell the tactic who's the Daddy
			tactic.setmyStrategy(TradeStrategy1_new.this);
			
			// And add it to the pile
			alltactics.add(tactic);
		}
		
		for (int j=0; j < numRestSteps_additional;j++) {
			
			// Determine the volume and price multpliers for the Rest offer
			// This is dependent on whether tactic-level learning is used
			// If yes, we set the initial multiplier values to be random
			// If not, we distribute the mutiplier values uniformly over the min to max range
			if (AllVariables.tacticDefaultLearn_PASellStrategy1 > 0) {
				randomMultStep = RandomHelper.nextDouble()*(AllVariables.tacticMaxRestPriceMult_PASellStrategy1-
						AllVariables.tacticMinRestPriceMult_PASellStrategy1); // between (-0.25 - 0.75) //Initial starting point for the variable bid price. Is changed in the tactic later
				multRestPrice = AllVariables.tacticMinRestPriceMult_PASellStrategy1+randomMultStep;				
			}
			else if (numRestSteps==1) {
				// Set the multiplier to be halfway between the min & max
				multRestPrice = 0.5*(AllVariables.tacticMaxRestPriceMult_PASellStrategy1+
						AllVariables.tacticMinRestPriceMult_PASellStrategy1);
			}
			else {
				multRestPrice = AllVariables.tacticMinRestPriceMult_PASellStrategy1 + 
						j*(AllVariables.tacticMaxRestPriceMult_PASellStrategy1-
						AllVariables.tacticMinRestPriceMult_PASellStrategy1)/(numRestSteps-1);
			}
			
			// Create the tactics for this sell strategy. Note we are using quite a few of the default values here - this
			// may change at a later date.
			SellStrategy1Tactic tactic = new SellStrategy1Tactic(1, 0, 0, multRestPrice, AllVariables.tacticDefaultLearn_PASellStrategy1);
			
			// Set the step size for the rest price multiplier (used if learning turned on) 
			multRestPriceStep = RandomHelper.nextDouble()*(AllVariables.tacticMaxRestPriceStep_PASellStrategy1 - 
					AllVariables.tacticMinRestPriceStep_PASellStrategy1) + AllVariables.tacticMinRestPriceStep_PASellStrategy1;
			tactic.setdeltapricemultiplier(multRestPriceStep); //Sets this to 0.025 in case only one tactic, but with multiple tactics we have tactics with bigger pricesteps
			
			// Tell the tactic who's the Daddy
			tactic.setmyStrategy(TradeStrategy1_new.this);
			
			// And add it to the pile
			alltactics_additional.add(tactic);
		}
		
		besttactic = alltactics.get(RandomHelper.nextIntFromTo(0, numberoftactics-1)); //Randomly selects the initial best strategy.
		besttactic_additional = alltactics.get(RandomHelper.nextIntFromTo(0, numberoftactics_additional-1)); //Randomly selects the initial best strategy.

		sellofferone = new BidOffer();
		buyofferone = new BidOffer();
		agentsselloffers.add(sellofferone);
		agentsbuyoffers.add(buyofferone);

	}	

	// Clears strategies buyoffers, updates offers from all tactics. set strategys buyoffers to those of the best tactic. 
	public void updateBidsAndOffers() {
		//Updates all tactics
		for (int i = 0; i < numberoftactics; ++i) {
			alltactics.get(i).updatetactictradeoffers();}
		for (int i = 0; i < numberoftactics_additional; ++i) {
			alltactics_additional.get(i).updatetactictradeoffers();}
		// Updates this strategies buyoffers and selloffers based on the best tactic (which in turn is given from the previous round).
		agentsbuyoffers.clear();
		agentsselloffers.clear();
		sellofferone = besttactic.getsellofferone();
		buyofferone = besttactic.getbuyofferone();
		agentsselloffers.add(sellofferone);
		agentsbuyoffers.add(buyofferone);
	
	}
	

} // END CLASS


	

	