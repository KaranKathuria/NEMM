package nemmtime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NemmCalendar {

	
	private int startYear;
	private int endYear;
	private int numYears;
	private int numObligatedPdsInYear;
	private int numTradePdsInObligatedPd;
	private int numTradePdsInYear;
	private int numTicks;
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
		this.startYear = startYear;
		this.endYear = endYear;
		this.numObligatedPdsInYear = numObligatedPdsInYear;
		this.numTradePdsInObligatedPd = numTradePdsInObligatedPd;
		this.numYears = this.endYear - this.startYear + 1;
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
	
	
	
}
