
public class Packet { 

	int sourceAddress; 
	int destinationAddress; 
	int sequenceNumber;
	double startTime; 
	double endTime;  

	public Packet(int sourceAddress, int destinationAddress, int sequenceNumber, double startTime ){
		this.sourceAddress = sourceAddress; 
		this.destinationAddress = destinationAddress; 
		this.sequenceNumber = sequenceNumber;
		this.startTime = startTime;
	}

	public String getDestination (){
		switch (destinationAddress){
			case 0: return "source"; 
			case 1: return "router";
			case 2: return "destination";
		}
		return "ERROR: no destination";
	}

	public String getSource (){
		switch (sourceAddress){
			case 0: return "source";
			case 1: return "router"; 
			case 2: return "destination";
		}
		return "ERROR: no source";
	}
}