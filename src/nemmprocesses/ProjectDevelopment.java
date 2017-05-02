/*
 * Version info:
 *     Public and static class containing the annual operation done to simulate the investmentprocess. All stages of the new project process should have separate methods.
 *     
 *     Last altered data: 20141020
 *     Made by: Karan Kathuria
 */

package nemmprocesses;

import java.util.ArrayList;
import java.util.Collections;

import repast.simphony.random.RandomHelper;
import nemmagents.CompanyAgent.DeveloperAgent;
import nemmcommons.AllVariables;
import nemmcommons.CommonMethods;
import nemmenvironment.FundamentalMarketAnalysis;
import nemmenvironment.PowerPlant;
import nemmenvironment.TheEnvironment;
import nemmenvironment.TheEnvironment.GlobalValues;

public class ProjectDevelopment {
	//Variables
	
	//Defualt NULL constructor
	public ProjectDevelopment() {};
	
	//Method finishind projects that are under construction (alter from status=2 to status=1) and has a start year smaller or equal to current year. 
	//FOr projects finishing the year, it adds the project to correct lists, set as starttick and updates status.
	public static void finalizeprojects() {
		
		int currenttick = TheEnvironment.theCalendar.getCurrentTick();
		int currentyear = TheEnvironment.theCalendar.getTimeBlock(currenttick).year + TheEnvironment.theCalendar.getStartYear();	//Get current year.
		ArrayList<PowerPlant> tempremoval = new ArrayList<PowerPlant>();															//Templist for removing projects.
		//Updating information for projects finished this year.
		for (PowerPlant PP : TheEnvironment.projectsunderconstruction) {
			if (PP.getstartyear() == currentyear) {
				PP.setstatus(1);
					if (!PP.getMyRegion().getcertificatespost2020flag() && currentyear > PP.getMyRegion().getcutoffyear()) {	//This takes care of projecs post 2020 in regions without certs post 2020.
						PP.setendyear(PP.getMyRegion().getcutoffyear());							//Does not matter what year this is set to.
						PP.setStarttick(TheEnvironment.theCalendar.getNumTicks());
						PP.setendtick(currenttick-1);}												//Setting this back in time, hence these certs are never produced.
					else {
						PP.setendyear(Math.min(PP.getlifetime()+currentyear, Math.min(currentyear+15, TheEnvironment.theCalendar.getEndYear())));	//Takes care of projects "in overgangsordningen" with 1 year lifetime.
						int temp = currenttick + RandomHelper.nextIntFromTo(0, TheEnvironment.theCalendar.getNumTradePdsInYear()-1); //KK 20160303 
						PP.setStarttick(temp);	//Random set starttick between now and 12 tick ahead.
						//if (currentyear>2020){  //KK 20160303 The original is actually gets its wrong as it sets project buildt after 2020 to only produce 14 years + rand tick.
						//temp = currenttick+11;
						//}
						if (!PP.getMyRegion().getcertificatespost2020flag()){
						PP.setendtick(Math.min(AllVariables.IRRcalculationtick,temp+(TheEnvironment.theCalendar.getNumTradePdsInYear()*(PP.getendyear()-currentyear)))); //KK 2017. Merket det over og lagt inn dette!
						} else {
						PP.setendtick(temp+(TheEnvironment.theCalendar.getNumTradePdsInYear()*(PP.getendyear()-currentyear))); //KK 2017. Merket det over og lagt inn dette!
					}}
				
				TheEnvironment.allPowerPlants.add(PP);			//Add to all operations powerplants
				PP.getMyCompany().getmypowerplants().add(PP);	//Add to company`s list of powerplants
				tempremoval.add(PP);
				//PP.getMyCompany().getmyprojects().remove(PP);	//NOT Remving these from company`s list of project! Keeping these eases the controll and counting.
			}
		}
	    //To work around the problem of concurring operations, the removement of the project from the Underconstruction list must be done in another operation below.
			TheEnvironment.projectsunderconstruction.removeAll(tempremoval);
	}
	
	
	//Method moving projects that fulfill investment criterieas from status=3 to status=2. Some projects are just unaltered.
	//Method iterating through all projects awaiting investment decision and deciding which to invest investment decison. SHould be ran after process are finished, and after construction are finished.
	public static void startconstrucion() {
		
		int currenttick = TheEnvironment.theCalendar.getCurrentTick();
		int currentyear = TheEnvironment.theCalendar.getTimeBlock(currenttick).year + TheEnvironment.theCalendar.getStartYear();	//Gets the current year.
		
		ArrayList<PowerPlant> temp_allprojectthatcanbebuild = new ArrayList<PowerPlant>();
		
		//For all Developers. This should be sorted by developertype, hence the FMA developer should build first. 
		for (DeveloperAgent DA : CommonMethods.getDAgentList()) {
			
			ArrayList<PowerPlant> templist = new ArrayList<PowerPlant>();
			ArrayList<Double> RRRpostponedtemplist = new ArrayList<Double>();
			double estimateRRR = 0;
			int numberofyearcertscanbehedged = AllVariables.numberofyearcertscanbehedged;
			
			//Collecting the projects that are awaiting investmetn decision (status = 3) and counting projects currently under construction.
			for (PowerPlant PP : DA.getmyprojects()) {
			double usedRRR = PP.getspecificRRR(); //DA.getmycompany().getInvestmentRRR()* removed as all should use the same RRR (its a benchmark for the project)
			double postponedRRR = usedRRR + AllVariables.RRRpostpondpremium;					//If postponed, there should be a risk premium. For now (se AllVariables) but 0.01 (1%).
			
				if (PP.getstatus() == 3) {														//3=Awaiting investment decision.
					int ti = 10;
					PP.addyearsincurrentstatus(1);												//Increasing number of years with this status with one.
					templist.add(PP);															//Adds all the projects, regardsless of having a cert price needed to high or low.
					RRRpostponedtemplist.add(postponedRRR);										//A not so good workaround for saving the projet specific postponedRRR
					PP.calculateLRMCandcertpriceneeded(currentyear, usedRRR, 3);				//Using the market forward power price in that given reigon. Notice that this is calculated for when the year the project can be invested in, not the year it can be finished!!
					PP.calculateLRMCandcertpriceneeded_ownRRR(currentyear, 3);
					estimateRRR = usedRRR*DA.getmycompany().getInvestmentRRR();
					int t = 33;
				}
			}
			
			//For each DeveloperAgent For all the relevant projects. Do the following:			
			Collections.sort(templist, new CommonMethods.customprojectcomparator());			//Sorting the of a DAs project awaiting from lowest certprieneeded to highest certpriceneeded
			
			//All the cirteria variables for the investment decision. Assumin default DA.getinvestmentdecisiontype() == 1 or 2
			double cutoffcertprice_fma = DA.getfundamentaleasefactor() * DA.getmycompany().getcompanyanalysisagent().getmarketanalysisagent().getmarketprognosis().getmedumrundpriceexpectations();
			double cutoffcertprice_price = DA.getpriceeasefactor()*TheEnvironment.GlobalValues.avrhistcertprice;
			double postpondedcertprice = DA.getmycompany().getcompanyanalysisagent().getmarketanalysisagent().getmarketprognosis().getlongrunpriceexpectatations();
			double equivivalentfactor = 1.0;
			
			//Problem occurs. That is all projects with curtoff higher than what they need builds, wiothout regards to what is really needed.
			if (DA.getinvestmentdecisiontype() == 3) {
				postpondedcertprice = -1;	//There is not an option to postponed if investmentdecisiontype = 3, hence this is set to -1;
				equivivalentfactor = 1.0;
				}
			if (DA.getinvestmentdecisiontype() == 4) {								//Only assuming the current average certprice for 2 years. 0 thereafter. //Need to create NPV equvalent
				equivivalentfactor = PowerPlant.calculateNPVfactor(15, estimateRRR)/PowerPlant.calculateNPVfactor(numberofyearcertscanbehedged, estimateRRR);
				postpondedcertprice = -1; 											//There is not an option to postponed if investmentdecisiontype = 0, hence this is set to -1;
				}
		
			//Updating and setting other limits to DA-buildout.
			int maxnumberofconstrucprojects = DA.getconstructionlimit();
			int constructionproject_counter = DA.getnumprojectsunderconstr();					//Newly updated values.
			double maxcapacitydeveloped		= DA.gettotalcapacitylimit();
			double capacitydeveloped_counter = DA.getcapacitydevorundrconstr();
			int potentialprojects = templist.size();
			int projects_pointer = 0;															//To ensure that the loop is not longer than number of objects.	
			
			//The critical investment decision. Fist checking the "other" criterias
			while ((constructionproject_counter < maxnumberofconstrucprojects) && (capacitydeveloped_counter < maxcapacitydeveloped) &&  (projects_pointer < potentialprojects )) {
					double certpriceneedednow = templist.get(projects_pointer).getcertpriceneeded();
					double certpriceneededownRRR = templist.get(projects_pointer).getcertpriceneeded_ownRRR();
					int a = 2;	//Test that the two certprice needed are somewhat different depending on investmentRRR

					//Then the profitability criteria. Notice that the curoffpricees are already multiplied with the ease factor above. 
				if (((equivivalentfactor*certpriceneedednow) <= cutoffcertprice_fma) && ((equivivalentfactor*certpriceneededownRRR) <= cutoffcertprice_price) && DA.iscvvaluesufficient(cutoffcertprice_price, certpriceneededownRRR)) {													//Starting with the best, if its worth investing...
					//20160120 KK: Added: Okey, if worth investing based on this. What about CV-value? lagt til i siste ledd i ifen over.
				
					
					//Okey. If its worth investing now, is it more lucrative to postpond the investment?
					double postponedRRR = RRRpostponedtemplist.get(projects_pointer);
					templist.get(projects_pointer).calculateLRMCandcertpriceneeded(currentyear+AllVariables.minpostpondyears, postponedRRR, 3);
					double certpriceneededpostpond = templist.get(projects_pointer).getcertpriceneeded();
					if ((cutoffcertprice_fma-certpriceneedednow)>(postpondedcertprice-certpriceneededpostpond)) {	//Only if its better to invest now than postponed, invest:
					//Note above will most likely not be true for DAs having a ease-factor on cutoffprice_fma. That is type 3 and 4. Hence only fundamental agents are filtered. 
					
					int b = 3;
					PowerPlant thisplant = templist.get(projects_pointer);
					temp_allprojectthatcanbebuild.add(thisplant);										//Add this project to the list of projects wantet (and could) be build
					capacitydeveloped_counter = capacitydeveloped_counter + thisplant.getCapacity();
					constructionproject_counter = constructionproject_counter + 1;
					
					//thisplant.setstatus(2);														//Changing status for the project (from 3=awaitingid to 2=underconstruction).
					thisplant.setstatus(9);										
				//	thisplant.setyearsincurrentstatus(0);  										//Setting this for consistency for project reaching new stag. This value is note used in later stages.
				//	thisplant.setstartyear(currentyear + thisplant.getminconstructionyears());	//Adding a startdate. Notice that this is done here rather than in the finalizeprojects.
				//	TheEnvironment.projectsunderconstruction.add(thisplant);								//Add to the Environment list of projects in process.
				//	TheEnvironment.projectsawaitinginvestmentdecision.remove(thisplant);		//Removing from Environment list of awaitinginvestmentsdecisions
					
					//No need for updating the developer number of projects as this is done in another method after this.
					projects_pointer++;}
					else {
						int utsatt = 3;
						int out = 5;
						System.out.print("Notice: A project is postponed"); 
						projects_pointer++;}			
					}
				else {projects_pointer++;}			//Note sure there is any point in having the else

			}
			
			
					
		}
		
		//Then something that uses the normalproduction of the plants added and the cutoff to determine the need for annual production. Then uses this to limit the total buildout.
		double tempfutureproduction = FundamentalMarketAnalysis.getbalanceandfutureproduction();
		double tempfuturedemand = FundamentalMarketAnalysis.getfuturedemand();
		
		double totalcertsneededbuilt = Math.max(-FundamentalMarketAnalysis.getallfuturecertificatebalance(), 0.0);		//Gets all the future uncovered need for certificates (normal year assumption) from the FMA
		double tempcertsdeveloperswantstobuild=0;														//Total certs added from the projects that the developers wants to build out.
		
		//To add up the total certs from the projects that now are marked as status = 9 (or in the temp_allprojectthatcanbebuild list)
		for (PowerPlant PP : temp_allprojectthatcanbebuild) {
			PP.calculateLRMCandcertpriceneeded(currentyear, PP.getspecificRRR(), 3);			//This to ensure that it the correct numbers stored in LRMC, and not the postponed one. and Certpriceneeded when its build. For output purposes.
			PP.calculateLRMCandcertpriceneeded_ownRRR(currentyear, 3);							//KK: Added 2017. This to ensure that it the correct numbers stored in LRMC, and not the postponed one. and Certpriceneeded when its build. For output purposes.
				if (!PP.getMyRegion().getcertificatespost2020flag() && (currentyear+PP.getminconstructionyears()) > PP.getMyRegion().getcutoffyear()) {
					//Nothing tempcertsdeveloperswantstobuild = tempcertsdeveloperswantstobuild
				}
				else {
					tempcertsdeveloperswantstobuild = tempcertsdeveloperswantstobuild + (PP.getestimannualprod()*Math.min(15, TheEnvironment.theCalendar.getEndYear()-(currentyear+PP.getminconstructionyears())));} 
		}
		
		//For checking purposes.
		if (tempcertsdeveloperswantstobuild >= totalcertsneededbuilt) {
			System.out.print("Notice: Developers wants to built out more than is needed");}
		else {
			System.out.print("Notice: Developers wants to built out less than is needed");}
			

		//Then we loop through all that can be build out, ensure that we do not "gold rush" and only build out if status = 9 (the developer actually wants to build it).
		double tempbuildout = 0.0; //measured in total amount of certificate produced
		double tempbuildoutnormalyear = 0.0; //measured in total normal year annual production.
		
		Collections.shuffle(temp_allprojectthatcanbebuild);
		for (PowerPlant PP : temp_allprojectthatcanbebuild) {
			
			//Removed 03.03.2016: double tempfactor = RandomHelper.nextDoubleFromTo(AllVariables.minbuildoutaggressivness, AllVariables.maxbuildoutaggressivness);	//Calculating the build-out limitation factor. Used to determining the "gold rush" limit factor used below.
			double tempfactor = PP.getMyCompany().getdeveloperagent().getbuildoutaggressivness();	//Calculating the build-out limitation factor. Used to determining the "gold rush" limit factor used below.
			double buildoutcutoff = Math.max(((tempfuturedemand*tempfactor)-tempfutureproduction),0.0);			//Rather then using the factor for balance (which is not good when there is no need for certs, the factor is multiplied with demand before adding current balance and all future production.

			if (tempbuildout <= buildoutcutoff) {	//Continue to build out as long as there is neeed and aggressivness i allowed. 
				
				//Added 04.07.2016 based on comment by Statkraft. When reaching the target, people will stop building. In short, target is reached by cutoffyear, do not build. 
				
				
				double target = TheEnvironment.GlobalValues.totalwithintargetbuildout;
				double f = AllVariables.totalbuildouttarget;
	
				if ((PP.getMyCompany().getdeveloperagent().getbuildiftargerisreached()) || (!GlobalValues.buildouttargetreached)){
							
				//if (target <= AllVariables.totalbuildouttarget){ //TheEnvironment.GlobalValues.totalwithintargetbuildout
					
					if (!PP.getMyRegion().getcertificatespost2020flag() && (currentyear+PP.getminconstructionyears()) > PP.getMyRegion().getcutoffyear()) {
					System.out.print("Notice: Projects does not qualify for certs, but is built anyway");
					PP.setstatus(2);
					PP.setyearsincurrentstatus(0);  										//Setting this for consistency for project reaching new stag. This value is note used in later stages.
					PP.setstartyear(currentyear + PP.getminconstructionyears());			//Adding a startdate. Notice that this is done here rather than in the finalizeprojects.
					TheEnvironment.projectsunderconstruction.add(PP);						//Add to the Environment list of projects in process.
					TheEnvironment.projectsawaitinginvestmentdecision.remove(PP);			//Removing from Environment list of awaitinginvestmentsdecisions
					}
					else {
					tempbuildout = tempbuildout + (PP.getestimannualprod()*Math.min(15, TheEnvironment.theCalendar.getEndYear()-(currentyear+PP.getminconstructionyears())));
					PP.setstatus(2);
					PP.setyearsincurrentstatus(0);  										//Setting this for consistency for project reaching new stag. This value is note used in later stages.
					PP.setstartyear(currentyear + PP.getminconstructionyears());			//Adding a startdate. Notice that this is done here rather than in the finalizeprojects.
					TheEnvironment.projectsunderconstruction.add(PP);						//Add to the Environment list of projects in process.
					TheEnvironment.projectsawaitinginvestmentdecision.remove(PP);			//Removing from Environment list of awaitinginvestmentsdecisions
					}
		}
				else {
					PP.setstatus(3);																	//For those whom are outside remove back to status 3 (also those that er 3 where never altered.
					}
			}
		else {
		PP.setstatus(3);																	//For those whom are outside remove back to status 3 (also those that er 3 where never altered.
		}
		}
		
}

