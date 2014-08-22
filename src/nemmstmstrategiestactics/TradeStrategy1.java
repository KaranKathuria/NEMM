/*
 * Version info:
 *     File defining a sell strategy. A sell strategy takes price expectations and physical position as input and produced a list of buy offers, by using its best tactic. 
 *     This strategy uses double expectedprice, int certdemand but could in future take physical position object as input. 
 *     
 *     Last altered data: 20140813
 *     Made by: Karan Kathuria
 */
package nemmstmstrategiestactics;

import java.util.ArrayList;
import repast.simphony.random.RandomHelper;
import nemmstmstrategiestactics.GenericStrategy.*;


public class TradeStrategy1 extends GenericStrategy {
	
	//A simple buy strategy creating one sell offer and one buy offer. Buying at a discount and selling at a premium around the expected price. 
	
	
	private SellOffer sellofferone;
	private BuyOffer buyofferone;
	private TradeStrategy1Tactic besttactic = null;
	
	//Constructor for TradeStrategy1
	public TradeStrategy1() {
		
		strategyname = "TradeStrategy1";
		numberoftactics = 4;
		strategyutilityscore = 0;

		
		//Adds four tactics with differen values of discount and premium. This is less sophisticated. 
		for (int i = 0; i < numberoftactics; ++i) {
			double randompremium = RandomHelper.nextDoubleFromTo(-0.1, 0.3);
			double randomdiscount = RandomHelper.nextDoubleFromTo(-0.1, 0.3);
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
		
	public void updatebesttactic() {
		//TBD. Should be run before the updatebuystrategy
	}
	
	public void calculatestrategyutility() {
		// TBD
	}
	
	
	
	
	
	}

	