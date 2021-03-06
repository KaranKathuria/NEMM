/*
 * Version info:
 *     A class creating the perfect foresight version of the LRMC-curve. The curve is based on fundamental modelling. 
 *     
 *     Last altered data: 20141010
 *     Made by: Karan Kathuria
 */

package nemmenvironment;

import java.util.ArrayList;
import java.util.Collections;
import nemmcommons.CommonMethods;

public class LRMCCurve {
	
	//Not sure about the fields, but should contain values sufficient to draw the curve for a given year. That is the LRMC for a powerplant for a powerplant build that year. This inus the powerprice provides
	//the fundamental certificate price. 
	private int runyear;			 	//Indicating which year the LRMC-curve is created. That is the simulation tick year. This is NOT of interest for the calculation 
	private int yearsahead; 		 	//For which year the LRMC-curve is created for. That is a future year from the simulation tick year. All years start in januar. 
	private double equilibriumprice;	//Fundamental price for certificates given by intersect of the LRMC for all projects that can provide the certificates demand minus the power price, and the access demand at that year.
	private ArrayList<Curvepair> projectsupplycurve = new ArrayList<Curvepair>(); //Sorted by needed cert price.
	private double certificatebalance;	//Stores the certificate balance. 
	
	public class Curvepair {
		
		private String name;									//THat is the name of the powerplant
		private double LRMC;									//For storing purposes only. 
		private double certpriceneeded;							//The average certprice needed to break even, taking into account LRMC and the number of years with certs and power price.
		private double annualcertproduction;					//Capacity times LF for the project.
	
		public Curvepair(String na, double LMC, double cn, double acp) {
			name = na;
			LRMC = LMC;
			certpriceneeded = cn;
			annualcertproduction = acp;
		};
		
		public Double getcertpriceneeded() {
			return certpriceneeded;
		}
		public Double getannualcertproduction() {
			return annualcertproduction;
		}
	}

	public LRMCCurve(int runye, int yearsah) {
		runyear = runye;										//This is the year of running the FMA in the simulation
		yearsahead = yearsah;									//This is for which future year the LRMC curve is beeing calculated. 
	}
	//Basicaly it calculats the must economically marginal cost to build our the required certificate defecit. 
	//Method calculating the certificateprice needed for the marginal project in the list and setting the equilibrium price. 
	public void calculatelrmccurve(ArrayList<PowerPlant> tempendogenousprojects, double certbalance, int xyears) {
		int yearsprod = Math.min(xyears,15);					//When calculating LRMC based on a certbalance that is for more than a year, this is needed.
		certificatebalance = certbalance;
		
		if (certificatebalance > 0) { 							// No need for new projects.
			equilibriumprice = 0;								
			projectsupplycurve = null;
			throw new Error("No need for new projects when there is access certificates");
			
		} else {
		
		for (PowerPlant PP : tempendogenousprojects) {														//Loop creating and adding all relevant project info to the supplycurve.
			double usedRRR = PP.getspecificRRR() * TheEnvironment.GlobalValues.RRRcorrector;				//RRR is the specific corrected RRR with a value. Why the corrector? Think is to take accont or the "fake" improvment years.
			PP.calculateLRMCandcertpriceneeded((yearsahead-PP.getminconstructionyears()), usedRRR, 3);		//Calculates the LRMC and needed Certificateprice based on the given year minus construction years due to the methods inputdefinitio.
			Curvepair cp = new Curvepair(PP.getname(), PP.getLRMC(), PP.getcertpriceneeded(), PP.getestimannualprod());
			projectsupplycurve.add(cp);
		}
		Collections.sort(projectsupplycurve, new CommonMethods.customcurvepaircomparator());		//Sorting the ArrayList from lowest certprie needed to highest cert price needed
		int a =1;
		int index = 0;
		double newproductionbuilt = 0;
		while (newproductionbuilt < (-certificatebalance) && index < projectsupplycurve.size()) {		// While the newbuiltproduction is not enough to fulfill the shortcommings of certs, take the next project.
			newproductionbuilt = newproductionbuilt + (projectsupplycurve.get(index).getannualcertproduction()*yearsprod);// - (projectsupplycurve.get(index).getannualcertproduction()*0.5);
			equilibriumprice = projectsupplycurve.get(index).getcertpriceneeded();
			index++;
		}
		//IF all are build and there is still not enough.
		//if (newproductionbuilt < (-certificatebalance)){ equilibriumprice = AllVariables.maxpricecerts; } //throw new Error("There is no projects that can be finished in order to meet demand");}
				
		} 
		
	}
	
	
	public double getequilibriumprice() {
		return equilibriumprice;
	}
	public ArrayList<Curvepair> getcurvepair() {
		return projectsupplycurve;
	}
}
