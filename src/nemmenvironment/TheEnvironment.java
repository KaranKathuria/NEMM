package nemmenvironment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import repast.simphony.engine.environment.RunEnvironment;
import nemmagents.CompanyAgent;
import nemmagents.CompanyAgent.ActiveAgent;
import nemmagents.MarketAnalysisAgent;
import nemmcommons.AllVariables;
import nemmcommons.CommonMethods;
import nemmcommons.ParameterWrapper;
import nemmcommons.TickArray;
import nemmcommons.YearArray;
import nemmprocesses.ShortTermMarket;
import inputreader.ReadExcel;

import repast.simphony.random.RandomHelper;
import cern.jet.random.Normal;

import nemmtime.NemmCalendar;

public final class TheEnvironment {

                // This class is used to hold all the stuff in the environment
                public static ArrayList<PowerPlant> allPowerPlants;                             //allPowerPlants referes to all power plants that HAVE BEEN or ARE in operation. Before an object PowerPlant is in operation, its reffered to as a project. To distinguish between have or are use endtick.
                public static ArrayList<PowerPlant> projectsunderconstruction;                  //PowerPlants currently under construction (status = 2)
                public static ArrayList<PowerPlant> projectsawaitinginvestmentdecision;         //PowerPlants projects awaiting investment decision (status = 3)
                public static ArrayList<PowerPlant> projectinprocess;                           //All powerplant in process of getting concession.  (status = 4)
                public static ArrayList<PowerPlant> projectsidentifyed;                         //All projects identifyed                                                                                                        (status = 5)
                public static ArrayList<PowerPlant> potentialprojects;                          //Auto-generated potential projects. Note distributed among development agents. (status = 6).
                public static ArrayList<PowerPlant> trashedprojects;                            //Arraylist of projects not receiving concession (status = 0).
                
                public static ArrayList<PowerPlant> allPowerPlantsandProjects;                  //Absolutly all. Including trashed, qoued etc. ALl provided in the input sheet.
                public static ArrayList<Region> allRegions;
                public static ArrayList<CompanyAgent> allCompanies;
                public static ArrayList<Scenario> allwindandppricescenarios;                                    //All scenarios of wind year mulitpliers and power prices
                public static ArrayList<ProjectRRR> alladjustedRRR;                                                                                     //List containing alle the adjusted RRR. Used by Projects to look up their specific RRR.
                public static NemmCalendar theCalendar;
                public static double wind4;
                public static double wind4_2;
                
                public static double produceragentscounter;
                public static double obligatedpurchaseragentcounter;
                public static double developeragentcounter;
                public static int a = 2;
                
                
                private TheEnvironment() {}
                
