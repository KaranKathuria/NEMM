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

import nemmagents.CompanyAgent;
import nemmagents.CompanyAgent.ActiveAgent;
import nemmagents.CompanyDemandShare;
import nemmcommons.CommonMethods;
import nemmenvironment.PowerPlant;
import nemmenvironment.TheEnvironment;
import nemmstrategy_shortterm.*;
import nemmprocesses.ShortTermMarket;


public class UpdatePhysicalPosition {
	
public UpdatePhysicalPosition(){};
	
public static void updateAllAgentPositions() {

	for (final ActiveAgent agent : CommonMethods.getAAgentList()) {
		agent.updateAgentPositions();
	}
}

//This method could be generalized to just iterate over all AAlist, and used a generic but overriden "postmupdate" method).
/*public static void markettransactions() {
	double totsold=0.0;
	double totbought = 0.0;
	for (final ActiveAgent agent : CommonMethods.getPAgentList()) {
		double certssold = returnsoldvolume(agent.getbeststrategy().getAgentsSellOffers(),ShortTermMarket.getcurrentmarketprice(), ShortTermMarket.getshareofmarignaloffersold()).getSoldInSTMcert();
		double certsbought = 0; // GenericStrategy.returnboughtvolume(agent.getbeststrategy().getAgentsBuyOffers(),currentmarketprice, shareofmarginalbuyofferbought).getBoughtInSTMnumberofcert();
		agent.poststmupdate(certssold, certsbought);
		totsold = totsold + certssold;
		totbought = totbought + certsbought;
	}
	
	for (final ActiveAgent agent : CommonMethods.getOPAgentList()) {
		double certssold = 0; //GenericStrategy.returnsoldvolume(agent.getbeststrategy().getAgentsSellOffers(),currentmarketprice, shareofmarignalselloffersold).getSoldInSTMcert();
		double certsbought = returnboughtvolume(agent.getbeststrategy().getAgentsBuyOffers(),ShortTermMarket.getcurrentmarketprice(), ShortTermMarket.getshareofmarignalofferbought()).getBoughtInSTMcert();
		agent.poststmupdate(certssold, certsbought);
		totsold = totsold + certssold;
		totbought = totbought + certsbought;
	}
	
	for (final ActiveAgent agent : CommonMethods.getTAgentList()) {
		double certssold = returnsoldvolume(agent.getbeststrategy().getAgentsSellOffers(),ShortTermMarket.getcurrentmarketprice(), ShortTermMarket.getshareofmarignaloffersold()).getSoldInSTMcert();
		double certsbought = returnboughtvolume(agent.getbeststrategy().getAgentsBuyOffers(),ShortTermMarket.getcurrentmarketprice(), ShortTermMarket.getshareofmarignalofferbought()).getBoughtInSTMcert();
		//double deltacapitalbase = (certssold-certsbought)*ShortTermMarket.getcurrentmarketprice(); No need
		agent.poststmupdate(certssold, certsbought);
		totsold = totsold + certssold;
		totbought = totbought + certsbought;
	}
	int tmp=1;
	tmp=2;
} */

//The following to methods estimates the result of a buy or sell offer array in the market. Hence it takes inn the offers, the outcome price and a "shareoflastoffer sold/bought. THe latter
	//is for splitting the volume sold or bought which cannot be bought all because it not enough on the other side of the market. The only problem below are the cases where the access volume
	// (the volume which has a price that means accepted, but the volume on the other side is limited) is larger than the volume bought or sold at the market price, so that dropping a share of the
	// highest priced bid on the access side is not enough to balance the market. For now this is not taken care of. 
/*	public static SoldInSTM returnsoldvolume(ArrayList<BidOffer> aso, double marketprice, double shareoflastoffersold) { //Method calculation the outcome of a selloffers array offered in a STM market
		SoldInSTM ret = new SoldInSTM();
		double soldcerts = 0;
		double averageprice = 0;
		double avragepricenotaccepted = 0; //averageprice is the average price for the bids that where not accepted
		double tempvol = 0;
		
		aso.removeAll(Collections.singleton(null));
		for (BidOffer s : aso) {
			if (s.getPrice() <= (marketprice-ShortTermMarket.getpricestep())) { //In this case the offers was accepted in the market
			averageprice = ((averageprice*soldcerts)+(s.getCertVolume()*s.getPrice()))/(soldcerts+s.getCertVolume()); //The new average price
			soldcerts = soldcerts + s.getCertVolume();	//Total number of certs sold is updated
			}
			if (s.getPrice() > marketprice-ShortTermMarket.getpricestep() && s.getPrice() <= marketprice) {
				averageprice = ((averageprice*soldcerts)+(s.getCertVolume()*s.getPrice()*shareoflastoffersold))/(soldcerts+(s.getCertVolume()*shareoflastoffersold)); //The new average price
				soldcerts = soldcerts + (s.getCertVolume()*shareoflastoffersold);	//Total number of certs sold is updated
			}
			if (s.getPrice() > marketprice) {
				avragepricenotaccepted = ((avragepricenotaccepted*tempvol) + (s.getPrice() * s.getCertVolume())) / (tempvol + s.getCertVolume());
				tempvol = tempvol + s.getCertVolume();
			}
				
		}
			ret.averageprice = averageprice;
			ret.numberofcert = soldcerts;
			ret.notacceptavrprice = avragepricenotaccepted;
		return ret; //If the bids in selloffers are null, then this would return 0.0 for the values. This is okey for the market transactions but must be handled in the utiltities. 
	}
	
	public static BoughtInSTM returnboughtvolume(ArrayList<BidOffer> abo, double marketprice, double shareoflastofferbought) { //Method calculation the outcome of a selloffers array offered in a STM market
		BoughtInSTM ret = new BoughtInSTM();
		if (marketprice == 0) { //All would have bought, but the price is zero.
			ret.averageprice = 0.0;
			ret.numberofcert = 0.0;
			ret.notacceptavrprice = 0.0;
			return ret;
		}
		else {
		double boughtcerts = 0;
		double averageprice = 0; //averageprice is the average price for the bids that where accepted
		double avragepricenotaccepted = 0; //averageprice is the average price for the bids that where not accepted
		double tempvol = 0;
		
		abo.removeAll(Collections.singleton(null)); //Have no idea why there are null-offers in the buyoffers list, but this bug is temporarly corrected her.
		for (BidOffer b : abo) {
			if (b.getPrice() >= (marketprice+ShortTermMarket.getpricestep())) { //In this case the offers was accepted in the market
			averageprice = ((averageprice*boughtcerts)+(b.getCertVolume()*b.getPrice()))/(boughtcerts+b.getCertVolume()); //The new average price
			boughtcerts = boughtcerts + b.getCertVolume();	//Total number of certs sold is updated
			}
			if (b.getPrice() >= (marketprice) && b.getPrice() < (marketprice+ShortTermMarket.getpricestep())) {
				averageprice = ((averageprice*boughtcerts)+(b.getCertVolume()*b.getPrice()*shareoflastofferbought))/(boughtcerts+(b.getCertVolume()*shareoflastofferbought)); //The new average price
				boughtcerts = boughtcerts + (b.getCertVolume()*shareoflastofferbought);	//Total number of certs sold is updated
			}
			if (b.getPrice() < marketprice) {
				avragepricenotaccepted = ((avragepricenotaccepted*tempvol) + (b.getPrice() * b.getCertVolume())) / (tempvol + b.getCertVolume());
				tempvol = tempvol + b.getCertVolume();
			}
			
		}
			ret.averageprice = averageprice;
			ret.numberofcert = boughtcerts;
			ret.notacceptavrprice = avragepricenotaccepted;
		return ret;
		}
	}
*/	
	//Method that runs demand and production and adds this to the agents demand, production and physical position.
/*public static void runproduction() {
	
	double testsum=0;
	for (ActiveAgent AA : CommonMethods.getPAgentList()) { //For all Companies
		double thistickprod = 0;
	for (PowerPlant PP : AA.getmypowerplants()) { //For all PowerPlants
		thistickprod = thistickprod + PP.getProduction();
	}
	AA.addtophysicalposition(thistickprod);// Pushes this ticks production to agents physical position and updates last tick production
	testsum = testsum + thistickprod;
	}
	
}

public static void updatedemand() {
	double testsum=0;

	for (ActiveAgent AA : CommonMethods.getOPAgentList()) { //For all activeagents type obligatedpurchaser. THis could have been done for the Companyagent also, but this is faster.
		double tempdemand = 0; //tempvalue
		for (CompanyDemandShare CDS : AA.getMyDemandShares()) { //Go through all demandshares (which consists of a region and a share. 
			tempdemand = tempdemand + (CDS.getMyRegion().getMyDemand().getCertDemand() * CDS.getDemandShare()); //Sum the product of that regions demandshare with that regions demand, for current tick. 
		}
		AA.addtophysicalposition(-tempdemand); //Note sure if you want to define a demand as a negative or positive number.
		testsum = testsum + -tempdemand;

	}
} */

}



