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
	
	
	private static Random strategyseed;
	private static Random someotherstream;
	
	
	private RandomWrapper() {}
	
	public void initiateseeds() {
		
		Random strategyseed = new Random();
		Random someotherstream = new Random();
		
	};
	
	public static void setstrategyseed(int i) {
		strategyseed.setSeed(i);
	}
	public static Random getstrategyseed() {
		return strategyseed;
	}
	
	
	
	
	

}