                // Initialise the environment ------------------------------------------------------------
                public static void InitEnvironment(){
                               // Create & set up the time calendar and create the lists to hold plants, projects and regions.
                           
                			//All testing 20160111 KK:
                	
                			/*
                            RandomHelper.setSeed(1);
                			RandomHelper.createUniform(); //Bruker timestamp som første, deretter følgen. om ikke seed resettes.
                			Normal test;
                			test = RandomHelper.createNormal(15, 1.2);
                	                			
                			int seed = RandomHelper.getSeed();
                			
                			int index = RandomHelper.nextIntFromTo(0, 10);
                			int pindex = RandomHelper.nextIntFromTo(0, 10);
                			int finetx = RandomHelper.nextIntFromTo(0, 10);
                			
                			double n1 = test.nextDouble();
                			double n2 = test.nextDouble();
                			double n3 = test.nextDouble();


                			Normal test2;
                			test2 = RandomHelper.createNormal(15, 1.2);
                			RandomHelper.setSeed(1);
                		

                			int index2 = RandomHelper.getUniform().nextIntFromTo(0, 10);
                			int pindex2 = RandomHelper.getUniform().nextIntFromTo(0, 10);
                			int finetx2 = RandomHelper.getUniform().nextIntFromTo(0, 10); 
                			
                			double n4 = test2.nextDouble();
                			double n5 = test2.nextDouble();
                			double n6 = test2.nextDouble();
                			
                			//RandomHelper.setSeed(1);
                			
                			         
                			//RandomHelper.setSeed(1);


                			/*
                			double n2 = RandomHelper.getNormal().nextDouble();
                			double n3 = RandomHelper.getNormal().nextDouble();
                			int fx = RandomHelper.getUniform().nextIntFromTo(0, 10);

                			RandomHelper.createNormal(5, 1);
                			double n21 = RandomHelper.getNormal().nextDouble();
                			double n22 = RandomHelper.getNormal().nextDouble();
                			double n23 = RandomHelper.getNormal().nextDouble();
                			*/
                			
                			//a = index;
                			
                			
                               inputreader.ReadExcel.InitReadExcel();
                               inputreader.ReadExcel.ReadCreateTime();
                               GlobalValues.initglobalvalues();
                               allPowerPlantsandProjects = new ArrayList<PowerPlant>() ;
                               allPowerPlants = new ArrayList<PowerPlant>() ;
                               potentialprojects = new ArrayList<PowerPlant>() ;
                               projectsidentifyed = new ArrayList<PowerPlant>() ;
                               projectinprocess = new ArrayList<PowerPlant>() ;
                               projectsawaitinginvestmentdecision = new ArrayList<PowerPlant>() ;
                               projectsunderconstruction = new ArrayList<PowerPlant>() ;
                               trashedprojects = new ArrayList<PowerPlant>();
                               allRegions = new ArrayList<Region>();  
                               alladjustedRRR = new ArrayList<ProjectRRR>();
                               allwindandppricescenarios = new ArrayList<Scenario>();
                               
                               produceragentscounter = 0;
                               obligatedpurchaseragentcounter = 0;
                               developeragentcounter = 0;
                               
                }
                
                // Populate the Environment ------------------------------------------------------------
                
                public static void PopulateEnvironment(){
                               inputreader.ReadExcel.ReadRegions();
                               inputreader.ReadExcel.ReadRRR();
                               inputreader.ReadExcel.ReadPowerPlants();
                               inputreader.ReadExcel.ReadScenarios();        //TBD by Anders. Reads all the scenarios an adds them to the "allwindandpricescenarios" list which then is used to generate power prices and wind years.
                               
                               //A bit misplaced, but adding all the project and powerplants to a collection Array
                               allPowerPlantsandProjects.addAll(allPowerPlants);
                               allPowerPlantsandProjects.addAll(projectsunderconstruction);
                               allPowerPlantsandProjects.addAll(projectsawaitinginvestmentdecision);
                               allPowerPlantsandProjects.addAll(projectinprocess);
                               allPowerPlantsandProjects.addAll(projectsidentifyed);
                               allPowerPlantsandProjects.addAll(potentialprojects);                                                                                                                                                                                                                                             
                }
                
                public static void setwindscenario() {
                               
                               //NOT NEEDED to reset/rewind the production, as this IS re-read from the excel-file.
                               //Get the correct scenario
                               Scenario runningscenario = TheEnvironment.allwindandppricescenarios.get(ParameterWrapper.getscenarionumber());
                               
                               int temptickid = 0;
                               
                               //For all years
                               for (int i = 0; i<TheEnvironment.theCalendar.getNumYears();i++) {       //For all år
                                               double tempmulti = runningscenario.getWindyearmultiplier().getElement(i);
                                               
                                               //For ticks in year
                                               for (int k = 0; k<TheEnvironment.theCalendar.getNumTradePdsInYear(); k++) {
                                                               //For all powerplants (wind)
                                                               for (PowerPlant PP : TheEnvironment.allPowerPlantsandProjects) {
                                                                              if (PP.gettechnologyid() == 2) { 
                                                                              double org = PP.getProduction(temptickid);
                                                                              double test = PP.getProduction(temptickid)*tempmulti;
                                                                              PP.setProduction((PP.getProduction(temptickid)*tempmulti),temptickid);                      
                                                                              }
                                                               }
                                               temptickid=temptickid+1;
                                               }
                               }
                }
                
