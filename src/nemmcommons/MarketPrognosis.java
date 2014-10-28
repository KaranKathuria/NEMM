/*
 * Version info:
 * 	   Class object for price prognosis. 
 * 
 *     Created: 20140818
 *     Made by: Karan Kathuria  
 */

package nemmcommons;

import java.util.Random;

import cern.jet.random.Normal;
import repast.simphony.random.RandomHelper;
import nemmenvironment.FundamentalMarketAnalysis;
import nemmenvironment.TheEnvironment;
// Import
import nemmprocesses.ShortTermMarket;


//Class definitions. Note that this is a  class as all its member variables are .
public class MarketPrognosis {
	
	private double forcastweights[] = new double[3];// = AllVariables.forcastweights;
	private double stpriceexpectation;
	
	private double mediumrunpriceexpectations; //Highest fundamental price for the future year 3 or 4. Based on FMA and a errorterm. 
	private double longrunpriceexpectatations; //Highest fundamental price for the future year 10 or 11. Based on FMA and a errorterm.
	
	private TickArray expectedcertificateprice;  //Tick array contaning history of what price where expected for the next tick. Could be used later to evaluate the STM-strategy.
	private TickArray expectedcertificatedemand; //Total certificate demand for each tick in the simulation. 
	private YearArray expectedpowerpricenorway; // Expected power price in Norway. Only point of having this Tick Array is if this differs from the one given in the Environment. 
	private YearArray expectedpowerpricesweden; // Expected power price in Sweden. Only point of having this Tick Array is if this differs from the one given in the Environment.

	//Methods
	public MarketPrognosis() {
		if (TheEnvironment.allRegions.size()>2) {
			throw new IllegalArgumentException("MarketPrognisis object does not currently handle more than two regions");}
		
		// Randomly setting the forcastweights. Notice the range for w1 can be altered to ensure a logical front-wheighted average. 
		// Later a market prognosis could have multiple forcastweights forcasting the next tick price. These forcasted prices could than be measured against the outcome to score them accoriding to 
		// how well they preform. THe more multiple forecast weights the market prognosis would have, the bigger is the chance for forcasting the right price, hence the number of such forcast-weights array 
		// could be used as a parameter for how "advanced" the analysisagents short term market prognosis would be. 
		
		double w3 = RandomHelper.nextDoubleFromTo(0.0,1); 
		forcastweights[2] = w3;  //weight used for the previous price
		double w2 = RandomHelper.nextDoubleFromTo(0.0,(1-w3));  
		forcastweights[1] = w2; //weight used for the current tick - 2 price ...
		double w1 = 1-w2-w3;
		forcastweights[0] = w1;
		
		stpriceexpectation = ParameterWrapper.getpriceexpectation() * RandomHelper.nextDoubleFromTo(1, 1); //Random
		mediumrunpriceexpectations =  32; //AllVariables.mediumrundpriceexpectations 
		longrunpriceexpectatations = 35;  //AllVariables.longrundpriceexpectations;  
		expectedcertificateprice = new TickArray(); 
		expectedcertificatedemand = new TickArray();
		expectedpowerpricenorway = new YearArray();
		expectedpowerpricesweden = new YearArray();		
		expectedcertificateprice.setElement(stpriceexpectation, 0);
		//Sets the MarketPrognosis expected certificatedemand and power price to what actually will be the world certificate demand (perfect foresight).
		for (int i = 0; i < TheEnvironment.theCalendar.getNumTicks(); ++i) {
			double tempworlddemand = TheEnvironment.allRegions.get(0).getMyDemand().getCertDemand(i) + TheEnvironment.allRegions.get(1).getMyDemand().getCertDemand(i);
			// Cert demand, power demand in Norway and Sweden
			expectedcertificatedemand.setElement(tempworlddemand, i);}	
		
		for (int i = 0; i < TheEnvironment.theCalendar.getNumYears(); ++i) {
		expectedpowerpricenorway.setElement(TheEnvironment.allRegions.get(0).getMyPowerPrice().getValue(i), i);
		expectedpowerpricesweden.setElement(TheEnvironment.allRegions.get(1).getMyPowerPrice().getValue(i), i);}		
}	
	
