/*
 * Version info:
 *
 *     Last altered data: 20160215
 *     Made by: Karan Kathuria
 *     
 *     Shortly explained, all methods used to simulate the effects of control stations adjustmetns on the sertificate demand. In case the law needs to be change in order to adjust the certificate demand, 
 *     thorugh adjusting the qouta, this prosess is only done at each CS. Hence, the future demand is only adjusted at this timing. 
 *     
 *     If deltademand is POSITIV, the actual demand have been to LOW and the certificate bank must be DECREASED.
 */


package nemmprocesses;

import nemmagents.CompanyAgent;
import nemmcommons.AllVariables;
import nemmcommons.CommonMethods;
import nemmenvironment.TheEnvironment;

public class ControlStation {
	
	public static int prevoiuscontrolstationtick = AllVariables.firstrealtick;	//As no controll-station can be preformed before the last tick, this is added as default.
	
	public ControlStation() {};
	
	// The control station itself
	public static void controlstationupdate() {
		
		int currenttick = TheEnvironment.theCalendar.getCurrentTick();
		double deltacertificatedemand = 0;
		double newPAbank = 0;
		double oldPAbank = TheEnvironment.GlobalValues.producersphysicalposition;
		
		
		//If control station
		if (timeforcs(currenttick)) {
			 
			//Calculate deltademand since last control station. Defined as Target/expected - actual. Hence, if actual have been lower, the delta is positiv and the bank should be DECREASED.
			deltacertificatedemand = deltacertificatedemand(prevoiuscontrolstationtick,currenttick);
			
			newPAbank = oldPAbank - deltacertificatedemand;	 //By definition: The new bank is the old bank minus deltacertificate demand, as the latter is defined as positiv if target is higher than actual. If target was higher, than the bank is to high and must be adjusted DOWN.
			
			//Scaling the producers bank accordingly. Notice that we dont mind scaling the other ActiveAgents bank.
			UpdatePhysicalPosition.scalePAphysicalpos(newPAbank);
			
			//Set the prevoiuscontrolstationtick to the current one.
			prevoiuscontrolstationtick = currenttick;
			int test = 2;
			
			
		}
	}
	
		
	//Method finding if it is time for cs
	public static final boolean timeforcs(int currenttick) {
		
		int test1 = 2;
			for (int i=0;i<AllVariables.controlstationtick.length;i++) {
				
				if (currenttick == AllVariables.firstrealtick) { //Exeption if the first tick is controll stations. Then no correction should be ran.
					return false;
				}
				if (currenttick == AllVariables.controlstationtick[i]) {
					return true;
				}
			}
			return false;
			}
	
	//Method that calculates the deltadifference of cert demand between previous adjustment and current adjustment. That is deltacertdemand from previouiscontrollstationtic until currenttick-1.
	public static final double deltacertificatedemand(int previouscs, int currenttick) {
		double deltacertdemand = 0; //Number defined as target/expected - actual cert demand. Hence, if negative then the demand have been to little. 
		double tempdeltacertdemand_N = 0;
		double tempdeltacertdemand_S = 0;
		
		for (int i = previouscs; i < currenttick; i++) {
			
			tempdeltacertdemand_N = TheEnvironment.allRegions.get(0).getMyDemand().getExpectedCertDemand(i) - TheEnvironment.allRegions.get(0).getMyDemand().getCertDemand(i);
			tempdeltacertdemand_S = TheEnvironment.allRegions.get(1).getMyDemand().getExpectedCertDemand(i) - TheEnvironment.allRegions.get(1).getMyDemand().getCertDemand(i);
			
			deltacertdemand = deltacertdemand + tempdeltacertdemand_N + tempdeltacertdemand_S;
		}
		
		int a = 3;
		return deltacertdemand;
			
			
			
			
		}
		
	}
		
	

		
	//Find results.
		
	
