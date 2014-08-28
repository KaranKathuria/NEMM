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


public class SellStrategy1 extends GenericStrategy {
	
	//A simple buy strategy creating two sell offers. One with a discount and one without.  
	
	
	private SellOffer sellofferone;
	private SellOffer selloffertwo;
	
	
	//Constructor for OPABidstrategy adding the four offers to arraylist of offers.
	public SellStrategy1() {
		
		strategyname = "SellStrategy1";
		this.strategyutilityscore.add(0.0);
		numberoftactics = 4;
		int seed = RandomHelper.nextInt(); //Is this right Gavin?
		RandomWrapper.setstrategyseed(seed);

		
		//Adds four tactics with differen values of sbd and discount, and stores them in alltactics
		for (int i = 0; i < numberoftactics; ++i) {
			double randomsharesoldtatdiscount = RandomWrapper.getstrategyseed().nextDouble();
			double randomdiscount = RandomWrapper.getstrategyseed().nextDouble(); // RandomHelper.nextDoubleFromTo((i*0.2), ((i*0.2)+0.2));
			SellStrategy1Tactic tactic = new SellStrategy1Tactic(randomsharesoldtatdiscount, randomdiscount);
			alltactics.add(tactic);
		}
		
		besttactic = alltactics.get(0);
		
		sellofferone = besttactic.getsellofferone();
		selloffertwo = besttactic.getsellofferone();
		agentsselloffers.add(sellofferone);
		agentsselloffers.add(selloffertwo);

	}	

	// Clears strategies selloffers, updates offers from all tactics. set strategys selloffers to those of the best tactic. 
	public void updatealloffers(double expectedprice, int physicalposition) {
		//Updates all tactics
		for (int i = 0; i < numberoftactics; ++i) {
			alltactics.get(i).updatetacticselloffers(expectedprice, physicalposition);}

		agentsbuyoffers.clear();
		agentsselloffers.clear();
		sellofferone = besttactic.getsellofferone();
		selloffertwo = besttactic.getselloffertwo();
		agentsselloffers.add(sellofferone);
		agentsselloffers.add(selloffertwo);
		
		}
		
	public void updatebesttactic() {
	}
	
	public void calculatestrategyutility() {
		// TBD
	}
		
	public ArrayList<GenericTactic> getalltactics() {
		return alltactics;}
	}


	