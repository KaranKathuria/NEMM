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
import nemmstrategy_shortterm.GenericStrategy.*;


public class SellStrategy1 extends GenericStrategy {
	
	//A simple buy strategy creating two sell offers. One with a discount and one without.  
	
	
	private BidOffer sellofferone;
	private BidOffer selloffertwo;
	
	
	//Constructor for OPABidstrategy adding the four offers to arraylist of offers.
	public SellStrategy1() {
		
		// Run the super constructor
		super();
		
		// Local variables
		double randomMultStep;
		double multRestPrice;
		double multRestPriceStep;
		int numMSSteps = AllVariables.tacticNumMustSellSteps_PASellStrategy1;
		int numRestSteps = AllVariables.tacticNumRestSteps_PASellStrategy1;
		double percMustSellVol;
		double percRestVol;
		double multMustSellPrice;

		strategyname = "SellStrategy1";
		this.strategyutilityscore.add(0.0);
		if (numMSSteps < 1) {numMSSteps = 1;}
		if (numRestSteps < 1) {numRestSteps = 1;}		
		numberoftactics = numMSSteps*numRestSteps;
		numberofmonthsmaxpp = 12; //This means that the maximum pp equalt the next twelve months expected production.
		floorroofpricemultiplier = 1; //uses floor/roof price
			
		// Calculates the volumes (as a percentage of production) and the prices (as a multiplier of the last market price)
		// for each tactic, creates the tactic as stores it
		// A tactic is created for each combination of must sell step and rest volume step.
		
		for (int i = 0; i < numMSSteps; ++i) {
			// Loop through the so-called Must Sell steps
			// Calculate the volume and price for the current must sell step
			percMustSellVol = AllVariables.tacticMinPhysPosSellShare_PASellStrategy1;
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
				SellStrategy1Tactic tactic = new SellStrategy1Tactic(AllVariables.tacticMaxPhysPosSellShare_PASellStrategy1, 
						percMustSellVol, multMustSellPrice, 
						multRestPrice, AllVariables.tacticDefaultLearn_PASellStrategy1);
				
				// Set the step size for the rest price multiplier (used if learning turned on) 
				multRestPriceStep = RandomHelper.nextDouble()*(AllVariables.tacticMaxRestPriceStep_PASellStrategy1 - 
						AllVariables.tacticMinRestPriceStep_PASellStrategy1) + AllVariables.tacticMinRestPriceStep_PASellStrategy1;
				tactic.setdeltapricemultiplier(multRestPriceStep); //Sets this to 0.025 in case only one tactic, but with multiple tactics we have tactics with bigger pricesteps
				
				// Tell the tactic who's the Daddy
				tactic.setmyStrategy(SellStrategy1.this);
				
				// And add it to the pile
				alltactics.add(tactic);
			}
		}
/* This is the previous code. Once the above has been tested, we can delete this		
		//Adds multiple tactics with different values of sbd and discount, and stores them in alltactics
		for (int i = 0; i < numberoftactics; ++i) {
			
			// First determine the must sell share
			double shareSoldtAtDiscount = AllVariables.tacticDefaultMustSellShare_PASellStrategy1; //(tacticstream.nextDouble()); this is the initial staring point and could be changed later.

			// Then the remaining volume price multiplier
			// This is dependent on whether tactic-level learning is used
			// If yes, we set the initial multiplier values to be random
			// If not, we distribute the mutiplier values uniformly over the min to max range
			if (AllVariables.tacticDefaultLearn_PASellStrategy1 > 0) {
				randomMultStep = RandomHelper.nextDouble()*(AllVariables.tacticMaxRestPriceMult_PASellStrategy1-
						AllVariables.tacticMinRestPriceMult_PASellStrategy1); // between (-0.25 - 0.75) //Initial starting point for the variable bid price. Is changed in the tactic later
				multRestPrice = AllVariables.tacticMinRestPriceMult_PASellStrategy1+randomMultStep;				
			}
			else if (numberoftactics==1) {
				// Set the multiplier to be halfway between the min & max
				multRestPrice = 0.5*(AllVariables.tacticMaxRestPriceMult_PASellStrategy1+
						AllVariables.tacticMinRestPriceMult_PASellStrategy1);
			}
			else {
				multRestPrice = AllVariables.tacticMinRestPriceMult_PASellStrategy1 + 
						i*(AllVariables.tacticMaxRestPriceMult_PASellStrategy1-
						AllVariables.tacticMinRestPriceMult_PASellStrategy1)/(numberoftactics-1);
			}
			
			// Create the tactics for this sell strategy. Note we are using quite a few of the default values here - this
			// may change at a later date.
			SellStrategy1Tactic tactic = new SellStrategy1Tactic(AllVariables.multOfferVol_PASellStrategy1, 
					shareSoldtAtDiscount, AllVariables.tacticDefaultMustSellPriceMult_PASellStrategy1, 
					multRestPrice, AllVariables.tacticDefaultLearn_PASellStrategy1);
			
			// Set the step size for the rest price multiplier (used if learning turned on) 
			multRestPriceStep = RandomHelper.nextDouble()*(AllVariables.tacticMaxRestPriceStep_PASellStrategy1 - 
					AllVariables.tacticMinRestPriceStep_PASellStrategy1) + AllVariables.tacticMinRestPriceStep_PASellStrategy1;
			tactic.setdeltapricemultiplier(multRestPriceStep); //Sets this to 0.025 in case only one tactic, but with multiple tactics we have tactics with bigger pricesteps
			
			// Tell the tactic who's the Daddy
			tactic.setmyStrategy(SellStrategy1.this);
			
			// And add it to the pile
			alltactics.add(tactic);
		} */
		besttactic = alltactics.get(RandomHelper.nextIntFromTo(0,numberoftactics-1));
		
		sellofferone = besttactic.getsellofferone();
		selloffertwo = besttactic.getselloffertwo();
		agentsselloffers.add(sellofferone);
		agentsselloffers.add(selloffertwo);

	}		
	
	public void updateBidsAndOffers() {
		// Clears strategies selloffers, updates offers from all tactics, 
		// set strategys selloffers to those of the best tactic. 
		
		for (int i = 0; i < numberoftactics; ++i) {
			alltactics.get(i).updatetacticselloffers();}
				
		agentsbuyoffers.clear();
		agentsselloffers.clear();
		sellofferone = besttactic.getsellofferone();
		selloffertwo = besttactic.getselloffertwo();
		agentsselloffers.add(sellofferone);
		agentsselloffers.add(selloffertwo);		
	}
	
	
} // END CLASS


	