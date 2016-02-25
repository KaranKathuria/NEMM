/*
 * Version info:
 *     File defining the class and methods used to read in input values from excel. Notice that the three methods all have do bee run in a preset order when 
 *     initializing the Environment. First time, then regions, then plants. 
 *     
 *     Made by: Karan Kathuria og Anders
 */

package inputreader;

import nemmcommons.AllVariables;
import nemmenvironment.PowerPlant;
import nemmenvironment.ProjectRRR;
import nemmenvironment.Region;
import nemmenvironment.TheEnvironment;
import nemmenvironment.Scenario;
import nemmtime.NemmCalendar;

//import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Paths;
//HSSF documentation at https://poi.apache.org/apidocs/

// Object reading in from excel
public class ReadExcel {
	 // Get working directory
	 private static String working_directory = Paths.get("").toAbsolutePath().toString();
	 private static int plantsnumber; 
	 private static int technologiesnumber;
	 private static int regionsnumber;
	 private static int genprofileentries;
	 private static int numbscenarios;
	 private static int ticks;
	 private static int startyear;
	 private static int endyear;
	 private static int years;
	 private static int numobpdinyear;
	 private static int numtrpdinyear;
	 private static PowerPlant[] plants;
	 private static String filePath;
	 //se
	 
 	public static void InitReadExcel() {

 		if (AllVariables.useTestData){
 			filePath = working_directory + File.separator + "NEMM_testdata.xls"; 
 		}
 		if (AllVariables.isbacktest){
 			filePath = working_directory + File.separator + AllVariables.backtestfile; 
 		}
 		else {
 			filePath = working_directory + File.separator + "data" + File.separator + AllVariables.inputfile; 
 			
 		}

 	}
	public static void ReadExcel() {}
	
	public static void ReadCreateTime() {
				
		try{      
			
			//Workbook workbook = new XSSFWorkbook();
			//XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(new File(filePath)));
			Workbook workbook = WorkbookFactory.create(new File(filePath));
			
			// Read number of plants and technologies
			Sheet ctr_sheet = workbook.getSheet("Control");
			startyear = (int) ctr_sheet.getRow(8).getCell(2).getNumericCellValue();
			endyear = (int) ctr_sheet.getRow(9).getCell(2).getNumericCellValue();
			numobpdinyear = (int) ctr_sheet.getRow(10).getCell(2).getNumericCellValue();
			numtrpdinyear = (int) ctr_sheet.getRow(11).getCell(2).getNumericCellValue();
			plantsnumber = (int) ctr_sheet.getRow(2).getCell(2).getNumericCellValue();
			
			TheEnvironment.theCalendar = new NemmCalendar(startyear, endyear, numobpdinyear, numtrpdinyear);
			
			//Initializes ticks from the created calender.
			ticks = TheEnvironment.theCalendar.getNumTicks();
			
					
		}catch(Exception e) {
	        System.out.println("!! Bang Timeerror !! xlRead() : " + e );
	    }
	}
		
