/*
 * Version info:
 *     The class for Speculator Agents. These agents are profit maximizing traders that base their bids according to their margins, capital base and historic sucessrate. 
 *     
 *     Last altered data: 20140722
 *     Made by: Karan Kathuria

package nemmagents;

import java.util.ArrayList;
import java.util.List;

import nemmstmstrategiestactics.SABidstrategy;


//Class definitions
public class ZOldSpeculatorAgent extends ParentAgent {
	
	// As the other agents a speculator agent would have one or several bidding strategies for the STM and the LTM market. 
	// Input for their bidding strategy would be their lever of margins required and their capital base. I recon we could limit their level of "banked" certificates
	// but we might as well limit their total capital base/cash base, hence they are limited in the level of short or long positions they could take. 
	// Also their bidding behavior in both LTM and STM would be more interconnected and be a function of how successfull they have been in the past. So would also the margins be.
	/// (Margins/Spread - the difference between buy and sell offers provided by the SAgents in the same auction).
	
	// Attributes:
	
	// The list of Producer specific strategy objects this agent may use for forming bids in the stm-market. Really not sure we need a separate class in this case
	// as the specutaculor strategy could be only dependent on margin and capitalbase and successrate 
	private final List<SABidstrategy> stmstrategies = new ArrayList<SABidstrategy>();
	
	// The best strategy used so far. Initially it is set to the first one.	 
	private SABidstrategy beststmstrategy = null;
	
	private double initialcapitalbase; //Value of all capital initially. Contracts and cash.
	private double currentcapitalbase; //Value of all capital now. Contract and cash. That is contractvalue (discounted + currentaccount.
	
	private double currentaccount; //Cash in hand available for paying margins, investing in contracts etc. 
	private double contractvalue; //Value of all contracts at hand at current market price. Should be discounted. 
	private double contractcost; // Cost price payed for the certificates in hand. 
	
	// Constructors. 
	
	// Get methods
	}
	
	*/
	
