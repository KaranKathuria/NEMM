package nemmenvironment;

import nemmcommons.TickArray;
import nemmcommons.CommonMethods;
import nemmtime.NemmTime;


public class MarketDemand {
	
	//private TickArray powerDemand;
	private TickArray certDemand;
	private TickArray expectedcertDemand;
	//private TickArray certKvoteplikt;
	private int numTicks;

	// Each region has a demand for power and certificates

	// Constructor methods. The set up is slightly unusual - when the object is instantiated
	// the constructor is called and does nothing. Before use the initMarketDemand
	// method should be called - this does the actual constructing.
	// I've implemented in this way to enable the same code to be used for updating the 
	// market prices if and as desired
	
	public MarketDemand() {
		//powerDemand = new TickArray();
		certDemand = new TickArray();
		expectedcertDemand = new TickArray();
		//certKvoteplikt = new TickArray();
	}

	public void initMarketDemand(double[] certdem, double[] expectedcertdem){
		// Currently this just calls the setAllDemands. We can add additional initialisation
		// stuff here later if desired
		setCertDemand(certdem);		
		setExpectedCertDemand(expectedcertdem);
	}
	//blah
	public void setCertDemand(double[] cd) {
		certDemand.setArray(cd);	
	}
	public void setExpectedCertDemand(double[] cd) {
		expectedcertDemand.setArray(cd);	
	}
	
	public void setCertDemand_tick(double newdem, int... tickID) {
		if (tickID.length > 0) {	
			this.certDemand.setElement(newdem, tickID[0]);
		}
		else {
			int curTick = TheEnvironment.theCalendar.getCurrentTick();
			this.certDemand.setElement(newdem, curTick);
		}
	}
	
	// Methods --------------------------------------------------------------
	
	// The setAllDemands allows us to send a single demand and quota, or else an array of demands and quota values
	// of size numTicks. We need to add functionality to generate the power demands etc internally
	// e.g. allow annual demand and quota numbers to be inputted, and have the class generate tick level demand
	/*
	
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
	*/
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
	//returns the demand for the 12 next ticks
	public double getAnnualCertDemand(int... tickID) {
		double demandcalc;
		if (tickID.length > 0) {
			double temp = 0;
			for (int i = 0; i<TheEnvironment.theCalendar.getNumTradePdsInYear(); ++i) {
			temp = temp + this.certDemand.getElement(tickID[0]+1);}
			demandcalc = temp;
		}
		else {
			double temp = 0;
			int curTick = TheEnvironment.theCalendar.getCurrentTick();
			for (int i = 0; i<TheEnvironment.theCalendar.getNumTradePdsInYear(); ++i) {
			temp = temp + this.certDemand.getElement(curTick+1);}	
			demandcalc = temp;
			}
		return demandcalc;
	}
	
	
	public double getExpectedCertDemand(int... tickID) {
		double demandcalc;
		if (tickID.length > 0) {
			demandcalc = this.expectedcertDemand.getElement(tickID[0]);
		}
		else {
			int curTick = TheEnvironment.theCalendar.getCurrentTick();
			demandcalc = this.expectedcertDemand.getElement(curTick);
		}
		return demandcalc;
	}
	
	public double[] getExpectedCertDemand_all() {
		return expectedcertDemand.getArray();
		
	}
	

	
} 

