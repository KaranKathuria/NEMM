/*
 * Version info:
 *     File defining a generic abstract (parent) class strategies. A Strategy is a way of forming buy and sell offers based on you physical position. 
 *     The parent agent 
 *     
 *     Last altered data: 20140811
 *     Made by: Karan Kathuria
 */
package nemmstrategy_shortterm;

import java.util.ArrayList;
import java.util.Collections;
import repast.simphony.random.RandomHelper;
import nemmagents.CompanyAgent.ActiveAgent;
import nemmcommons.AllVariables;
import nemmcommons.CommonMethods;
import nemmenvironment.TheEnvironment;

// 
public abstract class GenericStrategy {
	
	//Variables. Notice that the generic strategy only contains the array of buy and sell offers and not the buy or sell offers itself because the number of these vary with the strategy. 
	protected ActiveAgent myAgent;
	protected ArrayList<BidOffer> agentsbuyoffers = new ArrayList<BidOffer>();
	protected ArrayList<BidOffer> agentsselloffers = new ArrayList<BidOffer>();
	protected int numberoftactics;
	protected int numberoftactics_additional;
	protected ArrayList<Double> strategyutilityscore = new ArrayList<Double>();
	protected String strategyname; 
	protected ArrayList<GenericTactic> alltactics = new ArrayList<GenericTactic>();
	protected ArrayList<GenericTactic> alltactics_additional = new ArrayList<GenericTactic>();

	protected GenericTactic besttactic = null;
	protected GenericTactic besttactic_additional = null;

	
	protected int floorroofpricemultiplier; //Thus this strategy us a floor/roof [zero or one]
	protected int numberofmonthsmaxpp; //This means that the maximum pp equalt this number of months expected demand or production.
	protected int myPreferenceScore; // used to specify how conservative the strategy is in selecting a new tactic
										 // The higher the number, the more conservative (that is, higher the chance the strategy
										// will select the tactic with the best utility rather than try something else
	
// ---- INNER CLASSES
	
	protected class TacticUtilityListElement implements Comparable<TacticUtilityListElement> {
		 	// Used to record, rank, weight and select tactics
			protected int tacticID; // ID of the tactic in the strategy's tactic list
			protected GenericTactic tacticPointer; // pointer to the tactic
			protected double tacticUtility; // Utility for the tactic
			protected double tacticWeight;
			protected double tacticProbabilityCutoff;
			@Override
			public int compareTo(TacticUtilityListElement o) {
				// Compare based on utility
				int lastCmp;
				if (tacticUtility == o.tacticUtility) {lastCmp = 0;}
				else if (tacticUtility < o.tacticUtility) {lastCmp = -1;}
				else {lastCmp = 1;}
				return lastCmp;
			}
	 }	
	
// ---- CONSTRUCTOR	

	//Constructor for parant class. Not sure about this. This construction will note be used as this class is abstract. 
	public GenericStrategy() {
		// Specify the tacticPreferenceScore
		//Random generator = new Random(); 

		myPreferenceScore = RandomHelper.nextIntFromTo(0, AllVariables.MaxTacticPreferenceScore - AllVariables.MinTacticPreferenceScore) + AllVariables.MinTacticPreferenceScore;
		//myPreferenceScore = generator.nextInt(AllVariables.MaxTacticPreferenceScore - 
		//												AllVariables.MinTacticPreferenceScore+1) 
	}
			
// ---- GETS & SETS	
	
	public ArrayList<BidOffer> getAgentsBuyOffers() {
		return agentsbuyoffers;
	}
	
	public ArrayList<BidOffer> getAgentsSellOffers() {
		return agentsselloffers;}
	
	public void setmyAgent(ActiveAgent aa) {
		myAgent = aa;}
	
	public ActiveAgent getmyAgent() {
		return myAgent;}
	
	public int getnumberoftactics() {
		return numberoftactics;}
	
	public GenericTactic getbesttactic() {return besttactic;}
	
	public ArrayList<GenericTactic> getalltactics() {
		
		return alltactics;}	
	
	

// ----	UPDATE STRATEGY
	
	// Three things to update:
	//  - the bids and offers for all the strategy's tactics
	//  - the utilities and parameters for all the strategy's tactics
	//  - the strategy's best tactic (and utility)
	
	//This is later overwritten by the specific strategy objects
	public void updateBidsAndOffers() {};
	
