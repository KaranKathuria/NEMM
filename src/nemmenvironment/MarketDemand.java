package nemmenvironment;

import nemmtime.NemmTime;


public class MarketDemand {
	
	private double[] powerDemand;
	private double[] certDemand;
	private double[] certKvoteplikt;
	private double defaultPowerDemand;
	private double defaultCertDemand;
	private double defaultCertKvoteplikt;
	private int numTicks;

	// Each region has a demand for power and certificates

	// The constructor allows us to send a single demand and quota, or else an array of demands and quota values
	// of size numTicks. We need to add functionality to generate the power demands etc internally
	// e.g. allow annual demand and quota numbers to be inputted, and have the class generate tick level demand
	// and quota arrays. But this will do for now.
	public MarketDemand(double[] powerDem, double[] certPlikt) {
		// error checking
		if (powerDem.length != certPlikt.length){
			throw new IllegalArgumentException("power demand and certificate plikt arrays must be the same length");
		}
		int numPoints = powerDem.length+1;
		numTicks = TheEnvironment.theCalendar.getNumTicks();
		if (numPoints > 1 & numPoints != numTicks){
			throw new IllegalArgumentException("Number of power demand elements needs to be either 1 or equal to the number of ticks");
		}
		powerDemand = new double[numTicks-1];
		certDemand = new double[numTicks-1];
		certKvoteplikt = new double[numTicks-1];
		// set the defaults equal to the first data elements
		defaultPowerDemand = powerDem[0];
		defaultCertKvoteplikt = certPlikt[0];
		defaultCertDemand = defaultPowerDemand*defaultCertKvoteplikt;
		// set the demands and quotas for each tick
		if(numPoints==1){
			for (int y = 0; y < numTicks; ++y){
				powerDemand[y] = powerDem[0];
				certKvoteplikt[y] = certPlikt[0];
			}
			
		}
		else {
			powerDemand = powerDem;
			certKvoteplikt = certPlikt;
		}
		// calculate the certificate demand
		for (int y = 0; y < numTicks; ++y){
			certDemand[y] = powerDemand[y]*certKvoteplikt[y];
		}
		
	}
	
	
	public double PowerDemand(int... tickID) {
		double demandcalc;
		if (tickID.length > 0) {
			demandcalc = this.powerDemand[tickID[0]];
		}
		else {
			demandcalc = defaultPowerDemand;
		}				

		return demandcalc;
	}	
	
	public double CertDemand(int... tickID) {
		double demandcalc;
		if (tickID.length > 0) {
			demandcalc = this.certDemand[tickID[0]];
		}
		else {
			demandcalc = defaultCertDemand;
		}
		return demandcalc;
	}
	
} // MarketDemand class

