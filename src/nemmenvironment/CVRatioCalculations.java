package nemmenvironment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.apache.commons.lang3.ArrayUtils;

import antlr.collections.List;
import nemmcommons.AllVariables;
import repast.simphony.random.RandomHelper;

public class CVRatioCalculations {
	
	private static ArrayList<CVObject> allcvobjects = new ArrayList<CVObject>();		//Contains a list of cvobjects for all ticks given by 1 to max in AllVariables
	private static int maxsthetick;
	
	
	public CVRatioCalculations() {};
	
	
	public static CVObject getCVObject(int thetick) {
		return allcvobjects.get(thetick-1);	
	}
	
	
	//Static method precalculation all the needed CVObjects for the simulation. Needs to be called on everytick in the context.
	public static void calculateallcvobjects() {
		allcvobjects.clear();
		
		//As the maksthetick, the highest future tick needed CVObject for, is not altered each tick now, but this could be in the future.
		java.util.List<Integer> tempa = Arrays.asList(ArrayUtils.toObject(AllVariables.numTicksPAExit));
		java.util.List<Integer> tempb = Arrays.asList(ArrayUtils.toObject(AllVariables.numTicksOPExit));
		java.util.List<Integer> tempc = Arrays.asList(ArrayUtils.toObject(AllVariables.numTicksTExit));

				
		maxsthetick = Math.max(Math.max(Collections.max(tempa), Collections.max(tempb)), Collections.max(tempc));
		
		
		
		for (int thetick = 1; thetick <= maxsthetick; thetick++) {
			CVObject temp = calculatecvobject(thetick);
			allcvobjects.add(temp);
		}

	}
	
