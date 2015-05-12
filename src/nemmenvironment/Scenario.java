/*
 * Version info:
 *     Scenario object consisting of a wind and power scenario. 
 *     
 *     Last altered data: 20150511
 *     Made by: Karan Kathuria
 */

package nemmenvironment;

import nemmcommons.TickArray;
import nemmcommons.YearArray;
import nemmtime.NemmTime;


public class Scenario {
	
	private String scenarioname;					//descripitve name of scenario. Could be used.
	private YearArray windyearmultiplier;
	private double[] annualpowerpricerregion1;
	private double[] annualpowerpricerregion2;


	
	public Scenario() {
		windyearmultiplier = new YearArray();
		annualpowerpricerregion1 = new double[47]; //Should ideally not be hard-coded, but for now okey. The years is total number of spot-price years given.
		annualpowerpricerregion2 =  new double[47]; //This is larger than YearArray (24) due to forwardprice-initilization.
	}



	public YearArray getWindyearmultiplier() {
		return windyearmultiplier;
	}

	public void setWindyearmultiplier(double[] wp) {
		windyearmultiplier.setArray(wp);
	}


	public double[] getAnnualpowerpricerregion1() {
		return annualpowerpricerregion1;
	}

	public void setAnnualpowerpricerregion1(double[] annualpowerpricerre1) {
		if (annualpowerpricerre1.length != annualpowerpricerregion1.length){
			throw new IllegalArgumentException("The scenarioarray does not match in lenght");
		}
		else {
		this.annualpowerpricerregion1 = annualpowerpricerre1;
		}
	}

	public double[] getAnnualpowerpricerregion2() {
		return annualpowerpricerregion2;
	}



	public void setAnnualpowerpricerregion2(double[] annualpowerpricerre2) {
		if (annualpowerpricerre2.length != annualpowerpricerregion1.length){
			throw new IllegalArgumentException("The scenarioarray does not match in lenght");
		}
		else {
		this.annualpowerpricerregion2 = annualpowerpricerre2;
		}
	}
	

	
}