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

import nemmagents.CompanyAgent.ActiveAgent;
import nemmcommons.AllVariables;
import nemmcommons.CommonMethods;
import nemmenvironment.TheEnvironment;
import nemmstrategy_shortterm.BuyOffer;
import nemmstrategy_shortterm.SellOffer;

// 
public abstract class GenericStrategy {
	
	//Variables. Notice that the generic strategy only contains the array of buy and sell offers and not the buy or sell offers itself because the number of these vary with the strategy. 
	protected ActiveAgent myAgent;
	protected ArrayList<BuyOffer> agentsbuyoffers = new ArrayList<BuyOffer>();
	protected ArrayList<SellOffer> agentsselloffers = new ArrayList<SellOffer>();
	protected int numberoftactics;
	protected ArrayList<Double> strategyutilityscore = new ArrayList<Double>();
	protected String strategyname; 
	protected ArrayList<GenericTactic> alltactics = new ArrayList<GenericTactic>();
	protected GenericTactic besttactic = null;
	
	protected int floorroofpricemultiplier; //Thus this strategy us a floor/roof [zero or one]
	protected int numberofmonthsmaxpp; //This means that the maximum pp equalt this number of months expected demand or production.

// ---- CONSTRUCTOR	

	//Constructor for parant class. Not sure about this. This construction will note be used as this class is abstract. 
	public GenericStrategy() {}
			
// ---- GETS & SETS	
	
	public ArrayList<BuyOffer> getAgentsBuyOffers() {
		return agentsbuyoffers;
	}
	
	public ArrayList<SellOffer> getAgentsSellOffers() {
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
	
	public void updateBidsAndOffers() {};
	public void updateUtilitiesAndParams() { 
		for (GenericTactic tactic : alltactics) { 
			// Update the tactic
			tactic.updateTactic();
		}
	}
	public void updateBestTactic() {
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

	