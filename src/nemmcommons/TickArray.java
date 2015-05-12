package nemmcommons;

import nemmenvironment.PowerPlant;
import nemmenvironment.TheEnvironment;

public class TickArray {
	
	private double[] theData;
	
	// Array of size numTicks that holds and returns array data
	// This is put in a separate class (instead of using an array
	// directly) to bundle in error checking
	
	public TickArray() {
		// automatically initialise to the number of ticks
		theData = new double[TheEnvironment.theCalendar.getNumTicks()];
	}

	// Get and set the array all at once
	
	public double[] getArray() {
		return theData;
	}

	public void setArray(double[] newData) {
		// if newData is of length 1 (it is one number) then theData for all
		// ticks is set to newData. Otherwise, newData is copied to theData.
		if (newData.length > 1) {
			if (newData.length != this.theData.length){
				throw new IllegalArgumentException("The newData parameter is not of length numTicks");
			}		
			this.theData = newData;			
		}
		else {
			for (int y = 0; y < TheEnvironment.theCalendar.getNumTicks(); ++y){
				this.theData[y] = newData[0];
			}
		}

	}

	// get and set array elements
	
	public double getElement(int tickID) {
		if (tickID<0) {
			throw new IllegalArgumentException("Negative tick ID passed to getElement");				
		}	
		if (tickID>this.theData.length-1) {
			throw new IllegalArgumentException("Tick ID: " + tickID + " passed to getElement is too large");				
		}	
		return this.theData[tickID];

	}

	public void setElement(double newElement, int tickID) {

		if (tickID<0) {
			throw new IllegalArgumentException("Negative tick ID passed to getElement");				
		}	
		if (tickID>this.theData.length-1) {
			throw new IllegalArgumentException("Tick ID: " + tickID + " passed to getElement is too large");				
		}	
		this.theData[tickID] = newElement;			
	}	
	
	public void setFutureElements(double[] values) {
		if (values.length != TheEnvironment.theCalendar.getNumTicks()-TheEnvironment.theCalendar.getCurrentTick()) {
			throw new IllegalArgumentException("Array given to setFutureElements has invalid length (short or long)");	
		}
		//In the Array, it sets the values from current tick and until end equal to the values of the incoming array
		for (int i = 0; i < values.length; ++i) {
			int index = TheEnvironment.theCalendar.getCurrentTick() + i;
			this.theData[index] = values[i];
		}
	}
		
	
	//Needed to clone() powerplants
	@Override
	public TickArray clone()
	{
		TickArray foo;
	    try
	    {      foo = (TickArray) super.clone();}
	    catch (CloneNotSupportedException e)
	    {throw new Error("TA noclone"); }
	    // Deep clone member fields here
	    return foo;
	}
	
	
	
}