	//Method updating projects to receive concession from status 4=in process to 3=awaiting investment decision. 
	//Currently randomized such that project can receive concession after a given number of years until a max. After that they are trash if they have not got concession.
	public static void receivingconcession() {
		
		int currenttick = TheEnvironment.theCalendar.getCurrentTick();
		int currentyear = TheEnvironment.theCalendar.getTimeBlock(currenttick).year + TheEnvironment.theCalendar.getStartYear();	//Get current year.
		ArrayList<PowerPlant> tempremoval = new ArrayList<PowerPlant>();															//Templist for removing projects.
		
		for (PowerPlant PP : TheEnvironment.projectinprocess) {
			PP.addyearsincurrentstatus(1);														//Increase number of years by one for all projects.
			
			//If trashable due to no concession in 7 years.
			int maxyearsinconcessionqueue = PP.getminyearinprocess() + AllVariables.maxyearsinconcessionqueue;     //Max years depends on the project estimated minconcession years.
			if (PP.getyearsincurrentstatus() >= maxyearsinconcessionqueue) { //Have not received concession in so many years, it will nevere get it.
				
				PP.setstatus(0);																//The project is trashed, hence status is 0.
				PP.setyearsincurrentstatus(0); 													//Updating status means clearing years with this status.
				TheEnvironment.trashedprojects.add(PP);											//Add to all operations powerplants
				tempremoval.add(PP);															//Add to removal list.

			}
			
			//If in the lottery for getting concession.
			if ((PP.getyearsincurrentstatus() >= PP.getminyearinprocess()) && (PP.getyearsincurrentstatus() < maxyearsinconcessionqueue)) {
				double rand = RandomHelper.nextDoubleFromTo(0.0, 1.0);
				if (rand <= AllVariables.annualprobforreceivingconcession)	{ 				//Tricksy way of having a given chance for receiving concession. If concession:
					
					PP.setstatus(3);															//The project is granted concession, hence status = 3.
					PP.setyearsincurrentstatus(0); 												//Updating status means clearing years with this status.
					TheEnvironment.projectsawaitinginvestmentdecision.add(PP);					//Add to all operations powerplants
					tempremoval.add(PP);

				}	}
			//No else
		}
		TheEnvironment.projectinprocess.removeAll(tempremoval);
		
	}
	
