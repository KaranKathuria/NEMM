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
	private double certValueDeveloper;
	private double prevCertValueRatio;
	
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
		int developerhorizont = 0;
		int numTicksToEmpty;
		
		// Note - we get the "numTicksToEmpty" from each of the above agents each tick. This is because
		// in the future we may allow these to adapt to a changing market. As for now though, they
		// are hard coded in AllVariables
		
		// First, ensure the prognosis for the certificate value data is updated
		if (myCAAgent.getMyCompany().getproduceragent()!=null) {
			numTicksToEmptyProd = myCAAgent.getMyCompany().getproduceragent().getNumTicksToEmptyPosition();
		}
		if (myCAAgent.getMyCompany().getobligatedpurchaseragent()!=null) {
			 numTicksToEmptyPurch = myCAAgent.getMyCompany().getobligatedpurchaseragent().getNumTicksToEmptyPosition();
		}
		if (myCAAgent.getMyCompany().gettraderagent()!=null) {
			numTicksToEmptyTrad = myCAAgent.getMyCompany().gettraderagent().getNumTicksToEmptyPosition();
		}
		if (myCAAgent.getMyCompany().getdeveloperagent()!=null) {
			developerhorizont = myCAAgent.getMyCompany().getdeveloperagent().getcvvaluehorizont();
		}
		
		numTicksToEmpty = Math.max(developerhorizont, Math.max(numTicksToEmptyTrad, Math.max(numTicksToEmptyPurch, numTicksToEmptyProd)));
		
		if(numTicksToEmpty>=0) {
			marketprognosis.updateCertValueData(numTicksToEmpty); //As this calculation is the same for all actors by finding the max we calculate CVvalues for all ticks up to max.
		} 
			
		certValueTrader = 0;
		certValueProducer = 0;
		certValuePurchaser = 0;
		certValueDeveloper = 0;
		
		if (numTicksToEmptyProd>0) {
			 certValueProducer = calcCertificateValueNew(numTicksToEmptyProd);
		}
		if (numTicksToEmptyPurch>0) {
			// Note: the obligated purchaser does not use the CV stuff as yet - this is in place
			// for when they do
			 certValuePurchaser = calcCertificateValueNew(numTicksToEmptyPurch);
		}
		if (numTicksToEmptyTrad>0) {
			 certValueTrader = calcCertificateValueNew(numTicksToEmptyTrad);
		}
		if (developerhorizont>0) {
			certValueDeveloper = calcCertificateValueNew(developerhorizont);
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
	
	public double getCertValueDeveloper() {
		return certValueDeveloper;
	}

	public double calcCertificateValueNew(int numTicksAheadMax) {
		// returns the analysis agent's estimate of the certificate value
		// over the coming numTicksAhead ticks (thereafter the value is assumed 0)
		double certVal=0;
		double discRate = 0.025/12; // Get the correct version of this. How? This is probably a 
		double priceSpot;
		double certShortfall;
		double ratioCurrent;
		double ratioCurrentAdj;
		double ratioFuture;
		double ratioFutureAdj;
		double priceAdjusted;
		double priceAdjStep;
		double bankEndPd;
		double priceAdjEnd;
		double[] estSaleProb = new double[numTicksAheadMax+1];
		int numTicksAhead;
		CVObject certValueData;	
		CVObject certValueData2;
		int numTicksRemaining;
		int z;
		double priceNegBank;
		int numRatioAdjustments;
		double ratioAdj;
		double priceEstimate;
		double prodExcess;
		double bankCurrentStart;
		double bankFutureStart;
		double totalDemandFromCurrent;
		double totalDemandFromFuture;
		
		// Parameters used in the analysis
		numTicksRemaining = Math.max(0,TheEnvironment.theCalendar.getNumTicks()-TheEnvironment.theCalendar.getCurrentTick()
				-1); //20160307KK: Former: "-numTicksAheadMax+1);"
		numTicksAhead = Math.min(numTicksRemaining, numTicksAheadMax);
		numRatioAdjustments = AllVariables.ratioAdjFactor.length;
		if(numTicksAhead>0) {
			certValueData = marketprognosis.getCertValueData(numTicksAhead);
		} else {
			// At the end of the cert market, so set future ratios to 1 as 
			// certs have no demand. Everything else is set to 0
			certValueData = new CVObject();
			certValueData.setCurrentsupplyratio(1);
			certValueData.setFuturesupplyratio(1);
		}
		ratioCurrent = certValueData.getCurrentsupplyratio();
		ratioFuture = certValueData.getFuturesupplyratio();
		bankCurrentStart = certValueData.getCurrentbank();
		bankFutureStart = certValueData.getFuturebank();
		totalDemandFromCurrent = certValueData.getDemandcurrenttoend();
		totalDemandFromFuture = certValueData.getDemandfuturetoend();
		priceEstimate = 0;
		certVal = 0;
//		totalDemand = TO COME
		// Calculate the price_adjusted - that is, the price in numTicksAhead for the
		// value of ratio_current assuming a linear rate of change in price towards the max or min
		priceSpot = ShortTermMarket.getcurrentmarketprice();
		if (ratioCurrent>1)
			{priceAdjEnd = AllVariables.certMinPrice;}
		else
			{priceAdjEnd = AllVariables.certMaxPrice;}
		priceAdjStep = 0;
		if(numTicksRemaining>0){
			priceAdjStep = (priceAdjEnd-priceSpot)/numTicksRemaining;
		}
		
		priceAdjusted = priceSpot+priceAdjStep*numTicksAhead;
		estSaleProb[0]=1;
		if(TheEnvironment.theCalendar.getCurrentTick()==20 & numTicksAhead==2) {
			int tmp = 1;
			tmp = 1;
		}
		// Calculate estimated price for each ratio adjustment multiplier, and weight the result by the
		// given ratio adjustment probability in the certVal calculation
		for(z=0;z<numRatioAdjustments;z++){
			ratioAdj = AllVariables.ratioAdjFactor[z];
			
			ratioCurrentAdj = ratioAdj*ratioCurrent - (ratioAdj-1)*bankCurrentStart/totalDemandFromCurrent;
			ratioFutureAdj = ratioAdj*ratioFuture - (ratioAdj-1)*bankFutureStart/totalDemandFromFuture;
			
			if (ratioCurrentAdj<=0) {
				priceEstimate = AllVariables.certMaxPrice;
			}
			else if (ratioCurrentAdj>=1) {
				priceEstimate = AllVariables.certMinPrice;
			}
			else {
				// This is a temp fix until the adjusted ratio calcs are made correct
				if (ratioFutureAdj>ratioCurrentAdj) {ratioFutureAdj = ratioCurrentAdj;}
				// future ratio will approach 0 (i.e. be less than current ratio)
				priceEstimate = priceAdjusted + (AllVariables.certMaxPrice - priceAdjusted)*
						(1-ratioFutureAdj/ratioCurrentAdj);
				if (ratioFutureAdj>ratioCurrentAdj) {
					int tmp = 10;
					tmp = 10;
				}
				if (ratioFutureAdj<0) {
					int tmp = 20;
					tmp = 20;
				}
			}
			if (priceEstimate<0){
				int tmp=20;
				tmp=20;
			}			
			if(TheEnvironment.theCalendar.getCurrentTick()==20 & numTicksAhead==2) {
				int tmp = 2;
				tmp = 2;
			}			
			// Calculate the base certificate value as the price estimate in the next period
//			certVal+=(priceSpot+(priceEstimate-priceSpot)/numTicksAhead)*AllVariables.ratioAdjProb[z];
			certVal += priceEstimate*AllVariables.ratioAdjProb[z];
			// Now adjust the certificate value for any periods of negative bank
			certShortfall = 0;
			for (int i=1;i<=numTicksAhead;i++) {
				certValueData2 = marketprognosis.getCertValueData(i);
				bankEndPd = certValueData2.getBetweentickscumulativesupply()*ratioAdj+certValueData2.getCurrentbank()-
						certValueData2.getBetweentickscumulativedemand();
				prodExcess = certValueData2.getFutureticksupply()*ratioAdj-certValueData2.getFuturetickdemand();
				if (bankEndPd<0 & prodExcess<0) {
					// no certs in bank to meet production short fall, thus penalty pricing
					certShortfall-=prodExcess;
				}
			}
			priceNegBank = (certShortfall/(ratioAdj*certValueData.getBetweentickscumulativesupply()))*
								Math.max(1.5*priceAdjusted, AllVariables.valueCertShortfall);

			if (priceNegBank<0){
				int tmp=5;
				tmp=5;
			}
			if(TheEnvironment.theCalendar.getCurrentTick()==20 & numTicksAhead==2) {
				int tmp = 3;
				tmp = 3;
			}

			certVal += priceNegBank*AllVariables.ratioAdjProb[z];
		}
		
		if(AllVariables.flagDiscountCV == true){
			certVal = certVal*Math.pow(1-discRate,numTicksAhead);
		}
		
			double tmp = certVal;
			int a = 3;
			
		if (certVal > AllVariables.certMaxPrice) {
			certVal = AllVariables.certMaxPrice;
		}
		
		return certVal;
	}

}



//OLD
/* Not in use anymore (2014)
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
	}
	
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
//			ratioFutureAdj = ratioAdj*ratioFuture - 
//					(ratioAdj-1)*certValueData.getBetweentickscumulativedemand()/1;
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

public double calcCertificateValueTest(int numTicksAhead) {
	return ShortTermMarket.getcurrentmarketprice();
}

*/

