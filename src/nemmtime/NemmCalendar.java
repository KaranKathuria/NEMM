package nemmtime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nemmcommons.CommonMethods;
import nemmenvironment.TheEnvironment;
import repast.simphony.engine.environment.RunEnvironment;

public class NemmCalendar {

	
	private int startYear;
	private int endYear;
	private int numYears;
	private int numObligatedPdsInYear;
	private int numTradePdsInObligatedPd;
	private int numTradePdsInYear;
	private int numTicks;
	private int currentTick;
	private ArrayList<NemmTime> timeBlocks;
	
	
	/**
	 * @param startYear
	 * @param endYear
	 * @param numObligatedPdsInYear
	 * @param numTradePdsInObligatedPd
	 */
	public NemmCalendar(int startYear, int endYear, int numObligatedPdsInYear,
			int numTradePdsInObligatedPd) {
		// should throw errors if start year later than end year, other vals <=0 etc
		this.currentTick=0;
		this.startYear = startYear;
		this.endYear = endYear;
		this.numObligatedPdsInYear = numObligatedPdsInYear;
		this.numTradePdsInObligatedPd = numTradePdsInObligatedPd;
		this.numYears = (this.endYear - this.startYear) + 1;			
		this.numTradePdsInYear = this.numObligatedPdsInYear*this.numTradePdsInObligatedPd;
		this.numTicks = this.numYears * this.numTradePdsInYear;
		timeBlocks = new ArrayList<NemmTime>();
		
		int curTick = 0;
		for (int y = 0; y < numYears; ++y){
			for (int b = 0; b < this.numObligatedPdsInYear; ++b){
				for (int t = 0; t < this.numTradePdsInObligatedPd; ++t){
					NemmTime newBlock = new NemmTime(y,b,t, curTick);
					timeBlocks.add(newBlock);
					curTick = curTick+1;
				}
			}
		}
		Collections.sort(timeBlocks, new NemmTimeCompare());
	}
	
	public int getCurrentTick() {
		// will grab this from repast
		return (int) RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
	}
	
	public int getCurrentYear() { //returns the year (e.g 2025) not 13.
		return startYear + getTimeBlock((int) RunEnvironment.getInstance().getCurrentSchedule().getTickCount()).year;
	}

	public NemmTime getTimeBlock(int tickID){
		return timeBlocks.get(tickID);
	}

	public int getStartYear() {
		return startYear;
	}

	public int getEndYear() {
		return endYear;
	}

	public int getNumYears() {
		return numYears;
	}

	public int getNumObligatedPdsInYear() {
		return numObligatedPdsInYear;
	}

	public int getNumTradePdsInObligatedPd() {
		return numTradePdsInObligatedPd;
	}

	public int getNumTradePdsInYear() {
		return numTradePdsInYear;
	}

	public int getNumTicks() {
		return numTicks;
	}
	
	public int getNumTradePdsRemainingInCurrentObligationPd(int tickID) {
		// This is incorrect, and has to be fixed when we implement
		// obligation periods in the model
		// Does  include the current tick in the returned number
		int retVal = numTradePdsInObligatedPd - timeBlocks.get(tickID).tradepdID+1;
		return retVal;
	}
	
}
