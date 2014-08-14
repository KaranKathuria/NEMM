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
import java.lang.Double;
import java.util.List;
import javax.measure.quantity.Length;
import repast.simphony.ui.parameters.Size;
import nemmagents.ExternalAnalysisAgent;
import nemmagents.ActiveAgent;
import nemmagents.ParentAgent;
import nemmstmstrategiestactics.BuyOffer;
import nemmstmstrategiestactics.SellOffer;
import repast.simphony.engine.environment.RunState;

public class CommonMethods {
	
	//Method returning a list of all OPAgents. Usefull for the scheduler. 
	public static List<ActiveAgent> getActiveAgentList() {
		
		@SuppressWarnings("unchecked")
		
		final Iterable<ActiveAgent> Agents = RunState.getInstance().getMasterContext().getObjects(ActiveAgent.class);
		
		final ArrayList<ActiveAgent> ret = new ArrayList<ActiveAgent>();

		for (final ActiveAgent agent : Agents) {
			ret.add(agent);
		}

		return Collections.unmodifiableList(ret);
	}

	public static List<ExternalAnalysisAgent> getEAAgentList() {
	
	@SuppressWarnings("unchecked")
	
	final Iterable<ExternalAnalysisAgent> Agents = RunState.getInstance().getMasterContext().getObjects(ExternalAnalysisAgent.class);
	
	final ArrayList<ExternalAnalysisAgent> ret = new ArrayList<ExternalAnalysisAgent>();

	for (final ExternalAnalysisAgent agent : Agents) {
		ret.add(agent);
	}

	return Collections.unmodifiableList(ret);
}
	
	public static int getnumberofagents() {
		int ret = getActiveAgentList().size() + getEAAgentList().size();
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
		
	}
	