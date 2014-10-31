/*
 * Version info:
 *     Static class for keeping reginal, technological, and cost and size dependent adjusted RRR before tax.
 *     
 *     Last altered data: 20141031
 *     Made by: Karan Kathuria
 */

package nemmenvironment;

public class ProjectRRR {
	
	private Region myRegion;
	private int technologyid;
	private int sizecategory_hydro;
	private int costcategory_hydro;
	private double specificRRR;
	
	public ProjectRRR(Region R, int t, int s, int c) {
		technologyid = t;
		myRegion = R;
		sizecategory_hydro = s;
		costcategory_hydro = c;
	}
	
public double getRRR() {return specificRRR;}


}
