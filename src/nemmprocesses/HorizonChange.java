/*
 * Version info:
 *
 *     Last altered data: 20160310
 *     Made by: Karan Kathuria
 *     
 *
 */


package nemmprocesses;

import repast.simphony.random.RandomHelper;
import nemmagents.CompanyAgent;
import nemmagents.CompanyAgent.ActiveAgent;
import nemmcommons.AllVariables;
import nemmcommons.CommonMethods;
import nemmenvironment.TheEnvironment;

public class HorizonChange {
	
	public static int tickforchangeinhh = AllVariables.tickforchangeinhh;	
	
	public HorizonChange() {};
	
	// The control station itself
	public static void horizontchangeprocess() {
		
		int currenttick = TheEnvironment.theCalendar.getCurrentTick();		
		
		//If control station
		if (timeforhhc(currenttick) && AllVariables.changeinholdinghorzint) {
			 
			for (final ActiveAgent PA : CommonMethods.getPAgentList()) {
				if (PA.getNumTicksToEmptyPosition() == AllVariables.numTicksPAExit[0] || PA.getNumTicksToEmptyPosition() == AllVariables.numTicksPAExit[1]) { //Shortest hh for PAs.
					double temp = RandomHelper.nextDoubleFromTo(0, 1);
					if (temp < AllVariables.changeshare){
					PA.setnumTicksToEmptyPosition(PA.getNumTicksToEmptyPosition()+AllVariables.newhh);
				}
				}
				else {
					
				}
			}
			
			for (final ActiveAgent OPA : CommonMethods.getOPAgentList()) {
				if (OPA.getNumTicksToEmptyPosition() == AllVariables.numTicksOPExit[0] || OPA.getNumTicksToEmptyPosition() == AllVariables.numTicksOPExit[1]) { //Shortest hh for PAs.
					
				double temp = RandomHelper.nextDoubleFromTo(0, 1);
				if (temp < AllVariables.changeshare){	
					OPA.setnumTicksToEmptyPosition(OPA.getNumTicksToEmptyPosition()+AllVariables.newhh);
				}
				}
				else {
					
				}
			}

			
			
		}
		else {
			//Nothing
		}
	}
	
		
	//Method finding if it is time for change in holdinhorizont.
	public static final boolean timeforhhc(int currenttick) {

				if (currenttick == AllVariables.tickforchangeinhh) { 
					return true;
				}
				else {
					return false;
				}
			}


		
	}

		
	
