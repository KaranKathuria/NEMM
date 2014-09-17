/*
 * Version info:
 *     File defining the short term (within-year) market for trading certificates. This market is cleared by a double auction logic.
 *     Last altered data: 20140722
 *     Made by: Karan Kathuria
 */

package nemmprocesses;

import java.util.ArrayList;
import java.util.Collections;
import nemmagents.CompanyAgent.ActiveAgent;
import nemmcommons.CommonMethods;
import nemmstrategy_shortterm.BuyOffer;
import nemmstrategy_shortterm.SellOffer;


//To be implemntetd Should in short take inn all bids, find the price which maximates traded volume. excetute trades. And set the price! (and do somethings smart with the imbalance of trade).
// Take inn bids of differen type. Use them to clear the market and set price. Inputt agents based bids, output price. as well as a methods that updates agents salse volume maybe)
public class ShortTermMarket {
	
	//Static means that the market price exist without the object short term market. Hence we can get the market price without having to refer or create a object short term market.
	private static double currentmarketprice;
	private static double marketsupply;
	private static double marketdemand;
	private static double shareofmarginalbuyofferbought;
	private static double shareofmarignalselloffersold;
	private static double tradedvolume;
	private static double pricestep;
	private static double initLow;
	private static double initHigh;
	private static ArrayList<BuyOffer> Allbuyoffers = new ArrayList<BuyOffer>();
	private static ArrayList<SellOffer> Allselloffers = new ArrayList<SellOffer>();
	
	// For display purposes
	private static double buyoffer1;
	private static double[] buyoffer2;
	private static double bestbuyoffer2;
	private static double selloffer1;
	private static double[] selloffer2;
	private static double bestselloffer2;
	private static double floor;
	
	public ShortTermMarket() {}
			
