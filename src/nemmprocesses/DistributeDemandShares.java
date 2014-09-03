/*
 * Version info:
 *     File distributing demand shares among company agents with active agents of type obligeted purchaser agents. 
 *     Last altered data: 20140829
 *     Made by: Karan Kathuria
 */

package nemmprocesses;

import nemmcommons.CommonMethods;
import nemmenvironment.Region;
import nemmenvironment.TheEnvironment;

public class DistributeDemandShares {
	
	public DistributeDemandShares() {}
	
	public static void Uniformdemanddistribution(int agentsregion1, int agentsregion2) {
		if (TheEnvironment.allRegions.size() == 2){
		double demandshareregion1 = (1/agentsregion1);
		double demandshareregion2 = (1/agentsregion2);
		int numberofregions;
		int numberofOPAgents = CommonMethods.getOPAgentList().size();
		Region region1 = TheEnvironment.allRegions.get(0);
		Region region2 = TheEnvironment.allRegions.get(1);
		int agentcount = 0;
		
		//Frist for-loop starts shareing the demandshares for all agents that should share demand for region 1
		for (int i = 0; i < (agentsregion1); ++i) { //For all agents shareing the first regions demand
			CommonMethods.getOPAgentList().get(agentcount).AddDemandShare(demandshareregion1, region1);
			agentcount = agentcount+1;
			if (agentcount == numberofOPAgents){ 
				agentcount = 0;}} //Starts share to agents all over. Only used if the total number of agents are less then agents that should share region one.
		
		for (int j = 0; j < agentsregion2; ++j) { //Likewise for region2
			CommonMethods.getOPAgentList().get(agentcount).AddDemandShare(demandshareregion2, region2);
			if (agentcount == numberofOPAgents){ 
				agentcount = 0;}} //Starts share to agents all over if all agents have recived a share.
			
		}
		else {
			throw new IllegalArgumentException("There are more than two regions, hence Uniformdemanddistribution(int agentsregion1, int agentsregion2) cannot be used");
		
		
		}
		
	}

}
