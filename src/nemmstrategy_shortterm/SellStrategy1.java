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
		
		strategyname = "SellStrategy1";
		this.strategyutilityscore.add(0.0);
		numberoftactics = AllVariables.numberoftacticsSellStrategy1;
		numberofmonthsmaxpp = 12; //This means that the maximum pp equalt the next twelve months expected production.
		floorroofpricemultiplier = 1; //uses floor/roof price
		
		
		
		int seed = RandomWrapper.getstrategyseed(); //Gets a seed form the strategyseed seedgenerator.
		Random tacticstream = new Random(seed); //uniq stream for this strategies tactics. 

		
		//Adds four tactics with differen values of sbd and discount, and stores them in alltactics
		for (int i = 0; i < numberoftactics; ++i) {
			double randomsharesoldtatdiscount = AllVariables.PAgentmustsellshare; //(tacticstream.nextDouble()); this is the initial staring point and could be changed later.
			double randomdiscount = (tacticstream.nextDouble()-0.25); // between (-0.25 - 0.75) //Initial starting point for the variable bid price. Is changed in the tactic later
			SellStrategy1Tactic tactic = new SellStrategy1Tactic(randomsharesoldtatdiscount, randomdiscount);
			tactic.setmyStrategy(SellStrategy1.this);
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
		
		//recalculate floor
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
	
	public void calculatefloorroofprice() { //Calculates and updates the floorroofprice based on the agents risk adjusted rate and the risk free rate and the market prognosis future price expectatiosn
		double temp
		
	}
	}


	