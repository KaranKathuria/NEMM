/*
 * Version info:
 *     File defining the Analysis Agent class. This agent is currently implemented simply by only having a parameter given price expectation.
 *     For future development each analysis agent can have differen price expectations or even different strategies for determining price expectations based on future and historic values. 
 *     For instance a method similar to that of the ElFarol implementation but which also incorporates future variables. 
 *     
 *     Last altered data: 20140722
 *     Made by: Karan Kathuria
 */
package nemmagents;
//Imports


import nemmcommons.AllVariables;
import nemmcommons.MarketPrognosis;
import nemmenvironment.CVObject;
import nemmenvironment.CVRatioCalculations;
import nemmenvironment.TheEnvironment;
import nemmprocesses.ShortTermMarket;
import nemmagents.CompanyAgent.CompanyAnalysisAgent;
import nemmagents.ParentAgent;

//Class definition
public class MarketAnalysisAgent extends ParentAgent {

	private MarketPrognosis marketprognosis; //All current and future market expectations.
	private CompanyAnalysisAgent myCAAgent;
	private double certValueProducer;
	private double certValueTrader;
	private double certValuePurchaser;
	
	public MarketAnalysisAgent() {
		marketprognosis = new MarketPrognosis();
	}
	
	public MarketPrognosis getmarketprognosis() {
		return marketprognosis;
	}
	
	public CompanyAnalysisAgent getMyCAAgent() {
		return myCAAgent;
	}
	
	public void setMyCAAgent(CompanyAnalysisAgent objAgent) {
		myCAAgent = objAgent;
	}
	
	public void updateSTMarketPrognosis() {
		
		int numTicksToEmptyProd = 0;
		int numTicksToEmptyPurch = 0;
		int numTicksToEmptyTrad = 0;
		int numTicksToEmpty;
		
		// Updates the agent's prognosis for short term market prices
		// There may be more stuff to come here in the future
		marketprognosis.updateSTMarketPricePrognosis();
		
		// Update the certificate value prognoses for the company's producer, trader, and purchaser
		// agents (as and where they exist)
		
		// Note - we get the "numTicksToEmpty" from each of the above agents each tick. This is because
		// in the future we may allow these to adapt to a changing market. As for now though, they
		// are hard coded in AllVariables
		
		// First, ensure the prognosis for the certificate value data is updated
		if (myCAAgent.getMyCompany().getproduceragent()!=null) {
			numTicksToEmptyProd = myCAAgent.getMyCompany().getproduceragent().getNumTicksToEmptyPosition();
		}
		if (myCAAgent.getMyCompany().getobligatedpurchaseragent()!=null) {
			// Note: the obligated purchaser does not use the CV stuff as yet - this is in place
			// for when they do
			 numTicksToEmptyPurch = myCAAgent.getMyCompany().getobligatedpurchaseragent().getNumTicksToEmptyPosition();
		}
		numTicksToEmpty = Math.max(numTicksToEmptyTrad, Math.max(numTicksToEmptyPurch, numTicksToEmptyProd));
		if(numTicksToEmpty>=0) {
			marketprognosis.updateCertValueData(numTicksToEmpty);
		}
		
		certValueTrader = 0;
		certValueProducer = 0;
		certValuePurchaser = 0;
		if (numTicksToEmptyProd>0) {
			 certValueProducer = calcCertificateValue(numTicksToEmptyProd);
		}
		if (numTicksToEmptyPurch>0) {
			// Note: the obligated purchaser does not use the CV stuff as yet - this is in place
			// for when they do
			 certValuePurchaser = calcCertificateValue(numTicksToEmptyPurch);
		}
		
		
	}
	
	public double getCertValueProducer() {
		return certValueProducer;
	}

	public double getCertValueTrader() {
		return certValueTrader;
	}

	public double getCertValuePurchaser() {
		return certValuePurchaser;
	}

