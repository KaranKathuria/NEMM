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
	
	private BidOffer buyofferone;
	private BidOffer buyoffertwo;
	
	//Constructor for OPABidstrategy adding the four offers to arraylist of offers.
	
	//Constructor for OPABidstrategy adding the four offers to arraylist of offers.
	public BuyStrategy1() {
		
		// Run the super constructor
		super();
		
		// Local variables
		double randomMultStep;
		double multRestPrice;
		double multRestPriceStep;
		int numMSSteps = AllVariables.tacticNumMustBuySteps_OPBuyStrategy1;
		int numRestSteps = AllVariables.tacticNumRestSteps_OPBuyStrategy1;
		double percMustBuyVol;
		double percRestVol;
		double multMustBuyPrice;

		strategyname = "BuyStrategy1";
		this.strategyutilityscore.add(0.0);
		if (numMSSteps < 1) {numMSSteps = 1;}
		if (numRestSteps < 1) {numRestSteps = 1;}		
		numberoftactics = numMSSteps*numRestSteps;
		numberofmonthsmaxpp = 12; //This means that the maximum pp equalt the next twelve months expected production.
		floorroofpricemultiplier = 1; //uses floor/roof price
			
		// Calculates the volumes (as a percentage of production) and the prices (as a multiplier of the last market price)
		// for each tactic, creates the tactic as stores it
		// A tactic is created for each combination of must Buy step and rest volume step.
		
		for (int i = 0; i < numMSSteps; ++i) {
			// Loop through the so-called Must Buy steps
			// Calculate the volume and price for the current must Buy step
			percMustBuyVol = AllVariables.tacticDefaultMustBuyShare_OPBuyStrategy1;
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
				
				// Create the tactics for this Buy strategy. Note we are using quite a few of the default values here - this
				// may change at a later date.
				BuyStrategy1Tactic tactic = new BuyStrategy1Tactic(AllVariables.multOfferVol_OPBuyStrategy1, 
						percMustBuyVol, multMustBuyPrice, 
						multRestPrice, AllVariables.tacticDefaultLearn_OPBuyStrategy1);
				
				// Set the step size for the rest price multiplier (used if learning turned on) 
				multRestPriceStep = RandomHelper.nextDouble()*(AllVariables.tacticMaxRestPriceStep_OPBuyStrategy1 - 
						AllVariables.tacticMinRestPriceStep_OPBuyStrategy1) + AllVariables.tacticMinRestPriceStep_OPBuyStrategy1;
				tactic.setdeltapricemultiplier(multRestPriceStep); //Sets this to 0.025 in case only one tactic, but with multiple tactics we have tactics with bigger pricesteps
				
				// Tell the tactic who's the Daddy
				tactic.setmyStrategy(BuyStrategy1.this);
				
				// And add it to the pile
				alltactics.add(tactic);
			}
		}
		besttactic = alltactics.get(RandomHelper.nextIntFromTo(0,numberoftactics-1));
		
		buyofferone = new BidOffer();
		buyoffertwo = new BidOffer();
		agentsbuyoffers.add(buyofferone);
		agentsbuyoffers.add(buyoffertwo);
	}
		
		
/* This is the previous code. Once the above has been tested, we can delete this			
	public BuyStrategy1() {
		// Run the super constructor
		super();
		
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
		
		buyofferone = new BidOffer();
		buyoffertwo = new BidOffer();
		agentsbuyoffers.add(buyofferone);
		agentsbuyoffers.add(buyoffertwo);

	}	*/

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
	
	

	