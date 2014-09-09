package inputreader;

import nemmenvironment.PowerPlant;
import nemmenvironment.Region;
import nemmenvironment.TheEnvironment;

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
 private static int loadprofileentries;
 private static int priceEntries;
 private static Region[] regions;
 private static PowerPlant[] plants;
 

	public static void ReadExcel() {
		
		//Finds file and starts reading
		String file_path = working_directory + "/NEMM_input.xls";  
		try{      
			HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(file_path));	
			
			// Read number of plants and technologies
			HSSFSheet ctr_sheet = workbook.getSheet("Control");
			plantsnumber = (int) ctr_sheet.getRow(2).getCell(2).getNumericCellValue();
			technologiesnumber = (int) ctr_sheet.getRow(3).getCell(2).getNumericCellValue();
			regionsnumber = (int) ctr_sheet.getRow(4).getCell(2).getNumericCellValue();
			genprofileentries = (int) ctr_sheet.getRow(5).getCell(2).getNumericCellValue();
			loadprofileentries = (int) ctr_sheet.getRow(6).getCell(2).getNumericCellValue();			
			priceEntries = (int) ctr_sheet.getRow(7).getCell(2).getNumericCellValue();	
			
			// Read regions
			//Creates an array of regions wiht the length given in the excel and adds reagions to this array of regions. 
			HSSFSheet list_sheet = workbook.getSheet("Lists");	
			//regions = new Region[regionsnumber];
			for(int region_ID = 0; region_ID < regionsnumber; region_ID++){
				String newregionName = list_sheet.getRow(2+region_ID).getCell(5).getStringCellValue();; 
				Region newRegion = new Region(newregionName);
				TheEnvironment.allRegions.add(newRegion);
			}
			
			/*
			// Read load
			HSSFSheet load_sheet = workbook.getSheet("Load");	
			for(int region_ID = 0; region_ID < regionsnumber; region_ID++){
				double[] doubledemand = new double[loadprofileentries];
				for(int load_entry = 0; load_entry < loadprofileentries; load_entry++){
					doubledemand[load_entry] = load_sheet.getRow(2+load_entry).getCell(2+region_ID).getNumericCellValue();					
				}
				// FIX : I am not sure how to construct a MarketDemand object
				// newdemand = new MarketDemand(doubledemand);
				// regions[region_ID].setMyDemand(newdemand);
			}
			
			// Read price
			HSSFSheet price_sheet = workbook.getSheet("Price");	
			for(int region_ID = 0; region_ID < regionsnumber; region_ID++){
				double[] doubleprice = new double[priceEntries];
				for(int price_entry = 0; price_entry < priceEntries; price_entry++){
					doubleprice[price_entry] = price_sheet.getRow(2+price_entry).getCell(2+region_ID).getNumericCellValue();	
				}
				// FIX
				// MarketSeries newprice = new MarketSeries(doubleprice);
				// regions[region_ID].setMyPowerPrice(newprice);
			}			
					
			// Read plant data
			HSSFSheet plant_sheet = workbook.getSheet("Plants");	   
			plants = new PowerPlant[plantsnumber];

			for(int plant_ID = 0; plant_ID < plantsnumber; plant_ID++){
				int newcapacity = (int) plant_sheet.getRow(3+plant_ID).getCell(3).getNumericCellValue();
				double newloadfactor = plant_sheet.getRow(3+plant_ID).getCell(4).getNumericCellValue();
				int newregion_ID = (int) plant_sheet.getRow(3+plant_ID).getCell(6).getNumericCellValue();				
				plants[plant_ID] = new PowerPlant(newcapacity, newloadfactor, regions[newregion_ID-1]);
			}
			*/

	    }catch(Exception e) {
	        System.out.println("!! Bang !! xlRead() : " + e );
	    }
	    
	}
}
 