	// The following method updates the STM price expectation for the next tick based on the forecast weights. Called each tick (through Forcast) and updates the market prognosis.
	public void updatemarketprognosis() {
		double nexttickcertprice;
		int numberhistoricprices = TheEnvironment.theCalendar.getCurrentTick() + 1;
		int curtick = TheEnvironment.theCalendar.getCurrentTick();
		if (numberhistoricprices < 3) {
			nexttickcertprice = TheEnvironment.GlobalValues.certificateprice.getElement(curtick);
		} else {
			nexttickcertprice = forcastweights[0] * TheEnvironment.GlobalValues.certificateprice.getElement(curtick-2) + forcastweights[1] * TheEnvironment.GlobalValues.certificateprice.getElement(curtick-1) + forcastweights[2] * TheEnvironment.GlobalValues.certificateprice.getElement(curtick);
		}
		this.setstpriceexpectation(nexttickcertprice);
		this.setExpectedcertificateprice(nexttickcertprice, curtick+1);
	}
	
	public void updatefunamentalmarketprognosis() {
		// Updates the medium and long run priceexpectations.
		
		//Calculates standardeviation based on the magnitude of the price. Creates a random normal distribution and retrives the next double:
		mediumrunpriceexpectations = FundamentalMarketAnalysis.getMPE(); //RandomWrapper.getmyNormalDistMPE().nextDouble(); 
		longrunpriceexpectatations = FundamentalMarketAnalysis.getLPE(); //RandomWrapper.getmyNormalDistLPE().nextDouble(); 
		
	}
	
	
	
//Get and set methods
	public double getstpriceexpectation() {
		return stpriceexpectation;}
	
	public double getmedumrundpriceexpectations() {
		return mediumrunpriceexpectations;
	}
	public double getlongrunpriceexpectatations() {
		return longrunpriceexpectatations;
	}

	public void setstpriceexpectation(double price) {
		stpriceexpectation = price;}
	
	//Sets and gets for the expectedcertificatedemand
	public void InitiateAllExpectedcertificatedemand(double[] cd) { //To set all expected future demand
		expectedcertificatedemand.setArray(cd);
	}
	public void setExpectedcertificatedemand(double cd, int tickID) {
		expectedcertificatedemand.setElement(cd, tickID);
	}
	public void updateFutureExpectedcertificatedemand(double[] values) {
		expectedcertificatedemand.setFutureElements(values);
	}
	public double getExpectedcertificatedemand(int tickID) {
		return expectedcertificatedemand.getElement(tickID);
	}
	
	//Sets for certprice
	public void setExpectedcertificateprice(double cd, int tickID) {
		expectedcertificateprice.setElement(cd, tickID);
	}
	public void InitiateAllExpectedcertificateprice(double[] cd) { //To set all expected future certprice
		expectedcertificateprice.setArray(cd);
	}
	public void updateFutureExpectedcertificateprice(double[] values) {
		expectedcertificateprice.setFutureElements(values);
	}
	public double getExpectedcertificateprice(int tickID) {
		return expectedcertificateprice.getElement(tickID);
	}
	
	//Sets for powerprice
	public void setExpectedpowerpricenorway(double cd, int YearID) {
		expectedpowerpricenorway.setElement(cd, YearID);
	}
	public void setExpectedpowerpricesweden(double cd, int YearID) {
		expectedpowerpricesweden.setElement(cd, YearID);
	}
	public void InitiateAllExpectedpowerpricenorway(double[] cd) { //To set all expected future certprice
		expectedpowerpricenorway.setArray(cd);
	}
	//public void updateFutureExpectedpowerpricenorway(double[] values) {		Marked out as this is not implemented for yeararray.
	//	expectedpowerpricenorway.setFutureElements(values);
	//}
	public double getExpectedpowerpricenorway(int YearID) {
		return expectedpowerpricenorway.getElement(YearID);
	}
	
	public void InitiateAllExpectedpowerpricesweden(double[] cd) { //To set all expected future certprice
		expectedpowerpricesweden.setArray(cd);
	}
	//public void updateFutureExpectedpowerpricesweden(double[] values) {
	//	expectedpowerpricesweden.setFutureElements(values);
	//}
	public double getExpectedpowerpricesweden(int YearID) {
		return expectedpowerpricesweden.getElement(YearID);
	}
	
}