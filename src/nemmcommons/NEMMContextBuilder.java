/*
 * Version info:
 *     File defining the context, e.g the main function which builds and makes the "rules" for the model
 *     Last altered data: 20140721
 *     Made by: Karan Kathuria
 */
package nemmcommons;

//Import field

import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.schedule.ScheduledMethod;
import nemmagents.CompanyAgent;
import nemmenvironment.FundamentalMarketAnalysis;
import nemmenvironment.TheEnvironment;
import nemmprocesses.DistributeProjectsandPowerPlants;
import nemmprocesses.Forcast;
import nemmprocesses.ProjectDevelopment;
import nemmprocesses.ShortTermMarket;
import nemmprocesses.UpdatePhysicalPosition;
import nemmprocesses.UtilitiesStrategiesTactics;
import nemmprocesses.DistributeDemandShares;
import static nemmcommons.ParameterWrapper.*;


//================================================================================================================================================================================================
//=== Building and initilizing the model =========================================================================================================================================================

public class NEMMContextBuilder extends DefaultContext<Object> 
		implements ContextBuilder<Object> {
	
	@Override
	public Context<Object> build(final Context<Object> context) {

		//Reads in parameters from the user interface
		ParameterWrapper.reinit(); 														//Reads the parametervalues provided in the user interface
		
		//Create the Environment
		TheEnvironment.InitEnvironment(); 												//Initiates ReadExcel, creates time and adds empty lists of Powerplants, projects and regions. Also initiates GlobalValues, the publicliy available market information in the model
		TheEnvironment.PopulateEnvironment(); 											//initiates ReadExcel and reads in region data (demand and power price) and Powerplant and projects data 
		
		//Adds Agents to context. Notice that Agents specific data is not read in and only given through constructors.
		for (int i = 0; i < getproduceragentsnumber(); ++i) {
			final CompanyAgent agent = new CompanyAgent(true, false, false);
			context.add(agent);}

		for (int i = 0; i < getobligatedpurchaseragentsnumber(); ++i) {
			final CompanyAgent agent = new CompanyAgent(false, true, false);
			context.add(agent);}
		
		for (int i = 0; i < gettraderagentsnumber(); ++i) {
			final CompanyAgent agent = new CompanyAgent(false, false, true);
			context.add(agent);}
			
 return context;}
	
	//Distribution and initiation 
	@ScheduledMethod(start = 0, priority = 3)
	public void DistributionandInitiation() {
		
	//Distributing Plants, projects and demand among Agents
	DistributeProjectsandPowerPlants.distributeallpowerplants(AllVariables.powerplantdistributioncode);	 //Distribute all PowerPlants among the Copmanies with PAgents.
	DistributeProjectsandPowerPlants.distributeprojects(AllVariables.projectsdistributioncode);			 //Distribute all Projects among the Companies with DAgents.
	DistributeDemandShares.distributedemand(AllVariables.demandsharedistrubutioncode);					 //Distribute all demand among the Companies with OPAgents.
	
	Forcast.initiatevolumeprognosis(); 																	 //Initiate MarketAnalysisagents and Volumeanalysisagents prognosis based om expected prodution for the future year
	FundamentalMarketAnalysis.runfundamentalmarketanalysis();											 //
	Forcast.updateMPEandLPE();																			 //Takes the result from the FMA and sets the MAA`s MPE and LPE according to that. 
	ProjectDevelopment.startconstrucion();																 //Take 
	ProjectDevelopment.startpreprojectandapplication();	 
}
	
// ============================================================================================================================================================================================
// === Simulation schedule ====================================================================================================================================================================


//All annual updates to come below. Currently not in use.
@ScheduledMethod(start = 12, interval = 12, priority = 2)		//Priority 2 means that whenever the tick is 12 this will be ran first. If the priority is the same, the order is random.
public void annualmarketschedule() {
	FundamentalMarketAnalysis.runfundamentalmarketanalysis();							
	Forcast.updateMPEandLPE();									 //Takes the result from the FMA and sets the MAA`s MPE and LPE according to that. 
	
	ProjectDevelopment.finalizeprojects();						//Updating projects that are finished. All starting at start are already started, hence start=12.
	ProjectDevelopment.receivingconcession();					//As this is given an not dependent on other stages. Starting with adding on year in this status.
	ProjectDevelopment.updateDAgentsnumber();					//Need to update DA number before taking decisions on projects to invest in.
	ProjectDevelopment.startconstrucion();						//The investment decision. This is ran after "receiveconcession", hence projects and investment decision can done the same year.
	ProjectDevelopment.startpreprojectandapplication();	        //The process of deciding which project to apply for concession. In the same manner as start construction
//	ProjectDevelopment.identifyprojects();						//Given how many projects the DA has in concession-stage and the limit, receice new projects.
	ProjectDevelopment.updateDAgentsnumber();					//Not really needed at end, but okey for displaypurposes.
	
}

//The monthly update
@ScheduledMethod(start = 0, interval = 1, priority = 1)
public void monthlymarketschedule() {
	
	//First the schedual updates all offers for all agents strategies and clears the market based on the best strategies, best tactics offers. Calcualtes market price.
	ShortTermMarket.runshorttermmarket(); 
	//All agents strategies utilities are scored. Tactics learn and the best strategies update their best tactics. This is done before the physical update as the intial physical position is part of the utility calcualtion.
	UtilitiesStrategiesTactics.calculatetilitiesandupdatebesttactics(); 
	UpdatePhysicalPosition.updateAllAgentPositions();
	
	//Reads the values to the global values arrays. Also calcualtes display values.
	TheEnvironment.GlobalValues.monthlyglobalvalueupdate();
	
	//Update the analysis agents forecasts. Must run after global values are updated as it uses the array of certprices
	Forcast.updatevolumeprognosis();
	Forcast.updatemarketprognosis();
}

//All obligation periods updates to come below. Priority 2 says this is done before the monthlymaret schedual.
@ScheduledMethod(start = 1, interval = AllVariables.obintr, priority = 2)
public void obligationsperiodshedule() {
 //Should for each obligations period ending sum up all demand of certificates, calculate the penelty price and "blanked out"
	//Also calculating the total demand and supply for the period at hand can be "official" news that the volume and market analyss agents can use. 
	//Later the fact that the OPA knows that it will be rewarded a penelty if not having enough certs, they will use this in their strategy.
	
	//The volume analysis agents "have" the expectaito informaston, and the real prouton and demand is in the PP and Region objects. Could nevertheless be calculated her as well.
	// - This obperiods demand
	// - THis OB periods supply
	// - This OB volumwheigted average price. 
	// - Peneltyprice  .. the opa agents could have a field "expected" penelty price that they use to calculate their penelty. 
	
}

//@ScheduledMethod(start = 0, interval = 24, priority = 0)
//public void projectprocesschedule() {
//	ParameterWrapper.reinit();
//	TheEnvironment.GlobalValues.marketshock();
//	}










//============================================================================================================================================================================================
// === Observer Methods ======================================================================================================================================================================

/* Returns the current interesting values to display in a simple
 * time series chart on the Repast GUI. */

	public double currentmarketprice() {
		return ShortTermMarket.getcurrentmarketprice();
}
	public double currentinterestrate() {
		return TheEnvironment.GlobalValues.currentinterestrate;
}	
	public int numberofbuyoffers() {
		return TheEnvironment.GlobalValues.numberofbuyoffersstm;
	}
	public int numberofselloffers() {
		return TheEnvironment.GlobalValues.numberofselloffersstm;
	}
	public double marketdemand() {
		return ShortTermMarket.getmarketdemand();
	}
	public double marketsupply() {
		return ShortTermMarket.getmarketsupply();
	}
	public double STMtradedvolume() {
		return ShortTermMarket.gettradedvolume();
	}
	
	public double getproducersphysicalposition() {
		return TheEnvironment.GlobalValues.producersphysicalposition;
	}
	public double gettotaltickproducion() {
		return TheEnvironment.GlobalValues.totaltickproduction;
	}	
	public double gettradersphysicalposition() {
		return TheEnvironment.GlobalValues.tradersphysicalposition;
	}
	public double getobligatedpurchaserssphysicalposition() {
		return TheEnvironment.GlobalValues.obligatedpurchasersphysiclaposition;
	}
	public double gettotaltickdemand() {
		return TheEnvironment.GlobalValues.totaltickdemand;
	}
	public double gettotalmarketphysicalposition() {
		return TheEnvironment.GlobalValues.totalmarketphysicalposition;
	}
	public double getticksupplyanddemandbalance() {
		return TheEnvironment.GlobalValues.ticksupplyanddemandbalance;
	}
	public int gettotalnumberofpowerplantsinoperation() {
		return TheEnvironment.allPowerPlants.size();
	}
	public int gettotalnumberofprojectsunderconstruction() {
		return TheEnvironment.projectsunderconstruction.size();
	}
	public int gettotalnumberofprojectsawatingID() {
		return TheEnvironment.projectsawaitinginvestmentdecision.size();
	}
	public int gettotalnumberofprojectsinconcessionqueue() {
		return TheEnvironment.projectinprocess.size();
	}
	public int gettotalnumberofprojectsidentifyed() {
		return TheEnvironment.projectsidentifyed.size();
	}
	public int gettotalnumberofpotentialprojects() {
		return TheEnvironment.potentialprojects.size();
	}
	public int gettotalnumberofprojectstrashed() {
		if (TheEnvironment.trashedprojects.isEmpty()) {
		return 0;}
		return TheEnvironment.trashedprojects.size();
	}
	public int gettotalnumberofpowerplantandprojeects() {
		int temp = TheEnvironment.trashedprojects.size() + TheEnvironment.potentialprojects.size() + TheEnvironment.projectsidentifyed.size() + TheEnvironment.projectinprocess.size() + TheEnvironment.projectsawaitinginvestmentdecision.size() +
				TheEnvironment.allPowerPlants.size() + TheEnvironment.projectsunderconstruction.size();
		return temp;
	}
	
	
	//The flowing methods gets the buy and sell offer prices of the last PA and OPA agents best strategy, and first tactic.
	//The getbest also gets the variable offer of the best tactic. 
	public double getalltacticbuyoffer1() {
		double ret = ShortTermMarket.getbuyoffer1();
		return ret;
	}
	public double getalltacticbuyoffer2() {
		double ret = ShortTermMarket.getbuyoffer2()[9];
		return ret;
	}
	public double getbestbuyoffer2() {
		double ret = ShortTermMarket.getbestbuyoffer2();
		return ret;
	}
	public double getalltacticselloffer1() {
		double ret = ShortTermMarket.getselloffer1();
		return ret;
	}
	public double getalltacticselloffer2() {
		double ret = ShortTermMarket.getselloffer2()[9];
		return ret;
	}
	public double getbestselloffer2() {
		double ret = ShortTermMarket.getbestselloffer2();
		return ret;
	}
	public double getpriceexpetations() {
		double ret = CommonMethods.getMAAgentList().get(0).getmarketprognosis().getstpriceexpectation();
		return ret;
	}
	public double getfloor() {
		return ShortTermMarket.getfloor();
	}
	public double getroof() {
		return ShortTermMarket.getroof();
	}
	
// TO get all sell and buyoffers (with one tactic):
	
	public double getallvariablebuyoffers0() {
		double ret = ShortTermMarket.getbuyoffer2()[0];
		return ret;
	}
	public double getallvariableselloffers0() {
		double ret = ShortTermMarket.getselloffer2()[0];
		return ret;
	}
	public double getallvariablebuyoffers1() {
		double ret = ShortTermMarket.getbuyoffer2()[1];
		return ret;
	}
	public double getallvariableselloffers1() {
		double ret = ShortTermMarket.getselloffer2()[1];
		return ret;
	}
	public double getallvariablebuyoffers2() {
		double ret = ShortTermMarket.getbuyoffer2()[2];
		return ret;
	}
	public double getallvariableselloffers2() {
		double ret = ShortTermMarket.getselloffer2()[2];
		return ret;
	}
	public double getallvariablebuyoffers3() {
		double ret = ShortTermMarket.getbuyoffer2()[3];
		return ret;
	}
	public double getallvariableselloffers3() {
		double ret = ShortTermMarket.getselloffer2()[3];
		return ret;
	}
	public double getallvariablebuyoffers4() {
		double ret = ShortTermMarket.getbuyoffer2()[4];
		return ret;
	}
	public double getallvariableselloffers4() {
		double ret = ShortTermMarket.getselloffer2()[4];
		return ret;
	}
	public double getallvariablebuyoffers5() {
		double ret = ShortTermMarket.getbuyoffer2()[5];
		return ret;
	}
	public double getallvariableselloffers5() {
		double ret = ShortTermMarket.getselloffer2()[5];
		return ret;
	}
	public double getallvariablebuyoffers6() {
		double ret = ShortTermMarket.getbuyoffer2()[6];
		return ret;
	}
	public double getallvariableselloffers6() {
		double ret = ShortTermMarket.getselloffer2()[6];
		return ret;
	}
	public double getallvariablebuyoffers7() {
		double ret = ShortTermMarket.getbuyoffer2()[7];
		return ret;
	}
	public double getallvariableselloffers7() {
		double ret = ShortTermMarket.getselloffer2()[7];
		return ret;
	}
	public double getallvariablebuyoffers8() {
		double ret = ShortTermMarket.getbuyoffer2()[8];
		return ret;
	}
	public double getallvariableselloffers8() {
		double ret = ShortTermMarket.getselloffer2()[8];
		return ret;
	}
	public double getallvariablebuyoffers9() {
		double ret = ShortTermMarket.getbuyoffer2()[9];
		return ret;
	}
	public double getallvariableselloffers9() {
		double ret = ShortTermMarket.getselloffer2()[9];
		return ret;
	}
	public double getmedimutermprice() {
		double ret = FundamentalMarketAnalysis.getMPE();
		return ret;
	}
	public double getlongtermprice() {
		double ret = FundamentalMarketAnalysis.getLPE();
		return ret;
	}
	public double getcurrentpowerprice_N() {
		return TheEnvironment.allRegions.get(0).getMyPowerPrice().getValue();
	}
	
	public int getbidsovermp() {
		int over = 0;
		//int under = 0;
		double mp = ShortTermMarket.getcurrentmarketprice();
		for (int i = 0; i <10; ++i) {
			if (ShortTermMarket.getselloffer2()[i] > mp) {
				over++;
			}
			if (ShortTermMarket.getbuyoffer2()[i] > mp) {
				over++;
			}
		}
		return over;
		}
	
	public int getbidsundermp() {
		int under = 0;
		double mp = ShortTermMarket.getcurrentmarketprice();
		for (int i = 0; i <10; ++i) {
			if (ShortTermMarket.getselloffer2()[i] < mp) {
				under++;
			}
			if (ShortTermMarket.getbuyoffer2()[i] < mp) {
				under++;
			}
		}
		return under;
		}
	
}


