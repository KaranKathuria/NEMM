	package nemmenvironment;


import nemmagents.CompanyAgent;
import nemmcommons.TickArray;


public class PowerPlant implements Cloneable{

	//Variables given by inputt sheet
	private String name;
	private Region myRegion;
	private CompanyAgent myCompany;			//Belonging CompanyAgent for both the developer and the Producer reciving the power plant when in operation (status=1).
	private int status; 					//Integer indicating which status the project/powerplant is in (1=in operation, 2=under construction, 3=waiting investment decision, 4=in process, 5=identifyed, 6=generic)
	private int capacity;
	private double loadfactor;
	private int technologyid; 				//Technology could also be a object/class in itself later if needed
	private int lifetime; 					//Number of years expected as lifetime for project from startdate, that is even without certs. For Powerpland part of "overgangsordningen", the year indicates when they are out of the scheme.
	private int startyear;					//If the Powerplant is in operation or under construcion at start, this indicates when it is put in operation. If 0, the start is endogenous in the model and set in the development game.
	private int earlieststartyear;			//31.10.2014: Not in use in projectdevelopment after introducing minyearinprocess and minconstruction years, but used in FMA. 
	private double capex;					//Total capex for project
	private double opex;					//Opex per MWh for project, assumed that this does not vary with the fluctiations in annual production. 
	private double annualcostreduction;		//Annual rate of cost reduction due to technology improvment. 
	private int minyearinprocess;			//Projects specific. Min number of years this projects needs in process (preconstruction and concession)
	private int minconstructionyears;		//Minimum number of years this project needs in construction. Currently this is used without variation, only adding starttick randomly. 
	private double specificRRR;				//Technology, regional and Capex- adjuster RRR before tax. For practiacl reasons this is simply made project specific.
	
	private TickArray myProduction; 		//Future production (good given) used in simulations. Hence this is adjusted for the specific scenario ran.
	private TickArray mynormalproduction;	//The initally read in production not adjusted for scenario spesific wind years. Stored as an intiall duplicate in order to "rewind" the "myProduction" table after a scenario have been ran.
	private TickArray ExpectedProduction;	//Expected production. This is the amount of certs the plant is expected to generate and used by the owners to estimate. 
	
	//Variables calculated/used in sumulation
	private int endyear; 					//THis is the last year eligable for certificates (if 2020, it gets certs for that year).
	private double LRMC; 					//Long run marginal cost for this powerplant build at a given year. This is update for each annual update.
	private Double certpriceneeded;			//Reason for having this field is that the projects cannot be sorted by LRMC as the region defines when its certificatesobligated.
	private int starttick;					//The month/tickid of a year that the production starts (the tick is in the starting year).
	private int endtick; 					//The tickid of a year that the production ends (including this tick).
	private int yearsincurrentstatus;		//Annual counter counting years in current status for the purpose of deciding if its ready for concession.
	
	public PowerPlant() {}
	
	public PowerPlant(String newname, Region newregion, int newstatus, int newcapacity, double newloadfactor, int newtechnology, 
					  int newlifetime, int newstartyear, double newcapex, double newopex, double newannualcostreduction, int newminyearinprocess, int newminconstructionyears) {
		name = newname;
		myRegion = newregion;
		status = newstatus;
		capacity = newcapacity;
		loadfactor = newloadfactor;
		technologyid = newtechnology;
		lifetime = newlifetime;
		startyear = newstartyear;
		capex = newcapex;
		opex = newopex;
		annualcostreduction = newannualcostreduction;
		minyearinprocess = newminyearinprocess;
		minconstructionyears = newminconstructionyears;
		
		if (startyear != 0) {												//null equals 0 for int in java. This part takes care of exogenous plants and "overgangsordningen"
			endyear = startyear + Math.min(lifetime, 15) - 1;}
		if (status == 1)	{												//Starttick is set in the project development for all other projects.
			starttick = 0;
			endtick = 0 + Math.min(lifetime, 15)*TheEnvironment.theCalendar.getNumTradePdsInYear();
			earlieststartyear = startyear;
		}
		if (status == 2)	{												//earlieststartyear is already determined by startyear as this is exognous. 
			earlieststartyear = startyear;
		}
		if (status > 2)		{
			earlieststartyear = minyearinprocess + minconstructionyears + TheEnvironment.theCalendar.getStartYear();	//This is really best case.
		}
			
		myProduction = new TickArray();
		mynormalproduction = new TickArray();
		ExpectedProduction = new TickArray();
		specificRRR = 0.0;													//Default, but this is updated on the following line.
		setprojectRRR();
		
	}

