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
	
	private double[] tickCertProduction;
	private double[] obpdCertProduction;
	private double[] tickCertDemand;
	private double[] obpdCertDemand;
	//Do we need the same for power?
	private VolumeAnalysisAgent myVolumeAnalysisAgent;

	public VolumePrognosis() {
	}
	
	//Methods
	public void setmyVAA(VolumeAnalysisAgent myVAA) {
		myVolumeAnalysisAgent = myVAA;
	}
	public double getCurTickCertProduction(int tickID) {
		return tickCertProduction[tickID];
	}
	
	public double getCurObPdCertProduction(int tickID) {
		return obpdCertProduction[tickID];
	}
	public double getCurObPdCertDemand(int tickID) {
		return obpdCertDemand[tickID];
	}
	
	public void initialiseVolumePrognosis() {
		int numTotalTicks = TheEnvironment.theCalendar.getNumTicks();
		tickCertProduction = new double[numTotalTicks];
		tickCertDemand = new double[numTotalTicks];
		obpdCertProduction = new double[numTotalTicks];
		obpdCertDemand = new double[numTotalTicks];
		updateVolumePrognosis(0);
	}
	
	public void updateVolumePrognosis(int tickID) { 
		double curTickProd = 0;
		double curObPdProd = 0;
		double curTickDemand = 0;
		double curObPdDemand = 0;
		int fromTick = tickID;
		int maxTicks = TheEnvironment.theCalendar.getNumTicks();
		int numTicksRemainingInObPd = TheEnvironment.theCalendar.getNumTradePdsRemainingInCurrentObligationPd(fromTick) ; //Calculates the number of ticks to inlude (1-12).
		if (fromTick >= TheEnvironment.theCalendar.getNumTicks()) {return;}							//Handels last tick cases.
		for (PowerPlant pp : myVolumeAnalysisAgent.getmyCompany().getmypowerplants()) {
			
			if ((pp.getStartTick() <= fromTick) && (pp.getendtick() >= fromTick)) {						//Only count production if the plant has actually started and not close.
				curTickProd = curTickProd + pp.getExpectedProduction(fromTick);
			}
			
			for (int i = fromTick; i < Math.min(fromTick+numTicksRemainingInObPd,maxTicks); ++i) {
				if (pp.getStartTick() <= i && i <= pp.getendtick()) {			//Only count production if the plant has actually started and not closed.
					curObPdProd = curObPdProd + pp.getExpectedProduction(i); 
				}	
			}	
		}
		
		for (CompanyDemandShare CDS : this.myVolumeAnalysisAgent.getmyCompany().getMyDemandShares()) {
			curTickDemand = curTickDemand + (CDS.getDemandShare(fromTick) * CDS.getMyRegion().getMyDemand().getCertDemand(fromTick));
			
			for (int i = fromTick; i < Math.min(fromTick+numTicksRemainingInObPd,maxTicks); ++i) {
				curObPdDemand = curObPdDemand + (CDS.getDemandShare(i) * CDS.getMyRegion().getMyDemand().getCertDemand(i)); //The tick`s next ticks
				}	
			
		}
		tickCertProduction[tickID] = curTickProd;
		obpdCertProduction[tickID] = curObPdProd;
		tickCertDemand[tickID] = -curTickDemand;
		obpdCertDemand[tickID] = -curObPdDemand;
		
	}
	

	
}