	public static void ReadRegions() {
				
		try{      
			Workbook workbook = WorkbookFactory.create(new File(filePath));
			
			// Read number of plants and technologies
			Sheet ctr_sheet = workbook.getSheet("Control");
			//plantsnumber = (int) ctr_sheet.getRow(2).getCell(2).getNumericCellValue();
			//technologiesnumber = (int) ctr_sheet.getRow(3).getCell(2).getNumericCellValue();
			regionsnumber = (int) ctr_sheet.getRow(4).getCell(2).getNumericCellValue();		
			
			// Read regions
			//Creates an array of regions wiht the length given in the excel and adds reagions to this array of regions. 
			Sheet list_sheet = workbook.getSheet("Lists");	

			for(int i = 0; i < regionsnumber; i++){
				String newregionName = list_sheet.getRow(2+i).getCell(5).getStringCellValue(); 
				Region newRegion = new Region(newregionName);
				TheEnvironment.allRegions.add(newRegion);
			}
			
			//Starts reading in the regions MarketDemand and MarketSeries objects consisting of only certdemand and powerPrice
			
			Sheet certdemand_sheet = workbook.getSheet("Certificate demand");	
			Sheet expectedcertdemand_sheet = workbook.getSheet("Expected certdemand");	
			//Sheet powerDemand_sheet = workbook.getSheet("powerDemand");		Not read in.
			Sheet powerPrice_sheet = workbook.getSheet("Power price");
			years = endyear-startyear+1;
			
			for(int j = 0; j < regionsnumber; j++){
				double[] tempcertdem = new double[ticks];
				double[] tempexpcertdem = new double[ticks];
				double[] temppowerprice = new double[years];
				double[][] tempfwdprice = new double[years][years];												//For each year there is a Forward powerprice curve.
				
				for(int i = 0; i < ticks; i++){
					tempcertdem[i] = certdemand_sheet.getRow(2+i).getCell(3+j).getNumericCellValue();
					tempexpcertdem[i] = expectedcertdemand_sheet.getRow(2+i).getCell(3+j).getNumericCellValue();
				}
				
				//KK20151117: 48 or (49 for backtesting) ensuring right startingpoint for second region.
				int t = 48;
		 		if (AllVariables.isbacktest){t = 49;}
		 	
				for(int f = 0; f < years; f++){
				temppowerprice[f] = powerPrice_sheet.getRow(2+f+(t*j)).getCell(3).getNumericCellValue();		 //48 or (49 for backtesting) ensuring right startingpoint for second region.
				for(int fw = 0; fw < years; fw++){
				tempfwdprice[f][fw] = powerPrice_sheet.getRow(2+f+(t*j)+fw).getCell(4+f).getNumericCellValue(); //For each row, looping across the columns. Notice + fw as historic fwd does not make sense.
				}
				// Set alle the forward AnnualMarketSeries
				TheEnvironment.allRegions.get(j).getMyForwardPrice(f).initAnnualMarketSeries(tempfwdprice[f]);
	
				}
				// Set market demand (both power price, cert demand and expected cert demand
				TheEnvironment.allRegions.get(j).getMyDemand().initMarketDemand(tempcertdem, tempexpcertdem);
				// Set AnnualMarketSeries (power prices)
				TheEnvironment.allRegions.get(j).getMyPowerPrice().initAnnualMarketSeries(temppowerprice);
			}
			
		}catch(Exception e) {
	        System.out.println("!! Bang Regionerror !! xlRead() : " + e );
	    }
	}
		
		public static void ReadPowerPlants() {
						
			try{      	
				Workbook workbook = WorkbookFactory.create(new File(filePath));
				
				// Read plant data
				Sheet plant_sheet = workbook.getSheet("PowerPlants");	   
				Sheet production_sheet = workbook.getSheet("Production");
				Sheet expproduction_sheet = workbook.getSheet("Expected production");

				for(int j = 0; j < plantsnumber; j++){
					String newname = plant_sheet.getRow(3+j).getCell(1).getStringCellValue();
					int newregion_ID = (int) plant_sheet.getRow(3+j).getCell(16).getNumericCellValue();
					double newcapacity = plant_sheet.getRow(3+j).getCell(3).getNumericCellValue();
					double newloadfactor = plant_sheet.getRow(3+j).getCell(4).getNumericCellValue();
					int newtechnology = (int) plant_sheet.getRow(3+j).getCell(17).getNumericCellValue();
					int newstatus = (int) plant_sheet.getRow(3+j).getCell(6).getNumericCellValue();
					int newyearstarted = (int) plant_sheet.getRow(3+j).getCell(7).getNumericCellValue();
					int newlifetime = (int) plant_sheet.getRow(3+j).getCell(8).getNumericCellValue();
					//int newearlieststartyear = (int) plant_sheet.getRow(3+j).getCell(9).getNumericCellValue();
					double newcapex = plant_sheet.getRow(3+j).getCell(10).getNumericCellValue();
					double newopex = plant_sheet.getRow(3+j).getCell(11).getNumericCellValue();
					double newlearningrate = plant_sheet.getRow(3+j).getCell(12).getNumericCellValue();
					int newminyearinprocess = (int) plant_sheet.getRow(3+j).getCell(13).getNumericCellValue();
					int newminconstructionyears = (int) plant_sheet.getRow(3+j).getCell(14).getNumericCellValue();
					int newovergangsordningflag = (int) plant_sheet.getRow(3+j).getCell(15).getNumericCellValue();
					
					//newregion_ID starts by 1, hence to indexs it we subtract 1.
					int f = 2;
					PowerPlant pp = new PowerPlant(newname, TheEnvironment.allRegions.get(newregion_ID-1), newstatus, newcapacity, newloadfactor, newtechnology, newlifetime, newyearstarted, newcapex, newopex, newlearningrate, newminyearinprocess, newminconstructionyears, newovergangsordningflag);
					
					//Setting the powerplant/project to the relevant ArrayList. 
					if (newstatus == 1) {
						TheEnvironment.allPowerPlants.add(pp);}
					else if (newstatus == 0) {
						TheEnvironment.trashedprojects.add(pp);
					}
					else if (newstatus == 2) {
						TheEnvironment.projectsunderconstruction.add(pp);}
					else if (newstatus == 3) {
						TheEnvironment.projectsawaitinginvestmentdecision.add(pp);}
					else if (newstatus == 4) {
						TheEnvironment.projectinprocess.add(pp);}
					else if (newstatus == 5) {
						TheEnvironment.projectsidentifyed.add(pp);}
					else {TheEnvironment.potentialprojects.add(pp);}
					
					double[] tempproduction = new double[ticks];
					double[] expproduction = new double[ticks];
					
					for(int i = 0; i < ticks; i++){
						tempproduction[i] = production_sheet.getRow(5+i).getCell(3+j).getNumericCellValue();
					}
					
					for (int k = 0; k < ticks; k++){
					expproduction[k] = expproduction_sheet.getRow(5+k).getCell(3+j).getNumericCellValue();
					}
					//Add production in form of tick array
					pp.setAllProduction(tempproduction);
					pp.setAllExpectedProduction(expproduction);
					pp.setAllannualProduction(tempproduction);
					//Add all expected production to tick array

				}
				
			}catch(Exception e) {
		        System.out.println("!! Bang Powerplanterror !! xlRead() : " + e );
		    }
	    
	    
	}
	public static void ReadRRR() {
		
		try{      
			

			Workbook workbook = WorkbookFactory.create(new File(filePath));
			
			// Read number of plants and technologies
			Sheet RRR_sheet = workbook.getSheet("RRR");
			
			for (int j = 0; j < 16; j++) {
				
				int newregion_ID = (int) RRR_sheet.getRow(2+j).getCell(0).getNumericCellValue();
				int newtech_ID = (int) RRR_sheet.getRow(2+j).getCell(1).getNumericCellValue();
				int sizecat = (int) RRR_sheet.getRow(2+j).getCell(2).getNumericCellValue();
				int costcat = (int) RRR_sheet.getRow(2+j).getCell(3).getNumericCellValue();
				double RRR = RRR_sheet.getRow(2+j).getCell(4).getNumericCellValue();
			
			
				ProjectRRR PR = new ProjectRRR(TheEnvironment.allRegions.get(newregion_ID-1), newtech_ID, sizecat, costcat, RRR); 
				TheEnvironment.alladjustedRRR.add(PR);
			}					
					
		}catch(Exception e) {
	        System.out.println("!! Bang RRR Error !! xlRead() : " + e );
	    }
	}
	
