/*
 * Version info:
 *     Common global methods in use
 *     
 *     Last altered data: 20140721
 *     Made by: Karan Kathuria
 */
package nemmcommons;
//Imports

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.math.*;

import nemmagents.CompanyAgent.ActiveAgent;
import nemmagents.CompanyAgent;
import nemmagents.MarketAnalysisAgent;
import nemmstrategy_shortterm.BuyOffer;
import nemmstrategy_shortterm.SellOffer;
import nemmtime.NemmCalendar;
import repast.simphony.engine.environment.RunState;

public class CommonMethods {
	
	//Method returning a list of all OPAgents. Usefull for the scheduler. 
public static List<CompanyAgent> getCompanyAgenList() {
		
		@SuppressWarnings("unchecked")
		
		final Iterable<CompanyAgent> Agents = RunState.getInstance().getMasterContext().getObjects(CompanyAgent.class);
		
		final ArrayList<CompanyAgent> ret = new ArrayList<CompanyAgent>();

		for (final CompanyAgent agent : Agents) {
			ret.add(agent);
		}

		return Collections.unmodifiableList(ret);
	}

	public static List<ActiveAgent> getAAgentList() {
	
	@SuppressWarnings("unchecked")
	
	final Iterable<CompanyAgent> Agents = RunState.getInstance().getMasterContext().getObjects(CompanyAgent.class);
	
	final ArrayList<ActiveAgent> ret = new ArrayList<ActiveAgent>();

	for (final CompanyAgent agent : Agents) {
		if (agent.getproduceragent() != null){
			ret.add(agent.getproduceragent());}
		if (agent.getobligatedpurchaseragent() != null){
			ret.add(agent.getobligatedpurchaseragent());}
		if (agent.gettraderagent() != null){
			ret.add(agent.gettraderagent());}
	}

	return Collections.unmodifiableList(ret);
}
	
	public static List<ActiveAgent> getPAgentList() {
			
			@SuppressWarnings("unchecked")
			
			final Iterable<CompanyAgent> Agents = RunState.getInstance().getMasterContext().getObjects(CompanyAgent.class);
			
			final ArrayList<ActiveAgent> ret = new ArrayList<ActiveAgent>();

			for (final CompanyAgent agent : Agents) {
				if (agent.getproduceragent() != null){
				ret.add(agent.getproduceragent());}
			}

			return Collections.unmodifiableList(ret);
		}
	
	public static List<ActiveAgent> getOPAgentList() {
		
		@SuppressWarnings("unchecked")
		
		final Iterable<CompanyAgent> Agents = RunState.getInstance().getMasterContext().getObjects(CompanyAgent.class);
		
		final ArrayList<ActiveAgent> ret = new ArrayList<ActiveAgent>();

		for (final CompanyAgent agent : Agents) {
			if (agent.getobligatedpurchaseragent() != null){
			ret.add(agent.getobligatedpurchaseragent());}
		}

		return Collections.unmodifiableList(ret);
	}
	
	public static List<ActiveAgent> getTAgentList() {
		
		@SuppressWarnings("unchecked")
		
		final Iterable<CompanyAgent> Agents = RunState.getInstance().getMasterContext().getObjects(CompanyAgent.class);
		
		final ArrayList<ActiveAgent> ret = new ArrayList<ActiveAgent>();

		for (final CompanyAgent agent : Agents) {
			if (agent.gettraderagent() != null){
			ret.add(agent.gettraderagent());}
		}

		return Collections.unmodifiableList(ret);
	}

	public static List<MarketAnalysisAgent> getMAAgentList() {
	
	@SuppressWarnings("unchecked")
	
	final Iterable<CompanyAgent> Agents = RunState.getInstance().getMasterContext().getObjects(CompanyAgent.class);
	
	final ArrayList<MarketAnalysisAgent> ret = new ArrayList<MarketAnalysisAgent>();

	for (final CompanyAgent agent : Agents) {
		ret.add(agent.getcompanyanalysisagent().getmarketanalysisagent());
	}

	return Collections.unmodifiableList(ret);
}
	
	public static int getnumberofagents() {
		int ret = getPAgentList().size() + getMAAgentList().size() + getOPAgentList().size() + getTAgentList().size();
		return ret;
	}
	//The following two methods makes it possible to compare sell and byoffers by price. 
	public static class custombuyoffercomparator implements Comparator<BuyOffer> {
	    @Override
	    public int compare(BuyOffer o1, BuyOffer o2) {
	        return o1.getBuyOfferprice().compareTo(o2.getBuyOfferprice());
	    }
	}
	public static class customselloffercomparator implements Comparator<SellOffer> {
	    @Override
	    public int compare(SellOffer o1, SellOffer o2) {
	        return o1.getSellOfferprice().compareTo(o2.getSellOfferprice());
	    }
	}
	
	
	// ----------------------------------------------------------------------------
		
	// Calculate the difference between two Calendar dates, in hours
	// GJB 19Aug2014	
	//public static long HoursBetween(NemmCalendar startDate, NemmCalendar endDate) {
	//    long end = endDate.getTimeInMillis();
	//    long start = startDate.getTimeInMillis();
	//    return TimeUnit.MILLISECONDS.toHours(Math.abs(end - start));
	//}
	

	// Added by GJB 20Aug2014. Source: see
	// http://stackoverflow.com/questions/363681/generating-random-integers-in-a-range-with-java  and
	// http://docs.oracle.com/javase/7/docs/api/java/util/Random.html#nextInt%28int%29
	
	public static int randInt(int min, int max) {

	    // NOTE: Usually this should be a field rather than a method
	    // variable so that it is not re-seeded every call.
	    Random rand = new Random();

	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}
	
	//Multiply two equal length arrays element by element
	
	public static double[] elArrayMult(double[] Array1, double[] Array2){
		
		if (Array1.length != Array2.length){
			throw new IllegalArgumentException("elArrayMult: Array1 and Array2 are of unequal sizes");				
		}
		double[] resultArray = new double[Array1.length];
		for (int y = 0; y < Array1.length; ++y){
			resultArray[y] = Array1[y]*Array2[y];
		}
		return resultArray;
	}
	
	// GJB LEARNING
	public static int signDbl(double testNum){ 	// Sign of a double - returns -1 if <0, 0 if 0 and 1 if >0
		int retVal;
		if (testNum < 0){
			retVal = -1;
		} 
		else if (testNum == 0) {
			retVal = 0;
		} 
		else {
			retVal = 1;
		}
		return retVal;
	}
	
	// NPV
	public static double calculateNPV(double value, double R, double Years) { //Calculates the NPV of a given value with given R and number of years.
		return (value/(Math.pow(1+R, Years)));	
	}
	
}
	