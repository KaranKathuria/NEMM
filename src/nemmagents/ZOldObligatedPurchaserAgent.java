/*
 * Version info:
 *     File defining the obligated purchaser agent class. Purchasers have access to market expectations given by their connected analysis agent. Further they utilize this and expectations about
 *     production and a certain bidding strategy to form bids in the short-term market. 
 *     
 *     First created: 20140722
 *     Made by: Karan Kathuria (KK)
 */

/*
package nemmagents;

// Import section
import java.util.ArrayList;
import java.util.List;

import nemmagents.ParentAgent;
import nemmstmstrategiestactics.BuyStrategy1;
import nemmstmstrategiestactics.PABidstrategy;
import nemmcommons.ParameterWrapper;
import nemmagents.AllAnalysisAgents;



//Class definitions
public class ZOldObligatedPurchaserAgent extends ParentAgent{

	private final List<OPABidstrategy> strategies = new ArrayList<OPABidstrategy>();
	

	//The best strategy used so far
	private OPABidstrategy beststrategy = null;
	//Could be a paramater, but hardcoded her for now
	private int numberofstrategies = 1;
	//Some code that adds strategies to the strategies array and initially select one of them
	private int monthlydemand = ParameterWrapper.getmonthlydemand();
	//Internal variables that is updated each tick and clear every clearingperiod (_cp).
	// Total certificates banked in this trading period (accumulated sum)
	private int bankedcertificates_cp = 0;
	// Total demand of certificates accumulated this trading period
	private int totaldemand_cp = 0;
	// Balance of certificates this clearing period. That is total demand minus banked for this cp.
	private int certificatebalance_cp = 0;
	//The price expectation is given by the linked AnalysisAgent`s price expectation. ALternativly a PAgent should have an analystagent, but this would make it hard to share this agent with others
	private int relatedanalysisagentindex = 0;
	private double stpriceexpectation = nemmcommons.ParameterWrapper.getpriceexpectation();
	
	//Simple ObligatedPurchaserAgent constructor. Creates a list of exactly "numberofstrategies" number of strategies and the OPABidstrategyinstance. Recall that this OPABidstrategy instance only contains zeros.
	public ZOldObligatedPurchaserAgent() {
		for (int i = 0; i < numberofstrategies; i++) {
			strategies.add(new OPABidstrategy());
			
				}
		beststrategy = strategies.get(0); // Choose the first one initially 
		//Advanced. Sets this OPAgents price expectation to that of the related analysisagent.
		//The related analysisagent is given by "relatedanalysisagentindex", hence we have to go by AllAnalysisagents which is a public record/list of all AAgents.
		//Not sure about this static/non-static thing.	
	}
	
	//Get methods
	public int getbankedcertificates_cp() {
		return bankedcertificates_cp;
	}
	public int getOPAmonthlydemand() {
		return monthlydemand;
	}
	public OPABidstrategy getOPAbeststrategy() {
		return beststrategy;
		}
	public double getpriceexpectations() {
		return stpriceexpectation;
	}
	
	//Update methods
	
	//Used for monthly updates within a clearing period
	public void monthlyupdateOPAgent(int certificatesbought) {
		bankedcertificates_cp = bankedcertificates_cp + certificatesbought;
		totaldemand_cp = totaldemand_cp + monthlydemand;
		certificatebalance_cp = certificatebalance_cp + (bankedcertificates_cp - totaldemand_cp);
		monthlydemand = ParameterWrapper.getmonthlydemand();
		stpriceexpectation = AllAnalysisAgents.getrelatedanalysisagent(relatedanalysisagentindex).getstpriceexpectation(); 
	}

	// ========================================================================
		// === Public Interface ===================================================
}
*/
