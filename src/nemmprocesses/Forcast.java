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
	
	public static void updatemarketforcasts() {
		for (CompanyAgent CA : CommonMethods.getCompanyAgenList()) { 
			CA.getcompanyanalysisagent().getmarketanalysisagent().updatecertpriceexpectations();
			
		}
	}
}
