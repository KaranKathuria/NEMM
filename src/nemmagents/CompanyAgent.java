/*
 * Version info:
 *     File defining the Company Agent, which consist of one or none of the following active agents: Producer, Obligated Purchaser or Trader. And a companyanalsysagent. 
 *     
 *     Last altered data: 20140819
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
import nemmagents.ActiveAgent;


// Class definition
public class CompanyAgent extends ParentAgent {

	private String companyname;
	private ActiveAgent produceragent;
	private ActiveAgent obligatedpurchaseragent;
	private ActiveAgent traderagent;
	private CompanyAnalysisAgent companyanalysisagent;
	
	public CompanyAgent() {
		companyname = "zeroagent";
		produceragent = null;
		obligatedpurchaseragent = null;
		traderagent = null;
		companyanalysisagent = null;
	}
	
	public CompanyAgent(boolean p, boolean op, boolean t) {
		if (p==true) {
			produceragent = new ActiveAgent(1);}
		if (p==false) {
			produceragent = null;}
		if (op==true) {
			obligatedpurchaseragent = new ActiveAgent(2);}
		if (op==false) {
			obligatedpurchaseragent = null;}
		if (t==true) {
			traderagent = new ActiveAgent(3);}
		if (t==false) {
			traderagent = null;}
		
		companyanalysisagent = new CompanyAnalysisAgent();	
		}	
	
	public ActiveAgent getproduceragent() {
		return produceragent;
	}
	public ActiveAgent getobligatedpurchaseragent() {
		return obligatedpurchaseragent;
	}
	public ActiveAgent gettraderagent() {
		return traderagent;
	}
	
	
	
	
	
	
	
}