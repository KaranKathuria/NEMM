/*
 * Version info:
 *     File defining a sell and buy strategy for traders. 
 *     
 *     Created 20150630: Added some simplifyed logic. Fixed buy and sell volumes.
 *     
 *     Made by: Karan Kathuria
 */
package nemmstrategy_shortterm;

import java.util.ArrayList;

import repast.simphony.random.RandomHelper;
import nemmcommons.AllVariables;
import nemmstrategy_shortterm.GenericStrategy.*;


public class TradeStrategy1_new extends GenericStrategy {
	
	//A simple buy strategy creating one sell offer and one buy offer. Buying at a discount and selling at a premium around the expected price. 
	
	private BidOffer buyofferone;
	private BidOffer buyoffertwo;
	private BidOffer sellofferone;
	private BidOffer selloffertwo;
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
		double percMustBuyVol;
		double multMustBuyPrice;
		
		double percMustSellVol;
		double multMustSellPrice;
		
		int numMSSteps = 4;				//Hardcoded for now. should be taken from all variables. Notice also same for buy and sell.
		int numRestSteps = 4;
		numberoftactics = numMSSteps*numRestSteps;
		numberoftactics_additional = numberoftactics;
		
		strategyname = "TradeStrategy1";
		strategyutilityscore.add(0.0);
		//Not in use
		maximumshortpos = AllVariables.tradermaximumshortpos;
		maximumlongpos = AllVariables.tradermaximumlongpos;
		//not in use end.
		
		for (int i = 0; i < numMSSteps; ++i) {
			// Loop through the so-called Must Buy steps
			// Calculate the volume and price for the current must Buy step
			percMustBuyVol = 0.1;
			if (numMSSteps == 1){
				// set the price multiplier to be half way between the min and the max
				multMustBuyPrice = (AllVariables.tacticMinMustBuyPriceMult_OPBuyStrategy1 + 
						AllVariables.tacticMaxMustBuyPriceMult_OPBuyStrategy1)/2;
			}
			else {
				multMustBuyPrice = AllVariables.tacticMinMustBuyPriceMult_OPBuyStrategy1 + 
					i*(AllVariables.tacticMaxMustBuyPriceMult_OPBuyStrategy1-
					AllVariables.tacticMinMustBuyPriceMult_OPBuyStrategy1)/(numMSSteps-1);
			}
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
				
				// Create the tactics for this Buy strategy.
				TraderBuyStrategy1Tactic tactic = new TraderBuyStrategy1Tactic(AllVariables.tacticMaxPhysPosBuyShare_OPBuyStrategy1, 
						percMustBuyVol, multMustBuyPrice, 
						multRestPrice, AllVariables.tacticDefaultLearn_OPBuyStrategy1);
				
				// Set the step size for the rest price multiplier (used if learning turned on) 
				multRestPriceStep = RandomHelper.nextDouble()*(AllVariables.tacticMaxRestPriceStep_OPBuyStrategy1 - 
						AllVariables.tacticMinRestPriceStep_OPBuyStrategy1) + AllVariables.tacticMinRestPriceStep_OPBuyStrategy1;
				tactic.setdeltapricemultiplier(multRestPriceStep); //Sets this to 0.025 in case only one tactic, but with multiple tactics we have tactics with bigger pricesteps
				
			// Tell the tactic who's the Daddy
			tactic.setmyStrategy(TradeStrategy1_new.this);
			
			// And add it to the pile
			alltactics.add(tactic);
		}
		}
		
		for (int i = 0; i < numMSSteps; ++i) {

			//Notice hardcoding 0.1
			percMustSellVol = 0.1;
			if (numMSSteps == 1){
				// set the price multiplier to be half way between the min and the max
				multMustSellPrice = (AllVariables.tacticMinMustSellPriceMult_PASellStrategy1 + 
						AllVariables.tacticMaxMustSellPriceMult_PASellStrategy1)/2;
			}
			else {
				multMustSellPrice = AllVariables.tacticMinMustSellPriceMult_PASellStrategy1 + 
					i*(AllVariables.tacticMaxMustSellPriceMult_PASellStrategy1-
					AllVariables.tacticMinMustSellPriceMult_PASellStrategy1)/(numMSSteps-1);
			}
			for (int j=0; j < numRestSteps;j++) {
			
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
				TraderSellStrategy1Tactic tactic = new TraderSellStrategy1Tactic(AllVariables.tacticMaxPhysPosSellShare_PASellStrategy1, 
						percMustSellVol, multMustSellPrice, 
						multRestPrice, AllVariables.tacticDefaultLearn_PASellStrategy1);
				
				// Set the step size for the rest price multiplier (used if learning turned on) 
				multRestPriceStep = RandomHelper.nextDouble()*(AllVariables.tacticMaxRestPriceStep_PASellStrategy1 - 
						AllVariables.tacticMinRestPriceStep_PASellStrategy1) + AllVariables.tacticMinRestPriceStep_PASellStrategy1;
				tactic.setdeltapricemultiplier(multRestPriceStep); //Sets this to 0.025 in case only one tactic, but with multiple tactics we have tactics with bigger pricesteps
			
			// Tell the tactic who's the Daddy
			tactic.setmyStrategy(TradeStrategy1_new.this);
			
			// And add it to the pile
			alltactics_additional.add(tactic);
		}
		}
	
		
		besttactic = alltactics.get(RandomHelper.nextIntFromTo(0, numberoftactics-1)); //Randomly selects the initial best strategy.
		besttactic_additional = alltactics_additional.get(RandomHelper.nextIntFromTo(0, numberoftactics_additional-1)); //Randomly selects the initial best strategy.

		sellofferone = new BidOffer();
		selloffertwo = new BidOffer();
		buyofferone = new BidOffer();
		buyoffertwo = new BidOffer();
		agentsselloffers.add(sellofferone);
		agentsselloffers.add(selloffertwo);
		agentsbuyoffers.add(buyofferone);
		agentsbuyoffers.add(buyoffertwo);
		

	}	

	// Clears strategies buyoffers, updates offers from all tactics. set strategys buyoffers to those of the best tactic. 
	public void updateBidsAndOffers() {
		//Updates all tactics
		for (int i = 0; i < numberoftactics; ++i) {
			alltactics.get(i).updatetacticbuyoffers();}
		for (int i = 0; i < numberoftactics_additional; ++i) {
			alltactics_additional.get(i).updatetacticselloffers();}
		// Updates this strategies buyoffers and selloffers based on the best tactic (which in turn is given from the previous round).
		agentsbuyoffers.clear();
		agentsselloffers.clear();
		buyofferone = besttactic.getbuyofferone();
		buyoffertwo = besttactic.getbuyoffertwo();
		//for the selloffers we use the same, but underscore "additional" (KK 20150630)
		sellofferone = besttactic_additional.getsellofferone();
		selloffertwo = besttactic_additional.getselloffertwo();
		//add to arrays

		agentsbuyoffers.add(buyofferone);
		agentsbuyoffers.add(buyoffertwo);
		agentsselloffers.add(sellofferone);
		agentsselloffers.add(selloffertwo);
		int a = 2;
		int b = a+1;
		int c = b;

	}
	

} // END CLASS


	

	