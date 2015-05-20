package nemmenvironment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.random.RandomHelper;
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

import java.util.Comparator;

import nemmtime.NemmCalendar;

public final class TheEnvironment {

                // This class is used to hold all the stuff in the environment
                public static ArrayList<PowerPlant> allPowerPlants;                                                                     //allPowerPlants referes to all power plants that have been or are in operation. Before an object PowerPlant is in operation, its reffered to as a project.
                public static ArrayList<PowerPlant> projectsunderconstruction;                                            //PowerPlants currently under construction (status = 2)
                public static ArrayList<PowerPlant> projectsawaitinginvestmentdecision;         //PowerPlants projects awaiting investment decision (status = 3)
                public static ArrayList<PowerPlant> projectinprocess;                                                                 //All powerplant in process of getting concession.  (status = 4)
                public static ArrayList<PowerPlant> projectsidentifyed;                                                                             //All projects identifyed                                                                                                        (status = 5)
                public static ArrayList<PowerPlant> potentialprojects;                                                                //Auto-generated potential projects. Note distributed among development agents. (status = 6).
                public static ArrayList<PowerPlant> trashedprojects;                                                                  //Arraylist of projects not receiving concession (status = 0).
                
                public static ArrayList<PowerPlant> allPowerPlantsandProjects;                                            //Absolutly all. Including trashed, qoued etc. ALl provided in the input sheet.

                
                public static ArrayList<Region> allRegions;
                public static ArrayList<CompanyAgent> allCompanies;
                public static ArrayList<Scenario> allwindandppricescenarios;                                    //All scenarios of wind year mulitpliers and power prices
                public static ArrayList<ProjectRRR> alladjustedRRR;                                                                                     //List containing alle the adjusted RRR. Used by Projects to look up their specific RRR.
                public static NemmCalendar theCalendar;
                public static double wind4;
                public static double wind4_2;

                
                
                private TheEnvironment() {}
                
                // Initialise the environment ------------------------------------------------------------
                public static void InitEnvironment(){
                               // Create & set up the time calendar and create the lists to hold plants, projects and regions.
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
                }
                
                // Populate the Environment ------------------------------------------------------------
                