		public static void projectidentification() {
		
		//Ønsker å fordele prosjekter til DAs en etter en, helt til de har fylt opp kvoten sin

		//getnumprojectsinprocess()
		ArrayList<DeveloperAgent> identifyingdevelopers = new ArrayList<DeveloperAgent>();
		ArrayList<PowerPlant> tempremovelist = new ArrayList<PowerPlant>();

		int maxprojectidentifyed = 0;
		
		for (DeveloperAgent DA : CommonMethods.getDAgentList()) {
			if (DA.getnumprojectsinprocess() < DA.getprojectprocessandidylimit()) {
				identifyingdevelopers.add(DA);
				maxprojectidentifyed = maxprojectidentifyed + (DA.getprojectprocessandidylimit() - DA.getnumprojectsinprocess());
			}
		}
		int numberofdevelopers = identifyingdevelopers.size();
		int maxiterations = Math.min(maxprojectidentifyed, TheEnvironment.potentialprojects.size());	//Cannot identify more project than there are nor the DAs resourcess can allow or projects open for identification.
		int a = 0;
		for (int i = 0; i < maxiterations; i++) {
			DeveloperAgent DA = identifyingdevelopers.get(a);
			PowerPlant PA = TheEnvironment.potentialprojects.get(i);
			tempremovelist.add(PA);
			TheEnvironment.projectsidentifyed.add(PA);
			PA.setstatus(5);
			
			PA.setMyCompany(DA.getmycompany());
			DA.getmycompany().getmyprojects().add(PA);
			
			a = Math.min(a+1, numberofdevelopers-1);
	
		}
		TheEnvironment.potentialprojects.removeAll(tempremovelist);

	}
		
