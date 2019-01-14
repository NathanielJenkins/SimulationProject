import java.util.*; 

public class RouterSingleFIFONode {
	public int currentSeq;
	public Queue <Packet> queue;
	public Packet packetInService;

	public RouterSingleFIFONode(int currentSeq){
		this.packetInService = null; 
		this.currentSeq = currentSeq;
		this.queue = new LinkedList <Packet>(); 

	}

}