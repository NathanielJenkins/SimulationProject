import java.util.*; 

public class RouterPriorityNode { 
	public int currentSeq; 
	public PriorityQueue <Packet> lowQueue;
	public PriorityQueue <Packet> highQueue;
	public Packet packetInService;  

	public RouterPriorityNode(int currentSeq){
		this.packetInService = null; 
		this.currentSeq = currentSeq;
		this.lowQueue = new PriorityQueue <Packet>(5, new QueueComparator() ); 
		this.highQueue = new PriorityQueue <Packet>(5, new QueueComparator() ); 

	}

}