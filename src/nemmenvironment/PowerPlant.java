package nemmenvironment;

import java.util.Calendar;
import java.util.Random;

import nemmagents.CompanyAgent;
import nemmcommons.CommonMethods;
import nemmcommons.TickArray;
import nemmtime.NemmTime;

public class PowerPlant {

	private String name;
	private Region myRegion;
	private CompanyAgent myCompany;			//Belonging CompanyAgent for both the developer and the Producer reciving the power plant when in operation (status=1).
	private int status; 					//Integer indicating which status the project/powerplant is in (1=in operation, 2=under construction, 3=waiting investment decision, 4=in process, 5=identifyed, 6=generic)
	private int capacity;
	private double loadfactor;
	private int technologyid; 				//Technology could also be a object/class in itself later if needed
	private int lifetime; 					//Number of years expected as lifetime for project, that is even without certs. For Powerpland part of "overgangsordningen", the year indicates when they are out of the scheme.
	
	private int startyear;					//If the Powerplant is in operation when simulation is ran, this indicates when it is put in operation. If 0, the start is endogenous in the model
	private int earlieststartyear;			//Given for all real projects (those identifyd).
	private double capex;
	private double opex;
	private double annualcostreduction;		//Annual rate of cost reduction due to technology improvment. 
	private double LRMC; 					//Long rund marginal cost for this powerplant build at a given year. This is update for each annual update. 
	
	private TickArray myProduction; 		//Future production (good given).
	private TickArray ExpectedProduction;	//Expected production. This is the amount of certs the plant is expected to generate and used by the owners to estimate. 
	
	public PowerPlant() {}
	
	public PowerPlant(String newname, int newtechnology, int newcapacity, double newloadfactor, Region newregion) {
		name = newname;
		technologyid = newtechnology;
		capacity = newcapacity;
		loadfactor = newloadfactor;
		myRegion = newregion;
		myProduction = new TickArray();
		ExpectedProduction = new TickArray();
		
		
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
			int curTick = TheEnvironment.theCalendar.getCurrentTick();
			prodcalc = this.myProduction.getElement(curTick);
		}

		return prodcalc;
	}
	
	//Get expected production from given tick
	public double getExpectedProduction(int... tickID) {
		
		double prodcalc;
		// eventually this will be replaced by a look-up
		if (tickID.length > 0) {
			prodcalc = this.ExpectedProduction.getElement(tickID[0]);
		}
		else {
			int curTick = TheEnvironment.theCalendar.getCurrentTick();
			prodcalc = this.ExpectedProduction.getElement(curTick);
		}

		return prodcalc;
	}
	
	
	public void pushCertstoCompany(int... tickID) {
		// Deliver produced certificates to my owner
		double certcalc;
		int curTick;
		if (tickID.length > 0) {
			curTick = tickID[0];
			certcalc = this.myProduction.getElement(curTick);
		}
		else {
			curTick = TheEnvironment.theCalendar.getCurrentTick();
			certcalc = this.myProduction.getElement(curTick);
		}
		//this.myCompany.addCertificates(certcalc, curTick);
	}	
	
	public void setProduction(double newProd, int... tickID) {

		if (tickID.length > 0) {	
			this.myProduction.setElement(newProd, tickID[0]);
		}
		else {
			int curTick = TheEnvironment.theCalendar.getCurrentTick();
			this.myProduction.setElement(newProd, curTick);
		}
	}
	
	public void setExpectedProduction(double newProd, int... tickID) {

		if (tickID.length > 0) {	
			this.ExpectedProduction.setElement(newProd, tickID[0]);
		}
		else {
			int curTick = TheEnvironment.theCalendar.getCurrentTick();
			this.ExpectedProduction.setElement(newProd, curTick);
		}
	}
	
	public void setAllProduction(double[] newProd) {

		int numPoints = newProd.length;
		int numTicks = TheEnvironment.theCalendar.getNumTicks();

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
	
	public void setAllExpectedProduction(double[] newProd) {

		int numPoints = newProd.length;
		int numTicks = TheEnvironment.theCalendar.getNumTicks();

		// set the demands and quotas for each tick
		if(numPoints==1){
			for (int y = 0; y < numTicks; ++y){
				ExpectedProduction.setElement(newProd[0], y);
			}			
		}
		else {
			ExpectedProduction.setArray(newProd);
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
