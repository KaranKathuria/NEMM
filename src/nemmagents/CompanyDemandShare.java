package nemmagents;

import nemmenvironment.Region;
import nemmenvironment.TheEnvironment;
import nemmtime.NemmTime;


public class CompanyDemandShare {

		private double[] demandShare; // demand share (between 0 and 1)
		private double defaultDemandShare;
		private Region myRegion;
		private int numTicks;

		// Currently the code ignores the NemmTime - that is, the demand share
		// recored here is effectively the current demand share. However
		// we could change this to maintain a record of previous demand shares
		
		// constructor is empty - need to set shares and regions explicitly
		public CompanyDemandShare(double defaultDS, Region demRegion){
			defaultDemandShare = defaultDS;
			myRegion = demRegion;
			numTicks = TheEnvironment.theCalendar.getNumTicks();
			demandShare = new double[numTicks-1];
			for (int y = 0; y < numTicks; ++y){
				demandShare[y] = defaultDemandShare;
			}
		}

		// Gets and Sets ---------------------------------------------------------------------

		// the NemmTime object is optional (and currently is ignored if used)
		// Later if not used it will assume the reference is to the current time block
		
		public double getDemandShare(int... tickID) {
			double retShare;
			if (tickID.length > 0 ) {
				retShare = demandShare[tickID[0]];
			}
			else {
				retShare = this.defaultDemandShare;
			}				
			return retShare;
		}

		public void setDemandShare(double demandShare, int... tickID) {
			if (tickID.length > 0) {
				this.demandShare[tickID[0]] = demandShare;
			}
			else {
				this.defaultDemandShare = demandShare;
			}				
			
		}

		public double getDemand(int... tickID){
			double calcDemand;
			double curShare;
			if (tickID.length > 0) {
				curShare = demandShare[tickID[0]];
			}
			else {
				curShare = this.defaultDemandShare;
			}				
			calcDemand = curShare*myRegion.getMyDemand().CertDemand(tickID);
			return calcDemand;
		}
		
		public Region getMyRegion() {
			return myRegion;
		}

		


}
