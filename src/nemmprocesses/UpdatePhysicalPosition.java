/*
 * Version info:
 *     Object containing files that ensures that the agents physical position is updated.
 *     
 *     Last altered data: 20140722
 *     Made by: Karan Kathuria
 */

package nemmprocesses;

import java.util.ArrayList;
import java.util.Collections;
import nemmagents.CompanyAgent.ActiveAgent;
import nemmagents.CompanyDemandShare;
import nemmcommons.CommonMethods;
import nemmenvironment.PowerPlant;
import nemmenvironment.TheEnvironment;
import nemmstrategy_shortterm.*;
import nemmprocesses.ShortTermMarket;


public class UpdatePhysicalPosition {
	
public UpdatePhysicalPosition(){};
	
//This method could be generalized to just iterate over all AAlist, and used a generic but overriden "postmupdate" method).
public static void markettransactions() {
	for (final ActiveAgent agent : CommonMethods.getPAgentList()) {
		double certssold = returnsoldvolume(agent.getbeststrategy().getAgentsSellOffers(),ShortTermMarket.getcurrentmarketprice(), ShortTermMarket.getshareofmarignaloffersold()).getSoldInSTMcert();
		double certsbought = 0; // GenericStrategy.returnboughtvolume(agent.getbeststrategy().getAgentsBuyOffers(),currentmarketprice, shareofmarginalbuyofferbought).getBoughtInSTMnumberofcert();
		agent.poststmupdate(certssold, certsbought);
	}
	
	for (final ActiveAgent agent : CommonMethods.getOPAgentList()) {
		double certssold = 0; //GenericStrategy.returnsoldvolume(agent.getbeststrategy().getAgentsSellOffers(),currentmarketprice, shareofmarignalselloffersold).getSoldInSTMcert();
		double certsbought = returnboughtvolume(agent.getbeststrategy().getAgentsBuyOffers(),ShortTermMarket.getcurrentmarketprice(), ShortTermMarket.getshareofmarignalofferbought()).getBoughtInSTMcert();
		agent.poststmupdate(certssold, certsbought);
	}
	
	for (final ActiveAgent agent : CommonMethods.getTAgentList()) {
		double certssold = returnsoldvolume(agent.getbeststrategy().getAgentsSellOffers(),ShortTermMarket.getcurrentmarketprice(), ShortTermMarket.getshareofmarignaloffersold()).getSoldInSTMcert();
		double certsbought = returnboughtvolume(agent.getbeststrategy().getAgentsBuyOffers(),ShortTermMarket.getcurrentmarketprice(), ShortTermMarket.getshareofmarignalofferbought()).getBoughtInSTMcert();
		//double deltacapitalbase = (certssold-certsbought)*ShortTermMarket.getcurrentmarketprice(); No need
		agent.poststmupdate(certssold, certsbought);
	}
	//method that estimates the volume traded by  taking the price and the bidded price. If lower/higher, that traded is the volume. Should take in market price, offers and give out nnumber of certs and average price they where sold at. 
}

//The following to methods estimates the result of a buy or sell offer array in the market. Hence it takes inn the offers, the outcome price and a "shareoflastoffer sold/bought. THe latter
	//is for splitting the volume sold or bought which cannot be bought all because it not enough on the other side of the market. The only problem below are the cases where the access volume
	// (the volume which has a price that means accepted, but the volume on the other side is limited) is larger than the volume bought or sold at the market price, so that dropping a share of the
	// highest priced bid on the access side is not enough to balance the market. For now this is not taken care of. 
	public static SoldInSTM returnsoldvolume(ArrayList<SellOffer> aso, double marketprice, double shareoflastoffersold) { //Method calculation the outcome of a selloffers array offered in a STM market
		SoldInSTM ret = new SoldInSTM();
		double soldcerts = 0;
		double averageprice = 0; //averageprice is the average price for the bids that where accepted
		aso.removeAll(Collections.singleton(null));
		for (SellOffer s : aso) {
			if (s.getSellOfferprice() <= (marketprice-ShortTermMarket.getpricestep())) { //In this case the offers was accepted in the market
			averageprice = ((averageprice*soldcerts)+(s.getnumberofcert()*s.getSellOfferprice()))/(soldcerts+s.getnumberofcert()); //The new average price
			soldcerts = soldcerts + s.getnumberofcert();	//Total number of certs sold is updated
			}
			if (s.getSellOfferprice() > marketprice-ShortTermMarket.getpricestep() && s.getSellOfferprice() <= marketprice) {
				averageprice = ((averageprice*soldcerts)+(s.getnumberofcert()*s.getSellOfferprice()*shareoflastoffersold))/(soldcerts+(s.getnumberofcert()*shareoflastoffersold)); //The new average price
				soldcerts = soldcerts + (s.getnumberofcert()*shareoflastoffersold);	//Total number of certs sold is updated
			}}
			ret.averageprice = averageprice;
			ret.numberofcert = soldcerts;
		return ret;
	}
	
