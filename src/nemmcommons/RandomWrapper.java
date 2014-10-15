/*
 * Version info:
 *     A simple wrapper class generation multiple random numbergenerators. 
 *     Last altered data: 20140827
 *     Made by: Karan Kathuria  
 */

//This issue is quite hard as the random controll would require a lot of work. Currently the best idea is to have a strategy-seed stream and give out fized seed for each agents strategy. 
//This would require agents to have the same number of strategies and we give out seeds by looping through strategies and then agents. (Giving out seed to the first strategy for each agent etc..).
package nemmcommons;

import java.util.Random;

import cern.jet.random.Normal;
import nemmenvironment.FundamentalMarketAnalysis;
import repast.simphony.random.DefaultRandomRegistry;
import repast.simphony.random.RandomHelper;

public class RandomWrapper  {
	
	
	private static Random strategyseed = new Random(RandomHelper.nextInt());
	private static Random someotherstream = new Random(RandomHelper.nextInt());
	
	static Normal erroredMPE = RandomHelper.createNormal(FundamentalMarketAnalysis.getMPE(), AllVariables.stdmediumrunpriceexpect);
	static Normal erroredLPE = RandomHelper.createNormal(FundamentalMarketAnalysis.getLPE(), AllVariables.stdmediumrunpriceexpect);

	private RandomWrapper() {}
	
	public static int getstrategyseed() {
		return strategyseed.nextInt();
	}
	
	public static double geterroredMPE() {
		return erroredMPE.nextDouble();
	}
	
	public static double geterroredLPE() {
		return erroredLPE.nextDouble();
	}
	
	

	
	
	

}
