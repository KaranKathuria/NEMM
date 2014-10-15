package nemmenvironment;


import nemmagents.CompanyAgent;
import nemmcommons.TickArray;


public class PowerPlant {

	//Variables given by inputt sheet
	private String name;
	private Region myRegion;
	private CompanyAgent myCompany;			//Belonging CompanyAgent for both the developer and the Producer reciving the power plant when in operation (status=1).
	private int status; 					//Integer indicating which status the project/powerplant is in (1=in operation, 2=under construction, 3=waiting investment decision, 4=in process, 5=identifyed, 6=generic)
	private int capacity;
	private double loadfactor;
	private int technologyid; 				//Technology could also be a object/class in itself later if needed
	private int lifetime; 					//Number of years expected as lifetime for project from startdate, that is even without certs. For Powerpland part of "overgangsordningen", the year indicates when they are out of the scheme.
	private int startyear;					//If the Powerplant is in operation when simulation is ran, this indicates when it is put in operation. If 0, the start is endogenous in the model and set in the development game.
	private int earlieststartyear;			//Given for all real projects (those identifyd). Must be set and updated each year. 
	private double capex;					//Total capex for project
	private double opex;					//Opex per MWh for project, assumed that this does not vary with the fluctiations in annual production. 
	private double annualcostreduction;		//Annual rate of cost reduction due to technology improvment. 
	
	private TickArray myProduction; 		//Future production (good given).
	private TickArray ExpectedProduction;	//Expected production. This is the amount of certs the plant is expected to generate and used by the owners to estimate. 
	
	//Variables calculated
	private int endyear; 					//THis is the last year eligable for certificates.
	private double LRMC; 					//Long run marginal cost for this powerplant build at a given year. This is update for each annual update.
	private double certpriceneeded;			//Reason for having this field is that the projects cannot be sorted by LRMC as the region defines when its certificatesobligated.
	
	public PowerPlant() {}
	
	public PowerPlant(String newname, Region newregion, int newstatus, int newcapacity, double newloadfactor, int newtechnology, 
					  int newlifetime, int newstartyear, int newearlieststartyear, double newcapex, double newopex, double newannualcostreduction) {
		name = newname;
		myRegion = newregion;
		status = newstatus;
		capacity = newcapacity;
		loadfactor = newloadfactor;
		technologyid = newtechnology;
		lifetime = newlifetime;
		startyear = newstartyear;
		earlieststartyear = newearlieststartyear;
		capex = newcapex;
		opex = newopex;
		annualcostreduction = newannualcostreduction;
		
		if (startyear != 0) {									//null equals 0 for int in java This snippet takes care of exogenous plants and "overgangsordningen"
			endyear = startyear + Math.min(lifetime, 15) - 1;}
			
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
		return prodcalc;}
	
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
		}	}
	
	//THis method caluclates the LRMC and certificate price needed for a project realised in a given year. This is only usefull for endogenous projects, hence it does not have to take care of "overgangsordningen" projects. 
	//Takes in the realisation year as this alters the LRMC and Certpriceneeeded through improvment in Capex. Also, now the current power price (simlation tick) is used as bases for certpriceneeded.
	public void calculateLRMCandcertpriceneeded(int currentyear) {
		
		//int currentyear = TheEnvironment.theCalendar.getTimeBlock(TheEnvironment.theCalendar.getCurrentTick()).year;
		int yearsoftechnologyimprovment = currentyear - TheEnvironment.theCalendar.getStartYear();
		
		//Her is the certificatelogic
		int yearswithcertificates = 15;
			if (currentyear > 2020) {
				if (myRegion.getRegionName() == "Norway") {
					yearswithcertificates = 0;}
				else {
					yearswithcertificates = 2035 - currentyear;
			}}
		
		double newCapex = capex*Math.pow((1-annualcostreduction),yearsoftechnologyimprovment);			//Note that the Capex value of the powerplant is not set/updated.
		
		double NPVfactor_lifetime = 0;
		for (int i = 1; i <= lifetime; i++) {
			NPVfactor_lifetime = NPVfactor_lifetime + 1/Math.pow((1+TheEnvironment.GlobalValues.RRR),i);}
		
		LRMC = (newCapex/(NPVfactor_lifetime*this.loadfactor*this.capacity*8760))+this.opex; 			//Calculates the average nominal income needed per MWh (Certprice + Powerprice) for the project lifetime.
		
		//Calculating the needed average cert price is not trival as the certificates are only valid for a subperiod of the lifetime. First take into account the yearsofcertificates
		double NPVfactor_certyears = 0;
		for (int i = 1; i <= yearswithcertificates; i++) {
			NPVfactor_certyears = NPVfactor_certyears + 1/Math.pow((1+TheEnvironment.GlobalValues.RRR),i);}
		
		//Calculating the needed price for certificates, with the correct assumptions of when the plant is eligable and the simulation-current local power price. T
		certpriceneeded = (LRMC*NPVfactor_lifetime - myRegion.getMyPowerPrice().getValue()*NPVfactor_lifetime) / NPVfactor_certyears; //Drawback: IS the powerprice assumption okey?
	
	}
	
	
	public void setendyear(int e) {
		endyear = e;
	}
	public int getstartyear() {
		return startyear;}
	public int getendyear() {
		return endyear;}
	public int getearlieststartyear() {
		return earlieststartyear;
	}
	public double getestimannualprod() {
		return this.loadfactor*this.capacity*8760;
	}
	public double getLRMC() {
		return LRMC;
	}
	public double getcertpriceneeded() {
		return certpriceneeded;
	}
	public String getname() {
		return name;}
	}
	
	