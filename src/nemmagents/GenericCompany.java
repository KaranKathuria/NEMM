package nemmagents;

import java.util.ArrayList;

import nemmenvironment.Region;

public class GenericCompany extends ParentAgent{

	private String companyName;
	
	
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
	


	// Methods --------------------------------------------------------------------
	

	
	
	
}
