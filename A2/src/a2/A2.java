package a2;


import processing.core.PApplet;
import processing.data.JSONObject;
import processing.data.JSONArray;

public class A2 extends PApplet {

	JSONObject json;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void setup() {
		
		size(1000, 1000);
		background(0);
		
		
		json = loadJSONObject("/Users/raul/Documents/Aerogramm/Data/20140615_133144.json");
		//DataParser.FlightRadar24JSON(json);
		
		
	}

	public void draw() {
	}
	
	
	
	
	static class DataParser{
	
			
		public static void Update_Fleet_with_FlightRadar24JSON(JSONObject json){
			
			/***
			 * 
			 * Data Mapper
			 * 
			 * JSON ->
			 * 
			 * ID : String
			 * STAMP : int
			 * VECTOR : double[]
			 * DATA : dictionary
			 * 
			 */
			
			/**
			 * 
			 *      # 00 Some code      > "868012"    : String
			        # 01 Latitude       > 55.266      : double
			        # 02 Longitude      > 5.4957      : double
			        # 03 Heading        > 223 	      : double
			        # 04 Altitude       > 35975	      : double	
			        # 05 Code           > 516	      : int
			        # 06 Code           >"3232"	      : String
			        # 07 Radar          > "T-EDWR1"   : Sting
			        # 08 Equip.         > "B77W" 	  : String
			        # 09 Flight         > "JA736J"    : String
			        # 10 Time now       > 1402839086  : int < Unix epoch time 
			        # 11 Airport from   > "NRT"	 	  : String
			        # 12 Airport to     > "LHR"		  : String
			        # 13 Flight         > "JL401"	  : String
			        # 14 ?              > 0			  : int
			        # 15 ?              > 0			  : int
			        # 16 Flight         > "JAL401"	  : String
			        # 17 Arrival time   > 1381674691  : int < Unix epoch time 
			 * 
			 */
			
			String id;
			int stamp;
			double[] vector =  new double[3];
			int[] signal =  new int[0];
			String[] data = new String[4];
			
			JSONArray json_array;
			
			for (Object key : json.keys()){
				
				//println(key);
				
				if (key.equals("full_count")){  continue; }
				if (key.equals("version")){  continue; }
			
				json_array = json.getJSONArray((String) key);
				
				//
				
				id = (String) key;
				
				stamp = json_array.getInt(10);
				
				vector[0] = json_array.getDouble(1); // Latitude in degrees
				vector[1] = json_array.getDouble(2); // Longitude in degrees
				vector[2] = json_array.getDouble(3); // Altitude in feet
				
				data[0] = json_array.getString(8);  // Equipment
				data[1] = json_array.getString(9);  // Flight
				data[2] = json_array.getString(10); // From
				data[3] = json_array.getString(11); // To
				
				// Update Fleet
				
				
			}	
		}
	}
	
	
	
	
	
	public static void main(String _args[]) {
		PApplet.main(new String[] { a2.A2.class.getName() });
	}
}
