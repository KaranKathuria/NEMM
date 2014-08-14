/*
 * Version info:
 *     File defining a speculators way of forming bids for stm-market. 
 *     
 *     Last altered data: 20140729
 *     Made by: Karan Kathuria
 */
/*
package nemmstmstrategiestactics;

import java.util.ArrayList;

import nemmstmstrategiestactics.OPABidstrategy.buyoffer;
import nemmstmstrategiestactics.PABidstrategy.selloffer;

public class ZOldSABidstrategy {
	
	//Private parameters
	private String strategyname; // Could be used to display number of agents using differnt string-names. 
	private double buysellspread; //in percent. 10 % means that the difference between buy and sell price is 10 % symmetrically around the expected or observed market price. 
	private double shareofcurrentaccountalwaysused; // The volume bid would at least be dependent on the current account. A share of these are always used for arbitrage. 
	
	//Classes buy and sell offers are implemented in PAstrategies and OPAstrategies respectively. In the future this agents could form several such bids for the stm through one strategy.
	// Rather then having several buy and sell offers per SAgents, we could have more Agents. This depending on how such agents acutally operate and how we define one agent (is it one trader or a company?)
	private selloffer sellofferone;
	private buyoffer buyofferone;

	private ArrayList<selloffer> agentsselloffers = new ArrayList<selloffer>();
	private ArrayList<buyoffer> agentsbuyoffers = new ArrayList<buyoffer>();
	
	//Constructor for SABidstrategy adding the offers to arraylist of offers. Different strategies could have different buysellspreads (random?), and a speculator then have different strategies
	public ZOldSABidstrategy() {
		
		buysellspread = 0.1;
		shareofcurrentaccountalwaysused = 0.5;
		sellofferone = null;
		buyofferone = null;
		
		agentsselloffers.add(sellofferone);
		agentsbuyoffers.add(buyofferone);

	}
				
	// Set of private functions to create the two offers according to the private variable buysellspread. Input are expected price and current account. 
	private selloffer creatsellofferone(int ep, double ca) {
		selloffer ret = null;
		ret.numberofcert = (int) ((shareofcurrentaccountalwaysused*ca)/ep); //sell certificates amounting to half of what they could afford buying with current account balance. 
		ret.price = (ep*(1+(buysellspread/2))); // willing to sell for a price higher than the expected price. 
		return ret;
		}
	private buyoffer creatbuyofferone(int ep, double ca) {
		buyoffer ret = null;
		ret.numberofcert = (int) ((shareofcurrentaccountalwaysused*ca)/ep); //rest of the monthly production sold at expected price.
		ret.price = (ep*(1-(buysellspread/2)));
		return ret;
		}

	
	// This method updates this agents strategies four bids and the arraylist containing the four bids. The four bids are hence overwritten each time this method is called.
	public void updateSAgentsoffers(int ep, double ca) {
		agentsselloffers.clear();
		agentsbuyoffers.clear();
		//Arrays of offered are cleared. 
		sellofferone = creatsellofferone(ep,ca);
		buyofferone = creatbuyofferone(ep,ca);
		//created the offers and adding them to the array.
		agentsselloffers.add(sellofferone);
		agentsbuyoffers.add(buyofferone);

		//KK: Do I have to create a new table and set this equal to agentsselloffers, or does this tables instances get updated? Trying without for now.
	}
	
	
	
	// Get methods. 

	public ArrayList<selloffer> getSAgentsselloffers() {
		return agentsselloffers;
	}
	public ArrayList<buyoffer> getSAgentsbuyoffers() {
		return agentsbuyoffers;
	}
	
	
	}	
	*/

	
	
	

