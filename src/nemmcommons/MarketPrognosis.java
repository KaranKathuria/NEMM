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
	private TickArray expectedpowerprice;

	
	//Methods
	public MarketPrognosis() {
		stpriceexpectation = ParameterWrapper.getpriceexpectation();
		expectedcertificatedemand = new TickArray();
		expectedcertificateprice = new TickArray(); 
		expectedpowerprice = new TickArray();
		
	}
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
	public void setExpectedpowerprice(double cd, int tickID) {
		expectedpowerprice.setElement(cd, tickID);
	}
	public void InitiateAllExpectedpowerprice(double[] cd) { //To set all expected future certprice
		expectedpowerprice.setArray(cd);
	}
	public void updateFutureExpectedpowerprice(double[] values) {
		expectedpowerprice.setFutureElements(values);
	}
	public double getExpectedpowerprice(int tickID) {
		return expectedpowerprice.getElement(tickID);
	}
	
}