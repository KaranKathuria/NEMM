/*
 * Version info:
 *     File defining the Internal Analysis Agents. Prognosis of future demand/production and also analysis in some cases.
 *     
 *     Last altered data: 20140813
 *     Made by: Karan Kathuria
 */

package nemmagents;

//Imports
import nemmcommons.GlobalValues;
import nemmcommons.ParameterWrapper;
import nemmagents.ParentAgent;
import repast.simphony.adaptation.ga.RepastGA;


//Class definition
public class InternalAnalysisAgent extends ParentAgent {
	
	private int nextmonthsproduction = ParameterWrapper.getmonthlysupply();
	private int nextmonthsdemand = ParameterWrapper.getmonthlydemand();
	private double stpriceexpectation;
		

	public int getmonthlyproduction() {
		return nextmonthsproduction;
	}
	
	public int getnextmonthsdemand() {
		return nextmonthsdemand;
	}
	
	
}
