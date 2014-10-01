/*
 * Version info:
 *
 *     Last altered data: 20141001
 *     Made by: Karan Kathuria
 */

package nemmprocesses;

public class ObligationPeriodClearing {

	public ObligationPeriodClearing() {};
	
	//Method calculating this obligations periods key numbers. That is: Total certificates produced, total certificates demanded, average price (volume weighted) and penelty price. 
	//Add this info to global values. (Note that some of this information is already given in region but summing this to global values might still be beneficial in terms of runtime).
	
	//This is usefull for the volume-prognosis and market analysis agents and for calculating the penelty. 
	
	//To calculate the vlumewheigheter price. Both price and volume should be stored each tick. Our volumwhweited price is just volume and price per tick as all tick tradede volume ar traded at a givn price. 
	//Ad a "expected" penelty price at each analysis agent. THis is in fact sort of the same as the roof price for OPA-agents. AT this end however, the peneltyprice is final
	
	//Idea to have a oB-data update each tick for storing and updating the penelty-price expectations.
	
	public void updateglobalvaluesobinfo() {
		
	}
	
	public void updateregulations() {
		//In case aspects of the market is change, this can be altered here. The only thing that could be altered I assume is the kvotekurve. 
	}
	
	public void cancellation() {
		//Clearing out the OPAs banked certificates and 
	}
	
}
