/*
 * Version info:
 *
 *     Last altered data: 20140912
 *     Made by: Karan Kathuria
 */
package nemmprocesses;

import nemmagents.CompanyAgent;
import nemmcommons.CommonMethods;

public class Forcast {
	
	public Forcast() {};
	
	// Updates the certificatepriceexpectations
	public static void updatemarketforcasts() {
		for (CompanyAgent CA : CommonMethods.getCompanyAgenList()) { 
			CA.getcompanyanalysisagent().getmarketanalysisagent().updatecertpriceexpectations();
			}
	}
	
	//Updates the volume prognosis for all company agents. Could contain demand prognosis as well
	public static void updatevolumeprognosis() { 
		for (CompanyAgent CA : CommonMethods.getCompanyAgenList()) { 
			CA.getcompanyanalysisagent().getvolumeanalysisagent().getvolumeprognosis().updatevolumeprognosis();
	}
}
	//Initiate volume prognosis. This is a method because this cannot be set in the constructor. 
public static void initiatevolumeprognosis() { 
	for (CompanyAgent CA : CommonMethods.getCompanyAgenList()) { 
		CA.getcompanyanalysisagent().getvolumeanalysisagent().getvolumeprognosis().initiatevolumeprognosis();
	}
}
	
	
}