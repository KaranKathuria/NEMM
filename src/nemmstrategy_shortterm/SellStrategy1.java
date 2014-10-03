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


public class SellStrategy1 extends GenericStrategy {
	
	//A simple buy strategy creating two sell offers. One with a discount and one without.  
	
	
	private SellOffer sellofferone;
	private SellOffer selloffertwo;
	
	
	//Constructor for OPABidstrategy adding the four offers to arraylist of offers.
	public SellStrategy1() {
		
		// Local variables
		double randomMultStep;
		double multRestPrice;
		double multRestPriceStep;
		
		strategyname = "SellStrategy1";
		this.strategyutilityscore.add(0.0);
		numberoftactics = AllVariables.numTactics_PASellStrategy1;
		numberofmonthsmaxpp = 12; //This means that the maximum pp equalt the next twelve months expected production.
		floorroofpricemultiplier = 1; //uses floor/roof price
			
		int seed = RandomWrapper.getstrategyseed(); //Gets a seed form the strategyseed seedgenerator.
		Random tacticstream = new Random(seed); //uniq stream for this strategies tactics. 

		
		//Adds multiple tactics with different values of sbd and discount, and stores them in alltactics
		for (int i = 0; i < numberoftactics; ++i) {
			
			// First determine the must sell share
			double shareSoldtAtDiscount = AllVariables.tacticDefaultMustSellShare_PASellStrategy1; //(tacticstream.nextDouble()); this is the initial staring point and could be changed later.

			// Then the remaining volume price multiplier
			// This is dependent on whether tactic-level learning is used
			// If yes, we set the initial multiplier values to be random
			// If not, we distribute the mutiplier values uniformly over the min to max range
			if (AllVariables.tacticDefaultLearn_PASellStrategy1 > 0) {
				randomMultStep = tacticstream.nextDouble()*(AllVariables.tacticMaxRestPriceMult_PASellStrategy1-
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
			multRestPriceStep = tacticstream.nextDouble()*(AllVariables.tacticMaxRestPriceStep_PASellStrategy1 - 
					AllVariables.tacticMinRestPriceStep_PASellStrategy1) + AllVariables.tacticMinRestPriceStep_PASellStrategy1;
			tactic.setdeltapricemultiplier(multRestPriceStep); //Sets this to 0.025 in case only one tactic, but with multiple tactics we have tactics with bigger pricesteps
			
			// Tell the tactic who's the Daddy
			tactic.setmyStrategy(SellStrategy1.this);
			
			// And add it to the pile
			alltactics.add(tactic);
		}
		besttactic = alltactics.get(tacticstream.nextInt(numberoftactics));
		
		sellofferone = besttactic.getsellofferone();
		selloffertwo = besttactic.getsellofferone();
		agentsselloffers.add(sellofferone);
		agentsselloffers.add(selloffertwo);

	}	

	// Clears strategies selloffers, updates offers from all tactics. set strategys selloffers to those of the best tactic. 
	public void updatealloffers() {
		//Updates all tactics
		
		for (int i = 0; i < numberoftactics; ++i) {
			alltactics.get(i).updatetacticselloffers();}
		
		
		agentsbuyoffers.clear();
		agentsselloffers.clear();
		sellofferone = besttactic.getsellofferone();
		selloffertwo = besttactic.getselloffertwo();
		agentsselloffers.add(sellofferone);
		agentsselloffers.add(selloffertwo);		
		}
	
	public ArrayList<GenericTactic> getalltactics() {
		return alltactics;}
	
	}


	