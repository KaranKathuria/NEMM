/*
 * Version info:
 *     File defining the Analysis Agent class. This agent is currently implemented simply by only having a parameter given price expectation.
 *     For future development each analysis agent can have differen price expectations or even different strategies for determining price expectations based on future and historic values. 
 *     For instance a method similar to that of the ElFarol implementation but which also incorporates future variables. 
 *     
 *     Last altered data: 20140722
 *     Made by: Karan Kathuria
 */
package nemmagents;
//Imports
import nemmcommons.GlobalValues;
import nemmcommons.ParameterWrapper;
import nemmcommons.MarketPrognosis;
import nemmagents.ParentAgent;

//Class definition
public class MarketAnalysisAgent extends ParentAgent {
		
	private MarketPrognosis marketprognosis; //The price expectations for next month. 
	// Could have a prise expectations for future prices that deviates form that of today corrected for cost of holding. 
	
	public MarketAnalysisAgent() {
		marketprognosis = new MarketPrognosis();
	}
	
	public MarketPrognosis getpriceprognosis() {
		return marketprognosis;
	}

	
}
