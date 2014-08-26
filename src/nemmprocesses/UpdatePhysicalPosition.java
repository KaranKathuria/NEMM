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
import nemmcommons.CommonMethods;
import nemmstrategy_shortterm.*;
import nemmprocesses.ShortTermMarket;


//
public class UpdatePhysicalPosition {
	
public UpdatePhysicalPosition(){};
	
//This method could be generalized to just iterate over all AAlist, and used a generic but overriden "postmupdate" method).
public static void markettransactions() {
	for (final ActiveAgent agent : CommonMethods.getPAgentList()) {
		int certssold = returnsoldvolume(agent.getbeststrategy().getAgentsSellOffers(),ShortTermMarket.getcurrentmarketprice(), ShortTermMarket.getshareofmarignaloffersold()).getSoldInSTMcert();
		int certsbought = 0; // GenericStrategy.returnboughtvolume(agent.getbeststrategy().getAgentsBuyOffers(),currentmarketprice, shareofmarginalbuyofferbought).getBoughtInSTMnumberofcert();
		agent.poststmupdate(certssold, certsbought);
	}
	
	for (final ActiveAgent agent : CommonMethods.getOPAgentList()) {
		int certssold = 0; //GenericStrategy.returnsoldvolume(agent.getbeststrategy().getAgentsSellOffers(),currentmarketprice, shareofmarignalselloffersold).getSoldInSTMcert();
		int certsbought = returnboughtvolume(agent.getbeststrategy().getAgentsBuyOffers(),ShortTermMarket.getcurrentmarketprice(), ShortTermMarket.getshareofmarignalofferbought()).getBoughtInSTMcert();
		agent.poststmupdate(certssold, certsbought);
	}
	
	for (final ActiveAgent agent : CommonMethods.getTAgentList()) {
		int certssold = returnsoldvolume(agent.getbeststrategy().getAgentsSellOffers(),ShortTermMarket.getcurrentmarketprice(), ShortTermMarket.getshareofmarignaloffersold()).getSoldInSTMcert();
		int certsbought = returnboughtvolume(agent.getbeststrategy().getAgentsBuyOffers(),ShortTermMarket.getcurrentmarketprice(), ShortTermMarket.getshareofmarignalofferbought()).getBoughtInSTMcert();
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
		int soldcerts = 0;
		double averageprice = 0;
		for (SellOffer s : aso) {
			if (s.getSellOfferprice() < marketprice) { //In this case the offers was accepted in the market
			averageprice = ((averageprice*soldcerts)+(s.getnumberofcert()*s.getSellOfferprice()))/(soldcerts+s.getnumberofcert()); //The new average price
			soldcerts = soldcerts + s.getnumberofcert();	//Total number of certs sold is updated
			}
			if (s.getSellOfferprice() == marketprice) {
				averageprice = ((averageprice*soldcerts)+(s.getnumberofcert()*s.getSellOfferprice()*shareoflastoffersold))/(soldcerts+(s.getnumberofcert()*shareoflastoffersold)); //The new average price
				soldcerts = soldcerts + s.getnumberofcert();	//Total number of certs sold is updated
			}}
			ret.averageprice = averageprice;
			ret.numberofcert = soldcerts;
		return ret;
	}
	
	public static BoughtInSTM returnboughtvolume(ArrayList<BuyOffer> abo, double marketprice, double shareoflastofferbought) { //Method calculation the outcome of a selloffers array offered in a STM market
		BoughtInSTM ret = new BoughtInSTM();
		int boughtcerts = 0;
		double averageprice = 0;
		for (BuyOffer b : abo) {
			if (b.getBuyOfferprice() > marketprice) { //In this case the offers was accepted in the market
			averageprice = ((averageprice*boughtcerts)+(b.getnumberofcert()*b.getBuyOfferprice()))/(boughtcerts+b.getnumberofcert()); //The new average price
			boughtcerts = boughtcerts + b.getnumberofcert();	//Total number of certs sold is updated
			}
			if (b.getBuyOfferprice() == marketprice) {
				averageprice = ((averageprice*boughtcerts)+(b.getnumberofcert()*b.getBuyOfferprice()*shareoflastofferbought))/(boughtcerts+(b.getnumberofcert()*shareoflastofferbought)); //The new average price
				boughtcerts = boughtcerts + b.getnumberofcert();	//Total number of certs sold is updated
			}}
			ret.averageprice = averageprice;
			ret.numberofcert = boughtcerts;
		return ret;
	}
	

public static void runproduction() {
	//TBD
}

public static void updatedemand() {
	//
}
}



