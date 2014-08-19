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


//Class definitions. Note that this is a static class as all its member variables are static.
public class VolumePrognosis {
	
	private int nextmonthsproduction;
	private int nextyearsproduction;
	private int nextmonthsdemand;
	private int nextyearsdemand;

	public VolumePrognosis() {
		nextmonthsproduction = 0;
		nextyearsproduction = 0;
		nextmonthsdemand = 0;
		nextyearsdemand = 0;
	}
	
	//Methods
	public int getnextmonthsproduction() {
		return nextmonthsproduction;
	}
	public int getnextmonthsdeamnd() {
		return nextmonthsdemand;
	}
	
	public void setnextmonthsproduction(int prod) {
		nextmonthsproduction = prod;
  }
	public void setnextmonthsdemand(int dem) {
		nextmonthsdemand = dem;
  }
	
	
}