	//Method moving projects from identifyed to apply for concession. THis is in part equal to the method of start construcion, but with an premimum RRR and a different CAPEX due to learning.
	//HENCE the currentyer for the LERMC should be current + expected concession period +1 or +2.
	public static void startpreprojectandapplication() {
		
		int currenttick = TheEnvironment.theCalendar.getCurrentTick();
		int currentyear = TheEnvironment.theCalendar.getTimeBlock(currenttick).year + TheEnvironment.theCalendar.getStartYear(); //Get current year.
		
		for (DeveloperAgent DA : CommonMethods.getDAgentList()) {

			ArrayList<PowerPlant> templist = new ArrayList<PowerPlant>();
			
			//Collecting the projects that the DA have identrifyed
			for (PowerPlant PP : DA.getmyprojects()) {
				double usedRRR = AllVariables.earlystageInvestRRRAdjustFactor * PP.getspecificRRR();							 //Project specific multipled with earlistage corrctor.
				if (PP.getstatus() == 5) {																					     //5=Identyfied project.
					PP.addyearsincurrentstatus(1);																				 //Increasing number of years with this status.
					templist.add(PP);															
					PP.calculateLRMCandcertpriceneeded((currentyear+AllVariables.expectedyersinconcession), usedRRR, 3);		 //3=Using the forward power price in the region of the project.
					//Notice the "currentyear+AllVariables.expectedyersinconcession". The LRMC takes account for the years of construcion deciding the eligability for certifiates. Whereas the input here
					//takes account for the years in concessions. Adding this up the DA then takes account for both the years in concess
				}
				//No else. Projects with other statuses should not be keept.
			}
	
			//For each DeveloperAgent For all the relevant projects. Do the following:			
			Collections.sort(templist, new CommonMethods.customprojectcomparator());			//Sorting the of a DAs project awaiting from lowest certprie needed to highest cert price needed
			
			//All the cirteria variables for the investment decision
			double cutoffcertprice = DA.getmycompany().getcompanyanalysisagent().getmarketanalysisagent().getmarketprognosis().getlongrunpriceexpectatations(); 
			int maxnumberofprocess = DA.getprojectprocessandidylimit();
			int processprojects_counter = DA.getnumprojectsinprocess();							//Not newly updated required. Just that the Concession process is updated for this tick.
			int potentialprojects = templist.size();
			int projects_pointer = 0;															//To ensure that the loop is not longer than number of objects.
			
			//The critical investment decision.
			while ((processprojects_counter < maxnumberofprocess) &&  (projects_pointer < potentialprojects )) {
				if (templist.get(projects_pointer).getcertpriceneeded() <= cutoffcertprice) {	//Starting with the best, if its worth investing...apply for concession.
					
					PowerPlant thisplant = templist.get(projects_pointer);
					processprojects_counter = processprojects_counter + 1;						//Temporary (local) counter for projects in concessionqueue for the Developer.
					
					thisplant.setstatus(4);														//Changing status for the project (from 5=identyfied to 4=preconstruction&process).
					thisplant.setyearsincurrentstatus(0);  										//Setting this for consistency for project reaching new stag. This value is note used in later stages.
					TheEnvironment.projectinprocess.add(thisplant);								//Add to the Environment list of projects in process.
					TheEnvironment.projectsidentifyed.remove(thisplant);						//Removing from Environment list of awaitinginvestmentsdecisions
					projects_pointer++;
					//DA.
					}
				else {break;}																	//If the current project is not wort investing, the following are not either.
			}
		}}
	
