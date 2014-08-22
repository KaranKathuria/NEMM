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
	private double strategyutility;
	private BuyStrategy1Tactic besttactic = null;
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
		//Sets the strategies buyoffers equal to those of the best tactic.
		agentsbuyoffers.clear();
		agentsselloffers.clear();
		buyofferone = besttactic.getbuyofferone();
		buyoffertwo = besttactic.getbuyoffertwo();
		agentsbuyoffers.add(buyofferone);
		agentsbuyoffers.add(buyoffertwo);
		
		}
		
	public void updatebesttactic(double marketprice, double shareoflastofferbought) {
		int tempvalue=0;
		int highest = 0;
		//Calculates the utility for each tactics. Currenlty scored by which tactics maximises number of certs bought.
		for (int i = 0; i < numberoftactics; ++i) {
			tempvalue = returnboughtvolume(alltactics.get(i).gettacticbuyoffers(), marketprice, shareoflastofferbought).getBoughtInSTMnumberofcert();
			alltactics.get(i).settacticscore(tempvalue);
			//Determines the best tactics simply by which tactic bought most certificates last round. 
			if (tempvalue>highest) {
				besttactic = alltactics.get(i); //If this tactic would have given the highest number of certs sold, this is the besttactics, hence used the next time.
			}} //If all the tactics give zero certificates, then the best tactics is unchanged.
		}
	
	public void setstrategyutility(double su) {
		strategyutility = su;
	}
	
	
	
	}

	