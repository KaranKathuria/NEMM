/*
 * Version info:
 * 	   Class object for price prognosis. 
 * 
 *     Created: 20140818
 *     Made by: Karan Kathuria  
 */

package nemmcommons;

import nemmenvironment.TheEnvironment;
// Import
import nemmprocesses.ShortTermMarket;


//Class definitions. Note that this is a  class as all its member variables are .
public class MarketPrognosis {
	private double stpriceexpectation;
	private TickArray expectedcertificatedemand;
	private TickArray expectedcertificateprice;
	private TickArray expectedpowerpricenorway;
	private TickArray expectedpowerpricesweden;

	//Methods
	public MarketPrognosis() {
		if (TheEnvironment.allRegions.size()>2) {
			throw new IllegalArgumentException("MarketPrognisis object does not currently handle more than two regions");}
		
		stpriceexpectation = ParameterWrapper.getpriceexpectation();
		expectedcertificateprice = new TickArray(); 
		expectedcertificatedemand = new TickArray();
		expectedpowerpricenorway = new TickArray();
		expectedpowerpricesweden = new TickArray();
		
		
		expectedcertificateprice.setElement(stpriceexpectation, 0); //The first arrayvalue is given by the parametervalue
		
		//Sets the MarketPrognosis expected certificatedemand and power price to what actually will be the world certificate demand (perfect foresight).
		for (int i = 0; i < TheEnvironment.theCalendar.getNumTicks(); ++i) {
			double tempworlddemand = TheEnvironment.allRegions.get(0).getMyDemand().getCertDemand(i) + TheEnvironment.allRegions.get(1).getMyDemand().getCertDemand(i);
			expectedcertificatedemand.setElement(tempworlddemand, i);
			
			expectedpowerpricenorway.setElement(TheEnvironment.allRegions.get(0).getMyPowerPrice().getValue(i), i);
			expectedpowerpricesweden.setElement(TheEnvironment.allRegions.get(1).getMyPowerPrice().getValue(i), i);
		}
		
			
}
	
	
//Get and set methods
	public double getstpriceexpectation() {
		return stpriceexpectation;}

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
	public void setExpectedpowerpricenorway(double cd, int tickID) {
		expectedpowerpricenorway.setElement(cd, tickID);
	}
	public void InitiateAllExpectedpowerpricenorway(double[] cd) { //To set all expected future certprice
		expectedpowerpricenorway.setArray(cd);
	}
	public void updateFutureExpectedpowerpricenorway(double[] values) {
		expectedpowerpricenorway.setFutureElements(values);
	}
	public double getExpectedpowerpricenorway(int tickID) {
		return expectedpowerpricenorway.getElement(tickID);
	}
	public void setExpectedpowerpricesweden(double cd, int tickID) {
		expectedpowerpricesweden.setElement(cd, tickID);
	}
	public void InitiateAllExpectedpowerpricesweden(double[] cd) { //To set all expected future certprice
		expectedpowerpricesweden.setArray(cd);
	}
	public void updateFutureExpectedpowerpricesweden(double[] values) {
		expectedpowerpricesweden.setFutureElements(values);
	}
	public double getExpectedpowerpricesweden(int tickID) {
		return expectedpowerpricesweden.getElement(tickID);
	}
	
}