                public static void  setpowerpricescenario() {
                               //Get the correct scenario.
                               Scenario runningscenario = TheEnvironment.allwindandppricescenarios.get(ParameterWrapper.getscenarionumber());
                               Region Norway = TheEnvironment.allRegions.get(0);
                               Region Sweden = TheEnvironment.allRegions.get(1);
                               
                               //Creates temporary spot, and adds the relevant spotpriceyears from the scenario
                               double[] spotN = new double[TheEnvironment.theCalendar.getNumYears()];
                               double[] spotS = new double[TheEnvironment.theCalendar.getNumYears()];
                               //Adds the relevnt years from the scenario to a double array.
                               System.arraycopy(runningscenario.getAnnualpowerpricerregion1(), 0, spotN, 0, TheEnvironment.theCalendar.getNumYears());
                               System.arraycopy(runningscenario.getAnnualpowerpricerregion2(), 0, spotS, 0, TheEnvironment.theCalendar.getNumYears());
                               int a = 5;
                               
                               //Setting the forward-prices
                               for (int i = 0; i<TheEnvironment.theCalendar.getNumYears();i++) {       //For forward-years. [i=1] is the array of future prices standing in year 2013, with 24 doubles. That is for each annualMarketSeries
                                               double[] tempams_N = new double[TheEnvironment.theCalendar.getNumYears()];
                                               double[] tempams_S = new double[TheEnvironment.theCalendar.getNumYears()];
                                               int gg=runningscenario.getAnnualpowerpricerregion1().length;
                                               int t=3;
                                                                              for (int j = 0; j<TheEnvironment.theCalendar.getNumYears();j++) {
                                                                                              tempams_N[j] = Norway.getMyForwardPrice(i).getValue(j) * runningscenario.getAnnualpowerpricerregion1()[j+i]; //Here the fwd of year 2035 will go from 2035 - 2058 (+23)
                                                                                              tempams_S[j] = Sweden.getMyForwardPrice(i).getValue(j) * runningscenario.getAnnualpowerpricerregion2()[j+i];
                                                                              }
                                                                              //Then for each fwd-year we set the annualmarketseries representing that years fwd-curve.
                                                                              Norway.getMyForwardPrice(i).setAllValues(tempams_N);
                                                                              Sweden.getMyForwardPrice(i).setAllValues(tempams_S);
                                                                              int b = 4;
                                                                              }
                               //At last setting spot prices
                               Norway.getMyPowerPrice().setAllValues(spotN);
                               Sweden.getMyPowerPrice().setAllValues(spotS);
                               int t = 3;
                               
                               }
                               
                
                // KK: 20150512 Old version of simulateweather used before the scenarios where red in directly.
                public static void simulateweatherdistribtion() {
                			   Normal tempn;
                               tempn = RandomHelper.createNormal(AllVariables.meanwindproductionfactor, AllVariables.stdwindfactor);                //Create the used normal distribution skal parametersers
                               int temptickid = 0;           
                               
                               //2 is Wind power
                               for (int i = 2012; i<TheEnvironment.theCalendar.getStartYear()+TheEnvironment.theCalendar.getNumYears();i++) {  //For all år
                                               Double temp = tempn.nextDouble();
                                               int tf= 3;
                                               
                                               //Section below two cut max and min values for wind productionfactor.
                                                               if(temp<(AllVariables.meanwindproductionfactor*(1-(AllVariables.stdwindfactor*AllVariables.maxstdwindfactor)))) {
                                                                              temp = AllVariables.meanwindproductionfactor*(1-(AllVariables.stdwindfactor*AllVariables.maxstdwindfactor));
                                                               }
                                                               double d = (1+(AllVariables.stdwindfactor*AllVariables.maxstdwindfactor));
                                                               if(temp > (AllVariables.meanwindproductionfactor*d)) {
                                                                              temp = AllVariables.meanwindproductionfactor*(1+(AllVariables.stdwindfactor*AllVariables.maxstdwindfactor));
                                                               }
                                               //section end
                                               
                                               for (int k = 0; k<TheEnvironment.theCalendar.getNumTradePdsInYear(); k++) {

                                                               for (PowerPlant PP : TheEnvironment.allPowerPlantsandProjects) {
                                                                              if (PP.gettechnologyid() == 2) { 
                                                                              double org = PP.getProduction(temptickid);
                                                                              double test = PP.getProduction(temptickid)*temp;
                                                                              PP.setProduction((PP.getProduction(temptickid)*temp),temptickid);
                                                                              }
                                                               }
                                                               temptickid=temptickid+1;
                                               }
                               }
                }
                
