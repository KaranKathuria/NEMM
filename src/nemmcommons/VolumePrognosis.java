/*
 * Version info:
 * 	   Class object for volume prognosis. This objet contains an VolumeAnalysisAgents own prognosis of internal production and demand for all future ticks.
 * 	   These valus will somehow be distributed around the actual world demand and production for the agent owning this volume analysis agent.
 *     Created: 20141209
 *     Made by: Karan Kathuria  
 */

package nemmcommons;
import nemmagents.CompanyAgent.CompanyAnalysisAgent.VolumeAnalysisAgent;

// Import


//Class definitions. Note that this is a static class as all its member variables are static.
public class VolumePrognosis {
	
	private TickArray expectedtotalproduction; //My companys total production	
	private TickArray expectedtotaldemand;	//My comapanys total demand
	private VolumeAnalysisAgent myVAAgent;

	public VolumePrognosis() {
		expectedtotalproduction = new TickArray();
		expectedtotaldemand = new TickArray();
	}
	

	
	
}