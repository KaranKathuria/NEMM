/*
 * Version info:
 *
 *     Last altered data: 20140912
 *     Made by: Karan Kathuria
 */
package nemmprocesses;

import nemmagents.CompanyAgent;
import nemmcommons.CommonMethods;
import nemmenvironment.TheEnvironment;

public class Forcast {
	
	public Forcast() {};
	
	// Updates the certificatepriceexpectations
	public static void updateAllShortTermMarketPrognosis() {
		for (CompanyAgent CA : CommonMethods.getCompanyAgenList()) { 
			CA.getcompanyanalysisagent().getmarketanalysisagent().updateSTMarketPrognosis();
			}
	}
	public static void updateAllCertValuePrognosis() {
		for (CompanyAgent CA : CommonMethods.getCompanyAgenList()) { 
			CA.getcompanyanalysisagent().getmarketanalysisagent().updateCertValuePrognosis();
			}
	}	
	//Updates the volume prognosis for all company agents. Could contain demand prognosis as well
	public static void updateAllVolumePrognosis() { 
		int curTick = TheEnvironment.theCalendar.getCurrentTick();
		for (CompanyAgent CA : CommonMethods.getCompanyAgenList()) { 
			CA.getcompanyanalysisagent().getvolumeanalysisagent().getvolumeprognosis().updateVolumePrognosis(curTick);
	}
}
	//Initiate volume prognosis. This is a method because this cannot be set in the constructor. 
public static void initiateAllVolumePrognosis() { 
	for (CompanyAgent CA : CommonMethods.getCompanyAgenList()) { 
		CA.getcompanyanalysisagent().getvolumeanalysisagent().getvolumeprognosis().initialiseVolumePrognosis();
	}
}

public static void updateMPEandLPE() { 
	for (CompanyAgent CA : CommonMethods.getCompanyAgenList()) { 
		CA.getcompanyanalysisagent().getmarketanalysisagent().getmarketprognosis().updatefunamentalmarketprognosis();
		}
}
	
	
}