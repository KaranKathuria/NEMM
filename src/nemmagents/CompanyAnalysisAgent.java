/*
 * Version info:
 *     File defining an companyanalysisagent which is an collective agent class for price analysis and volume analysis agents. Each Company Agent has just one Company Analysis Agent, and this agent 
 *     have one volume analysis agent, one internal price agent, and none, one or more external price agents.  
 *     Last altered data: 20140722
 *     Made by: Karan Kathuria
 */
package nemmagents;
//Imports
import nemmcommons.ParameterWrapper;
import nemmagents.ParentAgent;

//Class definition
public class CompanyAnalysisAgent extends ParentAgent {
		
	private MarketAnalysisAgent marketanalysisagent;
	private VolumeAnalysisAgent volumeanalysisagent;

	CompanyAnalysisAgent() {
		marketanalysisagent = new MarketAnalysisAgent();
		volumeanalysisagent = new VolumeAnalysisAgent();
	}
	public MarketAnalysisAgent getmarketanalysisagent() {
		return marketanalysisagent;
	}
	public VolumeAnalysisAgent getvolumeanalysisagent() {
		return volumeanalysisagent;
	}
}