                public static void PopulateEnvironment(){
                               inputreader.ReadExcel.ReadRegions();
                               inputreader.ReadExcel.ReadRRR();
                               inputreader.ReadExcel.ReadPowerPlants();
                               //inputreader.ReadExcel.ReadScenarios();        //TBD by Anders. Reads all the scenarios an adds them to the "allwindandpricescenarios" list which then is used to generate power prices and wind years.
                               
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
                               System.arraycopy(runningscenario.getAnnualpowerpricerregion1(), 0, spotN, 0, TheEnvironment.theCalendar.getNumYears());
                               System.arraycopy(runningscenario.getAnnualpowerpricerregion2(), 0, spotS, 0, TheEnvironment.theCalendar.getNumYears());
                               
                               //Setting the forward-prices
                               for (int i = 0; i<TheEnvironment.theCalendar.getNumYears();i++) {       //For forward-years. [i=1] is the array of future prices standing in year 2013, with 24 doubles. That is for each annualMarketSeries
                                               double[] tempams_N = new double[TheEnvironment.theCalendar.getNumYears()];
                                               double[] tempams_S = new double[TheEnvironment.theCalendar.getNumYears()];
                                                                              for (int j = 0; j<TheEnvironment.theCalendar.getNumYears();j++) {
                                                                                              tempams_N[j] = Norway.getMyForwardPrice(i).getValue(j) * runningscenario.getAnnualpowerpricerregion1()[j+i]; //Here the fwd of year 2035 will go from 2035 - 2058 (+23)
                                                                                              tempams_S[j] = Norway.getMyForwardPrice(i).getValue(j) * runningscenario.getAnnualpowerpricerregion2()[j+i];
                                                                              }
                                                                              //Then for each fwd-year we set the annualmarketseries representing that years fwd-curve.
                                                                              Norway.getMyForwardPrice(i).setAllValues(tempams_N);
                                                                              Sweden.getMyForwardPrice(i).setAllValues(tempams_S);
                                                                              }
                               //At last setting spot prices
                               Norway.getMyPowerPrice().setAllValues(spotN);
                               Sweden.getMyPowerPrice().setAllValues(spotS);
                               
                               }
                               
                
                /* KK: 20150512 Old version of simulateweather used before the scenarios where red in directly.*/
                public static void simulateweather() {
                               RandomHelper.createNormal(AllVariables.meanwindproductionfactor, AllVariables.stdwindfactor);                //Create the used normal distribution skal parametersers
                               int temptickid = 0;           
                               
                               //2 is Wind power
                               for (int i = 2012; i<TheEnvironment.theCalendar.getStartYear()+TheEnvironment.theCalendar.getNumYears();i++) {  //For all år
                                               double temp = RandomHelper.getNormal().nextDouble();
                                               int tf= 3;
                                               
                                               //Section below two cut max and min values for wind productionfactor.
                                                               if(temp<(AllVariables.meanwindproductionfactor*(1-(AllVariables.stdwindfactor*AllVariables.maxstdwindfactor)))) {
                                                                              temp = (AllVariables.meanwindproductionfactor*(1-(AllVariables.stdwindfactor*AllVariables.maxstdwindfactor)));
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
                                                                              if(PP.getname().equals("Wind_Norway_4")){
                                                                                              wind4 = org;
                                                                                              wind4_2 = test;
                                                                              }
                                                                              PP.setProduction((PP.getProduction(temptickid)*temp),temptickid);
                                                                              }
                                                               }
                                                               temptickid=temptickid+1;
                                               }
                               }
                }
                
                
                public static class GlobalValues {
                               
                               public static YearArray averageannualcertprice;               //Average annual cert price for the years passed.Updated every year.
                               public static TickArray certificateprice;
                               public static double currentmarketprice;
                               public static double RRRcorrector;                                           //Corrector for the project specific RRR. Initially set, then altered by randomness
                               public static double currentinterestrate;                             //Not in use.
                               public static int numberofbuyoffersstm;
                               public static int numberofselloffersstm;
                               public static double avrhistcertprice;                                     //Average historic cert price based on x number of ticks, where X is given by AllVariables.numberoftickstocalculatehistcertprice
                               public static int numberofpowerplantsinNorway;
                               public static int numberofpowerplantsinSweden;
                               public static double buildoutNorway;                                   //Accumulated annual production added in Norway through the certificate market
                               public static double buildoutSweden;                                   //Accumulated annual production added in Sweden through the certificate market
                               
                               public static double windcapacityaddedNorway;             //Accumulated wind-capacity added in Norway
                               public static double windcapacityaddedSweden;            //Accumulated wind-capacity added in Sweden
                               public static double hydrocapacityaddedNorway;           //Accumulated hydro-capacity added in Norway
                               public static double hydrocapacityaddedSweden;          //Accumulated hydro-capacity added in Norway
                               public static double allothercapacityaddedNorway;       //Accumulated other in the terms (bio, solar, and other) added in Norway. This added with hydro and wind is total.
                               public static double allothercapacityaddedSweden;       //Accumulated other in the terms (bio, solar, and other) added in Sweden. This added with hydro and wind is total.

                               
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
                               
                               public GlobalValues() {
                                               currentmarketprice = ParameterWrapper.getpriceexpectation();
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
                                               
                                               for (PowerPlant PP : TheEnvironment.allPowerPlants) {
                                                               if (PP.getMyRegion() == TheEnvironment.allRegions.get(0)) {
                                                                              buildoutNorway = buildoutNorway + (PP.getestimannualprod());
                                                                              if (PP.gettechnologyid() == 2) {
                                                                                              windcapacityaddedNorway = windcapacityaddedNorway + PP.getCapacity();
                                                                              }
                                                                              else if (PP.gettechnologyid() == 1) {
                                                                                              hydrocapacityaddedNorway = hydrocapacityaddedNorway + PP.getCapacity();            
                                                                              }
                                                                              else {
                                                                                              allothercapacityaddedNorway = allothercapacityaddedNorway + PP.getCapacity();
                                                                              }
                                                               }
                                                               
                                                               else {
                                                                              buildoutSweden = buildoutSweden + (PP.getestimannualprod());
                                                                              if (PP.gettechnologyid() == 2) {
                                                                                              windcapacityaddedSweden = windcapacityaddedSweden + PP.getCapacity();
                                                                              }
                                                                              else if (PP.gettechnologyid() == 1) {
                                                                                              hydrocapacityaddedSweden = hydrocapacityaddedSweden + PP.getCapacity();            
                                                                              }
                                                                              else {
                                                                                              allothercapacityaddedSweden = allothercapacityaddedSweden + PP.getCapacity();
                                                                              }
                                                               }
                                               }
                                               
                                               for (PowerPlant PP : TheEnvironment.allPowerPlants) {
                                                               if (PP.getMyRegion() == TheEnvironment.allRegions.get(0)) {
                                                                              numberofpowerplantsinNorway = numberofpowerplantsinNorway +1;
                                                               }
                                                               else {
                                                                              numberofpowerplantsinSweden = numberofpowerplantsinSweden +1;
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
                               




