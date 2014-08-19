/*
 * Version info:
 *     File defining the Active Agents. These agents are a agents of type Producer, Obligated Purchaser or Trader. 
 *     
 *     Last altered data: 20140813
 *     Made by: Karan Kathuria
 */

package nemmagents;

//Import section for other methods
import java.util.ArrayList;
import java.util.List;
import nemmagents.ParentAgent;
import nemmstmstrategiestactics.BuyStrategy1;
import nemmstmstrategiestactics.GenericStrategy;
import nemmstmstrategiestactics.SellStrategy1;
import nemmstmstrategiestactics.TradeStrategy1;
import nemmcommons.ParameterWrapper;


// Class definition
// yes really
public class ActiveAgent extends ParentAgent {
// comment to remove
	private String activeagenttypename;
	private int activeagenttypecode; //1 = ProducerAgent, 2 = ObligatedPurchaserAgent, 3 = TraderAgent
	private ArrayList<GenericStrategy> allstrategies = new ArrayList<GenericStrategy>();
	private int numberofstrategies;
	private GenericStrategy beststrategy = null;
	private int physicalnetposition;
	
	// Null constructor for ActiveAgent. Should not be used as this does not specify type of agent.
	public ActiveAgent() {
		activeagenttypename = "Unreal ActiveAgent";
		activeagenttypecode = 0;
		//Return exeption?
	}
	public ActiveAgent(int type) {
		activeagenttypecode = type;
		
		if (type == 1) {
			activeagenttypename = "ProducerAgent";
			physicalnetposition = 10;
			SellStrategy1 sellstrategy = new SellStrategy1();
			allstrategies.add(sellstrategy);
								
		} if (type == 2) {
			activeagenttypename = "ObligatedPurchaserAgent";
			physicalnetposition = -10;
			BuyStrategy1 buystrategy = new BuyStrategy1();
			allstrategies.add(buystrategy);
			
		} else { //Notice that else is all other added as Trader agents. This is okey for now but should call an expetion later. 
			activeagenttypename = "TraderAgent";
			TradeStrategy1 tradestrategy = new TradeStrategy1();
			allstrategies.add(tradestrategy);
		} 
		beststrategy = allstrategies.get(0); // Choose the first one initially 

	}
	
	//Get methods
	public int getphysicalnetposition() {
		return physicalnetposition;
		}

	public GenericStrategy getbeststrategy() {
		return beststrategy;
		}

	
	//Update methods
			
	//Used for monthly updates within a clearing period. Notice that and annual uppdate would also have to take care of total banked certificates and totalsold.
	//THis method should somehow take care of calculating how many certiicates the agents have sold, based on the market price in the stm.
	
	public void monthlyupdateActiveAgent(int certificatessold, int certificatesbought) {
		physicalnetposition = physicalnetposition + certificatesbought + certificatessold; //Sold and demand are negative numbers. 
		//totalsold_cp = totalsold_cp + certificatessold;
		}

}
