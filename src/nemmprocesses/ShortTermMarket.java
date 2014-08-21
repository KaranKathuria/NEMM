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
import nemmstmstrategiestactics.BuyOffer;
import nemmstmstrategiestactics.GenericStrategy;
import nemmstmstrategiestactics.SellOffer;
import repast.simphony.random.RandomHelper;


//To be implemntetd Should in short take inn all bids, find the price which maximates traded volume. excetute trades. And set the price! (and do somethings smart with the imbalance of trade).
// Take inn bids of differen type. Use them to clear the market and set price. Inputt agents based bids, output price. as well as a methods that updates agents salse volume maybe)
public class ShortTermMarket {
	
	//Static means that the market price exist without the object short term market. Hence we can get the market price without having to refer or create a object short term market.
	private static double currentmarketprice;
	private static int marketsupply;
	private static int marketdemand;
	private static double shareofmarginalbuyofferbought;
	private static double shareofmarignalselloffersold;
	private static int tradedvolume;
	private static double pricestep;
	private static double initLow;
	private static double initHigh;
	private static ArrayList<BuyOffer> Allbuyoffers = new ArrayList<BuyOffer>();
	private static ArrayList<SellOffer> Allselloffers = new ArrayList<SellOffer>();
	
	public ShortTermMarket() {}
			
	public static void runshorttermmarket() {
		//Clears the list of offers from previous iteration
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
			agent.getbeststrategy().updatealloffers(agent.getagentcompanyanalysisagent().getmarketanalysisagent().getpriceprognosis().getstpriceexpectation(), agent.getphysicalnetposition());
			//Updates all bids for all agents
			//Allselloffers.addAll(agent.getbeststrategy().getAgentsSellOffers()); None sell offers from the OP agent list. 
			//marketsupply = agent.getphysicalnetposition();
			Allbuyoffers.addAll(agent.getbeststrategy().getAgentsBuyOffers());
		}
		
		for (final ActiveAgent agent : CommonMethods.getTAgentList()) {
			agent.getbeststrategy().updatealloffers(agent.getagentcompanyanalysisagent().getmarketanalysisagent().getpriceprognosis().getstpriceexpectation(), agent.getphysicalnetposition()); //Updates all bids for all agents
			Allselloffers.addAll(agent.getbeststrategy().getAgentsSellOffers());
			Allbuyoffers.addAll(agent.getbeststrategy().getAgentsBuyOffers());
		}

		
		//Time for sorting the buy and selloffers. The comparator for objects sell and buyoffers are implementer in CommonMethods. Sort from lowest to highest. 
		Collections.sort(Allbuyoffers, new CommonMethods.custombuyoffercomparator());
		Collections.sort(Allselloffers, new CommonMethods.customselloffercomparator());
		
		
		int numberofbuyoffers = Allbuyoffers.size();
		int numberofselloffers = Allselloffers.size();
		//Should be changed to something smarter. 
		//currentmarketprice = Allbuyoffers.get(0).getbuyofferprice();
		
		initLow = Math.min(Allbuyoffers.get(0).getBuyOfferprice(), Allselloffers.get(0).getSellOfferprice()); 
		initHigh = Math.max(Allbuyoffers.get(numberofbuyoffers-1).getBuyOfferprice(), Allselloffers.get(numberofselloffers-1).getSellOfferprice());
		double certprice = 0;
		int mindiff = 1000;
		shareofmarginalbuyofferbought = 1;
		shareofmarignalselloffersold = 1;
		marketdemand = 0;
		marketsupply = 0;
		tradedvolume = 0;
		
