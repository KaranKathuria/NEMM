	package nemmenvironment;


import nemmagents.CompanyAgent;
import nemmcommons.AllVariables;
import nemmcommons.ParameterWrapper;
import nemmcommons.TickArray;
import nemmcommons.YearArray;
import nemmprocesses.ProjectMarket;

import org.apache.poi.ss.formula.functions.Irr;

import repast.simphony.random.RandomHelper;


public class PowerPlant implements Cloneable{

	//Variables given by inputt sheet
	private String name;
	private Region myRegion;
	private CompanyAgent myCompany;			//Belonging CompanyAgent for both the developer and the Producer reciving the power plant when in operation (status=1).
	private int status; 					//Integer indicating which status the project/powerplant is in (1=in operation, 2=under construction, 3=waiting investment decision, 4=in process, 5=identifyed, 6=generic)
	private double capacity;
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
	private YearArray annualproduction;		//The Actual annual production of the powerplant (given, not estimated).
	private int overgangsordningflag;		//Flag indicating if the project is part of the "overgangsordning". 1 indicates that it is. 	
	
	private TickArray myProduction; 		//Future production (good given) used in simulations. Hence this is adjusted for the specific scenario ran.
	//NOT NEEDED private TickArray mynormalproduction;	//The initally read in production not adjusted for scenario spesific wind years. Stored as an intiall duplicate in order to "rewind" the "myProduction" table after a scenario have been ran.
	private TickArray ExpectedProduction;	//Expected production. This is the amount of certs the plant is expected to generate and used by the owners to estimate. 
	
	//Variables calculated/used in sumulation
	private int endyear; 					//THis is the last year eligable for certificates (if 2020, it gets certs for parts of this year (dependent on tick)).
	private double LRMC; 					//Long run marginal cost for this powerplant build at a given year. This is update for each annual update.
	private double LRMC_ownRRR;
	private Double certpriceneeded;			//Reason for having this field is that the projects cannot be sorted by LRMC as the region defines when its certificatesobligated.
	private Double certpriceneeded_ownRRR;	//As above but with own RRR adjusted project specific RRR. use to evaluate investment in case of pricebased investmetns.
	private int starttick;					//The month/tickid of a year that the production starts (the tick is in the starting year). production from this tick (included).
	private int endtick; 					//The tickid of a year that the certificate elgiable production ends (not including this tick).
	private int yearsincurrentstatus;		//Annual counter counting years in current status for the purpose of deciding if its ready for concession.
	private double IRR; 					//Project IRR
	private int exougenousflagg = 0;		//Flag indicating if project is given in as exougenous. That is status 1 or 2. = Not exougenous. (0 means endougeous)
	private int projectmarketcandidateflag;	//Flag indicating if the project is a candidate for parttaking in the project market (0 indicates that is not a candidate).
	private int numberofownershipchange;	//Number indicating how many times the plant has changed ownership.
	
	public PowerPlant() {}
	