                public static void simulatecertdemand() {
                	if(!AllVariables.certificatedemandinqouta) {
                	//Danger danger. Overwriting the certdemand with expected cert demand.
                	TheEnvironment.allRegions.get(0).getMyDemand().setCertDemand(TheEnvironment.allRegions.get(0).getMyDemand().getExpectedCertDemand_all());
                	TheEnvironment.allRegions.get(1).getMyDemand().setCertDemand(TheEnvironment.allRegions.get(1).getMyDemand().getExpectedCertDemand_all());
                	}
                	else {
                	// Simulate the natural variation in annual cert demand in the same manner as wind variation.
                		 Normal tempn2;
                         tempn2 = RandomHelper.createNormal(AllVariables.meancertdemandfactor, AllVariables.stdcertdemandfactor);                //Create the used normal distribution skal parametersers
                         int temptickid = 0;           
                         
                         //2 is Wind power
                         for (int i = 2012; i<TheEnvironment.theCalendar.getStartYear()+TheEnvironment.theCalendar.getNumYears();i++) {  //For all år
                                         Double temp = tempn2.nextDouble();
                                         int tf= 3;
                                         
                                         //Section below tp cut max and min values for certdemand factor.
                                                         if(temp<(AllVariables.meancertdemandfactor*(1-(AllVariables.stdcertdemandfactor*AllVariables.maxstdcertdemandfactor)))) {
                                                                        temp = AllVariables.meancertdemandfactor*(1-(AllVariables.stdcertdemandfactor*AllVariables.maxstdcertdemandfactor));
                                                         }
                                                         double d = (1+(AllVariables.stdcertdemandfactor*AllVariables.maxstdcertdemandfactor));
                                                         if(temp > (AllVariables.meancertdemandfactor*d)) {
                                                                        temp = AllVariables.meancertdemandfactor*(1+(AllVariables.stdcertdemandfactor*AllVariables.maxstdcertdemandfactor));
                                                         }
                                         //section end
                                          for (int k = 0; k<TheEnvironment.theCalendar.getNumTradePdsInYear(); k++) {

                                          for (Region R : TheEnvironment.allRegions) {
                                                          
                                                          double org = R.getMyDemand().getCertDemand(temptickid);
                                                          double test = R.getMyDemand().getCertDemand(temptickid)*temp;
                                                          R.getMyDemand().setCertDemand_tick((R.getMyDemand().getCertDemand(temptickid)*temp),temptickid);
                                                          }        
                                                          temptickid=temptickid+1;
                	}
                }
                	}
                }
                
                public static void  calculateLRMC_exougenousprojects() {
                	for (PowerPlant PP : allPowerPlantsandProjects) {
                		if (PP.getstatus() == 1 || PP.getstatus() == 2) {
                			PP.calculateLRMCandcertpriceneeded(theCalendar.getStartYear(), PP.getspecificRRR(), 3);
                		}
                	}
                }

                
                
                public static class GlobalValues {
                               
