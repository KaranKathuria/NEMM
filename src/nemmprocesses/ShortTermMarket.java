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

import nemmagents.ActiveAgent;
import nemmcommons.CommonMethods;
import nemmcommons.ParameterWrapper;
import nemmstmstrategiestactics.BuyOffer;
import nemmstmstrategiestactics.SellOffer;
import repast.simphony.random.RandomHelper;


//To be implemntetd Should in short take inn all bids, find the price which maximates traded volume. excetute trades. And set the price! (and do somethings smart with the imbalance of trade).
// Take inn bids of differen type. Use them to clear the market and set price. Inputt agents based bids, output price. as well as a methods that updates agents salse volume maybe)
public class ShortTermMarket {
	
	//Static means that the market price exist without the object short term market. Hence we can get the market price without having to refer or create a object short term market.
	private static double currentmarketprice;
	private static double pricestep;
	private static double initLow;
	private static double initHigh;
	private static ArrayList<BuyOffer> Allbuyoffers = new ArrayList<BuyOffer>();
	private static ArrayList<SellOffer> Allselloffers = new ArrayList<SellOffer>();
	
	public ShortTermMarket() {
		
	}
			
	public static void runshorttermmarket() {
		//Clears the list of offers from previous iteration
		pricestep = 0.5;
		Allselloffers.clear();
		Allbuyoffers.clear();
		//Update all analysisagents price expectations		
		//Update and add all sell- and buy-offers.  
		for (final ActiveAgent agent : CommonMethods.getActiveAgentList()) {
			agent.getbeststrategy().updatealloffers(agent.getpriceexpectations(), agent.getphysicalnetposition()); //Updates all bids for all agents
			Allselloffers.addAll(agent.getbeststrategy().getAgentsSellOffers());
			Allbuyoffers.addAll(agent.getbeststrategy().getAgentsBuyOffers());
		}

		//TBD likewise implementation for speculator Agents
		
		//Time for sorting the buy and selloffers. The comparator for objects sell and buyoffers are implementer in CommonMethods. Sort from lowest to highest. 
		Collections.sort(Allbuyoffers, new CommonMethods.custombuyoffercomparator());
		Collections.sort(Allselloffers, new CommonMethods.customselloffercomparator());
		
		
		int numberofbuyoffers = Allbuyoffers.size();
		int numberofselloffers = Allselloffers.size();
		//Should be changed to something smarter. 
		//currentmarketprice = Allbuyoffers.get(0).getbuyofferprice();
		
		initLow = Math.min(Allbuyoffers.get(0).getBuyOfferprice(), Allselloffers.get(0).getSellofferprice());
		initHigh = Math.max(Allbuyoffers.get(numberofbuyoffers-1).getBuyOfferprice(), Allselloffers.get(numberofselloffers-1).getSellofferprice());
		double certprice = 0;
		int mindiff = 1000;
		
		//Finds market price. Iterates thorugh all steps, calculates diffs in demand and supply and set price where diff is minimal
		for (int i = 0, n = (int) ((initHigh-initLow)/pricestep); i < n; ++i) { //For all price steps
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
				if (s.getSellofferprice()<=tempprice) {
					supply = supply + s.getnumberofcert();}
			}
			diff = Math.abs(demand-supply);
			if (diff<mindiff) {
				mindiff = diff;
				certprice = tempprice;} //certprice is when the diff is minimum. 
			
		}
		
		currentmarketprice = certprice; 
		
	}
	
	
public static double getcurrentmarketprice() {
	return currentmarketprice;}

public static void runshorttermmarket1() {
	currentmarketprice = nemmcommons.ParameterWrapper.getpriceexpectation() + RandomHelper.nextDoubleFromTo(-10.0, +10.0);
	pricestep = 0.5;
	initLow = 100;
	initHigh = 500;	
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

