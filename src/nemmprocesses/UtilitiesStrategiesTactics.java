/*
 * Version info:
 *     File defining the update of tactics by caculation utilities for all tactics and strategies and reselecting best tactic.
 *     Last altered data: 20140822
 *     Made by: Karan Kathuria
 */

package nemmprocesses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import nemmagents.CompanyAgent.ActiveAgent;
import nemmcommons.CommonMethods;
import nemmcommons.ParameterWrapper;
import nemmstrategy_shortterm.GenericTactic;
import nemmstrategy_shortterm.BuyOffer;
import nemmstrategy_shortterm.GenericStrategy;
import nemmstrategy_shortterm.SellOffer;
import repast.simphony.random.RandomHelper;


//To be implemntetd Should in short take inn all bids, find the price which maximates traded volume. excetute trades. And set the price! (and do somethings smart with the imbalance of trade).
// Take inn bids of differen type. Use them to clear the market and set price. Inputt agents based bids, output price. as well as a methods that updates agents salse volume maybe)
public class UtilitiesStrategiesTactics {
	
	UtilitiesStrategiesTactics() {};
	
	public static void calculatetilitiesandupdatebesttactics() {

		GenericTactic tempbesttactic = new GenericTactic();
 
		for (ActiveAgent agent : CommonMethods.getAAgentList()) {
			for (GenericStrategy strategy : agent.getallstrategies()) { //
				double temputilityscore = 0;
				double bestutilityscore = -10000; //Quicfix as utilities currently can be negative. 
				for (GenericTactic tactic : strategy.getalltactics()) { 
					//Use the agents utilitymethod to calculate each tactics utility
					temputilityscore = agent.getutilitymethod().calculateutility(ShortTermMarket.getcurrentmarketprice(), tactic.gettacticbuyoffers(), tactic.gettacticselloffers(), agent.getphysicalnetposition(), ShortTermMarket.getshareofmarignaloffersold(), ShortTermMarket.getshareofmarignalofferbought());
					//Updates that tactics utility
					tactic.updatetacticutilityscore(temputilityscore);
					//Adds the tactics new current buy/sell-offers and utility to the tactichistoricvalues arrays.
					tactic.addtactichistory(); 
					//Updates the strategies best tactic based on which of that strategies tactics has the highest score. 
					if (temputilityscore >= bestutilityscore) {
						bestutilityscore = temputilityscore;
						tempbesttactic = tactic;
					}
				}
				strategy.addstrategyutilityscore((strategy.getbesttactic().gettacticutilityscore()));
				//Note that the strategy utility is set BEFORE the best tactic is updated! This is form the formerly best tactics utility we want to write. 
				strategy.updatebesttactic(tempbesttactic);	
			
			}
		}
		
	}
	
	public static void updatebeststrategy() {
		double temputilityscore = 0;
		double bestutilityscore = 0;
		
		for (ActiveAgent agent : CommonMethods.getAAgentList()) {
			for (GenericStrategy strategy : agent.getallstrategies()) {
				temputilityscore = strategy.getsumofstrategyutility();
						if (temputilityscore > bestutilityscore) {
							bestutilityscore = temputilityscore;
							agent.setbeststrategy(strategy);
						}
					
				}
			}
				
			}
		
	}


