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


public class TradeStrategy1 extends GenericStrategy {
	
	//A simple buy strategy creating one sell offer and one buy offer. Buying at a discount and selling at a premium around the expected price. 
	
	
	private SellOffer sellofferone;
	private SellOffer selloffertwo;
	private BuyOffer buyofferone;
	private BuyOffer buyoffertwo;
	private double maximumshortpos; //Number of certs a trader using this strategy can be short
	private double maximumlongpos;	//Number of certs a trader using this strategy can be long
	
	//Constructor for TradeStrategy1
	public TradeStrategy1() {
		
		strategyname = "TradeStrategy1";
		strategyutilityscore.add(0.0);
		numberoftactics = AllVariables.numberoftacticsTraderStrategy1;
		int seed = RandomWrapper.getstrategyseed(); //Gets a seed form the strategyseed seedgenerator.
		Random tacticstream = new Random(seed); //uniq stream for this strategies tactics. 
		maximumshortpos = AllVariables.tradermaximumshortpos;
		maximumlongpos = AllVariables.tradermaximumlongpos;
		
		//Adds four tactics with differen values of discount and premium. This is less sophisticated. 
		for (int i = 0; i < numberoftactics; ++i) {
			double randompremium = ((tacticstream.nextDouble())-0.2)/2; // Gives a random number between -0.1 and 0.4
			double randomdiscount = ((tacticstream.nextDouble())-0.2)/2;
			TradeStrategy1Tactic tactic = new TradeStrategy1Tactic(randompremium, randomdiscount, maximumshortpos, maximumlongpos);
			alltactics.add(tactic);
		}
		
		besttactic = alltactics.get(tacticstream.nextInt(numberoftactics)); //Randomly selects the initial best strategy.
		
		sellofferone = new SellOffer();
		selloffertwo = new SellOffer();
		buyofferone = new BuyOffer();
		buyoffertwo = new BuyOffer();
		agentsselloffers.add(sellofferone);
		agentsselloffers.add(selloffertwo);
		agentsbuyoffers.add(buyofferone);
		agentsbuyoffers.add(buyoffertwo);

	}	

	// Clears strategies buyoffers, updates offers from all tactics. set strategys buyoffers to those of the best tactic. 
	public void updatealloffers(double expectedprice, double physicalposition, double ...capitalbase) {
		//Updates all tactics
		for (int i = 0; i < numberoftactics; ++i) {
			alltactics.get(i).updatetactictradeoffers(expectedprice, physicalposition, capitalbase);}
		// Updates this strategies buyoffers and selloffers based on the best tactic (which in turn is given from the previous round).
		agentsbuyoffers.clear();
		agentsselloffers.clear();
		sellofferone = besttactic.getsellofferone();
		selloffertwo = besttactic.getselloffertwo();
		buyofferone = besttactic.getbuyofferone();
		buyoffertwo = besttactic.getbuyoffertwo();
		agentsselloffers.add(sellofferone);
		agentsselloffers.add(selloffertwo);
		agentsbuyoffers.add(buyoffertwo);
		agentsbuyoffers.add(buyofferone);
	
		}
	
	public ArrayList<GenericTactic> getalltactics() {
		return alltactics;}
	}


	

	