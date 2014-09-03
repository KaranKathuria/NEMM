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
import nemmagents.ParentAgent;
import nemmenvironment.TheEnvironment;
import nemmprocesses.ShortTermMarket;
import nemmprocesses.UpdatePhysicalPosition;
import nemmprocesses.UtilitiesStrategiesTactics;
import nemmprocesses.DistributePowerPlants;
import nemmprocesses.DistributeDemandShares;
import static nemmcommons.ParameterWrapper.*;


//========================================================================
//=== Building and initilizing the model =================================

public class NEMMContextBuilder extends DefaultContext<Object> 
		implements ContextBuilder<Object> {
	
	@Override
	public Context<Object> build(final Context<Object> context) {
		//
		//Initialize parameters
		ParameterWrapper.reinit(); //Reads the parametervalues provided
		//Create the Environment
		TheEnvironment.GlobalValues.initglobalvalues(); //initiates the global values such as price by giving them the parametervalues from the above method. 
		TheEnvironment.InitEnvironment();
		TheEnvironment.PopulateEnvironment();
	
		// Creates the Agents and adds them to context
		for (int i = 0; i < getproduceragentsnumber(); ++i) {
			final CompanyAgent agent = new CompanyAgent(true, false, false);
			context.add(agent);}

		for (int i = 0; i < getobligatedpurchaseragentsnumber(); ++i) {
			final CompanyAgent agent = new CompanyAgent(false, true, false);
			context.add(agent);}
		
		for (int i = 0; i < gettraderagentsnumber(); ++i) {
			final CompanyAgent agent = new CompanyAgent(false, false, true);
			context.add(agent);}
		
		//Distributing of Power Plants and Demand Shares among the Agents are taken in the annual schedual
			
 return context;}
	
// ========================================================================
// === Simulation schedule ===========================================================

	//The annual update of the project process as descried in the model specification. 
@ScheduledMethod(start = 1, priority = 3)
	public void Distributions() {
	DistributePowerPlants.distributeallpowerplants();
	DistributeDemandShares.Uniformdemanddistribution(5, 5);
}
	//The monthly update
@ScheduledMethod(start = 1, interval = 1, priority = 1)
public void monthlymarketschedule() {
	
	ShortTermMarket.runshorttermmarket(); //updates all offers for all agents strategies and clears the market based on the best strategies , best tactics offers. 
	UpdatePhysicalPosition.markettransactions();//updates the market outcomes and hence the physical position for all agents based on what they bid into the market
	//UpdatePhysicalPosition.runproduction(); //Loops to all powerplants and adds this ticks prodution to the CompanyAgents producers agents physical position. 
	//UpdatePhysicalPosition.updatedemand(); //Adds demand to the CompanyAgents physicalposition
	UtilitiesStrategiesTactics.calculatetilitiesandupdatebesttactics(); //Calculates the tactic and strategies utilities and changes the best tactics. 
	TheEnvironment.GlobalValues.monthlyglobalvalueupdate();
}

	// the lates monthly update is "hidden" for this update. 
@ScheduledMethod(start = 1, interval = 12, priority = 2)
public void annualmarketschedule() {
	//Priority 2 means that whenever the tick is 12 (annual tick) this will be ran first. If the priority is the same, the order is random. 
		TheEnvironment.GlobalValues.annualglobalvalueupdate();
		
}

	//Distributing of Power Plants and Demand Shares
@ScheduledMethod(start = 1, interval = 36, priority = 0)
public void projectprocesschedule() {
	TheEnvironment.GlobalValues.marketshock();
	
	
	}

// ========================================================================
// === Observer Methods ===================================================

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
	public double gettradersphysicalposition() {
		return TheEnvironment.GlobalValues.tradersphysicalposition;
	}
	public double getobligatedpurchaserssphysicalposition() {
		return TheEnvironment.GlobalValues.obligatedpurchasersphysiclaposition;
	}
	public double gettotalmarketphysicalposition() {
		return TheEnvironment.GlobalValues.totalmarketphysicalposition;
	}
	
}