	// ALE 2015-05-13: Read scenario data
    public static void ReadScenarios() {                  // Leser inn alle senarioene. For spesifikasjon av scenarioene, se Scenario.java.
                 
          try{ 
                 Workbook workbook = WorkbookFactory.create(new File(filePath));
                 
                 Sheet ctr_sheet = workbook.getSheet("Control");
                 numbscenarios = (int) ctr_sheet.getRow(1).getCell(2).getNumericCellValue();
                 
                 Sheet scenario_sheet = workbook.getSheet("Scenarios");
                 
                 for(int j = 0; j < numbscenarios; j++){
                        Scenario newscen = new Scenario();
                        
                        String newname = scenario_sheet.getRow(0).getCell(2+j).getStringCellValue();
                        newscen.setname(newname);
                        
                        
                        double[] newprodfactor = new double[years];
                        for (int k = 0; k < years; k++){
                               newprodfactor[k] = scenario_sheet.getRow(4+k).getCell(2+j).getNumericCellValue();
                        }
                        newscen.setWindyearmultiplier(newprodfactor);
                        
                        int numb_price_entries = 47;	// hardcoded for now. //47 for normal, 49 for backtesting.
                 		if (AllVariables.isbacktest){
                 			 numb_price_entries = 49;}
                        
                        double[] newpricereg1 = new double[numb_price_entries];
                        double[] newpricereg2 = new double[numb_price_entries];
                        for(int k = 0; k < numb_price_entries; k++){
                               newpricereg1[k] = scenario_sheet.getRow(7+years+k).getCell(2+j).getNumericCellValue();
                               newpricereg2[k] = scenario_sheet.getRow(8+years+numb_price_entries+k).getCell(2+j).getNumericCellValue();
                        }
                        newscen.setAnnualpowerpricerregion1(newpricereg1);
                        newscen.setAnnualpowerpricerregion2(newpricereg2);
                        
                        // how to include a new scenario?
                        TheEnvironment.allwindandppricescenarios.add(newscen);
                        //System.out.println("Scen_1 : price 1 = " + newscen.getAnnualpowerpricerregion1().toString() + ", price 2 = "+newscen.getAnnualpowerpricerregion2().toString()+", wind mult = " + newscen.getWindyearmultiplier().toString() );
                 }

                 
                 
          }catch(Exception e){
            System.out.println("!! Bang RRR Error !! xlRead() : " + e );
        }
          
                        
    }
}


 

