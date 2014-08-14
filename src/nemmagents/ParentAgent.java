/*
 * Version info:
 *     Abstract class for all agents to ease references. 
 *     
 *     First created: 20140724
 *     Made by: Karan Kathuria (KK)
 */
package nemmagents;


public abstract class ParentAgent {

	// ID for the next agent
	private static int nextAvailableID = 1;

	// each agent has an unique ID that is also used as the bank account number
	private final int ID;

	//Constructor which updates and assigns the static variable nextAvailableID
	public ParentAgent() {
		
		ID = nextAvailableID++;
	}

	public final int getID() {
		return ID;
	}

}