                               public static YearArray averageannualcertprice;               //Average annual cert price for the years passed.Updated every year.
                               public static TickArray certificateprice;
                               public static double currentmarketprice;
                               public static double RRRcorrector;                              //Corrector for the project specific RRR. Initially set, then altered by randomness
                               public static double currentinterestrate;                       //Used in short term bidding strategy to deterimine the risk free rate. This is more the risk free rate.
                               public static int numberofbuyoffersstm;
                               public static int numberofselloffersstm;
                               public static double avrhistcertprice;                          //Average historic cert price based on x number of ticks, where X is given by AllVariables.numberoftickstocalculatehistcertprice
                               public static int numberofpowerplantsinNorway;
                               public static int numberofpowerplantsinSweden;
                               public static double buildoutNorway;                    //Accumulated annual production added in Norway through the certificate market (MWh)
                               public static double buildoutSweden;                    //Accumulated annual production added in Sweden through the certificate market (MWh)
                               public static double certificateeligableannualproductionNorway;	//Current eligable certificate producion measured in normal year production (MWh)
                               public static double certificateeligableannualproductionSweden;	//Current eligable certificate producion measured in normal year production (MWh)
                               
                               public static double windcapacityaddedNorway;           //Accumulated wind-capacity added in Norway (MW)
                               public static double windcapacityaddedSweden;           //Accumulated wind-capacity added in Sweden (MW)
                               public static double hydrocapacityaddedNorway;          //Accumulated hydro-capacity added in Norway (MW)
                               public static double hydrocapacityaddedSweden;          //Accumulated hydro-capacity added in Norway (MW)
                               public static double allothercapacityaddedNorway;       //Accumulated other in the terms (bio, solar, and other) added in Norway. This added with hydro and wind is total.
                               public static double allothercapacityaddedSweden;       //Accumulated other in the terms (bio, solar, and other) added in Sweden. This added with hydro and wind is total.
                               
                               public static double windproductionaddedNorway;           //Accumulated wind-production added in Norway (MWh)
                               public static double windproductionaddedSweden;           //Accumulated wind-production added in Sweden (MWh)
                               public static double hydroproductionaddedNorway;          //Accumulated hydro-production added in Norway (MWh)
                               public static double hydroproductionaddedSweden;          //Accumulated hydro-production added in Norway (MWh)
                               public static double allotherproductionaddedNorway;       //Accumulated other in the terms (bio, solar, and other) added in Norway. This added with hydro and wind is total.
                               public static double allotherproductionaddedSweden;       //Accumulated other in the terms (bio, solar, and other) added in Sweden. This added with hydro and wind is total.
                               
                               public static double totalwithintargetbuildout;			 //Total buildout within the cert market target, measured in normalyearproduction within target date (See Allvariables.buildouttargetyear). 
                               public static int targettick; 
                               public static boolean buildouttargetreached;

                               // Future cert prices
                               public static double endofyearpluss1;
                               public static double endofyearpluss2;
                               public static double endofyearpluss3;
                               public static double endofyearpluss4;
                               public static double endofyearpluss5;
                               // Power prices (pp), current and future. 
                               public static double powerprice;
                               public static double ppendofyearpluss1;
                               public static double ppendofyearpluss2;
                               public static double ppendofyearpluss3;
                               public static double ppendofyearpluss4;
                               public static double ppendofyearpluss5;
                               
                               public static double producersphysicalposition = 0;
                               public static double totaltickproduction = 0;
                               public static double tradersphysicalposition = 0;
                               public static double obligatedpurchasersphysiclaposition = 0;
                               public static double totaltickdemand = 0;
                               public static double totalmarketphysicalposition = 0;
                               public static double ticksupplyanddemandbalance = 0;
                               public static double bestbuyoffer1;
                               public static double bestbuyoffer2;
                               public static double bestselloffer1;
                               public static double bestselloffer2;
                               
