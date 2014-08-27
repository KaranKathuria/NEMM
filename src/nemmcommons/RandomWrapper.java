/*
 * Version info:
 *     A simple wrapper class generation multiple random numbergenerators. 
 *     Last altered data: 20140827
 *     Made by: Karan Kathuria  
 */

package nemmcommons;

import java.util.Random;
import repast.simphony.random.DefaultRandomRegistry;
import repast.simphony.random.RandomHelper;

public class RandomWrapper  {
	
	
	Random strategyseed;
	Random someotherstream;
	
	
	private RandomWrapper() {}
	
	public void initiateseeds() {
		
		Random strategyseed = new Random();
		Random someotherstream = new Random();
		
	};
	
	public Random getstrategyseed() {
		return strategyseed;
	}
	
	
	
	
	

}
