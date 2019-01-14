import java.util.*; 

public class RouterSinglePriorityNode {
	public int currentSeq;
	public PriorityQueue <Packet> queue;
	public Packet packetInService;

	public RouterSinglePriorityNode(int currentSeq){
		this.packetInService = null; 
		this.currentSeq = currentSeq;
		this.queue = new PriorityQueue <Packet>(5, new QueueComparator() ); 
	}

}