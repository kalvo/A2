package a2;

import processing.core.*;

import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.List;



// Smooth library is currently developed as a side of A2 visualization module.

/***
 * 
 * @author raul kalvo
 *
 *Smooth is library to make dynamic visualization easer
 *
 *
 */

public class Smooth {

	processing.core.PApplet parent;
	
	public Smooth(PApplet parent){
		
		this.parent = parent;
		parent.registerMethod("dispose", this);
		
	}
	
	public void dispose(){
		
		
	}

	public class Fleet{
		
		public static final int LINEAR = 0;
		
		int _vector_size;
		int _stamp_lbound;  // stamp can be any sequential number but it is recommended to use second from epoch   
		int _stamp_ubound;
		
		double _sample_life;
		double _time_out ;
		
		Hashtable<String, Vehicle> _fleet;
		
		public Fleet(){
			
			this._fleet = new Hashtable<>();
			
			// Settings
			
			this._vector_size = -1;
			this._stamp_lbound = -1;
			this._stamp_ubound = -1;
				
			this._sample_life = 10;
			this._time_out = 10;
			 
			 
		}
		
		public void update( String id, int stamp, double[] vector, int[] signal,  String[] static_data ){
			
			// update is only adding data.
			
			if (this._fleet.containsKey(id)){
				
				this._fleet.get(id).update(stamp, vector, signal);
				
			} else {
			
				this._fleet.put(id, new Vehicle(stamp, vector, signal, static_data)); 
				
			}
			
		}
		
		public void build(){
			
		}
		
	}	
	
	class Vehicle{
		
		String[] _data;
		
		// Samples
		
		List<Integer> _stamps;
		Hashtable<Integer, double[]> _vector; // it dynamically interpolated vector. XYZ coordinate system.
		Hashtable<Integer, int[]> _signal; // it is categorically interpolated vector. On/Off. 
		
		// Tail
		
		List<double[]> _tail_vector;
		List<int[]> _tail_stamps;
		List<int[]> _tail_signals;
		
		// State
		
		boolean _isDirty;
		boolean _isOrderd;
		
		int _stamp_lower;
		int _stemp_upper;
		
		// Parameters
		
		int _method;
		
		private Vehicle( int stamp, double[] vector,int[] signal, String[] data ){
			
			this._stamps = new ArrayList<>();
			
			this._vector = new Hashtable<Integer, double[]>(); 
			this._signal = new Hashtable<Integer, int[]>();
			
			this._data = data;
			this._vector.put(stamp, vector);
			this._stamps.add(stamp);
			
			this._tail_stamps = new ArrayList<int[]>();
			this._tail_vector = new ArrayList<double[]>();
			this._tail_signals = new ArrayList<int[]>();
			
			
			this._method = Fleet.LINEAR;
			
			this._isDirty = true;
			this._isOrderd = false;
			
			this._stamp_lower = stamp;
			this._stemp_upper = stamp;
			
		}
		
		void update( int stamp, double[] vector, int[] signal ){

			if (this._vector.containsKey(stamp)){
				
				this._vector.put(stamp, vector);
				this._signal.put(stamp, signal);
				
			} else {
				
				this._stamps.add(stamp);
				this._vector.put(stamp, vector);
				this._signal.put(stamp, signal);
				
				if (this._stemp_upper < stamp){
					this._stemp_upper = stamp;
				}
				
				if (this._stamp_lower > stamp){
					this._stamp_lower = stamp;
				}
			}
			
			this._isDirty = false;
			
		}
		
		void build(int stamp_head, int stamp_tail, int steps){

			if (this._isOrderd){
				Collections.sort( this._stamps);
			}
			
			if (this._method == Fleet.LINEAR){
				
				this._tail_vector.clear();
				this._tail_stamps.clear();
				this._tail_signals.clear();
				
				
				
				
				
				
				
				
				
			}
			
			
			this._isDirty = true;
			
		}
		
		
		
		
		String[] getData(){
			
			return this._data;
			
		}
			
		
	}
	
		
}