	public static void runshorttermmarket() {
		shareofmarginalbuyofferbought = 1;
		shareofmarignalselloffersold = 1;
		marketdemand = 0;
		marketsupply = 0;
		tradedvolume = 0;
		pricestep = 0.5;
		
		// For display purposes
		int counts = 0;
		int countb = 0;
		buyoffer2 = new double[10];
		selloffer2 = new double[10];
		
		

		Allselloffers.clear();
		Allbuyoffers.clear();
		//Update all analysisagents price expectations		
		//Update and add all sell- and buy-offers.  
		for (final ActiveAgent agent : CommonMethods.getPAgentList()) {
			//update their phusical net position
			agent.getbeststrategy().updatealloffers();
			//Updates all bids for all agents
			Allselloffers.addAll(agent.getbeststrategy().getAgentsSellOffers());
			//Allbuyoffers.addAll(agent.getbeststrategy().getAgentsBuyOffers()); For the time being the producer does not have buyoffers.
			
			//For displaypurposes
			selloffer1 = agent.getbeststrategy().getalltactics().get(0).getsellofferone().getSellOfferprice();
			selloffer2[counts] = agent.getbeststrategy().getalltactics().get(0).getselloffertwo().getSellOfferprice();
			bestselloffer2 = agent.getbeststrategy().getbesttactic().getselloffertwo().getSellOfferprice();
			floor = agent.getbeststrategy().getalltactics().get(0).getfloorroofprice();
			counts++;
			
		}
		
		for (final ActiveAgent agent : CommonMethods.getOPAgentList()) {
			agent.getbeststrategy().updatealloffers();
			//Updates all bids for all agents
			//Allselloffers.addAll(agent.getbeststrategy().getAgentsSellOffers()); None sell offers from the OP agent list. 
			Allbuyoffers.addAll(agent.getbeststrategy().getAgentsBuyOffers());
			
			buyoffer1 = agent.getbeststrategy().getalltactics().get(0).getbuyofferone().getBuyOfferprice(); 
			buyoffer2[countb] = agent.getbeststrategy().getalltactics().get(0).getbuyoffertwo().getBuyOfferprice();
			bestbuyoffer2 = agent.getbeststrategy().getbesttactic().getbuyoffertwo().getBuyOfferprice();
			countb++;
		}
		for (final ActiveAgent agent : CommonMethods.getTAgentList()) {
			agent.getbeststrategy().updatealloffers(); //Updates all bids for all agents
			Allselloffers.addAll(agent.getbeststrategy().getAgentsSellOffers());
			Allbuyoffers.addAll(agent.getbeststrategy().getAgentsBuyOffers());
		}
		
		//For errorcheking purposes
		ArrayList<BuyOffer> tempbuy = new ArrayList<BuyOffer>();
		tempbuy = Allbuyoffers;
		ArrayList<SellOffer> tempsell = new ArrayList<SellOffer>();
		tempsell = Allselloffers;
		
		Allbuyoffers.removeAll(Collections.singleton(null));
		Allselloffers.removeAll(Collections.singleton(null));

		
		int numberofbuyoffers = Allbuyoffers.size(); 
		int numberofselloffers = Allselloffers.size();
		if (numberofbuyoffers == 0 || numberofselloffers == 0){ //Sets price to null, and stops the execution in case one side of the market is null.
			currentmarketprice = 0;
			return;}
		//Time for sorting the buy and selloffers. The comparator for objects sell and buyoffers are implementer in CommonMethods. Sort from lowest to highest. 
		Collections.sort(Allselloffers, new CommonMethods.customselloffercomparator());
		Collections.sort(Allbuyoffers, new CommonMethods.custombuyoffercomparator());
		
		
		
		//Should be changed to something smarter. 
		//currentmarketprice = Allbuyoffers.get(0).getbuyofferprice();
		
		initLow = Math.min(Allbuyoffers.get(0).getBuyOfferprice(), Allselloffers.get(0).getSellOfferprice()); 
		initHigh = Math.max(Allbuyoffers.get(numberofbuyoffers-1).getBuyOfferprice(), Allselloffers.get(numberofselloffers-1).getSellOfferprice());
		//For debug pruposes
		double low = initLow;
		double high = initHigh;
		double certprice = 0;
		double mindiff = 2000000000;
		
		
		//Finds market price. Iterates thorugh all steps, calculates diffs in demand and supply and set price where diff is minimal
		//I situasjoner hvor krysset har ulike salgs og kj�psvillighet. Alts� at i den prisen hvor volume maksimeres, s� er selgeren villig til � selge billigere enn alle kj�pere er villig til � kj�pe. Dette er en lik situsajon som at to markedspriser gir samme maksimert handlet volum. 
		//Hva blir prisen da? Forel�pig satt til det laveste. Dette er ok, siden det ikke vil skje s� ofte n�r budene er mange. 
		for (int i = 0, n = (int) (((initHigh-initLow)/pricestep)+0.5); i <= n; ++i) { //For all price steps
			//Get supply
			// Get price with lowes abs diff in s and d
			double tempprice;
			double diff;
			 //TBD: Find a smarter initialiation for mindiff
			tempprice = initLow + (i*pricestep);
			double demand = 0;
			double supply = 0;
			
			for (BuyOffer b : Allbuyoffers) { //Sums all demand for that price (lowest to highest).
				if (b.getBuyOfferprice()>=tempprice){
					demand = demand + b.getnumberofcert();}
			}
			for (SellOffer s : Allselloffers) { //Sums all supply for that price (lowest to highest).
				if (s.getSellOfferprice()<=tempprice) {
					supply = supply + s.getnumberofcert();}
			}
			diff = Math.abs(demand-supply);
			if (diff<=mindiff && supply>0 && demand>0) {
				//marketsupply = supply;
				mindiff = diff;
				tradedvolume = Math.min(supply, demand);
				marketsupply = supply;
				marketdemand = demand;
				certprice = tempprice;}//certprice is when the diff is minimum. 	
		}
		
		// The following code takes care of the access demand or supply which is only partly accepted in the market. The code calculate the share of the marginal offer that is accepted. 
		// Note that if the marginal offer is not larger (volumewise) that the access offered, the share is set to zero, and nothing more is done. 
		if (marketsupply != marketdemand){
			double marginalsupply = 0;
			double marginaldemand = 0;
			if (marketsupply>marketdemand) {
				double accessupply = marketsupply - marketdemand;
				for (SellOffer s : Allselloffers) { //Sums all supply for that price (lowest to highest).
					if (s.getSellOfferprice()>certprice-pricestep && s.getSellOfferprice()<=certprice) {
						marginalsupply = marginalsupply + s.getnumberofcert();}}
				if (marginalsupply<accessupply) {
					shareofmarignalselloffersold = 0;}
				else {
				shareofmarignalselloffersold = (marginalsupply - accessupply)/marginalsupply;
				}
			}
			else {
				double accessdemand = marketdemand - marketsupply;
			for (BuyOffer b : Allbuyoffers) { //Sums all supply for that price (lowest to highest).
				if (b.getBuyOfferprice()>=(certprice) && b.getBuyOfferprice()<(certprice+pricestep)) {
					marginaldemand = marginaldemand + b.getnumberofcert();}}
			if (marginaldemand<accessdemand) {
				shareofmarginalbuyofferbought = 0;}
			else {
				shareofmarginalbuyofferbought = (marginaldemand - accessdemand)/marginaldemand;
			}
		}}
		double tempshb = shareofmarginalbuyofferbought;
		double tempshs = shareofmarignalselloffersold;
		currentmarketprice = certprice;
	}

public static double getshareofmarignaloffersold() {
	return shareofmarignalselloffersold;
}
public static double getshareofmarignalofferbought() {
	return shareofmarginalbuyofferbought;
}
public static double getcurrentmarketprice() {
	return currentmarketprice;}

public static double getmarketsupply() {
	return marketsupply;
}
public static double getmarketdemand() {
	return marketdemand;
}
public static double gettradedvolume() {
	return tradedvolume;
}
public static double getpricestep() {
	return pricestep;
}

public static int getnumberofbuyoffers() {
	return Allbuyoffers.size();
}

public static int getnumberofselloffers() {
	return Allselloffers.size();
}
public static double getfloor() {
	return floor;
}

//DisplayVariables
//The beststrategy and tactis offers for a selecter PA and OPA
public static double getbuyoffer1() {
return buyoffer1;
}
public static double[] getbuyoffer2() {
return buyoffer2;
}
public static double getbestbuyoffer2() {
return bestbuyoffer2;
}
public static double getselloffer1() {
return selloffer1;
}
public static double[] getselloffer2() {
return selloffer2;}
public static double getbestselloffer2() {
return bestselloffer2;
}

}

