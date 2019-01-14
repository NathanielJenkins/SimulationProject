import java.util.*; 

public class RouterTwoFIFONode { 
	public int currentSeq; 
	public Queue <Packet> lowQueue;
	public Queue <Packet> highQueue;
	public Packet packetInService;  

	public RouterTwoFIFONode(int currentSeq){
		this.packetInService = null; 
		this.currentSeq = currentSeq;
		this.lowQueue = new LinkedList <Packet>(); 
		this.highQueue = new LinkedList <Packet>(); 

	}

}