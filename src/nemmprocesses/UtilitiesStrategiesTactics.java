/*
 * Version info:
 *     File defining the update of tactics by caculation utilities for all tactics and strategies and reselecting best tactic.
 *     Last altered data: 20140822
 *     Made by: Karan Kathuria
 */

package nemmprocesses;

import nemmagents.CompanyAgent.ActiveAgent;
import nemmcommons.AllVariables;
import nemmcommons.CommonMethods;
import nemmenvironment.TheEnvironment;
import nemmstrategy_shortterm.GenericTactic;
import nemmstrategy_shortterm.GenericStrategy;


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
					double totalutilityscore = 0;
					//Use the agents utilitymethod to calculate each tactics utility
					temputilityscore = agent.getutilitymethod().calculateutility(ShortTermMarket.getcurrentmarketprice(), tactic.gettacticbuyoffers(), tactic.gettacticselloffers(), agent.getphysicalnetposition(), ShortTermMarket.getshareofmarignaloffersold(), ShortTermMarket.getshareofmarignalofferbought());
					//Updates that tactics utility
					tactic.updatetacticutilityscore(temputilityscore);
					//Adds the tactics new current buy/sell-offers and utility to the tactichistoricvalues arrays.
					tactic.addtactichistory(); 
					//Updates the strategies best tactic based on which of that strategies tactics has the highest score. 
					for (int i = 0; i < Math.min(AllVariables.numofhistutilitiesincluded, TheEnvironment.theCalendar.getCurrentTick()+1);++i) {
						totalutilityscore = totalutilityscore + tactic.gettacticutilityscore(AllVariables.numofhistutilitiesincluded)[i][1];
						}
					if (totalutilityscore >= bestutilityscore) {
						bestutilityscore = totalutilityscore;
						tempbesttactic = tactic;
					}
				}
				strategy.addstrategyutilityscore((strategy.getbesttactic().gettacticutilityscore(1))[0][1]); //[1] because we want to get the utility.
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


