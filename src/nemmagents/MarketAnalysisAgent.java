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
		
		// Updates the agent's prognosis for short term market prices
		// There may be more stuff to come here in the future
		marketprognosis.updateSTMarketPricePrognosis();
	}
	
	public void updateCertValuePrognosis() {
		// Update the certificate value prognoses for the company's producer, trader, and purchaser
		// agents (as and where they exist)
		
		int numTicksToEmptyProd = 0;
		int numTicksToEmptyPurch = 0;
		int numTicksToEmptyTrad = 0;
		int numTicksToEmpty;
		
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
		double ratioFutureAdj;
		double ratioCurrentAdj;
		double bankFuture;
		double priceAdjusted;
		double priceAdjStep;
		double priceAdjEnd;
		double[] estSaleProb = new double[numTicksAhead+1];
		double netDemand;
		double netSupply;
		CVObject certValueData;		
		int numTicksRemaining;
		int i;
		int z;
		double ratioShortFall;
		double sumNegBank = 0;
		double sumNegDem = 0;
		double bankEnd;
		double bankAdj;
		double bankAdjPrev;
		double priceNegBank;
		int numRatioAdjustments;
		double ratioAdj;
		double priceEstimate;
		
		priceArray[0] = ShortTermMarket.getcurrentmarketprice();
		estSaleProb[0]=1;
		numTicksRemaining = TheEnvironment.theCalendar.getNumTicks()-TheEnvironment.theCalendar.getCurrentTick()
				-numTicksAhead+1;
		numRatioAdjustments = AllVariables.ratioAdjFactor.length;
		// Calculate the shortfall ratio
		// SR = sum(bank in pds with -ve bank)/sum(demand in pds with -ve bank)
/*		for(i=1;i<=numTicksAhead;i++)
		{
			certValueData = marketprognosis.getCertValueData(i); 
			bankEnd = certValueData.getFuturebank() + certValueData.getFutureticksupply() -
					certValueData.getFuturetickdemand(); // we need the bank at the end of the period
			if(bankEnd<0) {
				sumNegBank += bankEnd; // bank is negative, so total is negative
				sumNegDem -= certValueData.getFuturetickdemand(); // demand is positive, so total is negative
			}
		}
		ratioShortFall = 0.0;
		if (sumNegDem<0) {
			ratioShortFall = sumNegBank/sumNegDem;
		}*/
		
		for(i=1;i<=numTicksAhead;i++)
		{
			// Get the calculated certificate ratio data for the tick
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
			priceAdjusted = priceArray[0]+priceAdjStep*numTicksAhead;
			
			// Calcualte the prognosed price. The difference between the prognosed price and the adjusted
			// price is proportional to the difference between 
			// the CV ratio in tick 0 (now) and the CV ratio numTicksAhead. 
			
			// We account for uncertainty in production and consumption by calculating prices for several
			// CV ratio values for numTicksAhead. The uncetainty is calculated on production: that is, lower and 
			// higher production levels that the expected are assumed. The CV ratio and bank are re-calculated
			// and these are used. We then take the average of all calculated prices as the CV for numTicksAhead.
			priceArray[i] = 0.0;
			for(z=0;z<numRatioAdjustments;z++){
				ratioAdj = AllVariables.ratioAdjFactor[z];
//				ratioFutureAdj = ratioAdj*ratioFuture - 
//						(ratioAdj-1)*certValueData.getBetweentickscumulativedemand()/1;
				ratioFutureAdj = ratioFuture;
				ratioCurrentAdj = ratioCurrent;
				
				// The shortfall ratio in tick t is the bank at end of t divided by the demand, if the expected bank is
				// negative. Thus it gives the proportion of demand that is "unmet" in tick t				
				// Calc the bank at the end of the tick, given expected production.
				// Note getFutureBank gives the bank at the start of the tick
				bankEnd = certValueData.getFuturebank() + certValueData.getFutureticksupply() -
						certValueData.getFuturetickdemand(); 
				// Adjust bank at end and bank at start of the tick for the adjusted production level
				bankAdj = bankEnd+certValueData.getBetweentickscumulativesupply()*(1-ratioAdj);
				bankAdjPrev = bankAdj - certValueData.getFutureticksupply()*ratioAdj+certValueData.getFuturetickdemand();
				// Calculate the shortfall ratio
				ratioShortFall = 0;
				if(bankAdj<0) {
					ratioShortFall = -1*(bankAdj/certValueData.getFuturetickdemand());
					int tmp2 = 2;
					tmp2 = 3;
				}
				
				// Sale probability is estimated based on the number of certificates for sale in the future tick
				// divided by total demand in that tick. The calculation includes the banked certificates as a
				// supply or demand
				if (bankAdjPrev<=0) {
					estSaleProb[i]=(certValueData.getFutureticksupply()*ratioAdj-bankAdj)/
							(certValueData.getFutureticksupply()*ratioAdj);
				} else {
					estSaleProb[i]=(certValueData.getFuturetickdemand())/
							(certValueData.getFuturetickdemand()+bankAdj);
				}
				// Limit the probabilities
				if (estSaleProb[i]<0) {estSaleProb[i]=0;}
				if (estSaleProb[i]>1) {estSaleProb[i]=1;}
								
				// Calculate future estimated price
				priceEstimate = 0;
				if (ratioCurrentAdj<=0) {
					priceEstimate = AllVariables.certMaxPrice;
				}
				else if (ratioCurrentAdj>=1) {
					priceEstimate = AllVariables.certMinPrice;
				}
				else if (ratioFutureAdj>0) {
					// market expected to get tighter
					priceEstimate = priceAdjusted + (AllVariables.certMaxPrice - priceAdjusted)*
							(ratioCurrentAdj-ratioFutureAdj)/ratioCurrentAdj;
				}
				else {
					// market expected to get looser
					priceEstimate = AllVariables.certMaxPrice;
				}
				// Finally, adjust the calculated price if there are periods with negative bank
				// Use the shortfall ratio - this gives a representation of how "bad" the negative bank period is
				// We then add on to the calcuated price an additional "cost of negative bank"
				priceNegBank = priceAdjusted*ratioShortFall*1.50;
				priceEstimate+=priceNegBank;
				if (priceEstimate<0) {
					int tmp3=1;
					tmp3=2;
				}
				// The price forecast is a probability weighted sum of the price estimates
				priceArray[i]+=priceEstimate*AllVariables.ratioAdjProb[z];
			}				
		}
			
		certVal = priceArray[numTicksAhead]; // set end value of certificates in bank = to the forecast price at that point			
		// Run through the periods backwards and calculate the discounted value
		for (i = numTicksAhead; i > 0; i--) {
			certVal = estSaleProb[i]*priceArray[i] + (1-estSaleProb[i])*certVal*(1-discRate);
		}
		
		if(TheEnvironment.theCalendar.getCurrentTick()==40 & numTicksAhead>=48) {
			int tmp = 1;
			tmp = 2;
		}		
		return certVal;
	}
	
}