	//Method updateing all endogenous variables for the DAagents.
	public static void updateDAgentsnumber() {
		
		double capacitydevorundrconstr=0;
		int numprojectstrashed=0;
		int numprojectsfinished=0;
		int numprojectsunderconstr =0;
		int numprojectsawaitingid=0;
		int numprojectsinprocess=0;
		int numprojectsidentyfied=0;
		
		for (DeveloperAgent DA : CommonMethods.getDAgentList()) {
			
			capacitydevorundrconstr=0;
			numprojectstrashed=0;
			numprojectsfinished=0;
			numprojectsunderconstr =0;
			numprojectsawaitingid=0;
			numprojectsinprocess=0;
			numprojectsidentyfied=0;
			
			for (PowerPlant PP : DA.getmyprojects()) {
				if (PP.getstatus() == 0) {
					numprojectstrashed = numprojectstrashed +1;}
				if (PP.getstatus() == 1) {
					numprojectsfinished = numprojectsfinished +1;
					capacitydevorundrconstr = capacitydevorundrconstr + PP.getCapacity();}
				if (PP.getstatus() == 2) {
					numprojectsunderconstr = numprojectsunderconstr +1;
					capacitydevorundrconstr = capacitydevorundrconstr + PP.getCapacity();}
				if (PP.getstatus() == 3) {
					numprojectsawaitingid = numprojectsawaitingid +1;}
				if (PP.getstatus() == 4) {
					numprojectsinprocess = numprojectsinprocess +1;}
				if (PP.getstatus() == 5) {
					numprojectsidentyfied = numprojectsidentyfied +1;}
			}
			DA.updateDAnumbers(capacitydevorundrconstr, numprojectstrashed, numprojectsfinished, numprojectsunderconstr, numprojectsawaitingid, numprojectsinprocess, numprojectsidentyfied);
		}
	}
	
	public static void calculateallIRRs() {
		for (PowerPlant PP : TheEnvironment.allPowerPlants) {
			if ((PP.gettechnologyid() == 2) && PP.getendyear() <= TheEnvironment.theCalendar.getEndYear()) {
				PP.calculateIRR();
			}
		}
	}
		

}

