/*
 * Version info:
 *     File defining the class and methods used to read in input values from excel. Notice that the three methods all have do bee run in a preset order when 
 *     initializing the Environment. First time, then regions, then plants. 
 *     
 *     Last altered data: 20140903
 *     Made by: Karan Kathuria og Anders
 */

package inputreader;

import nemmenvironment.PowerPlant;
import nemmenvironment.Region;
import nemmenvironment.TheEnvironment;
import nemmtime.NemmCalendar;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;

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
	 private static int ticks;
	 private static int startyear;
	 private static int endyear;
	 private static int numobpdinyear;
	 private static int numtrpdinyear;
	 private static PowerPlant[] plants;
	 private static String filePath;
 

 	public static void InitReadExcel() {
 		filePath = working_directory + File.separator + "NEMM_testdata_20.xls"; 
 	}
 
	public static void ReadExcel() {}
	
	public static void ReadCreateTime() {
				
		try{      
			HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(filePath));	
			
			// Read number of plants and technologies
			HSSFSheet ctr_sheet = workbook.getSheet("Control");
			startyear = (int) ctr_sheet.getRow(8).getCell(2).getNumericCellValue();
			endyear = (int) ctr_sheet.getRow(9).getCell(2).getNumericCellValue();
			numobpdinyear = (int) ctr_sheet.getRow(10).getCell(2).getNumericCellValue();
			numtrpdinyear = (int) ctr_sheet.getRow(11).getCell(2).getNumericCellValue();
			plantsnumber = (int) ctr_sheet.getRow(2).getCell(2).getNumericCellValue();
			
			TheEnvironment.theCalendar = new NemmCalendar(startyear, endyear, numobpdinyear, numtrpdinyear);
			
			//Initializes ticks from the created calender.
			ticks = TheEnvironment.theCalendar.getNumTicks();
					
		}catch(Exception e) {
	        System.out.println("!! Bang !! xlRead() : " + e );
	    }
	}
			
	public static void ReadRegions() {
				
		try{      
			HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(filePath));	
			
			// Read number of plants and technologies
			HSSFSheet ctr_sheet = workbook.getSheet("Control");
			//plantsnumber = (int) ctr_sheet.getRow(2).getCell(2).getNumericCellValue();
			//technologiesnumber = (int) ctr_sheet.getRow(3).getCell(2).getNumericCellValue();
			regionsnumber = (int) ctr_sheet.getRow(4).getCell(2).getNumericCellValue();		
			
			// Read regions
			//Creates an array of regions wiht the length given in the excel and adds reagions to this array of regions. 
			HSSFSheet list_sheet = workbook.getSheet("Lists");	
			//regions = new Region[regionsnumber];
			for(int i = 0; i < regionsnumber; i++){
				String newregionName = list_sheet.getRow(2+i).getCell(5).getStringCellValue(); 
				Region newRegion = new Region(newregionName);
				TheEnvironment.allRegions.add(newRegion);
			}
			
			//Starts reading in the regions MarketDemand and MarketSeries objects consisting of only certdemand and powerPrice
			
			HSSFSheet certdemand_sheet = workbook.getSheet("Certificate demand");	
			HSSFSheet expectedcertdemand_sheet = workbook.getSheet("Expected certdemand");	
			//HSSFSheet powerDemand_sheet = workbook.getSheet("powerDemand");
			HSSFSheet powerPrice_sheet = workbook.getSheet("Power price");
			
			for(int j = 0; j < regionsnumber; j++){
				double[] tempcertdem = new double[ticks];
				double[] tempexpcertdem = new double[ticks];
				double[] temppowerprice = new double[ticks];
				
				for(int i = 0; i < ticks; i++){
					tempcertdem[i] = certdemand_sheet.getRow(2+i).getCell(3+j).getNumericCellValue();
					tempexpcertdem[i] = certdemand_sheet.getRow(2+i).getCell(3+j).getNumericCellValue();
					temppowerprice[i] = powerPrice_sheet.getRow(2+i).getCell(3+j).getNumericCellValue();
				}
				// Set market demand (both power price, cert demand and expected cert demand
				TheEnvironment.allRegions.get(j).getMyDemand().initMarketDemand(tempcertdem, tempexpcertdem);
				// Set MarketSeries (power prices)
				TheEnvironment.allRegions.get(j).getMyPowerPrice().initMarketSeries(temppowerprice);
			}
			
		}catch(Exception e) {
	        System.out.println("!! Bang !! xlRead() : " + e );
	    }
	}
		
		public static void ReadPowerPlants() {
						
			try{      	
				HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(filePath));	
	
				// Read plant data
				HSSFSheet plant_sheet = workbook.getSheet("PowerPlants");	   
				HSSFSheet production_sheet = workbook.getSheet("Production");
				HSSFSheet expproduction_sheet = workbook.getSheet("Expected production");
				//plants = new PowerPlant[plantsnumber];

				for(int j = 0; j < plantsnumber; j++){
					String newname = plant_sheet.getRow(3+j).getCell(1).getStringCellValue();
					int newregion_ID = (int) plant_sheet.getRow(3+j).getCell(13).getNumericCellValue();
					int newcapacity = (int) plant_sheet.getRow(3+j).getCell(3).getNumericCellValue();
					double newloadfactor = plant_sheet.getRow(3+j).getCell(4).getNumericCellValue();
					int newtechnology = (int) plant_sheet.getRow(3+j).getCell(14).getNumericCellValue();
					int newstatus = (int) plant_sheet.getRow(3+j).getCell(6).getNumericCellValue();
					int newyearstarted = (int) plant_sheet.getRow(3+j).getCell(7).getNumericCellValue();
					int newlifetime = (int) plant_sheet.getRow(3+j).getCell(8).getNumericCellValue();
					int newearlieststartyear = (int) plant_sheet.getRow(3+j).getCell(9).getNumericCellValue();
					double newcapex = plant_sheet.getRow(3+j).getCell(10).getNumericCellValue();
					double newopex = plant_sheet.getRow(3+j).getCell(11).getNumericCellValue();
					double newlearningrate = plant_sheet.getRow(3+j).getCell(12).getNumericCellValue();
					
					//newregion_ID starts by 1, hence to indexs it we subtract 1.
					PowerPlant pp = new PowerPlant(newname, TheEnvironment.allRegions.get(newregion_ID-1), newstatus, newcapacity, newloadfactor, newtechnology, newlifetime, newyearstarted, newearlieststartyear, newcapex, newopex, newlearningrate);
					
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
					//Add all expected production to tick array
					pp.setAllExpectedProduction(expproduction);
					
					//Setting the powerplant/project to the relevant ArrayList. 
					if (newstatus == 1) {
						TheEnvironment.allPowerPlants.add(pp);}
					else if (newstatus == 2) {
						TheEnvironment.projectsunderconstruction.add(pp);}
					else if (newstatus == 3) {
						TheEnvironment.projectsawaitinginvestmentdecision.add(pp);}
					else if (newstatus == 4) {
						TheEnvironment.projectinprocess.add(pp);}
					else if (newstatus == 5) {
						TheEnvironment.projectsidentifyed.add(pp);}
					else {TheEnvironment.potentialprojects.add(pp);}

				}
				
			}catch(Exception e) {
		        System.out.println("!! Bangpp !! xlRead() : " + e );
		    }
	    
	    
	}
}
 

