public class Data {
	Double systemArrivalTime; 
	Double nodeArrivalTime; 
	Double systemDepartureTime;

	Data(){
		this.systemArrivalTime = null; 
		this.nodeArrivalTime = null; 
		this.systemDepartureTime = null;  
	}

	public String toString(){ 
		String s = "";
		s+= (" System Arrival Time: " + systemArrivalTime);
		s+= (" Node Arrival Time: " + nodeArrivalTime);
		s+= ("System Departure Time: " + systemDepartureTime);

		return s; 
	}
}
