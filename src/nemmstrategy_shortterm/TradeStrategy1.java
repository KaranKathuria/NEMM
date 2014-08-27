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

import repast.simphony.random.RandomHelper;
import nemmcommons.RandomWrapper;
import nemmstrategy_shortterm.GenericStrategy.*;


public class TradeStrategy1 extends GenericStrategy {
	
	//A simple buy strategy creating one sell offer and one buy offer. Buying at a discount and selling at a premium around the expected price. 
	
	
	private SellOffer sellofferone;
	private BuyOffer buyofferone;	
	
	//Constructor for TradeStrategy1
	public TradeStrategy1() {
		
		strategyname = "TradeStrategy1";
		strategyutilityscore.add(0.0);
		numberoftactics = 4;
		int seed = RandomHelper.nextInt();
		RandomWrapper.setstrategyseed(seed);
		
		//Adds four tactics with differen values of discount and premium. This is less sophisticated. 
		for (int i = 0; i < numberoftactics; ++i) {
			double randompremium = ((RandomWrapper.getstrategyseed().nextDouble())-0.2)/2; // Gives a random number between -0.1 and 0.4
			double randomdiscount = ((RandomWrapper.getstrategyseed().nextDouble())-0.2)/2;
			TradeStrategy1Tactic tactic = new TradeStrategy1Tactic(randompremium, randomdiscount);
			alltactics.add(tactic);
		}
		
		besttactic = alltactics.get(0);
		
		sellofferone = new SellOffer();
		buyofferone = new BuyOffer();
		agentsselloffers.add(sellofferone);
		agentsbuyoffers.add(buyofferone);

	}	

	// Clears strategies buyoffers, updates offers from all tactics. set strategys buyoffers to those of the best tactic. 
	public void updatealloffers(double expectedprice, int physicalposition) {
		//Updates all tactics
		for (int i = 0; i < numberoftactics; ++i) {
			alltactics.get(i).updatetactictradeoffers(expectedprice, physicalposition);}
		// Updates this strategies buyoffers and selloffers based on the best tactic (which in turn is given from the previous round).
		agentsbuyoffers.clear();
		agentsselloffers.clear();
		sellofferone = besttactic.getsellofferone();
		buyofferone = besttactic.getbuyofferone();
		agentsselloffers.add(sellofferone);
		agentsbuyoffers.add(buyofferone);

		
		}
	
	public ArrayList<GenericTactic> getalltactics() {
		return alltactics;}
	}


	

	