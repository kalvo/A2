package a2;

import processing.core.*;

import java.util.Collections;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.List;

import com.sun.org.apache.xpath.internal.axes.SelfIteratorNoPredicate;


public class Fleet{
	
	public static final int LINEAR = 0;
	
	private Hashtable<String, Equipment> _fleet;
	
	private int _stampLowerBound;
	private int _stampUpperBound;
	
	private boolean _initialized = false;
	
	processing.core.PApplet parent;
	
	public Fleet(PApplet parent){
		
		this.parent = parent;
		parent.registerMethod("dispose", this);
		
		this._fleet = new Hashtable<>();
		
	}
	
	public void dispose(){
		
		
	}
	
	public int length(){
		
		return this._fleet.size();
		
	}
	
	public double[] getBounds(){
		
		double[] d = {this._stampLowerBound, this._stampUpperBound};
		return d;
		
	}
	
	public void update( String id, int stamp, double[] vector, int[] signal,  String[] static_data ){
		
		if (this._fleet.containsKey(id)){
			
			this._fleet.get(id).update(stamp, vector.clone(), signal.clone());
			
			// statistics:
			
			if (_stampLowerBound > stamp) _stampLowerBound = stamp;
			if (_stampUpperBound < stamp) _stampUpperBound = stamp; 
			
			
		} else {
		
			this._fleet.put(id, new Equipment(id, stamp, vector.clone(), signal.clone(), static_data.clone())); 
		
			// statistics:
			
			if (!_initialized){
				
				_stampLowerBound = stamp;
				_stampUpperBound = stamp;
				_initialized =  true;
				
			} else {
				
				if (_stampLowerBound > stamp) _stampLowerBound = stamp;
				if (_stampUpperBound < stamp) _stampUpperBound = stamp; 
				
			}
				
		}
		
	}
	
	public void resample(int lower_bound, int upper_bound, int steps){
	 /***
	  * 
	  * builds set of vectors at very stamp between lower and upper bound so there is exactly steps.
	  * use different interpolation methods.
	  * 
	  */
		
		
	}
	
	public double[] getVector(String id, int stamp){
		
		if (this._fleet.containsKey(id)){
		
			return this._fleet.get(id).getVector(stamp);
			
		}
		
		double[] r = new double[0];
		
		return r;
		
	}
	
	public Hashtable<String, double[]>  collectActiveVectors(int stamp){
		
		Hashtable<String, double[]> VectorsAtStamp = new Hashtable<String, double[]>();
		
		for (Equipment e : this._fleet.values()){
			
			if (!e.inRange(stamp)) continue;
			VectorsAtStamp.put(e.getId(), e.getVector(stamp));
			
		}
		
		return VectorsAtStamp;
		
	}

	public Hashtable<String, List<double[]>> collectAcriveTails(int stampLower, int stampUpper){
		
		Hashtable<String, List<double[]>> Tails = new Hashtable<String, List<double[]>>();
		
		for (Equipment e: this._fleet.values()){
			
			List<double[]> tail = e.getVectorTail(stampLower, stampUpper);
			if (tail.size() == 0) continue;
			
			Tails.put(e.getId(), tail);
			
		}
		
		return Tails;
		
	}
	
	
	class Equipment{
		
		String[] _data;
		
		// Samples
		
		private String _id;
		List<Integer> _stamps; // sorted list
		Hashtable<Integer, double[]> _vectors; // it dynamically interpolated vector. XYZ coordinate system.
		Hashtable<Integer, int[]> _signals; // it is categorically interpolated vector. On/Off. 
		
		// State
		
		boolean _isDirty;
		boolean _isOrderd;
		
		int _stampLowerBound;
		int _stampUpperBound;
		
		// Tail
		
		List<double[]> _tail_vector;
		List<int[]> _tail_stamps;
		List<int[]> _tail_signals;
		
		int _method;
		
		private Equipment( String id, int stamp, double[] vector,int[] signal, String[] data ){
			
			
			this._id = id;
			this._stamps = new ArrayList<>();
			
			this._vectors = new Hashtable<Integer, double[]>(); 
			this._signals = new Hashtable<Integer, int[]>();
			
			this._data = data;
			this._stamps.add(stamp);

			this._vectors.put(stamp, vector);
			
			this._stampLowerBound = stamp;
			this._stampUpperBound = stamp;
			
			this._isOrderd = true;
			
			this._method = Fleet.LINEAR;
			
			// DEV:
			
			//this._tail_stamps = new ArrayList<int[]>();
			//this._tail_vector = new ArrayList<double[]>();
			//this._tail_signals = new ArrayList<int[]>();
			
			//this._isDirty = true;
			
			
		}
		
		void update( int stamp, double[] vector, int[] signal ){

			if (this._vectors.containsKey(stamp)){
				
				this._vectors.put(stamp, vector);
				this._signals.put(stamp, signal);
				
			} else {
				
				this._stamps.add(stamp);
				this._vectors.put(stamp, vector);
				this._signals.put(stamp, signal);
				
				if (this._stampUpperBound < stamp){
					this._stampUpperBound = stamp;
				}
				
				if (this._stampLowerBound > stamp){
					this._stampLowerBound = stamp;
				}
			}
			
		}
		
		//TODO: swipe (stamp) -> clean data before that stamp.
		