	//Method calculationg the expected supply/demand balance of certificates at current tick and at the given future tick (defined as current + the number of future ticks given). Returns exacly on CVObject 
	public static CVObject calculatecvobject(int thetick) {
		
		ArrayList<PowerPlant> allPowerPlants_kopi = new ArrayList<PowerPlant>();
		ArrayList<PowerPlant> projectsunderconstruction_kopi = new ArrayList<PowerPlant>();	
		
		for (PowerPlant pp: TheEnvironment.allPowerPlants){
			allPowerPlants_kopi.add(pp.clone());}
		for (PowerPlant pp: TheEnvironment.projectsunderconstruction){
			projectsunderconstruction_kopi.add(pp.clone());}
		

		
		//First get all future demand from current tick and futuretick. (Use perfect foresight).
		int currentick = TheEnvironment.theCalendar.getCurrentTick();							//Startingpoint Demand 1
		int thetick_tickID = currentick + thetick;												//Startingpoint Demand 2 in tickID
		int totalticks = TheEnvironment.theCalendar.getNumTicks();								//Total number of ticks in simulation
		int currentyear = TheEnvironment.theCalendar.getCurrentYear();
		int yearsleft = TheEnvironment.theCalendar.getNumYears() - (currentyear - TheEnvironment.theCalendar.getStartYear());
		int ticksinayear = TheEnvironment.theCalendar.getNumTradePdsInYear();
		
		double currentfuturedemand = 0;															//wrong nameAll future demand from now
		double thetickfuturedemand = 0;															//All future demand from thetick.
		double currentfuturesupply = 0;															//Wrong/misleading name. THis is all future..
		double thetickfuturesupply = 0;
		double theticksupply = 0;
		double thetickdemand = 0;
		double currentcertificatebalance = TheEnvironment.GlobalValues.totalmarketphysicalposition;	//Initial market physical position.
		double thetickcertificatebalance = 0;
		double exacltycurrenttickdemand = 0;
		
		//Calculating all future demand for both tickIDs (current and future)
		for (int i = currentick; i < totalticks; i++)	{										//For all tick from now and to the end
		for (Region R : TheEnvironment.allRegions) {																	
				currentfuturedemand = currentfuturedemand + R.getMyDemand().getExpectedCertDemand(i);
				if (i >= thetick_tickID) {														//Counting future demand from "thetick" in the same loop.
					thetickfuturedemand = thetickfuturedemand + R.getMyDemand().getExpectedCertDemand(i);
					}
				if (i == thetick_tickID) {
					thetickdemand = thetickdemand + R.getMyDemand().getExpectedCertDemand(i);
				}
				if (i == currentick) {
					exacltycurrenttickdemand = exacltycurrenttickdemand + R.getMyDemand().getExpectedCertDemand(i);
				}
				}
		}
		
		//Add the projects that are finished this year to the lokal kopi of powerplants in operation, with a random start and end-tick.
		for (int i=0; i < yearsleft;i++) {
		int currentyearstarttickID = ((currentyear - TheEnvironment.theCalendar.getStartYear())*ticksinayear);
		for (PowerPlant PP : projectsunderconstruction_kopi) {
			if (PP.getstartyear() == currentyear+i) {									 //Currentyear + i is the iterated year. Hence if they start this year --> Move.
			int tempstarttickinyear = RandomHelper.nextIntFromTo(0, TheEnvironment.theCalendar.getNumTradePdsInYear()-1);
			int temp = (currentyearstarttickID+(i*TheEnvironment.theCalendar.getNumTradePdsInYear())) + tempstarttickinyear;
			
			//Also takes care of that projects added after cutoff does not produce certificates.
			if (!PP.getMyRegion().getcertificatespost2020flag() && (PP.getstartyear()) > PP.getMyRegion().getcutoffyear()) { //If certflag is false and years is larger than cuoffyear.
				PP.setendyear(PP.getstartyear());
				PP.setendtick(temp);
			}
			//Then of not disturbed by the cut-off.
			else {
			PP.setendyear(Math.min(PP.getlifetime()+currentyear+i-1, currentyear+i+14));
			PP.setendtick(temp+(TheEnvironment.theCalendar.getNumTradePdsInYear()*Math.min(PP.getlifetime(), 15)));															// Not needed to remove projects from the projectsunderconstruction_copy as only those with startyear are added. Hence no chance of doublecounting.
			}
			PP.setStarttick(temp);	//Randoml set starttick between now and 12 tick ahead.
			allPowerPlants_kopi.add(PP);
			}
		}
		}

		//Calculating supply from existing powerplants and (added existing powerplants). The only difference is that thetickfuturesupply is less than currentfuturesupply as plants can be out of operation
		for (PowerPlant PP : allPowerPlants_kopi) {
		for (int i = currentick; i < totalticks; i++) {
			if((PP.getStartTick() <= i) && (PP.getendtick() >= i)) {
				currentfuturesupply = currentfuturesupply + PP.getExpectedProduction(i);
				if (i >= thetick_tickID) {
					thetickfuturesupply = thetickfuturesupply + PP.getExpectedProduction(i);
				}
				if (i == thetick_tickID) {
					theticksupply = theticksupply + PP.getExpectedProduction(i);
				}
			}
		}	
		}
		
		//Calculating thetick certificate bank based on current bank and the supply-demand in the in-between period.
		double tempdeltasupply = currentfuturesupply - thetickfuturesupply;
		double dempdeltademand = currentfuturedemand - thetickfuturedemand;
		thetickcertificatebalance = currentcertificatebalance + (tempdeltasupply-dempdeltademand);
		
		//Adding the current bank as well as the future bank.
		currentfuturesupply = currentcertificatebalance + currentfuturesupply;
		thetickfuturesupply = thetickcertificatebalance + thetickfuturesupply;
		
		CVObject ret = new CVObject();
		
		double currentsupplyratio = (currentfuturesupply/currentfuturedemand);
		double futuresupplyratio = (thetickfuturesupply/thetickfuturedemand);		
		
		ret.setCurrentsupplyratio(currentsupplyratio);
		ret.setFuturesupplyratio(futuresupplyratio);
		ret.setCurrentbank(currentcertificatebalance);
		ret.setFuturebank(thetickcertificatebalance);
		ret.setFuturetickdemand(thetickdemand);
		ret.setCurrenttickdemand(exacltycurrenttickdemand);				//just the current tick demand.
		ret.setBetweentickscumulativedemand(dempdeltademand);			//The total demand for the period inbetween the current and the provided tick
		ret.setBetweentickscumulativesupply(tempdeltasupply);
		ret.setDemandcurrenttoend(currentfuturedemand);
		ret.setDemandfuturetoend(thetickfuturedemand);
		return ret;
	}
	

	
	
	

}