	// Gets & Sets ------------------------------------------------------------------------

	public Region getMyRegion() {
		return myRegion;
	}
	
	public int getStartTick() {
		return starttick;
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
	public double getspecificRRR() {
		return specificRRR;
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
	
	public void setStarttick(int tickID) {
		starttick = tickID;
	}
	public void setendtick(int tickID) {endtick = tickID;}
	public void setstartyear(int sy) {startyear = sy;}
	public int getstatus() {return status;}
	public int getminconstructionyears() {return minconstructionyears;}
	public int getminyearinprocess() {return minyearinprocess;}
	public int getyearsincurrentstatus() {return yearsincurrentstatus;}
	public void setyearsincurrentstatus(int a) { yearsincurrentstatus = a;}
	public void addyearsincurrentstatus(int a) {yearsincurrentstatus = yearsincurrentstatus + a;}


	// Methods ------------------------------------------------------------------------
	
	//Needed to clone() powerplants
	@Override
	public PowerPlant clone()
	{
		PowerPlant foo;
	    try
	    {      foo = (PowerPlant) super.clone();}
	    catch (CloneNotSupportedException e)
	    {throw new Error("pp noclone"); }
	    // Deep clone member fields here
	    return foo;
	}
	
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
	
	public void clonesetAllNormalProduction() {	//Only to be used in intialization.

				mynormalproduction = myProduction.clone();
	
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
	
	public void setprojectRRR() {
		if (this.status > 2) {
		for (ProjectRRR PR : TheEnvironment.alladjustedRRR) {
			if (PR.getregion() == this.myRegion && PR.gettechnologyid() == this.technologyid && this.technologyid > 1) {
					this.specificRRR = PR.getRRR();
					break;}
			if (PR.getregion() == this.myRegion && PR.gettechnologyid() == this.technologyid && this.technologyid == 1) {	//Hydro
				throw new Error("Taxlevels for endogenous hydroprojects are not implemented");}
							
		}
	}
}
	
	//THis method caluclates the LRMC and certificate price needed for a project realised in a given year. This is only usefull for endogenous projects, hence it does not have to take care of "overgangsordningen" projects. 
	//Takes in the realisation year as this alters the LRMC and Certpriceneeeded through improvment in Capex. The powerprice used depends on the input.
	//Notice that the current year is the year the projects can take the investment decison, not the year the project is finised.
	public void calculateLRMCandcertpriceneeded(int currentyear, double RRR, int powerpricecode) {
		//Not sure there is a good reason for not sending the powerprice directly in the method (KK). One advantage is that its implementation is easier to change later.
		//Current year referes to actual year number (2012..), not YearID (0,1...)
		double usedRRR = RRR;
		double usedpowerprice = 0;
		int futureyearspowerprice = 5 + TheEnvironment.theCalendar.getTimeBlock(TheEnvironment.theCalendar.getCurrentTick()).year;		//5 indication 5 years horizont from when either the investment decision or the FMA i ran. That is, all Powerprices are regarded from the year ran.
		
		//Switch taking care of which powerprice assumption to use in the LRMC. 1=Current power price, 2=The Developers, companys, analysisagent expected power price in 5 years. 3=The 5 year fowardprice.
		switch (powerpricecode) {
			case 1: {usedpowerprice = myRegion.getMyPowerPrice().getValue(); break;}					//Use current powerprice
			case 2: {if (myRegion == TheEnvironment.allRegions.get(0)) {								//Use the MAA expected powerprice in 5 years from now. Now as in simulation tick
						usedpowerprice = myCompany.getcompanyanalysisagent().getmarketanalysisagent().getmarketprognosis().getExpectedpowerpricenorway(futureyearspowerprice);}
					else{usedpowerprice = myCompany.getcompanyanalysisagent().getmarketanalysisagent().getmarketprognosis().getExpectedpowerpricesweden(futureyearspowerprice);}
			break;}
			case 3: {double temp = ((myRegion.getMyForwardPrice(futureyearspowerprice-5).getValue(1) + myRegion.getMyForwardPrice(futureyearspowerprice-5).getValue(2) 
					+ myRegion.getMyForwardPrice(futureyearspowerprice-5).getValue(3) + myRegion.getMyForwardPrice(futureyearspowerprice-5).getValue(4))
					+ (myRegion.getMyForwardPrice(futureyearspowerprice-5).getValue(5)*16))/20;
					usedpowerprice = temp;
					break;
			
			}	//Use the market forwardprices for the 5 following years, and then the 5th year price in the next 16.}
		}

		int yearsoftechnologyimprovment = currentyear - TheEnvironment.theCalendar.getStartYear();
		
		//Her is the certificatelogic
		int yearswithcertificates = Math.min(15,(2035-(currentyear+minconstructionyears)));				//Notice the "+minconstructionyears" for taking account of buidingperiod when finding the certeligable period.
			if (!myRegion.getcertificatespost2020flag() && (currentyear+minconstructionyears) > 2020) { //If certflag is false and years is larger than 31.12.2020.
					yearswithcertificates = 0;}
		
		double newCapex = capex*Math.pow((1-annualcostreduction),yearsoftechnologyimprovment);			//Note that the Capex value of the powerplant is not set/updated.
		
		double NPVfactor_lifetime = calculateNPVfactor(lifetime, usedRRR);
		
		LRMC = (newCapex/(NPVfactor_lifetime*this.getestimannualprod()))+this.opex; 					//Calculates the average nominal income needed per MWh (Certprice + Powerprice) for the project lifetime.
		
		//Calculating the needed average cert price is not trival as the certificates are only valid for a subperiod of the lifetime. First take into account the yearsofcertificates
		double NPVfactor_certyears = calculateNPVfactor(yearswithcertificates, usedRRR);

		//Calculating the needed price for certificates, with the correct assumptions of when the plant is eligable and the simulation-current local power price. T
		certpriceneeded = (LRMC*NPVfactor_lifetime - usedpowerprice*NPVfactor_lifetime) / NPVfactor_certyears; //Drawback: IS the powerprice assumption okey?
	}
	
	public void updateearlieststartyear() {
		if (status == 3) {
			earlieststartyear = minconstructionyears + TheEnvironment.theCalendar.getTimeBlock(TheEnvironment.theCalendar.getCurrentTick()).year + TheEnvironment.theCalendar.getStartYear();
		}
		if (status == 4) {
			earlieststartyear = minconstructionyears + this.minyearinprocess - this.yearsincurrentstatus + TheEnvironment.theCalendar.getTimeBlock(TheEnvironment.theCalendar.getCurrentTick()).year + TheEnvironment.theCalendar.getStartYear();
		}
		if (status == 5 || status == 6){
			earlieststartyear = minconstructionyears + minyearinprocess + TheEnvironment.theCalendar.getTimeBlock(TheEnvironment.theCalendar.getCurrentTick()).year + TheEnvironment.theCalendar.getStartYear();
	}
	}
	
	public static double calculateNPVfactor(int years, double RRR) {
		double NPVfactor = 0;
		for (int i = 1; i <= years; i++) {
			NPVfactor = NPVfactor + 1/Math.pow((1+RRR),i);}
		return NPVfactor;}
	
	//Get and set methods
	public void setendyear(int e) {endyear = e;}
	public void setstatus(int e) {status = e;}
	public int getstartyear() {return startyear;}
	public int getendyear() {return endyear;}
	public int getendtick() {return endtick;}
	public int getearlieststartyear() {return earlieststartyear;}
	public int getlifetime() {return lifetime;}
	public double getestimannualprod() {return this.loadfactor*this.capacity*8760;}
	public double getLRMC() {return LRMC;}
	public Double getcertpriceneeded() {return certpriceneeded;}
	public String getname() {return name;}
	public int gettechnologyid() {return technologyid;}
	
}
	
	