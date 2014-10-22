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


public class UtilitiesStrategiesTactics {
	
	UtilitiesStrategiesTactics() {};
	
	public static void calculatetilitiesandupdatebesttactics() {
		// NOTE: This should be moved into the generic agent class
		GenericTactic tempbesttactic = new GenericTactic();
 
		for (ActiveAgent agent : CommonMethods.getAAgentList()) {
			for (GenericStrategy strategy : agent.getallstrategies()) { //
				strategy.updateUtilitiesAndParams();
				strategy.updateBestTactic();
			
			}
		}
		
	}
	
	
	// This should be moved into the generic agent class
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