                               //updated tacticMaxPhysPosSellShare_PASellStrategy1
                               public static double updated_tacticMaxPhysPosSellShare_PASellStrategy1 = AllVariables.tacticMaxPhysPosSellShare_PASellStrategy1;
                               
                               public GlobalValues() {
                                               currentmarketprice = ParameterWrapper.getpriceexpectation();
                               }
                               
                               public static void setupdated_tacticMaxPhysPosSellShare_PASellStrategy1(double a) {
                            	   updated_tacticMaxPhysPosSellShare_PASellStrategy1 = a; 
                               }
                               
                               public static void initglobalvalues() {
                                               
                                               //Initiating globale values consisting of public market information
                                               certificateprice = new TickArray();
                                               averageannualcertprice = new YearArray();
                                               currentmarketprice = ParameterWrapper.getpriceexpectation();                                         //This is the initial expected short term price at simulation start.
                                               avrhistcertprice = currentmarketprice;                                                                                                                               //Initially
                                               currentinterestrate = ParameterWrapper.getinitialinterestrate();
                                               RRRcorrector = AllVariables.initialRRRcorrector;
                                               producersphysicalposition = 0;// NOT in use as these are updatet en monthly schedual. AllVariables.bankPAFirstTick;    
                                               totaltickproduction = 0;
                                               tradersphysicalposition = 0;                                                                                      //Must be set to the sum of all agents startingposition. Just used for graph values.
                                               obligatedpurchasersphysiclaposition = 0; // NOT in use as these are updatet en monthly schedual. AllVariables.bankOPAFirstTick;
                                               totaltickdemand = 0;
                                               totalmarketphysicalposition = 0;                                                                            //Must be set to the sum of all agents startingposition. Just used for graph values.
                                               
                                               buildouttargetreached = false;
                                               totalwithintargetbuildout = 0;
                                               targettick = ((AllVariables.buildouttargetyear - theCalendar.getStartYear())*theCalendar.getNumTradePdsInYear())+(theCalendar.getNumTradePdsInYear()-1);
                                               
                               }
                               
                               public static void updatebankbalance() {
                                               //Modelling the effect of resetting the market price
                                               for (ActiveAgent pa: CommonMethods.getPAgentList()){
                                                               producersphysicalposition = producersphysicalposition + pa.getphysicalnetposition();
                                               }
                                               for (ActiveAgent opa: CommonMethods.getOPAgentList()){
                                                               obligatedpurchasersphysiclaposition = obligatedpurchasersphysiclaposition + opa.getphysicalnetposition();   
                                               }
                                               for (ActiveAgent ta: CommonMethods.getTAgentList()){
                                                               tradersphysicalposition = tradersphysicalposition + ta.getphysicalnetposition();             
                                               }
                                               totalmarketphysicalposition = tradersphysicalposition + obligatedpurchasersphysiclaposition + producersphysicalposition;
                                               
                                               
                                               }
                               
