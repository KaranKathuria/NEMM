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
import nemmagents.ParentAgent;

//Class definition
public class MarketAnalysisAgent extends ParentAgent {

	private MarketPrognosis marketprognosis; //All current and future market expectations.
	
	
	public MarketAnalysisAgent() {
		marketprognosis = new MarketPrognosis();
	}
	
	public MarketPrognosis getmarketprognosis() {
		return marketprognosis;
	}
	
	public double getCertificateValue(int numTicksAhead) {
		// returns the analysis agent's estimate of the certificate value
		// over the coming numTicksAhead ticks (thereafter the value is assumed 0)
		double certVal=0;
		double discRate = 0.05/12; // Get the correct version of this
		double[] priceArray = new double[numTicksAhead+1];
		double ratioCurrent;
		double ratioFuture;
		double[] estSaleProb = new double[numTicksAhead+1];
		double netDemand;
		double netSupply;
		CVObject certValueData;
		
		
		
		priceArray[0] = ShortTermMarket.getcurrentmarketprice();
		estSaleProb[0]=1;
		for(int i=1;i<=numTicksAhead;i++)
		{
//			certRatio = marketprognosis.getCertificateRatio(i);
			certValueData = marketprognosis.getCertValuePrognosis(i); 
			ratioCurrent = certValueData.getCurrentsupplyratio();
			ratioFuture = certValueData.getFuturesupplyratio();
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
				priceArray[i] = priceArray[0] + (AllVariables.certMaxPrice - priceArray[0])*
						(ratioCurrent-ratioFuture)/ratioCurrent;
			}
			else {
				// market expected to get looser
				priceArray[i] = priceArray[0]*(1- (ratioFuture-ratioCurrent)/(1-ratioCurrent) );
			}
		}
		certVal = priceArray[numTicksAhead]; // set end value of certificates in bank = to the forecast price at that point			
		// Run through the periods backwards and calculate the discounted value
		for (int i = numTicksAhead; i > 0; i--) {
			certVal = estSaleProb[i]*priceArray[i] + (1-estSaleProb[i])*certVal*(1-discRate);
		}
		
		if(TheEnvironment.theCalendar.getCurrentTick()==5 & numTicksAhead>48) {
			int tmp = 1;
			tmp = 2;
		}
	
/*		
		certRatio = marketprognosis.getCertificateRatio(numTicksAhead);
		// Calculate the prices
		priceStart = ShortTermMarket.getcurrentmarketprice();
		if (certRatio[0]<=0) {
			priceEnd = AllVariables.certMaxPrice;
		}
		else if (certRatio[0]>=1) {
			priceEnd = AllVariables.certMinPrice;
		}
		else if (certRatio[1]<= certRatio[0]) {
			// market expected to get tighter
			priceEnd = priceStart + (AllVariables.certMaxPrice - priceStart)*(certRatio[0]-certRatio[1])/certRatio[0];
		}
		else {
			// market expected to get looser
			priceEnd = priceStart*(1- (certRatio[1]-certRatio[0])/(1-certRatio[0]) );
		}
		// Note we go backwards from end to start
		priceStep = (priceStart-priceEnd)/numTicksAhead;
		priceNow = priceEnd;
		certVal = priceEnd; // set end value of certificates in bank = to the forecast price at that point			
		// Run through the periods backwards and calculate the discounted value
		for (int i = numTicksAhead; i > 0; i--) {
			certVal = probSale*priceNow + (1-probSale)*certVal*(1-discRate);
			priceNow = priceNow + priceStep;
		}
*/		
		return certVal;
	}
	
}