	public void updateStrategy() { 
		// First update all the tactics using their update function
		for (GenericTactic tactic : alltactics) { 
			tactic.updateTactic();
		}
		// Then update the best tactic
		updateBestTactic();
		
		//Added 20150703 for the _additional to support traders reusing producers and purchasers tactics.
		if (alltactics_additional.size() > 0) {
		for (GenericTactic tactic : alltactics_additional) { 
			tactic.updateTactic();
		}
		// And the best additional tactic (for the trader selling) added 20150703 KK
		updateBestTactic_additional();
		}
		
	}
	
	public void updateBestTacticOLD() {
		// Loop through the tactics and find the one with the highest utility
		double candBestUtilityScore = -10000000000000.0;
		double candBestUtility = 0.0;
		GenericTactic candBestTactic = new GenericTactic();
		candBestTactic = null;
		double curUtilityScore;
		int numPdsInUtilCalc = 1;
		double[][] curUtilityArray;
		for (GenericTactic curTactic : alltactics) { 
			curUtilityScore = 0;
			// Sums the utility for the previous X ticks
			numPdsInUtilCalc = Math.min(AllVariables.numofhistutilitiesincluded, TheEnvironment.theCalendar.getCurrentTick()+1);
			curUtilityArray = curTactic.getUtilityScore(AllVariables.numofhistutilitiesincluded);
			for (int i = 0; i < numPdsInUtilCalc;++i) {
				curUtilityScore = curUtilityScore + curUtilityArray[i][1];
			}
			// If the utility is the largest so far, store it and the tactic
			if (curUtilityScore >= candBestUtilityScore) {
				candBestUtilityScore = curUtilityScore;
				candBestTactic = curTactic;
				candBestUtility = curUtilityArray[0][1]; // the utility for the current tick (rather than the summed utility score)
			}
		}
		// Store the utility from the current tick of the best tactic,
		// in the strategy (this is also the strategy's utility)
		strategyutilityscore.add(candBestUtility); 
		// Update the best tactic pointer
		//Note that the strategy utility is set BEFORE the best tactic is updated! This is form the formerly best tactics utility we want to write. 
		besttactic = candBestTactic;	

	};		
	public void updateBestTactic() {
		// Use a genetic algorithm-type approach to select the best tactic
		// How this works:
		// Rank the tactics from high to low utility
		// Use a "weighting function" to calculate a weight for each tactic, with
		// good utilities weighted highly, and poor utilities lowly
		// Convert the weightings into probabilities
		// Randomly choose a tactic using the probabilities
		
		// Create a list with the tactic and utility info
		ArrayList<TacticUtilityListElement> tacticList = new ArrayList<TacticUtilityListElement>();
		TacticUtilityListElement listElement;
		double curUtilityScore;
		int numPdsInUtilCalc;
		int tacticID;
		double[][] curUtilityArray;
		double sumWeights = 0.0;
		double tmpProb;
		double tmpCumProb=0.0;
		for (GenericTactic curTactic : alltactics) { 
			listElement = new TacticUtilityListElement();
			listElement.tacticPointer = curTactic;
			curUtilityScore = 0;
			// Sums the utility for the previous X ticks
			numPdsInUtilCalc = Math.min(AllVariables.numofhistutilitiesincluded, TheEnvironment.theCalendar.getCurrentTick()+1);
			curUtilityArray = curTactic.getUtilityScore(AllVariables.numofhistutilitiesincluded);
			for (int i = 0; i < numPdsInUtilCalc;++i) {
				curUtilityScore = curUtilityScore + curUtilityArray[i][1];
			}
			listElement.tacticUtility = curUtilityScore;
			tacticList.add(listElement);
		}
		if(TheEnvironment.theCalendar.getCurrentTick()>0){
			int tmp1 = 1;
			tmp1 = 2;
		}
		// Rank the tactics from high to low utility		
		Collections.sort(tacticList); // sort using the TacticUtilityListElement comparator (see code above)
		// Calculate the weights using the weight function
		for (int i = 0; i< tacticList.size(); i++) { 
			listElement = tacticList.get(i);
			listElement.tacticWeight = (1/myPreferenceScore)^(i-1);
			sumWeights = sumWeights + listElement.tacticWeight;			
		}
		// Calculate the probability cutoff. Idea is as follows:
		// Let p = probability for current tactic, and Z be the cumulative probabilities for all
		// tactics with a better utility than the current. 
		// We store Z+p for the current tactic as the probability cut off
		// Let x be a random number between 0 & 1
		// Then i choose that tactic with the lowest cut off greater than x.
		for(int i = 0; i< tacticList.size(); i++) {
			listElement = tacticList.get(i);
			tmpProb = listElement.tacticWeight/sumWeights;
			tmpCumProb = tmpCumProb + tmpProb;
			listElement.tacticProbabilityCutoff = tmpCumProb;
		}
		
		// Randomly choose the tactic to use next time
		//Random generator = new Random(); 
		double randX = RandomHelper.nextDouble(); //generator.nextDouble();
		int keeplooking = 1;
		int curIndex=0;
		besttactic = tacticList.get(0).tacticPointer; // Default chose the utility with the best tactic
		while(keeplooking == 1) {
			listElement = tacticList.get(curIndex);
			if (listElement.tacticProbabilityCutoff >= randX) {
				// use this one
				besttactic = listElement.tacticPointer;
				keeplooking = 0;
			}
			curIndex++;
		}
		// Store the utility from the current tick of the best tactic,
		// in the strategy (this is also the strategy's utility)
		strategyutilityscore.add(besttactic.getUtilityScore(1)[0][1]); 

	}
	
