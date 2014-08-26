package nemmagents;

import nemmcommons.TickArray;
import nemmenvironment.Region;
import nemmenvironment.TheEnvironment;
import nemmtime.NemmTime;


public class CompanyDemandShare {

		private TickArray demandShare; // demand share (between 0 and 1)
		private double defaultDemandShare;
		private Region myRegion;
		private int numTicks;

		// The code records the demand shares in the demandShare TickArray
		
		public CompanyDemandShare(double defaultDS, Region demRegion){
			defaultDemandShare = defaultDS;
			myRegion = demRegion;
			numTicks = TheEnvironment.theCalendar.getNumTicks();
			demandShare = new TickArray();
			double[] tmpDem = new double[]{defaultDemandShare};
			demandShare.setArray(tmpDem);
		}

		// Gets and Sets ---------------------------------------------------------------------
		
		public double getDemandShare(int... tickID) {
			double retShare;
			if (tickID.length > 0 ) {
				retShare = demandShare.getElement(tickID[0]);
			}
			else {
				retShare = this.defaultDemandShare;
			}				
			return retShare;
		}

		public void setDemandShare(double demandShare, int... tickID) {
			// Allows external overwriting of the demand share
			// to be used if and when there is an external function that
			// re-calculates demand shares for all the companies (e.g. each
			// year or each tick).
			if (tickID.length > 0) {
				this.demandShare.setElement(demandShare, tickID[0]);
			}
			else {
				this.defaultDemandShare = demandShare;
			}				
			
		}

		public double getDemand(int... tickID){
			double calcDemand;
			double curShare;
			if (tickID.length > 0) {
				curShare = demandShare.getElement(tickID[0]);
				calcDemand = curShare*myRegion.getMyDemand().getCertDemand(tickID[0]);

			}
			else {
				curShare = this.defaultDemandShare;
				calcDemand = curShare*myRegion.getMyDemand().getCertDemand();
			}				
			return calcDemand;
		}
		
		public Region getMyRegion() {
			return myRegion;
		}

}
