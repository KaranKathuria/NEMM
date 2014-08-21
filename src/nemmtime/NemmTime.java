package nemmtime;

import java.util.Calendar;

public class NemmTime {

	// This is a structure to concisely hold NEMM time info
	
	public int year;
	public int obligationpdID;
	public int tradepdID;
	public Calendar startDate;
	public Calendar endDate;
	public int tickIndex;
	
	public NemmTime(int yearPd, int oblPd, int trdPd, int tickID) {
		year = yearPd;
		obligationpdID = oblPd;
		tradepdID = trdPd;
		tickIndex = tickID;
	}
		
	
}
