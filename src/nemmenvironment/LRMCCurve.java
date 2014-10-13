/*
 * Version info:
 *     A class creating the perfect foresight version of the LRMC-curve. The curve is based on fundamental modelling. 
 *     
 *     Last altered data: 20141009
 *     Made by: Karan Kathuria
 */

package nemmenvironment;

import java.util.ArrayList;

public class LRMCCurve {
	
	//Not sure about the fields, but should contain values sufficient to draw the curve for a given year. That is the LRMC for a powerplant for a powerplant build that year. This inus the powerprice provides
	//the fundamental certificate price. 
	private int runyear;			 //Indicating which year the LRMC-curve is created. That is the simulation tick year.
	private int yearsahead; 		 //For which year the LRMC-curve is created for. That is a future year from the simulation tick year. All years start in januar. 
	private double equilibriumprice; //Fundamental price for certificates given by intersect of the LRMC for all projects that can provide the certificates demand minus the power price, and the access demand at that year.
	private ArrayList<curvepair> projectsupplycurve = new ArrayList<curvepair>(); //Sorted by needed cert price, and with accumulated production volume. 
	private double accessdemand;
	
	public class curvepair {
		private double certpriceneeded;			//The average certprice needed to break even, taking into account LRMC and the number of years with certs and power price.
		private double annualcertproduction;	//Capacity times LF for the project.
	
		public curvepair(double cn, double acp) {
			certpriceneeded = cn;
			annualcertproduction = acp;
		};
	}
	
	public LRMCCurve(int runye, int yearsah) {
		runyear = runye;
		yearsahead = yearsah;
	}
	//Methods

	public void calculatelrmccurve(ArrayList<PowerPlant> tempendogenousprojects, double certificatebalance) {
		
		//In short: THis methods takes in the certificate balance and the list of possible projects. It then estimates the cheapest way to meet accessdemand. IF there is access supply, it returns zero.
		
		//Should take use of 
		
		//Get total bank
		//Get total demand from when until yearsah
		//Get total produciton (expected production or perfect foresight). Assuming normal years or perfect foresight or combination.
		//Fore all projects that can be realised and ar noe already buildt,calcualte LRMC and certprieneeded. sort by cert prce needed. While accessdemand > 0, select and withdraw projects from the list
		//Add the production form these prosjects to total production (annually). 
		//Calcualte curve values. 
		
	}
	
	
	public double getequilibriumprice() {
		return equilibriumprice;
	}
	public ArrayList<curvepair> getcurvepair() {
		return projectsupplycurve;
	}
}
