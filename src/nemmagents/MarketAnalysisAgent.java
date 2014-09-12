/*
 * Version info:
 *     File defining the Analysis Agent class. This agent is currently implemented simply by only having a parameter given price expectation.
 *     For future development each analysis agent can have differen price expectations or even different strategies for determining price expectations based on future and historic values. 
 *     For instance a method similar to that of the ElFarol implementation but which also incorporates future variables. 
 *     
 *     Last altered data: 20140722
 *     Made by: Karan Kathuria
 */
package nemmagents;
//Imports


import nemmcommons.AllVariables;
import nemmcommons.MarketPrognosis;
import nemmenvironment.TheEnvironment;
import nemmagents.ParentAgent;

//Class definition
public class MarketAnalysisAgent extends ParentAgent {

	private MarketPrognosis marketprognosis; //All current and future market expectations.
	private double forcastweights[] = AllVariables.forcastweights;
	
	public MarketAnalysisAgent() {
		marketprognosis = new MarketPrognosis();
	}
	
	public MarketPrognosis getpriceprognosis() {
		return marketprognosis;
	}
	
	public void updatecertpriceexpectations() { //Forcast the certprice for the next tick based on the forcastweights. Sets the prognosis cert price for next tick
		double nexttickcertprice;
		int numberhistoricprices = TheEnvironment.theCalendar.getCurrentTick() + 1;
		int curtick = TheEnvironment.theCalendar.getCurrentTick();
		if (numberhistoricprices < 3) {
			nexttickcertprice = TheEnvironment.GlobalValues.certificateprice.getElement(curtick);
		} else {
			nexttickcertprice = forcastweights[0] * TheEnvironment.GlobalValues.certificateprice.getElement(curtick-2) + forcastweights[1] * TheEnvironment.GlobalValues.certificateprice.getElement(curtick-1) + forcastweights[2] * TheEnvironment.GlobalValues.certificateprice.getElement(curtick);
		}
		this.marketprognosis.setstpriceexpectation(nexttickcertprice);
		this.marketprognosis.setExpectedcertificateprice(nexttickcertprice, curtick+1);
		
	}

	
}
