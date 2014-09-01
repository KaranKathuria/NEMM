/*
 * Version info:
 *     File defining the short term (within-year) market for trading certificates. This market is cleared by a double auction logic.
 *     Last altered data: 20140722
 *     Made by: Karan Kathuria
 */

package nemmprocesses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import nemmagents.CompanyAgent.ActiveAgent;
import nemmcommons.CommonMethods;
import nemmcommons.ParameterWrapper;
import nemmstrategy_shortterm.BuyOffer;
import nemmstrategy_shortterm.GenericStrategy;
import nemmstrategy_shortterm.SellOffer;
import repast.simphony.random.RandomHelper;


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
	
	public ShortTermMarket() {}
			
	public static void runshorttermmarket() {
		shareofmarginalbuyofferbought = 1;
		shareofmarignalselloffersold = 1;
		marketdemand = 0;
		marketsupply = 0;
		tradedvolume = 0;
		pricestep = 0.5;

		Allselloffers.clear();
		Allbuyoffers.clear();
		//Update all analysisagents price expectations		
		//Update and add all sell- and buy-offers.  
		for (final ActiveAgent agent : CommonMethods.getPAgentList()) {
			//update their phusical net position
			
			agent.getbeststrategy().updatealloffers(agent.getagentcompanyanalysisagent().getmarketanalysisagent().getpriceprognosis().getstpriceexpectation(), agent.getphysicalnetposition());
			//Updates all bids for all agents
			Allselloffers.addAll(agent.getbeststrategy().getAgentsSellOffers());
			//Allbuyoffers.addAll(agent.getbeststrategy().getAgentsBuyOffers()); For the time being the producer does not have buyoffers.
		}
		
		for (final ActiveAgent agent : CommonMethods.getOPAgentList()) {
			agent.getbeststrategy().updatealloffers(agent.getagentcompanyanalysisagent().getmarketanalysisagent().getpriceprognosis().getstpriceexpectation(), agent.getphysicalnetposition(), agent.getcapitalbase());
			//Updates all bids for all agents
			//Allselloffers.addAll(agent.getbeststrategy().getAgentsSellOffers()); None sell offers from the OP agent list. 
			//marketsupply = agent.getphysicalnetposition();
			Allbuyoffers.addAll(agent.getbeststrategy().getAgentsBuyOffers());
	
		}
		for (final ActiveAgent agent : CommonMethods.getTAgentList()) {
			agent.getbeststrategy().updatealloffers(agent.getagentcompanyanalysisagent().getmarketanalysisagent().getpriceprognosis().getstpriceexpectation(), agent.getphysicalnetposition(), agent.getcapitalbase()); //Updates all bids for all agents
			Allselloffers.addAll(agent.getbeststrategy().getAgentsSellOffers());
			Allbuyoffers.addAll(agent.getbeststrategy().getAgentsBuyOffers());
		}
		
		
		ArrayList<BuyOffer> temp = new ArrayList<BuyOffer>();
		temp = Allbuyoffers;
		Allbuyoffers.removeAll(Collections.singleton(null));
		Allselloffers.removeAll(Collections.singleton(null));

		
		int numberofbuyoffers = Allbuyoffers.size(); 
		int numberofselloffers = Allselloffers.size();
		
		//Time for sorting the buy and selloffers. The comparator for objects sell and buyoffers are implementer in CommonMethods. Sort from lowest to highest. 
		Collections.sort(Allselloffers, new CommonMethods.customselloffercomparator());
		Collections.sort(Allbuyoffers, new CommonMethods.custombuyoffercomparator());
		
		
		
		//Should be changed to something smarter. 
		//currentmarketprice = Allbuyoffers.get(0).getbuyofferprice();
		
		initLow = Math.min(Allbuyoffers.get(0).getBuyOfferprice(), Allselloffers.get(0).getSellOfferprice()); 
		initHigh = Math.max(Allbuyoffers.get(numberofbuyoffers-1).getBuyOfferprice(), Allselloffers.get(numberofselloffers-1).getSellOfferprice());
		double certprice = 0;
		double mindiff = 1000000000;
		
		
		//Finds market price. Iterates thorugh all steps, calculates diffs in demand and supply and set price where diff is minimal
		//I situasjoner hvor krysset har ulike salgs og kj�psvillighet. Alts� at i den prisen hvor volume maksimeres, s� er selgeren villig til � selge billigere enn alle kj�pere er villig til � kj�pe. Dette er en lik situsajon som at to markedspriser gir samme maksimert handlet volum. 
		//Hva blir prisen da? Forel�pig satt til det laveste. Dette er ok, siden det ikke vil skje s� ofte n�r budene er mange. 
		for (int i = 0, n = (int) ((initHigh-initLow)/pricestep); i <= n; ++i) { //For all price steps
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
			if (diff<mindiff && supply>0 && demand>0) {
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
					if (s.getSellOfferprice()>certprice-pricestep && s.getSellOfferprice()<certprice-pricestep) {
						marginalsupply = marginalsupply + s.getnumberofcert();}}
				if (marginalsupply<accessupply) {
					shareofmarignalselloffersold = 1;}
				else {
				shareofmarignalselloffersold =(marginalsupply - accessupply)/marginalsupply;
				}
			}
			else {
				double accessdemand = marketdemand - marketsupply;
			for (BuyOffer b : Allbuyoffers) { //Sums all supply for that price (lowest to highest).
				if (b.getBuyOfferprice()>(certprice-pricestep) && b.getBuyOfferprice()<(certprice-pricestep)) {
					marginaldemand = marginaldemand + b.getnumberofcert();}}
			if (marginaldemand<accessdemand) {
				shareofmarginalbuyofferbought = 1;}
			else {
				shareofmarginalbuyofferbought = (marginaldemand - accessdemand)/marginaldemand;
			}
		}}

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

}

