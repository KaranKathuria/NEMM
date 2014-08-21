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
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import nemmagents.CompanyAgent;
import nemmagents.MarketAnalysisAgent;
import nemmagents.ActiveAgent;
import nemmstmstrategiestactics.BuyOffer;
import nemmstmstrategiestactics.SellOffer;
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

	public static List<ActiveAgent> getActiveAgentList() {
		
		@SuppressWarnings("unchecked")
		
		final Iterable<ActiveAgent> Agents = RunState.getInstance().getMasterContext().getObjects(ActiveAgent.class);
		
		final ArrayList<ActiveAgent> ret = new ArrayList<ActiveAgent>();

		for (final ActiveAgent agent : Agents) {
			ret.add(agent);
		}

		return Collections.unmodifiableList(ret);
	}

	public static List<MarketAnalysisAgent> getMAAgentList() {
	
	@SuppressWarnings("unchecked")
	
	final Iterable<MarketAnalysisAgent> Agents = RunState.getInstance().getMasterContext().getObjects(MarketAnalysisAgent.class);
	
	final ArrayList<MarketAnalysisAgent> ret = new ArrayList<MarketAnalysisAgent>();

	for (final MarketAnalysisAgent agent : Agents) {
		ret.add(agent);
	}

	return Collections.unmodifiableList(ret);
}
	
	public static int getnumberofagents() {
		int ret = getActiveAgentList().size() + getMAAgentList().size();
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
	        return o1.getSellofferprice().compareTo(o2.getSellofferprice());
	    }
	}
	
	// ----------------------------------------------------------------------------
		
	// Calculate the difference between two Calendar dates, in hours
	// GJB 19Aug2014	
	public static long HoursBetween(Calendar startDate, Calendar endDate) {
	    long end = endDate.getTimeInMillis();
	    long start = startDate.getTimeInMillis();
	    return TimeUnit.MILLISECONDS.toHours(Math.abs(end - start));
	}
	
	// ----------------------------------------------------------------------------
				
	/**
	 * Returns a pseudo-random number between min and max, inclusive.
	 * The difference between min and max can be at most
	 * <code>Integer.MAX_VALUE - 1</code>.
	 *
	 * @param min Minimum value
	 * @param max Maximum value.  Must be greater than min.
	 * @return Integer between min and max, inclusive.
	 * @see java.util.Random#nextInt(int)
	 */
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
	
	// ----------------------------------------------------------------------------
		

	
	}
	