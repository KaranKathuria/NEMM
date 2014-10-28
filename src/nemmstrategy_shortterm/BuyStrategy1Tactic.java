/*
 * Version info:
 *      File defining a generic tactic for buystrategy1
 *     
 *     Last altered data: 20140811
 *     Made by: Karan Kathuria
 */
package nemmstrategy_shortterm;

import java.util.ArrayList;

import repast.simphony.engine.environment.RunEnvironment;
import nemmagents.ParentAgent;
import nemmcommons.AllVariables;
import nemmcommons.CommonMethods;
import nemmenvironment.TheEnvironment;
import nemmprocesses.ShortTermMarket;
import nemmstrategy_shortterm.BuyOffer;
import nemmstrategy_shortterm.SellOffer;
import nemmtime.NemmCalendar;


public class BuyStrategy1Tactic extends GenericTactic {
	 
	private double shareboughtatdiscount;
	private double pricemultiplier;
	private BuyOffer buyofferone;
	private BuyOffer buyoffertwo;
	private ArrayList<BuyOffer> tacticbuyoffers = new ArrayList<BuyOffer>();
	

	//Default constructor. Not in use. 
	BuyStrategy1Tactic() {}
	
	//Used constructor
	BuyStrategy1Tactic(double sbd, double d) {
		shareboughtatdiscount = sbd;
		pricemultiplier = 1-d; // gives pricemultiplier = (1.25 - 0.25)
		paramLearningMethod = 1; // Default learning method ID is 0 (= no learning)
		numberoflearningmethods = 3; //  Learning method IDs are 0, 1 & 2
		tacticutilityscore = 0.5;
		maxoffervolumemultiplier = 2; //Indicates how much the maximum offer volume compared is to last ticks demand.

	}
	
	private BuyOffer creatBuyOfferone(double expectedprice, double physicalposition, double lasttickdemand) { //physicalpos and lasttickdem are negatie number.
		BuyOffer ret = new BuyOffer();
		//equals must buy
		if (lasttickdemand == 0) {
			ret.setbuyoffervol(0.0);
		}
		else {
			ret.setbuyoffervol(Math.max((shareboughtatdiscount*(-lasttickdemand)), (-physicalposition) - (-maxppvolume))); //-As the phisical position of buyer would in most cases be negative, but as the offer only has positive numbers. 
		}
		ret.setbuyofferprice((1+AllVariables.OPAgentmustbuypremium)*expectedprice); //Given must buy volume price. 

		return ret;
		}
	

	private BuyOffer creatBuyOffertwo(double expectedprice, double physicalposition,  double lasttickdemand) {////physicalpos and lasttickdem are negatie number. 
		BuyOffer ret = new BuyOffer();
		double mustbuy = Math.max((shareboughtatdiscount*(-lasttickdemand)), (-physicalposition) - (-maxppvolume));
		if (physicalposition == 0) {
			ret.setbuyoffervol(0.0);
		}
		else {
			ret.setbuyoffervol(Math.max(0.0,Math.min(-maxoffervolume-mustbuy,-physicalposition-mustbuy))); //rest of the monthly production bought at expected price.
		}
		ret.setbuyofferprice(Math.min(expectedprice*pricemultiplier, floorroofprice)); //Most likely that the second offer is at at pricemultiplier. Hence they buy what they dont must, at a pricemultiplier.
		return ret;
		}
	
	public void updateinputvalues() { //Calculates and updates the floorroofprice based on the agents risk adjusted rate and the risk free rate and the market prognosis future pric
		double twoyearahead;
		double tempdisc;
		twoyearahead = this.getmyStrategy().getmyAgent().getagentcompanyanalysisagent().getmarketanalysisagent().getmarketprognosis().getmedumrundpriceexpectations();
		tempdisc = TheEnvironment.GlobalValues.currentinterestrate - this.getmyStrategy().getmyAgent().getRAR(); //For Sellers/Producers this is added + (instead of minus)
		
		floorroofprice = twoyearahead/Math.pow(tempdisc + 1, 2); //Hence this equals the discounted future expected cert price. Discounted with a risk free rate and a risk rate //In other words, the seller will not sell the variable part unless the sell price is better than the discounted future price expectations. In that case he would hold the certificates in two years.
		maxoffervolume = maxoffervolumemultiplier * this.getmyStrategy().getmyAgent().getlasttickdemand(); // * //What was demanded last tick (negativ number).
		maxppvolume = this.getmyStrategy().getmyAgent().getagentcompanyanalysisagent().getvolumeanalysisagent().getvolumeprognosis().getnexttwelvetickscertdemand(); //12 months total demand.
	}
		
	
	public void updatetacticbuyoffers() {
		double physicalposition = this.getmyStrategy().getmyAgent().getphysicalnetposition();
		double expectedprice = this.getmyStrategy().getmyAgent().getagentcompanyanalysisagent().getmarketanalysisagent().getmarketprognosis().getstpriceexpectation();
		double lasttickdemand = this.getmyStrategy().getmyAgent().getlasttickdemand();
		
		if (physicalposition >= 0){
			physicalposition = -0.0;} //To ensure that we dont get crazy bids.  
		
		updateinputvalues();
		
		tacticbuyoffers.clear();
		buyofferone = creatBuyOfferone(expectedprice,physicalposition, lasttickdemand);
		buyoffertwo = creatBuyOffertwo(expectedprice,physicalposition, lasttickdemand);
		tacticbuyoffers.add(buyofferone);
		tacticbuyoffers.add(buyoffertwo);
	}
	