	public static BoughtInSTM returnboughtvolume(ArrayList<BuyOffer> abo, double marketprice, double shareoflastofferbought) { //Method calculation the outcome of a selloffers array offered in a STM market
		BoughtInSTM ret = new BoughtInSTM();
		if (marketprice == 0) { //All would have bought, but the price is zero.
			ret.averageprice = 0.0;
			ret.numberofcert = 0.0;
			return ret;
		}
		else {
		double boughtcerts = 0;
		double averageprice = 0; //averageprice is the average price for the bids that where accepted
		abo.removeAll(Collections.singleton(null)); //Have no idea why there are null-offers in the buyoffers list, but this bug is temporarly corrected her.
		for (BuyOffer b : abo) {
			if (b.getBuyOfferprice() >= (marketprice+ShortTermMarket.getpricestep())) { //In this case the offers was accepted in the market
			averageprice = ((averageprice*boughtcerts)+(b.getnumberofcert()*b.getBuyOfferprice()))/(boughtcerts+b.getnumberofcert()); //The new average price
			boughtcerts = boughtcerts + b.getnumberofcert();	//Total number of certs sold is updated
			}
			if (b.getBuyOfferprice() >= (marketprice) && b.getBuyOfferprice() < (marketprice+ShortTermMarket.getpricestep())) {
				averageprice = ((averageprice*boughtcerts)+(b.getnumberofcert()*b.getBuyOfferprice()*shareoflastofferbought))/(boughtcerts+(b.getnumberofcert()*shareoflastofferbought)); //The new average price
				boughtcerts = boughtcerts + (b.getnumberofcert()*shareoflastofferbought);	//Total number of certs sold is updated
			}}
			ret.averageprice = averageprice;
			ret.numberofcert = boughtcerts;
		return ret;
		}
	}
	

public static void runproduction() {
	
	for (PowerPlant pp : TheEnvironment.allPowerPlants) {
		pp.getMyCompany().getproduceragent().addtophysicalposition(pp.getProduction()); //Cased as int temporarly. Pushes this ticks production to agents physical position.
	}
}

public static void updatedemand() {
	for (ActiveAgent AA : CommonMethods.getOPAgentList()) { //For all activeagents type obligatedpurchaser. THis could have been done for the Companyagent also, but this is faster.
		double tempdemand = 0; //tempvalue
		for (CompanyDemandShare CDS : AA.getMyDemandShares()) { //Go through all demandshares (which consists of a region and a share. 
			tempdemand = tempdemand + CDS.getMyRegion().getMyDemand().getCertDemand() * CDS.getDemandShare(); //Sum the product of that regions demandshare with that regions demand, for current tick. 
		}
		AA.addtophysicalposition(-tempdemand); //Note sure if you want to define a demand as a negative or positive number. 
	}
}

}



