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
import nemmagents.ActiveAgent;
import nemmprocesses.ShortTermMarket;
import static nemmcommons.ParameterWrapper.*;



//Class definitions
public class NEMMContextBuilder extends DefaultContext<Object> 
		implements ContextBuilder<Object> {
	
	@Override
	public Context<Object> build(final Context<Object> context) {
		//
		//Create World
		ParameterWrapper.reinit(); //Reads the parametervalues provided
		GlobalValues.initglobalvalues(); //initiates the global values such as price by giving them the parametervalues from the above method. 
	

	
// Adds the supply side agents (ProducerAgent), with one strategy calling PABidstrategy(). 
for (int i = 0; i < getproduceragentsnumber(); ++i) {
	final CompanyAgent agent = new CompanyAgent(true, false, false);
	context.add(agent);
}
// Adds ObligatedPurchaserAgents which is the demand side and calling OPABidstrategy().
for (int i = 0; i < getobligatedpurchaseragentsnumber(); ++i) {
	final CompanyAgent agent = new CompanyAgent(false, true, false);
	context.add(agent);
}

 	return context;
}


// ========================================================================
// === Simulation schedule ===========================================================

/* Main schedules of the simulation containing the following phases:
 * The monthly market updates and trading in the stm-market
 * The annual ltm market and under values
 * The development market
 * 
 * The priority argument tell Repast how important the function is. If there are lots of methods that can be called at the same iteration, 
 * Repast chooses the ones with the highest priority first. If lots of methods have the same priority then Repast chooses their order randomly.
 */

	//The monthly update. Updates the monthly market, interest rates etc.
@ScheduledMethod(start = 1, interval = 1, priority = 1)
public void monthlymarketschedule() {

	ShortTermMarket.runshorttermmarket();
	GlobalValues.monthlyglobalvalueupdate();
	//Some method that updates the analysisagents price expectations
	//Some method that updates the agents bank, caputal and priceexpectations (from AA). Could also update their preffered strategy and Analysis. 
	
}
	//The annual market update. Updates the long term market, interest rates etc, annually (thats why interval = 12). Notice as this i running in the same "tick" as the montly update
	// the lates monthly update is "hidden" for this update. 
@ScheduledMethod(start = 1, interval = 12, priority = 2)
public void annualmarketschedule() {
	//Priority 2 means that whenever the tick is 12 (annual tick) this will be ran first. If the priority is the same, the order is random. 
		GlobalValues.annualglobalvalueupdate();
		
}

	//The annual update of the project process as descried in the model specification. 
@ScheduledMethod(start = 1, interval = 12, priority = 0)
public void projectprocesschedule() {
				// For updating the project process. Values form this can be displayed in a histogram-chart.

	}

// ========================================================================
// === Observer Methods ===================================================

/* Returns the current interesting values to display in a simple
 * time series chart on the Repast GUI.
 * 
 * @return for exampel the price of certificates  */

	public double currentmarketprice() {
		return GlobalValues.currentmarketprice;
}
	public double currentinterestrate() {
		return GlobalValues.currentinterestrate;
}	
	public int numberofbuyoffers() {
		return GlobalValues.numberofbuyoffersstm;
	}
	public int numberofselloffers() {
		return GlobalValues.numberofselloffersstm;
	}
	
	public double endofyearpluss1() {
		return GlobalValues.endofyearpluss1;
}	
	public double endofyearpluss2() {
		return GlobalValues.endofyearpluss2;
}
	public double endofyearpluss3() {
		return GlobalValues.endofyearpluss3;
}	
	public double endofyearpluss4() {
		return GlobalValues.endofyearpluss4;
}
public double endofyearpluss5() {
		return GlobalValues.endofyearpluss5;
}
	public int numberofAgents() {
		return CommonMethods.getnumberofagents();
}

	
}


