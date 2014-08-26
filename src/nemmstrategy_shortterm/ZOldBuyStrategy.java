package nemmstrategy_shortterm;
/*
 * Version info:
 *     File defining a buy strategy
 *     
 *     Last altered data: 20140811
 *     Made by: Karan Kathuria
 */
/*
package nemmstmstrategiestactics;

import java.util.ArrayList;

// 
public class OldBuyStrategy extends GenericStrategy {
	
	//A simple buy strategy creating to buyoffers based on price expectations and certificates demand
	
	private String strategyname; 
	private double shareofmpboughtatdiscount; //100%
	private double mddiscount; //bought for 10% less than expected market price
	private double shareofcbboughtatmaxdiscount; //0% of shortcommings bought at 20 % less than market price
	private double cbdiscountmax;
	private double cbdiscountmin; //100% of the shortcommings tried bought at expected price.
	
	
	
	//A PABidstrategy has four such offers and an arraylist of these offers containing zero-values.
	private buyoffer buyofferone;
	private buyoffer buyoffertwo;
	private buyoffer buyofferthree;
	private buyoffer buyofferfour;
	
	private ArrayList<buyoffer> agentsbuyoffers = new ArrayList<buyoffer>();
	
	//Constructor for OPABidstrategy adding the four offers to arraylist of offers.
	public OldBuyStrategy() {
		
		shareofmpboughtatdiscount = 0.80; //100%
		mddiscount = 0.1; //bought for 10% less than expected market price
		shareofcbboughtatmaxdiscount = 0.7; //0% of shortcommings bought at 20 % less than market price
		cbdiscountmax = 0.08;
		cbdiscountmin = 0.00;
		
		buyofferone = new buyoffer();
		buyoffertwo = new buyoffer();
		buyofferthree = new buyoffer();
		buyofferfour = new buyoffer();
		
		agentsbuyoffers.add(buyofferone);
		agentsbuyoffers.add(buyoffertwo);
		agentsbuyoffers.add(buyofferthree);
		agentsbuyoffers.add(buyofferfour);
	}
			
	
	// Set of private functions to create the four offers according to the strategy. These functions are then used by the createagentsbuyoffers below.
	private buyoffer creatbuyofferone(double ep, int mp, int cb) {
		buyoffer ret = new buyoffer();
		ret.numberofcert = (int) (shareofmpboughtatdiscount*mp); //int in order to only sell integer values of certs.
		ret.price = (1-mddiscount)*ep; 
		return ret;
		}
	private buyoffer creatbuyoffertwo(double ep, int mp, int cb) {
		buyoffer ret = new buyoffer();
		ret.numberofcert = mp-( (int) (shareofmpboughtatdiscount*mp)); //rest of the monthly production bought at expected price.
		ret.price = ep;
		return ret;
		}
	//buyoffers for the quantity short commingsbeing accumulated are bought at less a discount. 
	private buyoffer creatbuyofferthree(double ep, int mp, int cb) {
		buyoffer ret = new buyoffer();
		ret.numberofcert = -((int) (shareofcbboughtatmaxdiscount*cb)); //int in order to only sell integer values of casts. Minus added as and OPAs balance will be negative.
		ret.price = (1-cbdiscountmax)*ep; 
		return ret;
		}
	private buyoffer creatbuyofferfour(double ep, int mp, int cb) {
		buyoffer ret = new buyoffer();
		ret.numberofcert = -(cb-( (int) (shareofcbboughtatmaxdiscount*cb))); //rest of the monthly production sold at expected price.Minus added as and OPAs balance will be negative.
		ret.price = (1-cbdiscountmin)*ep;
		return ret;
		}
	
	// This method updates this agents strategies four bids and the arraylist containing the four buybids. The four bids are hence overwritten each time this method is called.
	public void updateOPAgentsbuyoffers(double ep, int mp, int cb) {
		agentsbuyoffers.clear();
		//private ArrayList<buyoffer> ret = new ArrayList<buyoffer>();
		buyofferone = creatbuyofferone(ep, mp, cb);
		buyoffertwo = creatbuyoffertwo(ep, mp, cb);
		if (cb<0) { //only try to by certificates if net balance is negative (the OPA has to few certificates for this clearingperiod).
		buyofferthree = creatbuyofferthree(ep, mp, cb);
		buyofferfour = creatbuyofferfour(ep, mp, cb);
		agentsbuyoffers.add(buyofferone);
		agentsbuyoffers.add(buyoffertwo);
		agentsbuyoffers.add(buyofferthree);
		agentsbuyoffers.add(buyofferfour);
				}
		else {
		agentsbuyoffers.add(buyofferone);
		agentsbuyoffers.add(buyoffertwo);
		}
		
		
		//KK: Do I have to create a new table and set this equal to agentsbuyoffers, or does this tables instances get updated? Trying without for now.
	}
	 
	// Get methods. 
	public buyoffer getbuyofferone() {
		return buyofferone;
	}
	public buyoffer getbuyoffertwo() {
		return buyoffertwo;
	}
	public buyoffer getbuyofferthree() {
		return buyofferthree;
	}
	public buyoffer getbuyofferfour() {
		return buyofferfour;
	}
	public ArrayList<buyoffer> getOPAgentsbuyoffers() {
		return agentsbuyoffers;
	}
	
	
	
	}	
	*/
	