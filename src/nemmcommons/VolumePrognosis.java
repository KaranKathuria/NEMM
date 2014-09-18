/*
 * Version info:
 * 	   Class object for volume prognosis. This objet contains an VolumeAnalysisAgents own prognosis of internal production and demand for all future ticks.
 * 	   These valus will somehow be distributed around the actual world demand and production for the agent owning this volume analysis agent.
 *     Created: 2014ticks09
 *     Made by: Karan Kathuria  
 */

package nemmcommons;
import nemmagents.CompanyAgent.CompanyAnalysisAgent.VolumeAnalysisAgent;
import nemmagents.CompanyDemandShare;
import nemmenvironment.PowerPlant;
import nemmenvironment.TheEnvironment;

// Import


//Class definitions. Note that this is a static class as all its member variables are static.
public class VolumePrognosis {
	
	private double nexttickcertproduction;
	private double nexttwelvetickscertproduction;
	private int ticks = 12; //How many future ticks are included in the "nexttwelvetickscertproduction"
	private double nexttickcertdemand;
	private double nexttwelvetickscertdemand;
	//Do we need the same for power?
	private VolumeAnalysisAgent myVolumeAnalysisAgent;

	public VolumePrognosis() {
		ticks = 12;
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
	public double getnexttwelvetickscertdemand() {
		return nexttwelvetickscertdemand;
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
		for (CompanyDemandShare CDS : this.myVolumeAnalysisAgent.getmyCompany().getMyDemandShares()) {
			dtemp1 = dtemp1 + (CDS.getDemandShare(0) * CDS.getMyRegion().getMyDemand().getCertDemand(0));
			for (int i = 0; i < ticks; ++i) {
			dtempticks = dtempticks + (CDS.getDemandShare(i) * CDS.getMyRegion().getMyDemand().getCertDemand(i)); //The ticks next ticks
				}	
			
		}
		
		nexttickcertproduction = temp1;
		nexttwelvetickscertproduction = tempticks;
		nexttickcertdemand = -dtemp1;
		nexttwelvetickscertdemand = -dtempticks;

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
		
		for (CompanyDemandShare CDS : this.myVolumeAnalysisAgent.getmyCompany().getMyDemandShares()) {
			dtemp1 = dtemp1 + (CDS.getDemandShare(from) * CDS.getMyRegion().getMyDemand().getCertDemand(from));
			
			for (int i = from; i < ticks+from; ++i) {
				dtempticks = dtempticks + (CDS.getDemandShare(i) * CDS.getMyRegion().getMyDemand().getCertDemand(i)); //The ticks next ticks
				}	
			
		}
		nexttickcertproduction = temp1;
		nexttwelvetickscertproduction = tempticks;
		nexttickcertdemand = -dtemp1;
		nexttwelvetickscertdemand = -dtempticks;
		
}
	
	

	
	
}