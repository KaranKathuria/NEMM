/*
 * Version info:
 *     File defining a buy strategy. A buy strategy takes price expectations and physical position as input and produced a list of buy offers, by using its best tactic. 
 *     This strategy uses double expectedprice, int certdemand but could in future take physical position object as input. 
 *     
 *     Last altered data: 20140811
 *     Made by: Karan Kathuria
 */
package nemmstmstrategiestactics;

import static nemmcommons.ParameterWrapper.getproduceragentsnumber;
import java.util.ArrayList;
import repast.simphony.random.RandomHelper;
import nemmstmstrategiestactics.GenericStrategy.*;


public class BuyStrategy1 extends GenericStrategy {
	
	//A simple buy strategy creating two buy-offers based on price expectations and certificates demand. 
	// This the first buy offer is based on a fixed discount, while the other bid is the same as the expected price. The volumes and discunt is decided by the tactic.
	

	private BuyOffer buyofferone;
	private BuyOffer buyoffertwo;
	private BuyStrategy1Tactic besttactic = null;
	private double strategyutilityscore;
	private ArrayList<BuyStrategy1Tactic> alltactics = new ArrayList<BuyStrategy1Tactic>();
	
	
	//Constructor for OPABidstrategy adding the four offers to arraylist of offers.
	public BuyStrategy1() {
		
		strategyname = "BuyStrategy1";
		numberoftactics = 4;
		
		//Adds four tactics with differen values of sbd and discount, and stores them in alltactics
		for (int i = 0; i < numberoftactics; ++i) {
			double randomshareboughtatdiscount = RandomHelper.nextDoubleFromTo(0, 1);
			double randomdiscount = RandomHelper.nextDoubleFromTo((i*0.2), ((i*0.2)+0.2));
			BuyStrategy1Tactic tactic = new BuyStrategy1Tactic(randomshareboughtatdiscount, randomdiscount);
			alltactics.add(tactic);
		}
		
		besttactic = alltactics.get(RandomHelper.nextIntFromTo(0, (numberoftactics-1)));
		
		buyofferone = new BuyOffer();
		buyoffertwo = new BuyOffer();
		agentsbuyoffers.add(buyofferone);
		agentsbuyoffers.add(buyoffertwo);

	}	

	// Clears strategies buyoffers, updates offers from all tactics. set strategys buyoffers to those of the best tactic. 
	public void updatealloffers(double expectedprice, int physicalposition) {
		//Updates all tactics
		for (int i = 0; i < numberoftactics; ++i) {
			alltactics.get(i).updatetacticbuyoffers(expectedprice, physicalposition);}
		updatebesttactic();
		// Updates this strategies buyoffers based on the best tactic (which in turn is given from the previous round)
		agentsbuyoffers.clear();
		agentsselloffers.clear();
		buyofferone = besttactic.getbuyofferone();
		buyoffertwo = besttactic.getbuyoffertwo();
		agentsbuyoffers.add(buyofferone);
		agentsbuyoffers.add(buyoffertwo);
		
		}
		
	public void updatebesttactic() {
		besttactic = alltactics.get(RandomHelper.nextIntFromTo(0, (numberoftactics-1)));
	}
	
	public void calculatestrategyutility() {
		// TBD
	}
	public void setstrategyutilityscore(double t) {
		strategyutilityscore = t;
	}
	
	
	
	
	
	}

	