/*
 * Version info:
 *     File defining the short term (within-year) market for trading certificates. This market is cleared by a double auction logic.
 *     Last altered data: 20140722
 *     Made by: Karan Kathuria
 */

package nemmprocesses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import nemmagents.CompanyAgent.ActiveAgent;
import nemmcommons.CommonMethods;
import nemmcommons.ParameterWrapper;
import nemmstmstrategiestactics.BuyOffer;
import nemmstmstrategiestactics.GenericStrategy;
import nemmstmstrategiestactics.SellOffer;
import repast.simphony.random.RandomHelper;


//To be implemntetd Should in short take inn all bids, find the price which maximates traded volume. excetute trades. And set the price! (and do somethings smart with the imbalance of trade).
// Take inn bids of differen type. Use them to clear the market and set price. Inputt agents based bids, output price. as well as a methods that updates agents salse volume maybe)
public class ScoreStrategiesandTactics {
	
	//Static means that the market price exist without the object short term market. Hence we can get the market price without having to refer or create a object short term market.
	private static double se;
	
	public ScoreStrategiesandTactics() {}
			
	
}

