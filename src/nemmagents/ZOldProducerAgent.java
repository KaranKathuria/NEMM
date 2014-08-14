/*
 * Version info:
 *     File defining the producer agent class. Producers have access to market expectations given by their connected analysis agent. Further they utilize this and expectations about
 *     production and a certain bidding strategy to form bids in the short term market. 
 *     
 *     Last altered data: 20140722
 *     Made by: Karan Kathuria
 */
/*
package nemmagents;

//Import section
import java.util.ArrayList;
import java.util.List;
import nemmagents.ParentAgent;
import nemmstmstrategiestactics.PABidstrategy;
import nemmcommons.ParameterWrapper;
import nemmagents.AllAnalysisAgents;

//TBD: Implement long term and short term beststrategies.

// Class definition
public class ZOldProducerAgent extends ParentAgent 
{


	//The list of Producer specific strategy objects this agent may use for prediction.
	private final List<PABidstrategy> strategies = new ArrayList<PABidstrategy>();
	
	//Could be a paramater, but hardcoded her for now
	private int numberofstrategies = 1;
	
	// The best strategy used so far. Initially it is set to the first one.	
	private PABidstrategy beststrategy = null;
	
	// Initial monthly production for a producer agent is given by the input parameters
	private int monthlyproduction = ParameterWrapper.getmonthlysupply();
	
	// Initial values set to zero. Notice the differentiation between the banked certificates this period and from previous periods. 
	private int bankedcertificates_total = 0;
	// total produced certificates this clearing period.
	private int bankedcertificates_cp = 0;
	// total certificates sold this clearing period.
	private int totalsold_cp = 0;
	// Balance, that is the produced minus sold. Notice that this value is what some might call banked, as they refer to banked as net banked.
	private int certificatebalance_cp = 0;
	//The price expectation is given by the linked AnalysisAgent`s price expectation. ALternativly a PAgent should have an analystagent, but this would make it hard to share this agent with others
	private int relatedanalysisagentindex = 0;
	private double stpriceexpectation = nemmcommons.ParameterWrapper.getpriceexpectation();
	
	
	//ProducerAgent constructor. Creates a list of exactly "numberofstrategies" number of strategies and the PABidstrategyinstance. Recall that this PABidstrategy instance only contains zeros.
	public ZOldProducerAgent() {
		for (int i = 0; i < numberofstrategies; i++) {
			strategies.add(new PABidstrategy());
				}
		beststrategy = strategies.get(0); // Choose the first one initially 
		//Advanced. Sets this produceragents price expectation to that of the related analysisagent.
		//The related analysisagent is given by "relatedanalysisagentindex", hence we have to go by AllAnalysisagents which is a public record/list of all AAgents.
		//Not sure about this static/non-static thing.	
	}
	
	//Get methods
	public int getbankedcertificates_total() {
		return bankedcertificates_total;
		}
	public int getbankedcertificates_cp() {
		return bankedcertificates_cp;
		}
	public int getPAmonthlyproduction() {
		return monthlyproduction;
		}
	public PABidstrategy getPAbeststrategy() {
		return beststrategy;
		}
	public double getpriceexpectations() {
		return stpriceexpectation;
	}
	
	//Update methods
			
	//Used for monthly updates within a clearing period. Notice that and annual uppdate would also have to take care of total banked certificates and totalsold.
	//THis method should somehow take care of calculating how many certiicates the agents have sold, based on the market price in the stm.
	public void monthlyupdatePAgent(int certificatessold) {
		bankedcertificates_cp = bankedcertificates_cp + monthlyproduction;
		totalsold_cp = totalsold_cp + certificatessold;
		certificatebalance_cp = certificatebalance_cp + (monthlyproduction - certificatessold);
		monthlyproduction = ParameterWrapper.getmonthlysupply();
		stpriceexpectation = AllAnalysisAgents.getrelatedanalysisagent(relatedanalysisagentindex).getstpriceexpectation(); 
		}
	
	// ========================================================================
		// === Public Interface ===================================================
	
}
*/
