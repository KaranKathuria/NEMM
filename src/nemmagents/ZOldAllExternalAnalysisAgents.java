/*
 * Version info:
 *     Experimental class defining a list of all analysis agents in order to relate other agents with an analysis agent by index 
 *     and for not having to give all other agents a list of analysis agents.
 *     
 *     First created: 20140722
 *     Made by: Karan Kathuria (KK)
 */
package nemmagents;

import java.util.ArrayList;
import nemmagents.ParentAgent;
import java.util.List;
import nemmagents.ExternalAnalysisAgent;
import nemmcommons.ParameterWrapper;

public class ZOldAllExternalAnalysisAgents extends ParentAgent {
	
	public static List<ExternalAnalysisAgent> AAgentlist = new ArrayList<ExternalAnalysisAgent>();
	
	public ZOldAllExternalAnalysisAgents() {
		for (int i = 0, n = ParameterWrapper.getanalysisagentsnumber(); i < n; ++i) {
			AAgentlist.add(new ExternalAnalysisAgent());
		}
		}

	public static ExternalAnalysisAgent getrelatedanalysisagent(int a) {
	ExternalAnalysisAgent act = AAgentlist.get(a);
	return act; 
	}
	
}
