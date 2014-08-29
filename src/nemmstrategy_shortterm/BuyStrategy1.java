/*
 * Version info:
 *     File defining a buy strategy. A buy strategy takes price expectations and physical position as input and produced a list of buy offers, by using its best tactic. 
 *     This strategy uses double expectedprice, int certdemand but could in future take physical position object as input. 
 *     
 *     Last altered data: 20140811
 *     Made by: Karan Kathuria
 */
package nemmstrategy_shortterm;

import static nemmcommons.ParameterWrapper.getproduceragentsnumber;

import java.util.ArrayList;
import java.util.Random;

import repast.simphony.random.RandomHelper;
import nemmcommons.RandomWrapper;
import nemmstrategy_shortterm.GenericStrategy.*;


public class BuyStrategy1 extends GenericStrategy {
	
	//A simple buy strategy creating two buy-offers based on price expectations and certificates demand. 
	// This the first buy offer is based on a fixed discount, while the other bid is the same as the expected price. The volumes and discunt is decided by the tactic.
	

	private BuyOffer buyofferone;
	private BuyOffer buyoffertwo;
	
	
	//Constructor for OPABidstrategy adding the four offers to arraylist of offers.
	public BuyStrategy1() {
		
		strategyname = "BuyStrategy1";
		strategyutilityscore.add(0.0);
		numberoftactics = 4;
		int seed = RandomWrapper.getstrategyseed(); //Gets a seed form the strategyseed seedgenerator.
		Random tacticstream = new Random(seed); //uniq stream for this strategies tactics. 
		
		//Adds four tactics with differen values of sbd and discount, and stores them in alltactics
		for (int i = 0; i < numberoftactics; ++i) {
			double randomshareboughtatdiscount = tacticstream.nextDouble();
			double randomdiscount = tacticstream.nextDouble(); //RandomHelper.nextDoubleFromTo((i*0.2), ((i*0.2)+0.2));
			BuyStrategy1Tactic tactic = new BuyStrategy1Tactic(randomshareboughtatdiscount, randomdiscount);
			alltactics.add(tactic);
		}
		
		besttactic = alltactics.get(tacticstream.nextInt(numberoftactics));
		
		buyofferone = new BuyOffer();
		buyoffertwo = new BuyOffer();
		agentsbuyoffers.add(buyofferone);
		agentsbuyoffers.add(buyoffertwo);

	}	

	// Clears strategies buyoffers, updates offers from all tactics. set strategys buyoffers to those of the best tactic. 
	public void updatealloffers(double expectedprice, double physicalposition) {
		//Updates all tactics
		for (int i = 0; i < numberoftactics; ++i) {
			alltactics.get(i).updatetacticbuyoffers(expectedprice, physicalposition);}
		// Updates this strategies buyoffers based on the best tactic (which in turn is given from the previous round)
		agentsbuyoffers.clear();
		agentsselloffers.clear();
		buyofferone = besttactic.getbuyofferone();
		buyoffertwo = besttactic.getbuyoffertwo();
		agentsbuyoffers.add(buyofferone);
		agentsbuyoffers.add(buyoffertwo);
		
		}
	
	public ArrayList<GenericTactic> getalltactics() {
		return alltactics;}
	}
	
	

	