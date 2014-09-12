/*
 * Version info:
 *     Set of methods for calculating utilities, both for tactics and strategies. These utilities are agents specific. 
 *     
 *     Last altered data: 20140822
 *     Made by: Karan Kathuria
 */

package nemmstrategy_shortterm;

import java.util.ArrayList;

import nemmagents.CompanyAgent.ActiveAgent;
import nemmstrategy_shortterm.BoughtInSTM;
import nemmstrategy_shortterm.SoldInSTM;

//Class definition
public class GenericUtilityMethod {
	
	private ActiveAgent myAgent;
	
	public GenericUtilityMethod() {}
	
	//Method for scoring utility to be overwritten by subclass methods with same name. Hence this is never used.
	public Double calculateutility(double marketprice, ArrayList<BuyOffer> b, ArrayList<SellOffer> s, double shareofmarginaltoffersold, double shareofmarginalofferbought) {
		double ret = 1000;
		return ret;
	}
	
	public void setmyAgent(ActiveAgent aa) {
		myAgent = aa;
	}
	public ActiveAgent getmyAgent() {
		return myAgent;
	}

}
	
	
