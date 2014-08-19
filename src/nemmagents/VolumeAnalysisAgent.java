/*
 * Version info:
 *     File defining the Internal Volume analysis agent
 *     
 *     Last altered data: 20140819
 *     Made by: Karan Kathuria
 */

package nemmagents;

//Imports

import nemmcommons.VolumePrognosis;
import nemmagents.ParentAgent;



//Class definition
public class VolumeAnalysisAgent extends ParentAgent {
	
	private VolumePrognosis volumeprognosis;
		
	VolumeAnalysisAgent() {
		volumeprognosis = new VolumePrognosis();
	}
	
	public VolumePrognosis getvolumeprognosis() {
		return volumeprognosis;
	}

}
