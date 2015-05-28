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
	
	protected ActiveAgent myAgent;
	
	public GenericUtilityMethod() {}
	
	//Method for scoring utility to be overwritten by subclass methods with same name. Hence this is never used.
	public Double calculateutility(double marketprice, ArrayList<BidOffer> b, ArrayList<BidOffer> s, double shareofmarginaltoffersold, double shareofmarginalofferbought) {
		double ret = 1000;
		return ret;
	}
	// This needs to be overwritten for each utility method
	public ArrayList<double[]> CalcUtilityWithHistory(double marketprice, ArrayList<BidOffer> s, double shareofmarginaltoffersold) {
		ArrayList<double[]> retList = new ArrayList<double[]>();
		double[] tempArray = new double[]{1.0,2.0,3.0};	
		retList.add(tempArray);
		return retList;
	}
	
	public void setmyAgent(ActiveAgent aa) {
		myAgent = aa;
	}
	public ActiveAgent getmyAgent() {
		return myAgent;
	}

}
	
	
