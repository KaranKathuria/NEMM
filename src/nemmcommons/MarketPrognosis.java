/*
 * Version info:
 * 	   Class object for price prognosis. 
 * 
 *     Created: 20140818
 *     Made by: Karan Kathuria  
 */

package nemmcommons;

// Import
import nemmprocesses.ShortTermMarket;


//Class definitions. Note that this is a  class as all its member variables are .
public class MarketPrognosis {
	
	public double stpriceexpectation;

	public double endofyearprice;
	public double endofyearpluss1;
	public double endofyearpluss2;
	public double endofyearpluss3;
	public double endofyearpluss4;
	public double endofyearpluss5;
	// Power prices (pp), current and future. 
	public double nextmonthpowerprice;
	public double ppendofyearprice;
	public double ppendofyearpluss1;
	public double ppendofyearpluss2;
	public double ppendofyearpluss3;
	public double ppendofyearpluss4;
	public double ppendofyearpluss5;
	
	//Methods
	public MarketPrognosis() {
		stpriceexpectation = ParameterWrapper.getpriceexpectation();
	}
	public double getstpriceexpectation() {
		return stpriceexpectation;
	}

	public void setstpriceexpectation(double price) {
		stpriceexpectation = price;
  }

	public double getendofyearprice() {
		return endofyearprice;
	}
	
}