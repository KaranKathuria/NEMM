package nemmagents;

import java.util.ArrayList;

import nemmenvironment.Region;

public class GenericCompany extends ParentAgent{

	private String companyName;
	private ArrayList<CompanyDemandShare> myDemandShares;
	
	public GenericCompany(String... compname){
		this.myDemandShares = new ArrayList<CompanyDemandShare>();
		if (compname.length > 0){
			this.companyName = compname[0];
		}
		else
		{
			this.companyName = "No name";
		}
	}

	// Gets and Sets --------------------------------------------------------------
	
	public ArrayList<CompanyDemandShare> getMyDemandShares() {
		return this.myDemandShares;
	}

	// Methods --------------------------------------------------------------------
	
	public void AddNewDemandShare(double defaultShare, Region demRegion){
		CompanyDemandShare tempDS = new CompanyDemandShare(defaultShare, demRegion);
		this.myDemandShares.add(tempDS);
	}
	
	
	
}
