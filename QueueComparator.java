import java.util.*; 
public class QueueComparator implements Comparator <Packet>{
	
	@Override
	public int compare(Packet a, Packet b){
		if (a.sequenceNumber > b.sequenceNumber){
			return 1; 
		} else if (a.sequenceNumber < b.sequenceNumber){
			return -1; 
		} else {
			return 0; 
		}
	}
}	