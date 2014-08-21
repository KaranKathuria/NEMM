package nemmenvironment;

import java.util.Calendar;
import java.util.Random;

import nemmagents.CompanyAgent;
import nemmcommons.CommonMethods;
import nemmtime.NemmTime;

public class PowerPlant {

	private int capacity;
	private double loadfactor;
	private String name;
	private Region myRegion;
	private CompanyAgent myCompany;
	
	public PowerPlant(int newcapacity, double newloadfactor, Region newregion) {
		name = "temp name";
		capacity = newcapacity;
		loadfactor = newloadfactor;
		myRegion = newregion;
	}

	// Gets & Sets ------------------------------------------------------------------------

	public Region getMyRegion() {
		return myRegion;
	}

/* Commented out as the region should not be able to be changed	
 * public void setMyRegion(Region myRegion) {
		this.myRegion = myRegion;
	}
*/
	public CompanyAgent getMyCompany() {
		return myCompany;
	}

	public void setMyCompany(CompanyAgent myCompany) {
		this.myCompany = myCompany;
	}
	
	public double getLoadfactor() {
		return loadfactor;
	}

	public void setLoadfactor(double loadfactor) {
		this.loadfactor = loadfactor;
	}
	
	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	// Methods ------------------------------------------------------------------------

	
	// Production for a given plant for all periods in the simulation
	// will eventually be calculated when the plant is constructed,
	// and stored in an array, indexed by year, obligated pd, traded pd
	
	public double Production(NemmTime prodperiod) {
		double prodcalc;
		// eventually this will be replaced by a look-up
		
		prodcalc = capacity*30*24*loadfactor;
		return prodcalc;
	}
	
	public double ExpectedProduction(NemmTime prodperiod) {
		// for now, just get the expected production from the actual
		// production function. Expected is random, within a range of actual
		double expprod;
		double random = new Random().nextDouble();
		random = 0.8 + (random * (1.2 - 0.8));
		return expprod = this.Production(prodperiod)*random;
	}
	
	// OLD - kept just in case. 
/*	public double Production(Calendar startdate, Calendar enddate) {
		double prodcalc;
		prodcalc = capacity*CommonMethods.HoursBetween(startdate,enddate)*loadfactor;
		return prodcalc;
	}
	
	public double ExpectedProduction(Calendar startdate, Calendar enddate) {
		// for now, just get the expected production from the actual
		// production function
		double expprod;
		return expprod = this.Production(startdate, enddate);
	} */
	
	
	
}
