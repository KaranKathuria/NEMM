package nemmenvironment;

import nemmcommons.TickArray;
import nemmcommons.CommonMethods;
import nemmtime.NemmTime;


public class MarketDemand {
	
	private TickArray powerDemand;
	private TickArray certDemand;
	private TickArray certKvoteplikt;
	private int numTicks;

	// Each region has a demand for power and certificates

	// Constructor methods. The set up is slightly unusual - when the object is instantiated
	// the constructor is called and does nothing. Before use the initMarketDemand
	// method should be called - this does the actual constructing.
	// I've implemented in this way to enable the same code to be used for updating the 
	// market prices if and as desired
	
	public MarketDemand() {
		powerDemand = new TickArray();
		certDemand = new TickArray();
		certKvoteplikt = new TickArray();
	}

	public void initMarketDemand(double[] powerDem, double[] certPlikt){
		// Currently this just calls the setAllDemands. We can add additional initialisation
		// stuff here later if desired
		setAllDemands(powerDem, certPlikt);		
	}
	
	// Methods --------------------------------------------------------------
	
	// The setAllDemands allows us to send a single demand and quota, or else an array of demands and quota values
	// of size numTicks. We need to add functionality to generate the power demands etc internally
	// e.g. allow annual demand and quota numbers to be inputted, and have the class generate tick level demand
	// and quota arrays. But this will do for now.
	
	public void setAllDemands(double[] powerDem, double[] certPlikt) {
		// error checking is done in the TickArray object - if the parameters are not the correct
		// length (i.e. numTicks) an error will be thrown

		int numPoints = powerDem.length;
		numTicks = TheEnvironment.theCalendar.getNumTicks();
				
		// set the demands and quotas for each tick
		if(numPoints==1){
			for (int y = 0; y < numTicks; ++y){
				powerDemand.setElement(powerDem[0], y);
				certKvoteplikt.setElement(certPlikt[0], y);
				certDemand.setElement(powerDem[0]*certPlikt[0] , y);
			}
		}
		else {
			powerDemand.setArray(powerDem);
			certKvoteplikt.setArray(certPlikt);
		
		// calculate the certificate demand
		certDemand.setArray(CommonMethods.elArrayMult(powerDemand.getArray(),certKvoteplikt.getArray()));
		}
	}
		
	public double getPowerDemand(int... tickID) {
		double demandcalc;
		if (tickID.length > 0) {	
			demandcalc = this.powerDemand.getElement(tickID[0]);
		}
		else {
			int curTick = TheEnvironment.theCalendar.getCurrentTick();
			demandcalc = this.powerDemand.getElement(curTick);
		}				

		return demandcalc;
	}	
	
	public double getCertDemand(int... tickID) {
		double demandcalc;
		if (tickID.length > 0) {
			demandcalc = this.certDemand.getElement(tickID[0]);
		}
		else {
			int curTick = TheEnvironment.theCalendar.getCurrentTick();
			demandcalc = this.certDemand.getElement(curTick);
		}
		return demandcalc;
	}
	
	// We have not created methods to set individual power and certificate demands. Should these be required
	// they will have to be added
	
} // MarketDemand class