		//Finds market price. Iterates thorugh all steps, calculates diffs in demand and supply and set price where diff is minimal
		//I situasjoner hvor krysset har ulike salgs og kj�psvillighet. Alts� at i den prisen hvor volume maksimeres, s� er selgeren villig til � selge billigere enn alle kj�pere er villig til � kj�pe. Dette er en lik situsajon som at to markedspriser gir samme maksimert handlet volum. 
		//Hva blir prisen da? Forel�pig satt til det laveste. Dette er ok, siden det ikke vil skje s� ofte n�r budene er mange. 
		for (int i = 0, n = (int) ((initHigh-initLow)/pricestep); i <= n; ++i) { //For all price steps
			//Get supply
			// Get price with lowes abs diff in s and d
			double tempprice;
			int diff;
			 //TBD: Find a smarter initialiation for mindiff
			tempprice = initLow + (i*pricestep);
			int demand = 0;
			int supply = 0;
			
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
			int marginalsupply = 0;
			int marginaldemand = 0;
			if (marketsupply>marketdemand) {
				int accessupply = marketsupply - marketdemand;
				for (SellOffer s : Allselloffers) { //Sums all supply for that price (lowest to highest).
					if (s.getSellOfferprice()>certprice-pricestep && s.getSellOfferprice()<certprice-pricestep) {
						marginalsupply = marginalsupply + s.getnumberofcert();}}
				if (marginalsupply<accessupply) {
					shareofmarignalselloffersold = 0;}
				else {
				shareofmarignalselloffersold = accessupply/marginalsupply;
				}
			}
			else {
				int accessdemand = marketdemand - marketsupply;
			for (BuyOffer b : Allbuyoffers) { //Sums all supply for that price (lowest to highest).
				if (b.getBuyOfferprice()>(certprice-pricestep) && b.getBuyOfferprice()<(certprice-pricestep)) {
					marginaldemand = marginaldemand + b.getnumberofcert();}}
			if (marginaldemand<accessdemand) {
				shareofmarginalbuyofferbought = 0;}
			else {
				shareofmarginalbuyofferbought = accessdemand/marginalsupply;
			}
		}}
		marketsupply = (int) shareofmarignalselloffersold;
		marketdemand = (int) shareofmarginalbuyofferbought;
		currentmarketprice = certprice;	
	}
public static void updatemarketoutcome() {
	for (final ActiveAgent agent : CommonMethods.getPAgentList()) {
		int certssold = GenericStrategy.returnsoldvolume(agent.getbeststrategy().getAgentsSellOffers(),currentmarketprice, shareofmarignalselloffersold).getSoldInSTMcert();
		int certsbought = 0; // GenericStrategy.returnboughtvolume(agent.getbeststrategy().getAgentsBuyOffers(),currentmarketprice, shareofmarginalbuyofferbought).getBoughtInSTMnumberofcert();
		agent.poststmupdate(certssold, certsbought);
	}
	
	for (final ActiveAgent agent : CommonMethods.getOPAgentList()) {
		int certssold = 0; //GenericStrategy.returnsoldvolume(agent.getbeststrategy().getAgentsSellOffers(),currentmarketprice, 1.0).getSoldInSTMcert();
		int certsbought = GenericStrategy.returnboughtvolume(agent.getbeststrategy().getAgentsBuyOffers(),currentmarketprice, shareofmarginalbuyofferbought).getBoughtInSTMnumberofcert();
		agent.poststmupdate(certssold, certsbought);
	}
	
	for (final ActiveAgent agent : CommonMethods.getTAgentList()) {
		int certssold = GenericStrategy.returnsoldvolume(agent.getbeststrategy().getAgentsSellOffers(),currentmarketprice, shareofmarignalselloffersold).getSoldInSTMcert();
		int certsbought = GenericStrategy.returnboughtvolume(agent.getbeststrategy().getAgentsBuyOffers(),currentmarketprice, shareofmarginalbuyofferbought).getBoughtInSTMnumberofcert();
		agent.poststmupdate(certssold, certsbought);
	}
	//method that estimates the volume traded by  taking the price and the bidded price. If lower/higher, that traded is the volume. Should tak in market price, offers and give out nnumber of certs and average price they where sold at. 
}
public static double getshareofmarignalselloffersold() {
	return shareofmarignalselloffersold;
}
public static double getcurrentmarketprice() {
	return currentmarketprice;}

