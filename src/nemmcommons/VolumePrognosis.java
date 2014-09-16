/*
 * Version info:
 * 	   Class object for volume prognosis. This objet contains an VolumeAnalysisAgents own prognosis of internal production and demand for all future ticks.
 * 	   These valus will somehow be distributed around the actual world demand and production for the agent owning this volume analysis agent.
 *     Created: 2014ticks09
 *     Made by: Karan Kathuria  
 */

package nemmcommons;
import nemmagents.CompanyAgent.CompanyAnalysisAgent.VolumeAnalysisAgent;
import nemmenvironment.PowerPlant;
import nemmenvironment.TheEnvironment;

// Import


//Class definitions. Note that this is a static class as all its member variables are static.
public class VolumePrognosis {
	
	private double nexttickcertproduction;
	private double nexttwelvetickscertproduction;
	private int ticks;
	private double nexttickcertdemand;
	private double nexttwelvetickscertdemand;
	//Do we need the same for power?
	private VolumeAnalysisAgent myVolumeAnalysisAgent;

	public VolumePrognosis() {
		nexttickcertproduction = 0; //exected cert prod for my powerplants. This cannot be set in the constructor as myVAA is set afterwards
		ticks = ticks; //number of ticks total production summed
	}
	
	//Methods
	public void setmyVAA(VolumeAnalysisAgent myVAA) {
		myVolumeAnalysisAgent = myVAA;
	}
	public double getnexttickcertproduction() {
		return nexttickcertproduction;
	}
	
	public double getnexttwelvetickscertproduction() {
		return nexttwelvetickscertproduction;
	}
	
	public void initiatevolumeprognosis() { //To be run before first tick
		double temp1 = 0;
		double tempticks = 0;
		double dtemp1 = 0;
		double dtempticks = 0;
		for (PowerPlant pp : myVolumeAnalysisAgent.getmyCompany().getmypowerplants()) {
			temp1 = temp1 + pp.getExpectedProduction(0); //Tick 0 is the next tick
			for (int i = 0; i < ticks; ++i) {
			tempticks = tempticks + pp.getExpectedProduction(i); //The ticks next tick
			}
		}
		nexttickcertproduction = temp1;
		nexttwelvetickscertproduction = tempticks;
		//equally for expected demand
		//for (DemandShares ds : myVolumeAnalysisAgent.getmyCompany().getmypowerplants()et) {
	}
	
	public void updatevolumeprognosis() { //To be run after market, next to Forecasts
		double temp1 = 0;
		double tempticks = 0;
		double dtemp1 = 0;
		double dtempticks = 0;
		int from = TheEnvironment.theCalendar.getCurrentTick() + 1;
		for (PowerPlant pp : myVolumeAnalysisAgent.getmyCompany().getmypowerplants()) {
			temp1 = temp1 + pp.getExpectedProduction(from);
			
			for (int i = from; i < ticks+from; ++i) {
				tempticks = tempticks + pp.getExpectedProduction(i); //The ticks next ticks
				}		
		}
		nexttickcertproduction = temp1;
		nexttwelvetickscertproduction = tempticks;

		//equally for expected demand
		//for (DemandShares ds : myVolumeAnalysisAgent.getmyCompany().getmypowerplants()et) {
	
	}
	
	

	
	
}