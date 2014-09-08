/*
 * Version info:
 *    
 *     
 *     Last altered data: 20140823
 *     Made by: Karan Kathuria
 */
package nemmstrategy_shortterm;

import java.util.ArrayList;
import java.lang.*;

import nemmagents.ParentAgent;
import nemmenvironment.TheEnvironment;



public class GenericTactic {
	
	protected GenericStrategy myStrategy;
	protected double tacticutilityscore;
	protected ArrayList<HistoricTacticValue> historictacticvalues = new ArrayList<HistoricTacticValue>();
	protected int paramLearningMethod; // GJB LEARNING
	protected int NUMLEARNINGMETHODS; // GJB LEARNING
											// This has to be set in the constructor of each
											// subclass
	//This class could have had all the selloffers and buyoffers form the respective tactics...
	
	
	protected class HistoricTacticValue {
		 
			protected ArrayList<BuyOffer> tacticsbuyoffers;
			protected ArrayList<SellOffer> tacticselloffers; //with fixed length given as a parameter. 
			protected double tacticutilityscore;
			protected int tickID;
	 }
	 
	public GenericTactic() {};
	
	public BuyOffer getbuyofferone() {return null;} //All these methods are overridden by the respective subtactics hence they do return something
	public BuyOffer getbuyoffertwo() {return null;}

	public SellOffer getsellofferone() {return null;}
	public SellOffer getselloffertwo() {return null;}
	
	public ArrayList<BuyOffer> gettacticbuyoffers() {return null;}
	public ArrayList<SellOffer> gettacticselloffers() {return null;}
	
	public void setmyStrategy(GenericStrategy gs) {
		myStrategy = gs;
	}
	
	public GenericStrategy getmyStrategy() {
		return myStrategy;
	}
	
	public void updatetacticselloffers() {};
	public void updatetacticbuyoffers() {};
	public void updatetactictradeoffers() {};
	public void updatetacticutilityscore(double t) {tacticutilityscore = t;};
	public void addtactichistory() {};
	private void parameterLearning() {}; // GJB LEARNING
//	public double gettacticutilityscore() {return tacticutilityscore;}
	public ArrayList<HistoricTacticValue> gethistorictacticvalues() {
		return historictacticvalues;}
	public double[][] gettacticutilityscore(int numTicks) {
		// Returns  a 2-D array of length (numTicks,2). The row (first dim) indexes how many ticks
		// ago the data comes from (e.g. row 2 indexes the data from tick CurrentTick-2-1 (recall - indexes start
		// at 0)). The first column is the datapoints tickID, the second column is the utility.
		double[][] utilityScores;
		int nowTick = TheEnvironment.theCalendar.getCurrentTick();
		// ensure that you dont try to get data from ticks before tickID = 0
		int numTicksToGet = Math.min(nowTick+1,numTicks);
		utilityScores = new double[numTicksToGet][2];
		// return the last numTicksToGet utilities
		for (int i = 0; i < numTicksToGet; ++i) {
			int tickID = nowTick-i;
			double curUtil = historictacticvalues.get(tickID).tacticutilityscore;
			int curTickID = historictacticvalues.get(tickID).tickID;
			// raise error if the tick IDs do not match (it means that we have incorrectly set up
			// historictacticvalues
			if (curTickID != tickID){
				throw new IllegalArgumentException("Tick IDs do not match when trying to retrieve last tactic utility vals");
			}
			utilityScores[i][0]=tickID;
			utilityScores[i][1]=curUtil;
		}	
		
		return utilityScores;
	}

	// GJB LEARNING

	public int getParamLearningMethod() {
		return paramLearningMethod;
	}

	public void setParamLearningMethod(int paramLearningMethod) {
		if (paramLearningMethod <0 || paramLearningMethod > NUMLEARNINGMETHODS-1 ){
			throw new IllegalArgumentException("Illegal learning method");
		}
		
		this.paramLearningMethod = paramLearningMethod;
	}
	
	

	
}
