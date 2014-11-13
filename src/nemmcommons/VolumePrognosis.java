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
	private double thisobligationsperiodproduction;
	private int ticks = TheEnvironment.theCalendar.getNumTradePdsInObligatedPd(); 								//How many tick there intially are inlucluded in an obligation period.
	private double nexttickcertdemand;
	private double thisobligationsperioddemand;
	//Do we need the same for power?
	private VolumeAnalysisAgent myVolumeAnalysisAgent;

	public VolumePrognosis() {
		ticks = TheEnvironment.theCalendar.getNumTradePdsInObligatedPd();
	}
	
	//Methods
	public void setmyVAA(VolumeAnalysisAgent myVAA) {
		myVolumeAnalysisAgent = myVAA;
	}
	public double getnexttickcertproduction() {
		return nexttickcertproduction;
	}
	
	public double getnexttwelvetickscertproduction() {
		return thisobligationsperiodproduction;
	}
	public double getnexttwelvetickscertdemand() {
		return thisobligationsperioddemand;
	}
	
	public void initiatevolumeprognosis() { //To be run before first tick
		double temp1 = 0;
		double tempticks = 0;
		double dtemp1 = 0;
		double dtempticks = 0;
		
		for (PowerPlant pp : myVolumeAnalysisAgent.getmyCompany().getmypowerplants()) {
			temp1 = temp1 + pp.getExpectedProduction(0); 															//Next tick
			for (int i = 0; i < ticks; ++i) {
			tempticks = tempticks + pp.getExpectedProduction(i); 													//The ticks next tick. No plant starts or closes without production in current year.
			}
		}
		for (CompanyDemandShare CDS : this.myVolumeAnalysisAgent.getmyCompany().getMyDemandShares()) {
			dtemp1 = dtemp1 + (CDS.getDemandShare(0) * CDS.getMyRegion().getMyDemand().getCertDemand(0));
			for (int i = 0; i < ticks; ++i) {
			dtempticks = dtempticks + (CDS.getDemandShare(i) * CDS.getMyRegion().getMyDemand().getCertDemand(i)); 	//The ticks next ticks
				}	
			
		}
		
		nexttickcertproduction = temp1;
		thisobligationsperiodproduction = tempticks;
		nexttickcertdemand = -dtemp1;
		thisobligationsperioddemand = -dtempticks;

	}
	
	public void updatevolumeprognosis() { //To be run after market, next to Forecasts. This method uses calculates the expected production of next tick and the total expected production/demand of twele nest ticks
		double temp1 = 0;
		double tempticks = 0;
		double dtemp1 = 0;
		double dtempticks = 0;
		int from = TheEnvironment.theCalendar.getCurrentTick() + 1;
		int maksticks = TheEnvironment.theCalendar.getNumTicks();
		int tickleftinoblipd = TheEnvironment.theCalendar.getNumTradePdsInObligatedPd() - TheEnvironment.theCalendar.getTimeBlock(from).tradepdID ; //Calculates the number of ticks to inlude (1-12).
		if (from >= TheEnvironment.theCalendar.getNumTicks()) {return;}							//Handels last tick cases.
		for (PowerPlant pp : myVolumeAnalysisAgent.getmyCompany().getmypowerplants()) {
			
			if ((pp.getStartTick() <= from) && (pp.getendtick() >= from)) {						//Only count production if the plant has actually started and not close.
			temp1 = temp1 + pp.getExpectedProduction(from);
			}
			
			for (int i = from; i < from+tickleftinoblipd; ++i) {
				if (pp.getStartTick() <= (from+i) && (pp.getendtick() >= from+1) ) {			//Only count production if the plant has actually started and not closed.
				tempticks = tempticks + pp.getExpectedProduction(i); //The ticks next ticks
				}	}	
		}
		
		for (CompanyDemandShare CDS : this.myVolumeAnalysisAgent.getmyCompany().getMyDemandShares()) {
			dtemp1 = dtemp1 + (CDS.getDemandShare(from) * CDS.getMyRegion().getMyDemand().getCertDemand(from));
			
			for (int i = from; i < Math.min(ticks+from,maksticks); ++i) {
				dtempticks = dtempticks + (CDS.getDemandShare(i) * CDS.getMyRegion().getMyDemand().getCertDemand(i)); //The tick`s next ticks
				}	
			
		}
		nexttickcertproduction = temp1;
		thisobligationsperiodproduction = tempticks;
		nexttickcertdemand = -dtemp1;
		thisobligationsperioddemand = -dtempticks;
		
}
	
	

	
	
}