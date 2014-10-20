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
import cern.jet.random.*;
import cern.jet.random.engine.MersenneTwister;

public class RandomWrapper  {
	
	static Normal myNormalDistMPE;
	static Normal myNormalDistLPE;

	 public RandomWrapper() {
		    MersenneTwister generator1 = new MersenneTwister(123);
		    MersenneTwister generator2 = new MersenneTwister(321);

		    myNormalDistMPE = new Normal(FundamentalMarketAnalysis.getMPE(), AllVariables.stdmediumrunpriceexpect, generator1);
		    myNormalDistLPE = new Normal(FundamentalMarketAnalysis.getMPE(), AllVariables.stdmediumrunpriceexpect, generator2);
		  }
	
	 public static Normal getmyNormalDistMPE() {
		 return myNormalDistMPE;
	 }
	 public static Normal getmyNormalDistLPE() {
		 return myNormalDistMPE;
	 }

}
