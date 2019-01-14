
// event representation
class Event implements Comparable {

 public Event(int a_type, double a_time, Packet _p) { 
 	_type = a_type; 
 	time = a_time;
 	p = _p;  

 }
  
 public double time;
 private int _type;
 public Packet p; 
 
 public int get_type() { return _type; }
 public double get_time() { return time; }

 public String toString (){
 	String eventString; 
 	switch (_type){
 		case 0: eventString = "Source Departure";
 			break;
 		case 1: eventString = "Router Arrival";
 			break;
 		case 2: eventString = "Router Departure"; 
 			break;
 		case 3: eventString = "Destination Arrival"; 
 			break; 
 		default: eventString = "Unknown Event";
 			break; 

 	}
 	return eventString; 
 }

 public Event leftlink, rightlink, uplink;

 public int compareTo(Object _cmpEvent ) {
  double _cmp_time = ((Event) _cmpEvent).get_time() ;
  if( this.time < _cmp_time) return -1;
  if( this.time == _cmp_time) return 0;
  return 1;
 }
};
