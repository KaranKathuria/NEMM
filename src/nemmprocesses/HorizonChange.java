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
	
	public static int tickforchangeinhh1 = AllVariables.tickforchangeinhh1;	
	
	public HorizonChange() {};
	
	// The control station itself
	public static void horizontchangeprocess() {
		
		int currenttick = TheEnvironment.theCalendar.getCurrentTick();		
		
		//If control station
		if (timeforhhc1(currenttick) && AllVariables.changeinholdinghorzint) {
			 
			for (final ActiveAgent PA : CommonMethods.getPAgentList()) {
				if (PA.getNumTicksToEmptyPosition() == AllVariables.numTicksPAExit[0]) { //Shortest hh for PAs.
					double temp = RandomHelper.nextDoubleFromTo(0, 1);
					if (temp < AllVariables.changeshare1){
					PA.setnumTicksToEmptyPosition(PA.getNumTicksToEmptyPosition()+AllVariables.newhh1);
				}}
				
				if (PA.getNumTicksToEmptyPosition() == AllVariables.numTicksPAExit[1]) { //Shortest hh for PAs.
					double temp = RandomHelper.nextDoubleFromTo(0, 1);
					if (temp < AllVariables.changeshare1){
					PA.setnumTicksToEmptyPosition(PA.getNumTicksToEmptyPosition()+AllVariables.newhh2);
				}

				}
				else {
					
				}
			}
			
			for (final ActiveAgent OPA : CommonMethods.getOPAgentList()) {
				if (OPA.getNumTicksToEmptyPosition() == AllVariables.numTicksOPExit[0]) { //Shortest hh for OPAs.
				double temp = RandomHelper.nextDoubleFromTo(0, 1);
				if (temp < AllVariables.changeshare1){	
					OPA.setnumTicksToEmptyPosition(OPA.getNumTicksToEmptyPosition()+AllVariables.newhh1);
				}
				}
				if (OPA.getNumTicksToEmptyPosition() == AllVariables.numTicksOPExit[1]) { //Shortest hh for OPAs.
				double temp = RandomHelper.nextDoubleFromTo(0, 1);
				if (temp < AllVariables.changeshare1){	
					OPA.setnumTicksToEmptyPosition(OPA.getNumTicksToEmptyPosition()+AllVariables.newhh2);
				}
				}
				else {
					
				}
			}

			
			
		}
		if (timeforhhc2(currenttick) && AllVariables.changeinholdinghorzint) {
			for (final ActiveAgent PA : CommonMethods.getPAgentList()) {
				if (PA.getNumTicksToEmptyPosition() == AllVariables.numTicksPAExit[0]) { //Shortest hh for PAs.
					double temp = RandomHelper.nextDoubleFromTo(0, 1);
					if (temp < AllVariables.changeshare2){
					PA.setnumTicksToEmptyPosition(PA.getNumTicksToEmptyPosition()+AllVariables.newhh1);
				}
				}
				if (PA.getNumTicksToEmptyPosition() == AllVariables.numTicksPAExit[1]) { //Shortest hh for PAs.
					double temp = RandomHelper.nextDoubleFromTo(0, 1);
					if (temp < AllVariables.changeshare2){
					PA.setnumTicksToEmptyPosition(PA.getNumTicksToEmptyPosition()+AllVariables.newhh2);
				}
				}
				else {
					
				}
			}
			
			for (final ActiveAgent OPA : CommonMethods.getOPAgentList()) {
				if (OPA.getNumTicksToEmptyPosition() == AllVariables.numTicksOPExit[0]) { //Shortest hh for OPAs.
				double temp = RandomHelper.nextDoubleFromTo(0, 1);
				if (temp < AllVariables.changeshare2){	
					OPA.setnumTicksToEmptyPosition(OPA.getNumTicksToEmptyPosition()+AllVariables.newhh1);
				}
				}
				if (OPA.getNumTicksToEmptyPosition() == AllVariables.numTicksOPExit[1]) { //Shortest hh for OPAs.
				double temp = RandomHelper.nextDoubleFromTo(0, 1);
				if (temp < AllVariables.changeshare2){	
					OPA.setnumTicksToEmptyPosition(OPA.getNumTicksToEmptyPosition()+AllVariables.newhh2);
				}
				}
				else {
					
				}
			}

			
		}

	}
	
		
	//Method finding if it is time for change in holdinhorizont.
	public static final boolean timeforhhc1(int currenttick) {

				if (currenttick == AllVariables.tickforchangeinhh1) { 
					return true;
				}
				else {
					return false;
				}
			}
	
	public static final boolean timeforhhc2(int currenttick) {

		if (currenttick == AllVariables.tickforchangeinhh2) { 
			return true;
		}
		else {
			return false;
		}
	}


		
	}

		
	
