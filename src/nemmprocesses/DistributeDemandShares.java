/*
 * Version info:
 *     File distributing demand shares among company agents with active agents of type obligeted purchaser agents. 
 *     Last altered data: 20140829
 *     Made by: Karan Kathuria
 */

package nemmprocesses;

import java.util.ArrayList;
import repast.simphony.random.RandomHelper;
import nemmagents.CompanyAgent.ActiveAgent;
import nemmcommons.CommonMethods;
import nemmenvironment.Region;
import nemmenvironment.TheEnvironment;

public class DistributeDemandShares {
	
	public DistributeDemandShares() {}
	
	public static void Uniformdemanddistribution(int agentsregion1, int agentsregion2) {
		if (TheEnvironment.allRegions.size() == 2){
		double demandshareregion1 = (1/ agentsregion1);
		double demandshareregion2 = (double) (1/(double) agentsregion2);
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
			agentcount = agentcount+1;
			if (agentcount == numberofOPAgents){ 
				agentcount = 0;}} //Starts share to agents all over if all agents have recived a share.
			
		}
		else {
			throw new IllegalArgumentException("There are more than two regions, hence Uniformdemanddistribution(int agentsregion1, int agentsregion2) cannot be used");
		
		
		}
		
	}
	
	public static void distributedemand(int distcode) {
		if (TheEnvironment.allRegions.size() != 2){throw new IllegalArgumentException("Error: Distribution of demand does not suite more than two regions");}
					
		int numberofOPA = CommonMethods.getOPAgentList().size();
		if (numberofOPA <= 0){
		throw new IllegalArgumentException("Error: Zero Companies with OPAs");}
		
		int[] distribution = new int[3]; //Only works with two regions.
		
		//Assignes the probabilitydistribution used among the OPAagents with various sizecodes.
				switch (distcode) {
					case 1: distribution[0] = 1; distribution[1] = 1; distribution[2] = 1;
					break;
					case 2: distribution[0] = 1; distribution[1] = 2; distribution[2] = 3;
					break;
					case 3: distribution[0] = 2; distribution[1] = 3; distribution[2] = 6;
					break;
					}
		
		ArrayList<ActiveAgent> probadjustedagentlistNorway = new ArrayList<ActiveAgent>();
		ArrayList<ActiveAgent> probadjustedagentlistSweden = new ArrayList<ActiveAgent>();
		
		//Fills the two arraylists with PA´s having apperance in that region and with copies according to probabilitydistribution;
		for (ActiveAgent OPA : CommonMethods.getOPAgentList())	{
			if (OPA.getregionpartcode() > 3 || OPA.getsizecode() > 3) {throw new IllegalArgumentException("Error: Regionrepcode or sizecode of OPAS not accepted");}
			if (OPA.getregionpartcode() < 3) {				//Thats Norway or Sweden and Norway
				for (int i = 1; i <= distribution[OPA.getsizecode()-1]; i++) {
					probadjustedagentlistNorway.add(OPA);	//Adding the number of copies corresponding to the probability distirbution
					}
				}
			if (OPA.getregionpartcode() > 1) {			//Thats not just Norway (or Sweden and Sweden and Norway if you like)
				for (int i = 1; i <= distribution[OPA.getsizecode()-1]; i++) {
					probadjustedagentlistSweden.add(OPA);
							}
				}
			}
		
		double demandshareunitreg1 = (double) 1/((double) probadjustedagentlistNorway.size());	//One unit of demandshare difined as 1 diveded but total number of OPA copies havin business in that region.
		double demandshareunitreg2 = (double) 1/((double) probadjustedagentlistSweden.size());	//One unit of demandshare difined as 1 diveded but total number of OPA copies havin business in that region.		

	int randintervalNorway = probadjustedagentlistNorway.size() -1;		//Norway
	int randintervalSweden = probadjustedagentlistSweden.size() -1;		//Sweden	
	Region region1 = TheEnvironment.allRegions.get(0);					//Norway
	Region region2 = TheEnvironment.allRegions.get(1);					//Sweden

	
	//Asignes the demandshares to agents according to their apperance in the probadjustedagentlists. An OPA can have multiple demand shares for one specific region.
	for (int i = 0; i < probadjustedagentlistNorway.size() ; i++) {
		int assign = RandomHelper.nextIntFromTo(0,randintervalNorway);
		probadjustedagentlistNorway.get(assign).AddDemandShare(demandshareunitreg1, region1);} //THe randomly selected OPA object gets the demandshare. This is done as many times as there are demandshares. 
		
	for (int i = 0; i < probadjustedagentlistSweden.size() ; i++) {
		int assign = RandomHelper.nextIntFromTo(0,randintervalSweden);
		probadjustedagentlistNorway.get(assign).AddDemandShare(demandshareunitreg2, region2);} //THe randomly selected OPA object gets the demandshare. This is done as many times as there are demandshares. 
		
	}		

}
