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
 private static int ticks;
 private static PowerPlant[] plants;
 

	public static void ReadExcel() {}
	
	public static void ReadRegions() {
		
		//Finds file and starts reading
		String file_path = working_directory + "\\NEMM_input.xls";  
		
		try{      
			HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(file_path));	
			
			// Read number of plants and technologies
			HSSFSheet ctr_sheet = workbook.getSheet("Control");
			plantsnumber = (int) ctr_sheet.getRow(2).getCell(2).getNumericCellValue();
			technologiesnumber = (int) ctr_sheet.getRow(3).getCell(2).getNumericCellValue();
			regionsnumber = (int) ctr_sheet.getRow(4).getCell(2).getNumericCellValue();
			genprofileentries = (int) ctr_sheet.getRow(5).getCell(2).getNumericCellValue();
			ticks = (int) ctr_sheet.getRow(6).getCell(2).getNumericCellValue();			
			
			// Read regions
			//Creates an array of regions wiht the length given in the excel and adds reagions to this array of regions. 
			HSSFSheet list_sheet = workbook.getSheet("Lists");	
			//regions = new Region[regionsnumber];
			for(int i = 0; i < regionsnumber; i++){
				String newregionName = list_sheet.getRow(2+i).getCell(5).getStringCellValue(); 
				Region newRegion = new Region(newregionName);
				TheEnvironment.allRegions.add(newRegion);
			}
			
			//Starts reading in the regions MarketDemand and MarketSeries objects consisting of certQouta, powerDemand and powerPrice
			
			HSSFSheet certQouta_sheet = workbook.getSheet("certQuota");	
			HSSFSheet powerDemand_sheet = workbook.getSheet("powerDemand");
			HSSFSheet powerPrice_sheet = workbook.getSheet("powerPrice");
			
			for(int j = 0; j < regionsnumber; j++){
				double[] tempqouta = new double[ticks];
				double[] tempdemand = new double[ticks];
				double[] temppowerprice = new double[ticks];
				
				for(int i = 0; i < ticks; i++){
					tempqouta[i] = certQouta_sheet.getRow(2+i).getCell(2+j).getNumericCellValue();
					tempdemand[i] = powerDemand_sheet.getRow(2+i).getCell(2+j).getNumericCellValue();
					temppowerprice[i] = powerPrice_sheet.getRow(2+i).getCell(2+j).getNumericCellValue();
				}
				// Set market demand
				TheEnvironment.allRegions.get(j).getMyDemand().initMarketDemand(tempdemand, tempqouta);
				// Set MarketSeries (power prices)
				TheEnvironment.allRegions.get(j).getMyPowerPrice().initMarketSeries(temppowerprice);
			}
			
		
			
			
			
		}catch(Exception e) {
	        System.out.println("!! Bang !! xlRead() : " + e );
	    }
	}
		
		public static void ReadPowerPlants() {
			
			//Finds file and starts reading
			String file_path = working_directory + "\\NEMM_input.xls";  
			
			try{      
				HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(file_path));	
				
				// Read number of plants and technologies
				HSSFSheet ctr_sheet = workbook.getSheet("Control");
				plantsnumber = (int) ctr_sheet.getRow(2).getCell(2).getNumericCellValue();
				technologiesnumber = (int) ctr_sheet.getRow(3).getCell(2).getNumericCellValue();
				regionsnumber = (int) ctr_sheet.getRow(4).getCell(2).getNumericCellValue();
				genprofileentries = (int) ctr_sheet.getRow(5).getCell(2).getNumericCellValue();
				ticks = (int) ctr_sheet.getRow(6).getCell(2).getNumericCellValue();			
				
				// Read plant data
				HSSFSheet plant_sheet = workbook.getSheet("PowerPlants");	   
				HSSFSheet production_sheet = workbook.getSheet("Production");	 
				//plants = new PowerPlant[plantsnumber];

				for(int j = 0; j < plantsnumber; j++){
					int newcapacity = (int) plant_sheet.getRow(3+j).getCell(3).getNumericCellValue();
					double newloadfactor = plant_sheet.getRow(3+j).getCell(4).getNumericCellValue();
					int newregion_ID = (int) plant_sheet.getRow(3+j).getCell(6).getNumericCellValue();
					//newregion_ID starts by 1, hence to indexs it we subtract 1.
					PowerPlant pp = new PowerPlant(newcapacity, newloadfactor, TheEnvironment.allRegions.get(newregion_ID-1));
					
					double[] tempproduction = new double[ticks];
					for(int i = 0; i < ticks; i++){
						tempproduction[i] = production_sheet.getRow(2+i).getCell(2+j).getNumericCellValue();
					}
					//Add production in form of tickarray
					pp.setAllProduction(tempproduction);
					TheEnvironment.allPowerPlants.add(pp);
				}
				
			}catch(Exception e) {
		        System.out.println("!! Bang !! xlRead() : " + e );
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
					

			}
			*/

	    
	    
	}
}
 

