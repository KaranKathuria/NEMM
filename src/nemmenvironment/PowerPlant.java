package nemmenvironment;

import java.util.Calendar;
import java.util.Random;

import nemmagents.CompanyAgent;
import nemmcommons.CommonMethods;
import nemmcommons.TickArray;
import nemmtime.NemmTime;

public class PowerPlant {

	private int capacity;
	private double loadfactor;
	private String name;
	private Region myRegion;
	private CompanyAgent myCompany;
	private TickArray myProduction;
	private double defaultProduction;
	
	public PowerPlant(int newcapacity, double newloadfactor, Region newregion) {
		name = "temp name";
		capacity = newcapacity;
		loadfactor = newloadfactor;
		myRegion = newregion;
		myProduction = new TickArray();
		defaultProduction = 0;
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

	
	// Production methods
	
	public double getProduction(int... tickID) {
		double prodcalc;
		// eventually this will be replaced by a look-up
		if (tickID.length > 0) {
			prodcalc = this.myProduction.getElement(tickID[0]);
		}
		else {
			prodcalc = defaultProduction;
		}

		return prodcalc;
	}
	
	public double getExpectedProduction(int... tickID) {
		// Change this at some point to be different from actual production
		double prodcalc;
		// eventually this will be replaced by a look-up
		if (tickID.length > 0) {
			prodcalc = this.myProduction.getElement(tickID[0]);
		}
		else {
			prodcalc = defaultProduction;
		}

		return prodcalc;
	}
	
	public void setProduction(double newProd, int... tickID) {
		if (tickID.length > 0) {	
			this.myProduction.setElement(newProd, tickID[0]);
		}
		else {
			this.defaultProduction = newProd;
		}
	}
	
	public void setAllProduction(double[] newProd) {

		int numPoints = newProd.length;
		int numTicks = TheEnvironment.theCalendar.getNumTicks();

		// set the defaults equal to the first data elements
		defaultProduction = newProd[0];
		// set the demands and quotas for each tick
		if(numPoints==1){
			for (int y = 0; y < numTicks; ++y){
				myProduction.setElement(newProd[0], y);
			}			
		}
		else {
			myProduction.setArray(newProd);
		}
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