	public void addTacticValuesToHistory() {
		HistoricTacticValue a = new HistoricTacticValue();
		a.tacticsbuyoffers = tacticbuyoffers;
		a.tacticselloffers = null;
		a.tacticutilityscore = tacticutilityscore;
		a.tickID = TheEnvironment.theCalendar.getCurrentTick();
		historictacticvalues.add(a);
	}
	
	// GJB Added 6oct14
	public void calcUtilityForCurrentTick() {
	//Use the agents utilitymethod to calculate each tactics utility
		double temputilityscore = myStrategy.myAgent.getutilitymethod().calculateutility(ShortTermMarket.getcurrentmarketprice(), gettacticbuyoffers(), gettacticselloffers(), ShortTermMarket.getshareofmarignaloffersold(), ShortTermMarket.getshareofmarignalofferbought());
	//Updates that tactics utility
		setUtilityScore(temputilityscore);
	}
	// --End GJB Added
	
	public BuyOffer getbuyofferone() {
		return buyofferone;}
	
	public BuyOffer getbuyoffertwo() {
		return buyoffertwo;}
	
	public ArrayList<BuyOffer> gettacticbuyoffers() {
		return tacticbuyoffers;}
	
	public ArrayList<HistoricTacticValue> gethistorictacticvalues() {
		return historictacticvalues;}
	
	private void learnParameters() {
		// Call the appropriate learning method
		if (paramLearningMethod == 1) {
			learningMethod1();
		} else if (paramLearningMethod == 2) {
			learningMethod2();
		}
	}
	private void learningMethod1() {
		// Update the % of expected price for the rest volume
		// Utility = 1 - All variable offers where expeted thus try to bid a lower price
		// Utility = 0 - None of the variable offers where bought, hence reduce increase buyoffer next time
		// Utility <0,1> - Some where expeted, some where not. Try same again.
		
		//Learning the adjustment of price multiplier
		double deltapricemup;
		if (TheEnvironment.theCalendar.getCurrentTick() > 0 && (Math.abs(buyoffertwo.getBuyOfferprice() - ShortTermMarket.getcurrentmarketprice())/(ShortTermMarket.getcurrentmarketprice())) > 0.11) {
		 deltapricemup = 0.1;
		}
		else {
		 deltapricemup = deltapricemultiplier;
		}
		
		
		
		//If I reduce price with one step based on the offer price on last tick and the prices I get is lower than floor --> do the opposit.
		if (TheEnvironment.theCalendar.getCurrentTick() > 0 && buyoffertwo.getBuyOfferprice() + (deltapricemup*buyoffertwo.getBuyOfferprice()) >= floorroofprice) {
			pricemultiplier = pricemultiplier - deltapricemup;
		}
		else {
		if (tacticutilityscore == 1)
			pricemultiplier = pricemultiplier - deltapricemup; //reduce price
		else if (tacticutilityscore == 0) {
			pricemultiplier = pricemultiplier + deltapricemup; //increase price
		}
		else {
			//Unchanged
		}
		}
		
	}
		
	private void learningMethod2() {
		// here we write the learning method code
	}		
}


// OLD Learning 1

//GJB LEARNING
//paramOLDRestVoldiscount = 0; // GJB LEARNING - Need a better way to set this. Random could work. What is this?
//paramOLDUtilityScore = 0; // GJB LEARNING - This can be set another way // The learning method needs to be set here also. Now defaults to 0.
//private double paramOLDRestVoldiscount;
//private double paramOLDUtilityScore;

/*
int diffmultUtility; //Retning p� utility
int diffmultDelta;	//Retning p� pris fra forrige forrige gang til forrige gang. 
double deltapricemultipier;
// Utility comparison		
if (tacticutilityscore-paramOLDUtilityScore >= 0) { //Unchanged utility is positive change. 
	// Utility has improved
	diffmultUtility = 1;
}
else {
	diffmultUtility = -1;
}
// -ve if the mult is less than the previous, positive otherwise
diffmultDelta = CommonMethods.signDbl(pricemultiplier-paramOLDRestVoldiscount); //Unchanged price is positive (increase) price. 
if (diffmultDelta == 0) {diffmultDelta = 1;} // tie breaker
// set the new multiplier delta
deltapricemultipier = diffmultUtility * diffmultDelta * PRICEMULTDELTASTEP;
// Update the history parameters
paramOLDRestVoldiscount = pricemultiplier;
paramOLDUtilityScore = tacticutilityscore;
// Ensure not out of bounds. Note minus sign in difference to sellstrategy1tactiv1 learning 1
pricemultiplier = Math.min(MAXRESTVOLDISCOUNT, Math.max(pricemultiplier+deltapricemultipier,MINRESTVOLDISCOUNT));
//Says if positive change (utilttychange and pricechange) increase price.
*/