	public PowerPlant(String newname, Region newregion, int newstatus, double newcapacity, double newloadfactor, int newtechnology, 
					  int newlifetime, int newstartyear, double newcapex, double newopex, double newannualcostreduction, int newminyearinprocess, int newminconstructionyears, int newovergangsordningflag) {
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
		overgangsordningflag = newovergangsordningflag;
		
		myProduction = new TickArray();
		//mynormalproduction = new TickArray();
		ExpectedProduction = new TickArray();
		annualproduction = new YearArray();
		specificRRR = 0.0;													//Default, but this is updated on the following line.
		setprojectRRR();

		
		if (startyear != 0) {												//null equals 0 for int in java. The same as status 1 or 2. This part takes care of exogenous plants and "overgangsordningen". Exogenous plants does have null here.hence;
			endyear = startyear + Math.min(lifetime, 15) - 1;
			exougenousflagg = 1;			
			}
		
		if (status == 0)	{												//Starttick is set in the project development for all other projects.
			starttick = 0;
			endtick = 0;
			earlieststartyear = startyear;
		}
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
		//defaults to avoid nulls in powerplant output and to not having 2012 values in plants not build (for cert needed and LRMC), the initials as set her.

		IRR = 0.0;
		LRMC = 0.0;
		LRMC_ownRRR = 0.0;
		certpriceneeded = 0.0;
		certpriceneeded_ownRRR = 0.0;
		numberofownershipchange = 0;
		projectmarketcandidateflag = 0;										//Initially set to zero.
		//calculateLRMCandcertpriceneeded(startyear, specificRRR, 3);

					
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
	
	public double getCapacity() {
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
	public void setAllannualProduction(double[] newProd) {	
		//Method that adds up annual actual production given by input and stores this in a YearArray (annualprodution).
		int numPoints = newProd.length;
		int numTicks = TheEnvironment.theCalendar.getNumTicks();
		
		if (numPoints==numTicks) {
			
		int numticksayear = TheEnvironment.theCalendar.getNumTradePdsInYear();
		int numberofyears = TheEnvironment.theCalendar.getNumYears();
		int counter = 0;				//will go from 0 to 287
			
			for (int year = 0; year<numberofyears;year++) {								//For years
				double yearsum = 0;

				for (int ticksayear = 0; ticksayear <numticksayear;ticksayear++ ) {		//For ticks in year
					yearsum = yearsum + newProd[counter];
					counter++;
				}
			this.annualproduction.setElement(yearsum, year);
			}
			
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
		//Notice that the above future price uses the current (simulation tick) +5, and not the currentyear +5. Arguably beacuse this (simulation) +5 is the best knowledge when doing it. Hence the FMA does not have full foresight on powerprice.
		
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
			if (!myRegion.getcertificatespost2020flag() && (currentyear+minconstructionyears) > myRegion.getcutoffyear()) { //If certflag is false and years is larger than cuoffyear.
					yearswithcertificates = 0;}
		
		double newCapex = capex*Math.pow((1-annualcostreduction),yearsoftechnologyimprovment);			//Note that the Capex value of the powerplant is not set/updated.
		
		double NPVfactor_lifetime = calculateNPVfactor(lifetime, usedRRR);
		
		LRMC = (newCapex/(NPVfactor_lifetime*this.getestimannualprod()))+this.opex; 					//Calculates the average nominal income needed per MWh (Certprice + Powerprice) for the project lifetime.
		
		//Calculating the needed average cert price is not trival as the certificates are only valid for a subperiod of the lifetime. First take into account the yearsofcertificates
		double NPVfactor_certyears = calculateNPVfactor(yearswithcertificates, usedRRR);

		//Calculating the needed price for certificates, with the correct assumptions of when the plant is eligable and the simulation-current local power price. T
		certpriceneeded = (LRMC*NPVfactor_lifetime - usedpowerprice*NPVfactor_lifetime) / NPVfactor_certyears; //Drawback: IS the powerprice assumption okey?
	}
	
	public void calculateLRMCandcertpriceneeded_ownRRR(int currentyear, int powerpricecode) {
		//Not sure there is a good reason for not sending the powerprice directly in the method (KK). One advantage is that its implementation is easier to change later.
		//Current year referes to actual year number (2012..), not YearID (0,1...)
		double usedRRR = myCompany.getInvestmentRRR()*this.specificRRR;
		double usedpowerprice = 0;
		int futureyearspowerprice = 5 + TheEnvironment.theCalendar.getTimeBlock(TheEnvironment.theCalendar.getCurrentTick()).year;		//5 indication 5 years horizont from when either the investment decision or the FMA i ran. That is, all Powerprices are regarded from the year ran.
		//Notice that the above future price uses the current (simulation tick) +5, and not the currentyear +5. Arguably beacuse this (simulation) +5 is the best knowledge when doing it. Hence the FMA does not have full foresight on powerprice.
		
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
			if (!myRegion.getcertificatespost2020flag() && (currentyear+minconstructionyears) > myRegion.getcutoffyear()) { //If certflag is false and years is larger than cuoffyear.
					yearswithcertificates = 0;}
		
		double newCapex = capex*Math.pow((1-annualcostreduction),yearsoftechnologyimprovment);			//Note that the Capex value of the powerplant is not set/updated.
		
		double NPVfactor_lifetime = calculateNPVfactor(lifetime, usedRRR);
		
		LRMC_ownRRR = (newCapex/(NPVfactor_lifetime*this.getestimannualprod()))+this.opex; 					//Calculates the average nominal income needed per MWh (Certprice + Powerprice) for the project lifetime.
		
		//Calculating the needed average cert price is not trival as the certificates are only valid for a subperiod of the lifetime. First take into account the yearsofcertificates
		double NPVfactor_certyears = calculateNPVfactor(yearswithcertificates, usedRRR);

		//Calculating the needed price for certificates, with the correct assumptions of when the plant is eligable and the simulation-current local power price. T
		certpriceneeded_ownRRR = (LRMC_ownRRR*NPVfactor_lifetime - usedpowerprice*NPVfactor_lifetime) / NPVfactor_certyears; //Drawback: IS the powerprice assumption okey?
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
	
	public void calculateIRR() {
		int currenttick = TheEnvironment.theCalendar.getCurrentTick();
		int yearsoftechnologyimprovment = (this.getstartyear()) - TheEnvironment.theCalendar.getStartYear();						//Improvment untill start-year.
		
		if (this.endtick > currenttick) {
			throw new IllegalArgumentException("Cannot calculate profitability or IR if the plant is not ended);");}	
		
		if (opex != 0 && capex !=0 ) {			//Only for endogenous projects which as opex and capex		
		//Cash flows
		double newCapex = capex*Math.pow((1-annualcostreduction),yearsoftechnologyimprovment);	
		double[] annualincomefromcerts = new double[this.lifetime];				//NB! Lifetime even though its not the lifetime length which qualifies for certs.
		double[] annualincomefrompower = new double[this.lifetime];
		double[] annualopex = new double[this.lifetime];
		double[] totalcashflow = new double[this.lifetime+1];					//total. +1 to take account for the Capex
		totalcashflow[0] = -newCapex;											//negative
		
		int iterations = this.endyear-(this.startyear)+1; 						//Number of years with cert income. 
		int indexyear = startyear - TheEnvironment.theCalendar.getStartYear();	//E.g 2015 - 2012 = 3;
		//Production
		for (int i = 0; i < iterations; i++) { //E.g from 0 and even 14, thats 15 iterations. We calculate everything from januar startyear, hence last year can be cut as first year ticks are added. (2015 - 2030, but we also count whole 2015, hence 2015-2029. thats start + 14)
			annualincomefromcerts[i] = this.annualproduction.getElement(indexyear+i) * TheEnvironment.GlobalValues.averageannualcertprice.getElement(indexyear+i);
			annualincomefrompower[i] = this.annualproduction.getElement(indexyear+i) * this.myRegion.getMyPowerPrice().getValue(indexyear+i);
			annualopex[i] = this.annualproduction.getElement(indexyear+i) * this.opex;
		}
		for (int i = iterations; i < annualincomefrompower.length; i++ ) {
			//For the years after certificate year, it takes the last power price and the normal year production to calculate power-income and opex-costs. THis is an approximation!
			annualincomefrompower[i] = this.myRegion.getMyPowerPrice().getValue(indexyear+iterations-1) * this.getestimannualprod() ; //Minor estimate. For years after the plants does not recive certs, it takes the last power price (cert elgiable year) and assumes it to be flat. It shoudl take the once given in the input. But how much does it mean in practive?
			annualincomefromcerts[i] = 0;											//If plant is buildt in 2012 and not-cert-eligb in 2028, it not assumes the same power price in the end of lifetime as 2028, not the actual in 2029, 2030.. this simplifacation in order to avoid to many ifs. Also it assumes normalproduction.
			annualopex[i] = this.getestimannualprod() * this.opex;					//For years after 2035, the opex calculation assumes the production to be normal.
		}
		
		for (int i = 1; i < totalcashflow.length; i++ ) {
			totalcashflow[i] = annualincomefrompower[i-1] + annualincomefromcerts[i-1] - annualopex[i-1];
		}
		
		this.IRR = Irr.irr(totalcashflow, 0.05);	

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
	public double getcertpriceneeded_ownRRR() {return certpriceneeded_ownRRR;}
	public double getopex() {return this.opex;}
	public double getcapex() {return this.capex;}
	public int getexougenousflagg() {return this.exougenousflagg;}
	public int getovergangsordningflag() { return overgangsordningflag;}
	public int getprojectmarketcandidateflag() { return projectmarketcandidateflag;}
	public void addtonumberofownershipchange() {numberofownershipchange = numberofownershipchange +1;}
	public String getname() {return name;}
	public int gettechnologyid() {return technologyid;}
	public String getmyregion() {return myRegion.getRegionName();}
	public String getmyCompany() {return myCompany.getname();}
	public double getmyCompanyRRR() {return (myCompany.getInvestmentRRR()*specificRRR);}
	public void setprojectmarketcandidateflag(int a) {this.projectmarketcandidateflag = a;}
	
	public int getmyinvestmentdecisiontype() {
		if (myCompany.getdeveloperagent() != null) {
		return myCompany.getdeveloperagent().getinvestmentdecisiontype();}
	else {return 0;} //Returns the investment decision type for this agent.
	}
	public double getIRR() {return this.IRR;}
	public double getownrLRMC() {return LRMC_ownRRR;}
	
	
	//For Meta purposes. These field has nothing to do with a PowerPlant. They are just here to simplefy the output sheet.
	public String getrunningscenarioname() {
		return TheEnvironment.allwindandppricescenarios.get(ParameterWrapper.getscenarionumber()).getname();
	}
	public String getcasename() {
		return AllVariables.casename;
	}
	public void updatacriteriaflag_standard() {
		//Flag indicating that the project is subject for the project market. Notice that "yearscurrenstatus" are up to date after the january-tick have been ran. That is, if the status is changed in januar, the new status has years 0 until the rutine is ran next year. Then it increases to one. The number thus corresponds to full years in line after rutine have been ran.
		this.projectmarketcandidateflag = 0;
		int currentyear = TheEnvironment.theCalendar.getCurrentYear(); 	//Gets the current year.E.g 2015
		if ((this.myRegion.getcertificatespost2020flag() == false) && (currentyear > (this.myRegion.getcutoffyear() - this.minconstructionyears))) {	//Simplified as we should also add years in process for those of status 5, but by simply adding them would 
		return;}
		
		// That is, no point in trading projects if it cannot be build within the deadline
		else {
		
		if (this.status == 3 || this.status == 5) {	//Only those beeing stoped by the developer are moved to a better developer. (for status 5, all DA are fundamental, hence moving to a better developer does not really help.
			double rand = RandomHelper.nextDoubleFromTo(0.0, 1.0);
			
			//Rules for changing projectmarketcandidateflag
			if (this.yearsincurrentstatus == 1) {
				if (rand < AllVariables.chanceofownershipchange[0]) {
					this.projectmarketcandidateflag = 1;
					this.setyearsincurrentstatus(0);                           		//Status is et to zero as this projets will be "traded" in the market and the yeas should be resetted.
					this.addtonumberofownershipchange();
				}
			}
			if (this.yearsincurrentstatus == 2) {
				if (rand < AllVariables.chanceofownershipchange[1]) {
					this.projectmarketcandidateflag = 1;
					this.setyearsincurrentstatus(0);                           		//Status is et to zero as this projets will be "traded" in the market and the years should be resetted.
					this.addtonumberofownershipchange();
				}
			}
			if (this.yearsincurrentstatus == 3) {
				if (rand < AllVariables.chanceofownershipchange[2]) {
					this.projectmarketcandidateflag = 1;
					this.setyearsincurrentstatus(0);                           		//Status is et to zero as this projets will be "traded" in the market and the yeas should be resetted.
					this.addtonumberofownershipchange();
}
			}
			
			if (this.yearsincurrentstatus > 3) {
				if (rand < AllVariables.chanceofownershipchange[3]) {
					this.projectmarketcandidateflag = 1;
					this.setyearsincurrentstatus(0);                           		//Status is et to zero as this projets will be "traded" in the market and the yeas should be resetted.
					this.addtonumberofownershipchange();

				}
			}
		}

			}
				
	}
	public void updatacriteriaflag_initial() {
		this.projectmarketcandidateflag = 0;
		
		//Only projects awaiting decisions, in process or identifyed can be redistributed. 
		if (this.status > 2 && this.status < 6) {	

		//Only if "good" there is a chance for redistribution. (Better than the marginal defined by the project market.
		if (this.getcertpriceneeded() < ProjectMarket.getmarginalcertpriceneeded()) {
			double rand = RandomHelper.nextDoubleFromTo(0.0, 1.0);
			if (rand < AllVariables.chanceofownershipchange[0]) {
				this.projectmarketcandidateflag = 1;
			}

	
		}
	}
	}
}
	

	
	