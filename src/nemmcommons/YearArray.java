package nemmcommons;

import nemmenvironment.TheEnvironment;

public class YearArray {
	
	private double[] theData;
	
	
	public YearArray() {
	
	theData = new double[TheEnvironment.theCalendar.getNumYears()];
	}
	//f
	public double[] getArray() {
		return theData;
	}

	public void setArray(double[] newData) {
		// if newData is of length 1 (it is one number) then theData for all
		// ticks is set to newData. Otherwise, newData is copied to theData.
		if (newData.length > 1) {
			if (newData.length != this.theData.length){
				throw new IllegalArgumentException("The newData parameter is not of length numYears");
			}		
			this.theData = newData;			
		}
		else {
			for (int y = 0; y < TheEnvironment.theCalendar.getNumYears(); ++y){
				this.theData[y] = newData[0];
			}
		}

	}

	// get and set array elements
	
	public double getElement(int Year) {	//Year refering to year as in timeblock year, that is 0-24.
		if (Year<0) {
			throw new IllegalArgumentException("Negative Year ID passed to getElement");				
		}	
		if (Year>this.theData.length-1) {
			throw new IllegalArgumentException("Year: " + Year + " passed to getElement is too large");				
		}	
		return this.theData[Year];

	}

	public void setElement(double newElement, int Year) {

		if (Year<0) {
			throw new IllegalArgumentException("Negative Year passed to getElement");				
		}	
		if (Year>this.theData.length-1) {
			throw new IllegalArgumentException("Year: " + Year + " passed to getElement is too large");				
		}	
		this.theData[Year] = newElement;			
	}	
	
		
	}
	