	public double calcCertificateValue(int numTicksAhead) {
		// returns the analysis agent's estimate of the certificate value
		// over the coming numTicksAhead ticks (thereafter the value is assumed 0)
		double certVal=0;
		double discRate = 0.05/12; // Get the correct version of this
		double[] priceArray = new double[numTicksAhead+1];
		double ratioCurrent;
		double ratioFuture;
		double priceAdjusted;
		double priceAdjStep;
		double priceAdjEnd;
		double[] estSaleProb = new double[numTicksAhead+1];
		double netDemand;
		double netSupply;
		CVObject certValueData;		
		int numTicksRemaining;
		
		priceArray[0] = ShortTermMarket.getcurrentmarketprice();
		estSaleProb[0]=1;
		numTicksRemaining = TheEnvironment.theCalendar.getNumTicks()-TheEnvironment.theCalendar.getCurrentTick()
				-numTicksAhead+1;
		for(int i=1;i<=numTicksAhead;i++)
		{
//			certRatio = marketprognosis.getCertificateRatio(i);
			certValueData = marketprognosis.getCertValueData(i); 
			ratioCurrent = certValueData.getCurrentsupplyratio();
			ratioFuture = certValueData.getFuturesupplyratio();
			// Calculate the adjusted price. This recognises that the closer we get to 
			// the end of the certificate market, the "worse" a shortfall will be. Thus 20 years ahead,
			// a ratio of 80% is not bad and will have a low price. 2 years ahead it is terrible and will
			// have a high price. We adjust the price at the current tick to an equivalent price numTicksAhead
			// for the current ratio. We have implemented a linear interpolation for now - perhaps this should be
			// non linear (to be looked at later)
			if (ratioCurrent>1)
				{priceAdjEnd = AllVariables.certMinPrice;}
			else
				{priceAdjEnd = AllVariables.certMaxPrice;}
			priceAdjStep = (priceAdjEnd-priceArray[0])/numTicksRemaining;
			priceAdjusted = priceArray[0]+priceAdjStep*numTicksRemaining;
			
			// Sale probability is estimated based on the number of certificates for sale in the future tick
			// compared to the demand in that tick
			if(certValueData.getFuturebank()<=0){
				netDemand = certValueData.getFuturetickdemand() - certValueData.getFuturebank();
				netSupply = certValueData.getFutureticksupply();
			} 
			else {
				netDemand = certValueData.getFuturetickdemand();
				netSupply = certValueData.getFutureticksupply()+ certValueData.getFuturebank();
			}
			estSaleProb[i] = netDemand/netSupply;
			if (estSaleProb[i]<0) {estSaleProb[i]=0;}
			if (estSaleProb[i]>1) {estSaleProb[i]=1;}
			
			// Calculate future estimated price
			if (ratioCurrent<=0) {
				priceArray[i] = AllVariables.certMaxPrice;
			}
			else if (ratioCurrent>=1) {
				priceArray[i] = AllVariables.certMinPrice;
			}
			else if (ratioFuture<= ratioCurrent) {
				// market expected to get tighter
				priceArray[i] = priceAdjusted + (AllVariables.certMaxPrice - priceAdjusted)*
						(ratioCurrent-ratioFuture)/ratioCurrent;
			}
			else {
				// market expected to get looser
				priceArray[i] = priceAdjusted*(1- (ratioFuture-ratioCurrent)/(1-ratioCurrent) );
			}
		}
		certVal = priceArray[numTicksAhead]; // set end value of certificates in bank = to the forecast price at that point			
		// Run through the periods backwards and calculate the discounted value
		for (int i = numTicksAhead; i > 0; i--) {
			certVal = estSaleProb[i]*priceArray[i] + (1-estSaleProb[i])*certVal*(1-discRate);
		}
		
		if(TheEnvironment.theCalendar.getCurrentTick()==40 & numTicksAhead>48) {
			int tmp = 1;
			tmp = 2;
		}		
		return certVal;
	}
	
}