	public void updateBestTactic_additional() {
		// Just copied from updateBestTactic, but altered the list off alltactics to alltactics_additional
				
				// Create a list with the tactic and utility info
				ArrayList<TacticUtilityListElement> tacticList = new ArrayList<TacticUtilityListElement>();
				TacticUtilityListElement listElement;
				double curUtilityScore;
				int numPdsInUtilCalc;
				int tacticID;
				double[][] curUtilityArray;
				double sumWeights = 0.0;
				double tmpProb;
				double tmpCumProb=0.0;
				for (GenericTactic curTactic : alltactics_additional) { 
					listElement = new TacticUtilityListElement();
					listElement.tacticPointer = curTactic;
					curUtilityScore = 0;
					// Sums the utility for the previous X ticks
					numPdsInUtilCalc = Math.min(AllVariables.numofhistutilitiesincluded, TheEnvironment.theCalendar.getCurrentTick()+1);
					curUtilityArray = curTactic.getUtilityScore(AllVariables.numofhistutilitiesincluded);
					for (int i = 0; i < numPdsInUtilCalc;++i) {
						curUtilityScore = curUtilityScore + curUtilityArray[i][1];
					}
					listElement.tacticUtility = curUtilityScore;
					tacticList.add(listElement);
				}
				if(TheEnvironment.theCalendar.getCurrentTick()>0){
					int tmp1 = 1;
					tmp1 = 2;
				}
				// Rank the tactics from high to low utility		
				Collections.sort(tacticList); // sort using the TacticUtilityListElement comparator (see code above)
				// Calculate the weights using the weight function
				for (int i = 0; i< tacticList.size(); i++) { 
					listElement = tacticList.get(i);
					listElement.tacticWeight = (1/myPreferenceScore)^(i-1);
					sumWeights = sumWeights + listElement.tacticWeight;			
				}
				// Calculate the probability cutoff. Idea is as follows:
				// Let p = probability for current tactic, and Z be the cumulative probabilities for all
				// tactics with a better utility than the current. 
				// We store Z+p for the current tactic as the probability cut off
				// Let x be a random number between 0 & 1
				// Then i choose that tactic with the lowest cut off greater than x.
				for(int i = 0; i< tacticList.size(); i++) {
					listElement = tacticList.get(i);
					tmpProb = listElement.tacticWeight/sumWeights;
					tmpCumProb = tmpCumProb + tmpProb;
					listElement.tacticProbabilityCutoff = tmpCumProb;
				}
				
				// Randomly choose the tactic to use next time
				//Random generator = new Random(); 
				double randX = RandomHelper.nextDouble(); //generator.nextDouble();
				int keeplooking = 1;
				int curIndex=0;
				besttactic_additional = tacticList.get(0).tacticPointer; // Default chose the utility with the best tactic
				while(keeplooking == 1) {
					listElement = tacticList.get(curIndex);
					if (listElement.tacticProbabilityCutoff >= randX) {
						// use this one
						besttactic_additional = listElement.tacticPointer;
						keeplooking = 0;
					}
					curIndex++;
				}
				// Store the utility from the current tick of the best tactic,
				// in the strategy (this is also the strategy's utility)
				strategyutilityscore.add(besttactic.getUtilityScore(1)[0][1]); 

		
	}

// ---- STRATEGY UTILITY	
	
	//Used when adding/updating to the strategyutility ArrayList with the new score. 
	public void addstrategyutilityscore(double t) {
		strategyutilityscore.add(t);}
	
	public double getsumofstrategyutility() {
		double ret = 0;
		for (int i = 0; i < strategyutilityscore.size(); ++i) {
			ret = ret + strategyutilityscore.get(i);
		}
		return ret;}
	
}

	