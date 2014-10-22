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
	protected int numberoflearningmethods; 
	protected double floorroofprice;
	protected double maxppvolume; //calculated based on the numberof months given from strategy
	protected double maxoffervolume; //calculated in tactics based on internal number and last months production
	protected double maxoffervolumemultiplier;
	protected double deltapricemultiplier; //Used for learning and adjusting bid prices (currenly by PA and OPA only). 
	
	//This class could have had all the selloffers and buyoffers form the respective tactics...
	
// ---- INNER CLASSES
	
	protected class HistoricTacticValue {
		 	// The tactic's memory - used to store buy & sell offers and utility scores
			protected ArrayList<BuyOffer> tacticsbuyoffers;
			protected ArrayList<SellOffer> tacticselloffers; //with fixed length given as a parameter. 
			protected double tacticutilityscore;
			protected int tickID;
	 }
	
// ---- CONSTRUCTOR	
	 
	public GenericTactic() {};
	
// -- GETS & SETS	
	
	public ArrayList<HistoricTacticValue> gethistorictacticvalues() {
		return historictacticvalues;}
	
	public BuyOffer getbuyofferone() {return null;} //All these methods are overridden by the respective subtactics hence they do return something
	public BuyOffer getbuyoffertwo() {return null;}
	public double getfloorroofprice() {return floorroofprice;}

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
	
	public void setdeltapricemultiplier(double t) {
		deltapricemultiplier = t;
	}
	
//	public double gettacticutilityscore() {return tacticutilityscore;}
	
// ---- COMPLETELY UPDATE THE TACTIC
	
	// Performs all updating for the tactic, including
	//  -- Calculating the utility
	//  -- Parameter learning
	//  -- Stores the current bids & offers in the tactics memory
	// This will be called by the strategy when it wants the tactic to be updated (that is, every tick)
	public void updateTactic() {
		UpdateUtilityScore();
		parameterLearning();
		addtactichistory();
	};
	
// ---- UPDATE OFFERS AND BIDS	
	
	public void updatetacticselloffers() {};
	public void updatetacticbuyoffers() {};
	public void updatetactictradeoffers() {};
	
// ---- TACTIC MEMORY	
	
	public void addtactichistory() {};

// ---- UTILITIES	
	
	public void UpdateUtilityScore() {
		// Code to update the tactic's utility value
	}
	
	public void updatetacticutilityscore(double t) {tacticutilityscore = t;};
	


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

// ---- LEARNING

	private void parameterLearning() {};
	
	public int getParamLearningMethod() {
		return paramLearningMethod;
	}

	public void setParamLearningMethod(int paramLearningMethod) {
		if (paramLearningMethod <0 || paramLearningMethod > numberoflearningmethods-1 ){
			throw new IllegalArgumentException("Illegal learning method");
		}
		
		this.paramLearningMethod = paramLearningMethod;
	}
	
	

	
}