                               // Monthly update of current global values
                               public static void monthlyglobalvalueupdate() {
                                               currentmarketprice = ShortTermMarket.getcurrentmarketprice();
                                               certificateprice.setElement(ShortTermMarket.getcurrentmarketprice(), theCalendar.getCurrentTick()); //Adds certPrice to history.
                                               updateavrhistoriccertprice();                                                                                                                                                                                                                                                                               //Updates the averagecertprice
                                                                                              
                                               numberofbuyoffersstm = ShortTermMarket.getnumberofbuyoffers();
                                               numberofselloffersstm = ShortTermMarket.getnumberofselloffers();
                                               
                                               totaltickproduction = 0;
                                               totaltickdemand = 0;
                                               producersphysicalposition = 0;
                                               tradersphysicalposition = 0;
                                               obligatedpurchasersphysiclaposition = 0;
                                               totalmarketphysicalposition = 0;
                                               ticksupplyanddemandbalance= 0;
                                               
                                               for (ActiveAgent pa: CommonMethods.getPAgentList()){
                                                               producersphysicalposition = producersphysicalposition + pa.getphysicalnetposition();
                                                               totaltickproduction = totaltickproduction + pa.getlasttickproduction();
                                               }
                                               for (ActiveAgent opa: CommonMethods.getOPAgentList()){
                                                               obligatedpurchasersphysiclaposition = obligatedpurchasersphysiclaposition + opa.getphysicalnetposition();   
                                                               totaltickdemand = totaltickdemand + opa.getlasttickdemand();
                                               }
                                               for (ActiveAgent ta: CommonMethods.getTAgentList()){
                                                               tradersphysicalposition = tradersphysicalposition + ta.getphysicalnetposition();             
                                               }
                                               totalmarketphysicalposition = tradersphysicalposition + obligatedpurchasersphysiclaposition + producersphysicalposition;
                                               ticksupplyanddemandbalance = totaltickproduction + totaltickdemand;
                                               
                                               numberofpowerplantsinNorway = 0;
                                               numberofpowerplantsinSweden = 0;
                                               buildoutNorway = 0;
                                               buildoutSweden = 0;
                                               windcapacityaddedNorway = 0;               
                                               windcapacityaddedSweden = 0;                              
                                               hydrocapacityaddedNorway = 0;                            
                                               hydrocapacityaddedSweden = 0;                            
                                               allothercapacityaddedNorway = 0;         
                                               allothercapacityaddedSweden = 0;
                                               windproductionaddedNorway = 0;               
                                               windproductionaddedSweden = 0;                              
                                               hydroproductionaddedNorway = 0;                            
                                               hydroproductionaddedSweden = 0;                            
                                               allotherproductionaddedNorway = 0;         
                                               allotherproductionaddedSweden = 0;
                                               certificateeligableannualproductionNorway = 0;
                                               certificateeligableannualproductionSweden = 0;
                                               
                                               for (PowerPlant PP : TheEnvironment.allPowerPlants) {
                                           	   	//Tickwise buildout
                                            	//Only projects excluded from overgangsordningen!
                                           	   if (theCalendar.getCurrentTick() >= PP.getStartTick() && PP.getovergangsordningflag() == 0) {
                                           		   				//Region wise
                                                               if (PP.getMyRegion() == TheEnvironment.allRegions.get(0)) {

                                                            	   buildoutNorway = buildoutNorway + (PP.getestimannualprod());
                                                            	   numberofpowerplantsinNorway = numberofpowerplantsinNorway +1;
                     
                                                   	   						
                                                                             if (PP.gettechnologyid() == 2) { //Notice that this is not else because the above is not exclusive.
                                                                                              windcapacityaddedNorway = windcapacityaddedNorway + PP.getCapacity();
                                                                                              windproductionaddedNorway = windproductionaddedNorway + PP.getestimannualprod();

                                                                             }
                                                                             else if (PP.gettechnologyid() == 1) { //Same as if, but if satisfied, the next statement(s) are not evaluated.
                                                                                              hydrocapacityaddedNorway = hydrocapacityaddedNorway + PP.getCapacity(); 
                                                                                              hydroproductionaddedNorway = hydroproductionaddedNorway + PP.getestimannualprod();            

                                                                             }
                                                                             else {
                                                                                              allothercapacityaddedNorway = allothercapacityaddedNorway + PP.getCapacity();
                                                                                              allotherproductionaddedNorway = allotherproductionaddedNorway + PP.getestimannualprod();

                                                                              }
   
                                                            	   			 }

                                                               else {
                                                            	   
                                                            	   buildoutSweden = buildoutSweden + (PP.getestimannualprod());
                                                                   numberofpowerplantsinSweden = numberofpowerplantsinSweden +1;  
                                                                   
                                                                              if (PP.gettechnologyid() == 2) {
                                                                                              windcapacityaddedSweden = windcapacityaddedSweden + PP.getCapacity();
                                                                                              windproductionaddedSweden = windproductionaddedSweden + PP.getestimannualprod();

                                                                              }
                                                                              else if (PP.gettechnologyid() == 1) {
                                                                                              hydrocapacityaddedSweden = hydrocapacityaddedSweden + PP.getCapacity(); 
                                                                                              hydroproductionaddedSweden = hydroproductionaddedSweden + PP.getestimannualprod();            

                                                                              }
                                                                              else {
                                                                                              allothercapacityaddedSweden = allothercapacityaddedSweden + PP.getCapacity();
                                                                                              allotherproductionaddedSweden = allotherproductionaddedSweden + PP.getestimannualprod();

                                                                              }
                                                                             }
                                                               }
                                           	   
                                               }
                                               
                                               for (PowerPlant PP : TheEnvironment.allPowerPlants) {
                                              	   	//total buildout. Notice the equal to 0 thus excluding overgagnsordningen.

                                              	   if (theCalendar.getCurrentTick() >= PP.getStartTick() && theCalendar.getCurrentTick() < PP.getendtick() && PP.getovergangsordningflag() == 0) {
                                              		   				//Region wise
                                                                  if (PP.getMyRegion() == TheEnvironment.allRegions.get(0)) {
                                        	  
                                                                	  certificateeligableannualproductionNorway = certificateeligableannualproductionNorway + PP.getestimannualprod();
                                            	   		}
                                                                  else {
                                                                 	  certificateeligableannualproductionSweden = certificateeligableannualproductionSweden + PP.getestimannualprod();
  
                                                                  }                                                                             
                                              	   }
                                               }
                                               
                                               if ((theCalendar.getCurrentTick() <= targettick) && (!buildouttargetreached) ) {
                                            	   totalwithintargetbuildout = 0;
                                            	   totalwithintargetbuildout = buildoutSweden + buildoutNorway;
                                             	  int a = 2;
                                               
                                             	  if (totalwithintargetbuildout >= AllVariables.totalbuildouttarget){
                                            	   buildouttargetreached = true;
                                             	  }
                                               }
                                            	   
                               }
                                               
                                                        
                               public static void annualglobalvalueupdate() {
                                               RRRcorrector = Math.max(1,(RRRcorrector - 1*0.03));                                                 //Corrector redution to take account the learning and FMA.
                                               //update averageannualcertprice
                                               //Current tick is "january", hence we need to average the price from t-13 to t-1
                                               double lastyearavrcertprice = 0;
                                               int startindex = theCalendar.getCurrentTick() - (theCalendar.getNumTradePdsInYear());
                                               for (int i = 0; i < theCalendar.getNumTradePdsInYear();i++) {
                                                               lastyearavrcertprice = lastyearavrcertprice + certificateprice.getElement(startindex+i);
                                               }
                                               lastyearavrcertprice = lastyearavrcertprice/theCalendar.getNumTradePdsInYear();
                                               averageannualcertprice.setElement(lastyearavrcertprice, theCalendar.getTimeBlock(startindex).year);
                
                                               
                               }
                               
                               public static void updateavrhistoriccertprice() {
                                               if (TheEnvironment.theCalendar.getCurrentTick() <= AllVariables.numberoftickstocalculatehistcertprice) {
                                                               avrhistcertprice = currentmarketprice;                                                                                                                              //If the tick is less, then we just take the prevoius price as average.
                                               }
                                               else {                                                                                                                                                                                                                                                                               //Take the average of last X ticks
                                                               double tempcutoff = 0;
                                                               int startindex = TheEnvironment.theCalendar.getCurrentTick() - AllVariables.numberoftickstocalculatehistcertprice;
                                                               for (int i = 0; i < AllVariables.numberoftickstocalculatehistcertprice; i++) {
                                                                              tempcutoff = tempcutoff + certificateprice.getElement(startindex+i); }
                                                               avrhistcertprice = tempcutoff/AllVariables.numberoftickstocalculatehistcertprice;
                               }
                               }
                               
                               
                }
                
                
                }
                               