		boolean inRange(int stamp){
			
//			if (this._stampLowerBound != this._stampUpperBound ){
//				
//				PApplet.println( this._id );
//				PApplet.println( this._stampLowerBound, this._stampUpperBound, stamp );
//				
//			}
			
			if (stamp > this._stampLowerBound && stamp < this._stampUpperBound ) {
				
				return true;
				
			} else {
				
				return false;
			}
			
		}
		
		double[] getVector(int stamp){

			if (!this._isOrderd){
				Collections.sort( this._stamps);
			}
			
			int index = Collections.binarySearch(this._stamps, stamp);
			
			if (stamp <= this._stampLowerBound) return this._vectors.get(this._stampLowerBound);
			if (stamp >= this._stampUpperBound) return this._vectors.get(this._stampUpperBound);
			if (index >= 0) return this._vectors.get(stamp);
			
			if (index < 0){
				
				double[] interpolated_vector  =  new double[  this._vectors.get(this._stampLowerBound).length ];
				
				int lower_neighbor_stamp =  this._stamps.get( (-1 * index)-2 );
				int upper_neighbor_stamp =  this._stamps.get( (-1 * index)-1 );
				
				double[] lower_neighbor_vector =  this._vectors.get(lower_neighbor_stamp);
				double[] upper_neigbhor_vector = this._vectors.get(upper_neighbor_stamp);
				
				int vector_length =  lower_neighbor_vector.length;
				
				for (int i = 0 ;  i < vector_length; i++){
					
					interpolated_vector[i] = LinearInterpolation(lower_neighbor_stamp, upper_neighbor_stamp, stamp, lower_neighbor_vector[i], upper_neigbhor_vector[i]);
					
				}
				
				return interpolated_vector;
				
			}
			
			// it should never be reached here
			return new double[0];
			
			
		}
		
		public List<double[]> getVectorTail(int stampLower, int stampUpper){
			
			List<double[]> vectors =  new ArrayList<>();
			
			// find first vector
			// find last vector
			
			if (stampUpper < this._stampLowerBound) return vectors;
			if (stampLower > this._stampUpperBound) return vectors;
			
			
			
			// First vector
			
			int index = 0;
			
			if (stampLower > this._stampLowerBound) index = Collections.binarySearch(this._stamps, stampLower);
			
			if (index >= 0){
				
				vectors.add( this._vectors.get( this._stamps.get(index) ) );
				
			} else {
				
				// interpolationg first stamp.
				
				double[] interpolated_vector  =  new double[  this._vectors.get(this._stampLowerBound).length ];
				
				int lower_neighbor_stamp =  this._stamps.get( (-1 * index)-2 );
				int upper_neighbor_stamp =  this._stamps.get( (-1 * index)-1 );
				
				index =  (-1 * index)-2;
				
				double[] lower_neighbor_vector =  this._vectors.get(lower_neighbor_stamp);
				double[] upper_neigbhor_vector = this._vectors.get(upper_neighbor_stamp);
				
				int vector_length =  lower_neighbor_vector.length;
				
				for (int i = 0 ;  i < vector_length; i++){
					
					interpolated_vector[i] = LinearInterpolation(lower_neighbor_stamp, upper_neighbor_stamp, stampLower, lower_neighbor_vector[i], upper_neigbhor_vector[i]);
					
				}
				
				vectors.add(interpolated_vector);
				
			}
			
			
			
			// Vector between
			
			int current_stamp;
			
			if ((index+1) < this._stamps.size()) {
			
//				PApplet.println(index);
//				PApplet.println(this._stamps.size());
				
				for (int i =  index+1; i < this._stamps.size(); i++){
					
					current_stamp = this._stamps.get(i);
					
					if (current_stamp > stampUpper) break;
					
					vectors.add( this._vectors.get(current_stamp).clone() );
						
				}
			}
			
			
			
			// Last vector.
//			
			if (stampUpper < this._stampUpperBound){

				index = Collections.binarySearch(this._stamps, stampUpper);
				
				if (index >= 0 ) {
					
					vectors.add(this._vectors.get( this._stamps.get(index) ));

				}
				
				if (index < 0) {
				
				double[] interpolated_vector  =  new double[  this._vectors.get(this._stampLowerBound).length ];
				
				
				//PApplet.println(index);
				//PApplet.println((-1 * index)-2 );
				
				int lower_neighbor_stamp =  this._stamps.get( (-1 * index)-2 );
				int upper_neighbor_stamp =  this._stamps.get( (-1 * index)-1 );
				
				double[] lower_neighbor_vector =  this._vectors.get(lower_neighbor_stamp);
				double[] upper_neigbhor_vector = this._vectors.get(upper_neighbor_stamp);
				
				int vector_length =  lower_neighbor_vector.length;
				
				for (int i = 0 ;  i < vector_length; i++){
					
					interpolated_vector[i] = LinearInterpolation(lower_neighbor_stamp, upper_neighbor_stamp, stampUpper, lower_neighbor_vector[i], upper_neigbhor_vector[i]);
					
				}
				
				vectors.add(interpolated_vector);
				}
				
			}
			
			
			return vectors;
			
			
		}
		
		String[] getData(){
			
			return this._data;
			
		}
		
		String getId(){
			
			return this._id;
		}
		
		double LinearInterpolation(int stamp_lower, int stamp_upper, int stamp_current, double lower_value, double upper_value){
			
			double value_diff = upper_value - lower_value;
			
			double stamp_fraction = (stamp_current * 1.0 - stamp_lower * 1.0) / (stamp_upper * 1.0 - stamp_lower * 1.0);
			
			return lower_value + value_diff * stamp_fraction;
			
		}
		
		
	}

}