public static int getmarketsupply() {
	return marketsupply;
}
public static int getmarketdemand() {
	return marketdemand;
}
public static int gettradedvolume() {
	return tradedvolume;
}

public static int getnumberofbuyoffers() {
	return Allbuyoffers.size();
}

public static int getnumberofselloffers() {
	return Allselloffers.size();
}

	/**
	 * Add a buy offer
	 * 
	 * @param buyer
	 *            <p>
	 * @param demand
	 
	public void addBuyOffer(ObligatedPurchaserAgent buyer, Demand demand) {
		BuyOffer offer = new BuyOffer();
		offer.buyer = buyer;
		offer.demand = demand;
		buyOffers.add(offer);
	}

	/**
	 * Add a sell offer
	 * 
	 * @param seller
	 *            <p>
	 * @param qty
	 *            quantity of good available for sale
	 
	public void addSellOffer(ProducerAgent seller, double qty) {
		SellOffer offer = new SellOffer();
		offer.seller = seller;
		offer.qty = qty;
		sellOffers.add(offer);
	}

	/**
	 * Return the total demand given price
	 * 
	 * @param price
	 *            <p>
	 * @return total demand given price
	 
	private double getDemand(double price) {
		double demand = 0;
		for (BuyOffer offer : buyOffers)
			demand += offer.demand.getDemand(price);
		return demand;
	}

	/**
	 * Return the total supply
	 * 
	 * @return the total supply
	 
	private double getSupply() {
		double supply = 0;
		for (SellOffer offer : sellOffers)
			supply += offer.qty;
		return supply;
	}

	/**
	 * Clear the market.
	 
	//Setter maks og min grenser for markedet
	public void clear() {
		double low, high, price;
		if (Economy.getTimeStep() == 0) {
			low = initLow;
			high = initHigh;
		} else {
			low = mktPrice * (1 - zeta);
			high = mktPrice * (1 + zeta);
		}

		double supply = getSupply();
		double demand;
		
		// find market price
		while (true) {
			price = (low + high) / 2;
			demand = getDemand(price);
			if (Math.abs(demand - supply) < 0.1 || Math.abs(high - low) < 0.01)
				break;
			if (demand > supply)
				low = price;
			else
				high = price;
		}

		double vol = Math.min(supply, demand);
		

		// carry out transactions. Loops through all the by and sell offers. Gets demand for the given price in the market. For all offers extract price*qty and increase qty. 
		// Likewise for the sellers. Note that all the money is transferred through bank accounts.
		if (vol > 0.1) {
			for (BuyOffer offer : buyOffers) {
				double qty = offer.demand.getDemand(price) / demand
						* vol;
				double payAmt = qty * price;
				Bank.payFrom(offer.buyer.getID(), payAmt);
				offer.buyer.getGood(good).increase(qty);
			}
			for (SellOffer offer : sellOffers) {
				double qty = offer.qty / supply * vol;
				double payAmt = qty * price;
				Bank.payTo(offer.seller.getID(), payAmt, Bank.PRIIC);
				offer.seller.getGood(good).decrease(qty);
			}
		}

		mktPrice = price;
		mktGoodVol = vol;
		mktMoneyVol = price * mktGoodVol;
		mktSupply = supply;

		buyOffers.clear();
		sellOffers.clear();
	}

	
	 * Return market price
	 * 
	 * @return market price
	 
	public double getLastMktPrice() {
		return mktPrice;
	}

	/**
	 * Return volume of good traded
	 * 
	 * @return volume of good traded
	 
	public double getLastMktGoodVol() {
		return mktGoodVol;
	}

	/**
	 * Return volume of money exchanged
	 * 
	 * @return volume of money exchanged
	 
	public double getLastMktMoneyVol() {
		return mktMoneyVol;
	}

	/**
	 * Return total supply
	 * 
	 * @return total supply
	
	public double getLastMktSupply() {
		return mktSupply;
	}
	*/
}

