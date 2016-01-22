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
	private double holdinghtickdemand;		//KK 20160120 KK Impemented to enable OPAs to buy (restbuy). Holdingh refers to the companies holdinghorizont.
	private double holdinghtickprod;
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
	public double getholdinghtickdemand() {
		return holdinghtickdemand;	}
	
	public double getholdinghtickprod() {
		return holdinghtickprod;
	}
	
	public void initialiseVolumePrognosis() {
		int numTotalTicks = TheEnvironment.theCalendar.getNumTicks();
		tickCertProduction = new double[numTotalTicks];
		tickCertDemand = new double[numTotalTicks];
		obpdCertProduction = new double[numTotalTicks];
		obpdCertDemand = new double[numTotalTicks];
		holdinghtickdemand = 0;
		holdinghtickprod = 0;
		
		updateVolumePrognosis(0);
	}
	
	//KK 2016. Mulig vi kan bruke denne til å definere et annet kjøpsvolum enn nåværende kjøpsposisjon. Sjekk dog at demand eller om expected demand skal benyttes.
	public void updateVolumePrognosis(int tickID) { 
		double curTickProd = 0;
		double curObPdProd = 0;
		double curTickDemand = 0;
		double curObPdDemand = 0;
		holdinghtickdemand = 0;
		holdinghtickprod = 0;
		int tempholdinghdem = 0;
		int tempholdinghprod = 0;

		
		if (this.myVolumeAnalysisAgent.getmyCompany().getobligatedpurchaseragent() != null) {
			tempholdinghdem = this.myVolumeAnalysisAgent.getmyCompany().getobligatedpurchaseragent().getNumTicksToEmptyPosition();
		}
		
		if (this.myVolumeAnalysisAgent.getmyCompany().getproduceragent() != null) {
			tempholdinghprod = this.myVolumeAnalysisAgent.getmyCompany().getproduceragent().getNumTicksToEmptyPosition(); 
		}
							
		
		int fromTick = tickID;
		int maxTicks = TheEnvironment.theCalendar.getNumTicks();
		int numTicksRemainingInObPd = 12;//TheEnvironment.theCalendar.getNumTradePdsRemainingInCurrentObligationPd(fromTick) ; //Calculates the number of ticks to inlude (1-12).
		if (fromTick >= TheEnvironment.theCalendar.getNumTicks()) {return;}							//Handels last tick cases.
		for (PowerPlant pp : myVolumeAnalysisAgent.getmyCompany().getmypowerplants()) {
			
			if ((pp.getStartTick() <= fromTick) && (pp.getendtick() >= fromTick)) {						//Only count production if the plant has actually started and not close.
				curTickProd = curTickProd + pp.getExpectedProduction(fromTick);
			}
			
			//Obligation period
			for (int i = fromTick; i < Math.min(fromTick+numTicksRemainingInObPd,maxTicks); ++i) {
				if (pp.getStartTick() <= i && i <= pp.getendtick()) {			//Only count production if the plant has actually started and not closed.
					curObPdProd = curObPdProd + pp.getExpectedProduction(i); 
				}	
			}	
			
			//Holding horizont
			for (int j = fromTick; j < Math.min(fromTick+tempholdinghprod,maxTicks); ++j) {
				if (pp.getStartTick() <= j && j <= pp.getendtick()) {			//Only count production if the plant has actually started and not closed.
					holdinghtickprod = holdinghtickprod + pp.getExpectedProduction(j); 
				}	
			}
		}
		
		for (CompanyDemandShare CDS : this.myVolumeAnalysisAgent.getmyCompany().getMyDemandShares()) {
			curTickDemand = curTickDemand + (CDS.getDemandShare(fromTick) * CDS.getMyRegion().getMyDemand().getCertDemand(fromTick));
			
			//Obligation period
			for (int i = fromTick; i < Math.min(fromTick+numTicksRemainingInObPd,maxTicks); ++i) {
				curObPdDemand = curObPdDemand + (CDS.getDemandShare(i) * CDS.getMyRegion().getMyDemand().getCertDemand(i)); //The tick`s next ticks
				}
			
			//Holding horizont
			for (int j = fromTick; j < Math.min(fromTick+tempholdinghdem,maxTicks); ++j) {
				holdinghtickdemand = holdinghtickdemand + (CDS.getDemandShare(j) * CDS.getMyRegion().getMyDemand().getCertDemand(j)); //Sum of all demand wihtin the hodinghorizont.
				}
			int f = 3;
			int a = 3;
			
			
			
			
		}
		tickCertProduction[tickID] = curTickProd;
		obpdCertProduction[tickID] = curObPdProd;
		tickCertDemand[tickID] = -curTickDemand;
		obpdCertDemand[tickID] = -curObPdDemand;
		
	}
	

	
}