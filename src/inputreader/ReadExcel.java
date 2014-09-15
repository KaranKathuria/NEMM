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

import java.io.FileInputStream;
import java.nio.file.Path;
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
 

	public static void ReadExcel() {}
	
	public static void ReadCreateTime() {
		
		//Finds file and starts reading
		String file_path = working_directory + "\\NEMM_testdata.xls";  
		
		try{      
			HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(file_path));	
			
			// Read number of plants and technologies
			HSSFSheet ctr_sheet = workbook.getSheet("Control");
			startyear = (int) ctr_sheet.getRow(8).getCell(2).getNumericCellValue();
			endyear = (int) ctr_sheet.getRow(9).getCell(2).getNumericCellValue();
			numobpdinyear = (int) ctr_sheet.getRow(10).getCell(2).getNumericCellValue();
			numtrpdinyear = (int) ctr_sheet.getRow(11).getCell(2).getNumericCellValue();
			
			TheEnvironment.theCalendar = new NemmCalendar(startyear, endyear, numobpdinyear, numtrpdinyear);
		}catch(Exception e) {
	        System.out.println("!! Bang !! xlRead() : " + e );
	    }
	}
			
	public static void ReadRegions() {
		
		//Finds file and starts reading
		String file_path = working_directory + "\\NEMM_testdata.xls";  
		
		try{      
			HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(file_path));	
			
			// Read number of plants and technologies
			HSSFSheet ctr_sheet = workbook.getSheet("Control");
			plantsnumber = (int) ctr_sheet.getRow(2).getCell(2).getNumericCellValue();
			//technologiesnumber = (int) ctr_sheet.getRow(3).getCell(2).getNumericCellValue();
			regionsnumber = (int) ctr_sheet.getRow(4).getCell(2).getNumericCellValue();
			ticks = TheEnvironment.theCalendar.getNumTicks();		
			
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
			
			HSSFSheet certdemand_sheet = workbook.getSheet("Certificate Demand");	
			//HSSFSheet powerDemand_sheet = workbook.getSheet("powerDemand");
			HSSFSheet powerPrice_sheet = workbook.getSheet("Power price");
			
			for(int j = 0; j < regionsnumber; j++){
				double[] tempcertdem = new double[ticks];
				double[] temppowerprice = new double[ticks];
				
				for(int i = 0; i < ticks; i++){
					tempcertdem[i] = certdemand_sheet.getRow(2+i).getCell(3+j).getNumericCellValue();
					temppowerprice[i] = powerPrice_sheet.getRow(2+i).getCell(3+j).getNumericCellValue();
				}
				// Set market demand
				TheEnvironment.allRegions.get(j).getMyDemand().initMarketDemand(tempcertdem);
				// Set MarketSeries (power prices)
				TheEnvironment.allRegions.get(j).getMyPowerPrice().initMarketSeries(temppowerprice);
			}
			
		}catch(Exception e) {
	        System.out.println("!! Bang !! xlRead() : " + e );
	    }
	}
		
		public static void ReadPowerPlants() {
			
			//Finds file and starts reading
			String file_path = working_directory + "\\NEMM_testdata.xls";  
			
			try{      	
				HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(file_path));	
				
				// Read number of plants and technologies
				HSSFSheet ctr_sheet = workbook.getSheet("Control");
				
				plantsnumber = (int) ctr_sheet.getRow(2).getCell(2).getNumericCellValue();
				//technologiesnumber = (int) ctr_sheet.getRow(3).getCell(2).getNumericCellValue();
				regionsnumber = (int) ctr_sheet.getRow(4).getCell(2).getNumericCellValue();
				//genprofileentries = (int) ctr_sheet.getRow(5).getCell(2).getNumericCellValue();
				ticks = (int) ctr_sheet.getRow(6).getCell(2).getNumericCellValue();			
				
				// Read plant data
				HSSFSheet plant_sheet = workbook.getSheet("PowerPlants");	   
				HSSFSheet production_sheet = workbook.getSheet("Production");	 
				//plants = new PowerPlant[plantsnumber];

				for(int j = 0; j < plantsnumber; j++){
					String newname = plant_sheet.getRow(3+j).getCell(1).getStringCellValue();
					int newtechnology = (int) plant_sheet.getRow(3+j).getCell(7).getNumericCellValue();
					int newcapacity = (int) plant_sheet.getRow(3+j).getCell(3).getNumericCellValue();
					double newloadfactor = plant_sheet.getRow(3+j).getCell(4).getNumericCellValue();
					int newregion_ID = (int) plant_sheet.getRow(3+j).getCell(6).getNumericCellValue();
					//newregion_ID starts by 1, hence to indexs it we subtract 1.
					PowerPlant pp = new PowerPlant(newname, newtechnology, newcapacity, newloadfactor, TheEnvironment.allRegions.get(newregion_ID-1));
					
					double[] tempproduction = new double[ticks];
					
					for(int i = 0; i < ticks; i++){
						tempproduction[i] = production_sheet.getRow(5+i).getCell(3+j).getNumericCellValue();
					}
					//Add production in form of tickarray
					pp.setAllProduction(tempproduction);
					TheEnvironment.allPowerPlants.add(pp);
				}
				
			}catch(Exception e) {
		        System.out.println("!! Bangpp !! xlRead() : " + e );
		    }
	    
	    
	}
}
 

