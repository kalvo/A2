package a2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Hashtable;

import a2.Fleet;
import processing.core.PApplet;
import processing.data.JSONArray;
import processing.data.JSONObject;
import de.fhpotsdam.unfolding.*;
import de.fhpotsdam.unfolding.geo.*;
import de.fhpotsdam.unfolding.utils.*;

public class A2 extends PApplet {

	
	JSONObject json;
	UnfoldingMap map;
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Hashtable <String, double[]> t ;
	
	
	double[] bounds = {0,0};
	int counter =  -1;
	Fleet f;
	
	boolean isReady = false;
	
	
	public void setup() {
		
		size(700, 700, P2D);
		background(0);
		//frameRate(10);
		
		
//		List<Integer> I = new ArrayList<>();
//		
//		I.add(20);
//		I.add(30);
		
//		println(Collections.binarySearch(I, 10));
		
		
		//println(-1 * index - 2);
		
		//println(S.get(0));
		
		f = new Fleet(this);

//		json = loadJSONObject("/Users/raul/Documents/Aerogramm/Data/S1.json");
//		DataParser.Update_Fleet_with_FlightRadar24JSON(json,f);
//		json = loadJSONObject("/Users/raul/Documents/Aerogramm/Data/S2.json");
//		DataParser.Update_Fleet_with_FlightRadar24JSON(json,f);
//		json = loadJSONObject("/Users/raul/Documents/Aerogramm/Data/S3.json");
//		DataParser.Update_Fleet_with_FlightRadar24JSON(json,f);
//		json = loadJSONObject("/Users/raul/Documents/Aerogramm/Data/S4.json");
//		DataParser.Update_Fleet_with_FlightRadar24JSON(json,f);
//		json = loadJSONObject("/Users/raul/Documents/Aerogramm/Data/S5.json");
//		DataParser.Update_Fleet_with_FlightRadar24JSON(json,f);
		
		json = loadJSONObject("/Users/raul/Documents/Aerogramm/Data/20140719_083619.json");
		DataParser.Update_Fleet_with_FlightRadar24JSON(json,f);

		json = loadJSONObject("/Users/raul/Documents/Aerogramm/Data/20140719_083635.json");
		DataParser.Update_Fleet_with_FlightRadar24JSON(json,f);
		
		json = loadJSONObject("/Users/raul/Documents/Aerogramm/Data/20140719_083651.json");
		DataParser.Update_Fleet_with_FlightRadar24JSON(json,f);
		
		json = loadJSONObject("/Users/raul/Documents/Aerogramm/Data/20140719_083928.json");
		DataParser.Update_Fleet_with_FlightRadar24JSON(json,f);
		
		json = loadJSONObject("/Users/raul/Documents/Aerogramm/Data/20140719_083928.json");
		DataParser.Update_Fleet_with_FlightRadar24JSON(json,f);
		
		json = loadJSONObject("/Users/raul/Documents/Aerogramm/Data/20140719_085119.json");
		DataParser.Update_Fleet_with_FlightRadar24JSON(json,f);
		
		
		
		println(f.length());

		bounds = f.getBounds();
		counter =  (int) bounds[0];
		
//		map = new UnfoldingMap(this);
//		map.zoomAndPanTo(new Location(50f, 7f), 8);
//		MapUtils.createDefaultEventDispatcher(this, map);
		
		//noStroke();
		
		smooth();
		stroke(255, 255);
		strokeWeight(1);
		
		isReady = true;
		
		
	}

	public void draw() {
		
		//map.draw();
		
		background(0);
		
		
		if (!isReady) return;
		
		counter++;
		
		drawRawTail();
		//drawTail();
		//drawPoints();
		
	}
	
	public void drawPoints(){

		if (counter < bounds[1]){
			
			t = f.collectActiveVectors(counter);
		
			for(double[] vector : t.values()){
				
				Location l = new Location( vector[0], vector[1] );
				ScreenPosition sp = map.getScreenPosition(l);
				
				fill(255);
				ellipse(sp.x, sp.y, 2, 2);
				
			}
			
		}
		
		
		
	}
	
	public void drawTail(){
		
		double[] current_vector;
		double[] next_vector;
		
		if (counter >= bounds[1]) return;
		
		//println(counter);
		
		Hashtable<String, List<double[]>> Tails =  f.collectAcriveTails(counter-200, counter);	
		
		for (List<double[]> tail : Tails.values() ){
			
			for (int i = 0; i < tail.size()-1; i++){
				
				current_vector = tail.get(i);
				next_vector =  tail.get(i+1);
				
				Location current_loc = new Location( current_vector[0], current_vector[1] );
				Location next_loc = new Location( next_vector[0], next_vector[1] );
				
				ScreenPosition current_sp = map.getScreenPosition(current_loc);
				ScreenPosition next_sp = map.getScreenPosition(next_loc);
				
				line(current_sp.x, current_sp.y, next_sp.x, next_sp.y);
				
			}
			
		}
		
	}
	
	

	public void drawRawTail(){
		
		double[] current_vector;
		double[] next_vector;
		
		if (counter >= bounds[1]) return;
		
		//println(counter);
		
		Hashtable<String, List<double[]>> Tails =  f.collectAcriveTails(counter-100, counter);	
		
		int c = 20;
		
		for (List<double[]> tail : Tails.values() ){
			
			for (int i = 0; i < tail.size()-1; i++){
				
				current_vector = tail.get(i);
				next_vector =  tail.get(i+1);
				
				line((float) (current_vector[1]+180) * c, -(float) (current_vector[0]-90)*c, (float) (next_vector[1]+180) * c, -(float)(next_vector[0]-90)*c);
				
			}
			
		}
		
	}
	
	
	static class DataParser{
	
		
		
			
		public static void Update_Fleet_with_FlightRadar24JSON(JSONObject json, Fleet f){
			
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
				
				//print(json_array);
				
				data[0] = json_array.getString(8);  // Equipment
				data[1] = json_array.getString(9);  // Flight
				data[2] = json_array.getString(11); // From
				data[3] = json_array.getString(12); // To
				
				// Update Fleet
				
				f.update(id, stamp, vector, signal, data);
				
			}	
		}
	}
	
	
	
	
	
	public static void main(String _args[]) {
		PApplet.main(new String[] { a2.A2.class.getName() });
	}
}
