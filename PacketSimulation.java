import java.util.*; 

public  class PacketSimulation { 
	public final static boolean debug = false; 

	//Events
	public final static int sourceDeparture = 0; 
	public final static int routerArrival = 1; 
	public final static int routerDeparture = 2; 
	public final static int destinationArrival = 3; 

	//Nodes
	public final static int sourceNode = 0; 
	public final static int routerNode = 1; 
	public final static int destinationNode = 2; 

	public final static double x = 0; 
	public final static double y = 0.01; 

	public static double meanSourceInterDepartureTime; 

	public static RouterTwoPriorityNode rNode = new RouterTwoPriorityNode (-1); ; 
	//public static RouterTwoFIFONode rNode = new RouterTwoFIFONode (-1); ; 

	public static double clock;
	public static double totalBusy; 
	public static double lastEventTime; 

	public static int 	totalPackets, numberOfDepartures, numberOfDestinationArrivals, numberOfDroppedPackets, numberOfOutOfOrderPackets, 
						numberOfInOrderPackets, numberOfPacketsProcessedImmediately;

	public static int indexDestinationPacketArrival, highestSeqNumber;

	public static int qLMaxSize, qHMaxSize; 

	public static Random stream;

	public static EventList FutureEventList; 

	public static Packet packetArrivals []; 

	public static void main (String args [] ){
		long seed = Long.parseLong(args[0]); 
		stream = new Random(seed);	
		init(); 
		sim(); 
		computeData(); 
	}


	public static void init(){
 
		FutureEventList = new EventList(); 

		totalPackets = 100000	; 
		indexDestinationPacketArrival = 0; 

		clock = 0.0; 
		numberOfDestinationArrivals = 0; 
		numberOfDroppedPackets = 0; 
		numberOfDepartures = 0; 
		numberOfOutOfOrderPackets = 0; 
		numberOfInOrderPackets = 0;
		numberOfPacketsProcessedImmediately = 0;
		highestSeqNumber = 0; 

		meanSourceInterDepartureTime = 1.0/750.0; 

		qLMaxSize = 0; 
		qHMaxSize = 0; 

		packetArrivals = new Packet [totalPackets]; 		

		//schedule the first Departure
		scheduleSourceDeparture(); 

	}

	public static void sim(){
		if (debug){
			System.out.println("----RUNNING PROGRAM IN DEBUG MODE----");
			System.out.println("-------------STARTING TRACE----------");
			System.out.println();
		}


		while (numberOfDroppedPackets + numberOfDestinationArrivals < totalPackets ){

				Event evt = (Event)FutureEventList.getMin(); 
				FutureEventList.dequeue(); 
				clock = evt.get_time(); 

				if(evt.get_type() == sourceDeparture){
					processSourceDeparture(evt.p); 
					
				} else if (evt.get_type() == routerArrival){
					processRouterArrival(evt.p); 

				} else if (evt.get_type() == routerDeparture){
					processRouterDeparture();

				} else if (evt.get_type() == destinationArrival){
					processDestinationArrival(evt.p); 
				}
			}
	}

	//------Processing Events-------
	public static void processSourceDeparture(Packet p){

		if (debug){
			System.out.println("Processing a sourceDeparture: \tPacket " + p.sequenceNumber + "\tclock: " + clock);
		}

		scheduleRouterArrival(p); 

		//schedule the next departure from the source, continue up to total packets
		if (numberOfDepartures < totalPackets)
			scheduleSourceDeparture(); 
	}

	public static void processRouterArrival(Packet p){

		if (debug){
			System.out.println("Processing a routerArrival:    \tPacket " + p.sequenceNumber + "   \tclock: " + clock);
			//System.out.println("Router Current Seq: " + rNode.currentSeq);
		}

		int qSize = 10000;

		//transmit right away
		if (rNode.packetInService == null){
			numberOfPacketsProcessedImmediately++; 
			
			if (debug){
				System.out.println("\tserver was idle: adding packet to service: " + p.sequenceNumber);
			}
			if (p.sequenceNumber > rNode.currentSeq){ //inorder packet
				numberOfInOrderPackets++;
				rNode.currentSeq = p.sequenceNumber;
			} else {
				numberOfOutOfOrderPackets++; 
			}	
			
			rNode.packetInService = p; 
			scheduleRouterDeparture(p); 

		//not idle
		} else {


			if (p.sequenceNumber > rNode.currentSeq){ //inorder packet
				numberOfInOrderPackets++; 

				if (rNode.lowQueue.size() < qSize)	{	

					if (debug){
						System.out.println("\tserver was processing, currentSeq: "+ rNode.currentSeq +" adding packet to L queue: " + p.sequenceNumber);
					}

					rNode.currentSeq = p.sequenceNumber;
					rNode.lowQueue.add(p);

					if (rNode.lowQueue.size() > qLMaxSize)	qLMaxSize = rNode.lowQueue.size(); 

				} else {
					if (debug){
						System.out.println("\tL queue is full, size: " + rNode.lowQueue.size() + " Dropped Packet " + p.sequenceNumber);
					}

					numberOfDroppedPackets++; 
				}

			} else { //out of order packet
				numberOfOutOfOrderPackets++; 

				if (rNode.highQueue.size() < qSize){

					if (debug){
						System.out.println("\tserver was processing, currentSeq: "+ rNode.currentSeq +" adding packet to H queue: " + p.sequenceNumber);
					}

					rNode.highQueue.add(p);	

					if (rNode.highQueue.size() > qHMaxSize)	qHMaxSize = rNode.highQueue.size(); 

				} else {
					if (debug){
						System.out.println("\tH queue is full " + rNode.highQueue.size() + " Dropped Packet " + p.sequenceNumber);
					}

				numberOfDroppedPackets++;
				}
				
			}
		}			
	}

	public static void processRouterDeparture(){
		
		if (debug){
			System.out.println("Processing a RouterDeparture: \tPacket " + rNode.packetInService.sequenceNumber + "   \tclock: " + clock);
		}

		scheduleDestinationArrival(rNode.packetInService);
		rNode.packetInService = null;
		 

		if (rNode.highQueue.peek() != null){
			Packet p = rNode.highQueue.remove();
			rNode.packetInService = p;
			scheduleRouterDeparture(p);

			if (debug){
				System.out.println("\tMoving into service from H: " + p.sequenceNumber);
			}


		} else if (rNode.lowQueue.peek() != null){ 
			Packet p = rNode.lowQueue.remove();
			rNode.packetInService = p; 
			scheduleRouterDeparture(p);

			if (debug){
				System.out.println("\tMoving into service from L: " + p.sequenceNumber);
			}


		} else {

			if (rNode.lowQueue.size() != 0 || rNode.highQueue.size() != 0){
				System.err.println("ERROR: Queues should be empty");
				System.exit(-1);
			}
			if (debug){
				System.out.println("\tNothing in queues, server is idle");
			}
		}
	}

	public static void processDestinationArrival(Packet p){
		numberOfDestinationArrivals++; 
		p.endTime = clock; 
		packetArrivals[indexDestinationPacketArrival++] = p;

		if (debug){
			System.out.println("Processing a destArrival: \tPacket " + p.sequenceNumber + "\tclock: " + clock);
		}


	}




	//-----Scheduling new events--------
	public static void scheduleSourceDeparture(){
		numberOfDepartures++; 
		double interDepartureTime = exponential(stream, meanSourceInterDepartureTime); 
		double transmitTimePerPacket = 1.0/1250.0;
		Packet p = new Packet (sourceNode, routerNode, ++highestSeqNumber, clock + interDepartureTime); 
		Event nextSourceDeparture = new Event (sourceDeparture, (clock + interDepartureTime + transmitTimePerPacket), p); 
		FutureEventList.enqueue ( nextSourceDeparture );
	}

	public static void scheduleRouterArrival(Packet p){
		double delay = normal (stream, x, y); 
		Event nextRouterArrival = new Event (routerArrival, clock+delay, p);
		FutureEventList.enqueue ( nextRouterArrival );  
	}

	public static void scheduleRouterDeparture(Packet p){
 
		double transmitTimePerPacket = 29.0/1250.0; 
 		//double transmitTimePerPacket = 1.0/1250.0; 
 		p.sourceAddress = routerNode;
		p.destinationAddress = destinationNode;
		Event nextRouterDeparture = new Event (routerDeparture, clock+transmitTimePerPacket, p);
		FutureEventList.enqueue ( nextRouterDeparture ); 
	}

	public static void scheduleDestinationArrival(Packet p){
		
		double delay = 0.05; 
		Event nextDestinationArrival = new Event (destinationArrival, clock+delay, p);
		FutureEventList.enqueue (nextDestinationArrival); 	
	}


	public static void computeData(){
		System.out.println();
		System.out.println("---------------Data--------------------");
		System.out.println();
		System.out.println("OCCURENCES OF IMPORTANT EVENTS");
		System.out.println("\tTotal packets sent: \t" + totalPackets);
		System.out.println("\tOut of order router packets: \t" + numberOfOutOfOrderPackets);
		System.out.println("\tIn order router packets: \t" + numberOfInOrderPackets);
		System.out.println("\tNo Queued packets: \t" + numberOfPacketsProcessedImmediately);
		System.out.println("\tDropped packets: \t" + numberOfDroppedPackets);
		System.out.println("\tDestination arrivals: \t" + numberOfDestinationArrivals);
		System.out.println();
		System.out.println("RATIOS OF IMPORTANT EVENTS");
		System.out.println("\tPacket out of order Router rate: \t" + (double)numberOfOutOfOrderPackets/totalPackets);
		System.out.println("\tPacket in router order rate:     \t" + (double)numberOfInOrderPackets/totalPackets);
		System.out.println("\tNo queued packet rated:   \t" + (double)numberOfPacketsProcessedImmediately/totalPackets);
		System.out.println("\tAverage packet loss rate: \t" + (double)numberOfDroppedPackets/totalPackets);
		System.out.println("\tAverage packet success rate: \t" + (double)numberOfDestinationArrivals/totalPackets);
		//cal average packet delay
		double sumTime = 0.0; 
		for (int i=0; i<numberOfDestinationArrivals; i++){
			sumTime+= (packetArrivals[i].endTime - packetArrivals[i].startTime);
		}
		System.out.println("\tAverage packet delay rate: \t" + sumTime / numberOfDestinationArrivals);
		System.out.println();
		System.out.println("ROUTER INFORMATION");

		System.out.println("\tMax L queue: " + qLMaxSize);
		System.out.println("\tMax H queue: " + qHMaxSize);


		System.out.println();
		System.out.println("DESTINATION INFORMATION");
		if(numberOfDestinationArrivals < 20){	
			System.out.println("\tOrder of packets received");
			for (int i=0; i<numberOfDestinationArrivals; i++){
				System.out.println("\tPacket sequence:\t " + packetArrivals[i].sequenceNumber);
			}
		}
		//calculating the number of out of order packets at the desination
		int currentHighestDestinationSequence = -1;
		int numberOfOutOfOrderPacketsDestination = 0;  
		for (int i=0; i<numberOfDestinationArrivals; i++){
			if (packetArrivals[i].sequenceNumber < currentHighestDestinationSequence){
				numberOfOutOfOrderPacketsDestination++; 
			} else {
				currentHighestDestinationSequence = packetArrivals[i].sequenceNumber;
			}
		}
		System.out.println("\tOut of order destination packets: \t" + numberOfOutOfOrderPacketsDestination);
		System.out.println("\tPacket out of order ratio:\t" + (double)numberOfOutOfOrderPacketsDestination / numberOfDestinationArrivals);
	}













//-----------------------Distributions----------------------

	//This method is copied from the sim.java from a1,a2,a3
	public static double SaveNormal;
	public static int  NumNormals = 0;
	public static final double  PI = 3.1415927 ;

	public static double normal(Random rng, double mean, double sigma){
		double ReturnNormal;

		if (NumNormals == 0){
			double r1 = rng.nextDouble();
			double r2 = rng.nextDouble(); 
			ReturnNormal = Math.sqrt(-2*Math.log(r1))*Math.cos(2*PI*r2);
			SaveNormal = Math.sqrt(-2*Math.log(r1))*Math.sin(2*PI*r2);
			NumNormals = 1;
		
		} else {
			NumNormals = 0; 
			ReturnNormal = SaveNormal; 
		}
		if ((ReturnNormal*sigma + mean) < 0) 
			return -1*(ReturnNormal*sigma + mean); 
		return ReturnNormal*sigma + mean;

	}

	public double uniform(Random rng){
		return rng.nextDouble();
	}

	public static double exponential(Random rng, double mean) {
 		return -mean*Math.log( rng.nextDouble() );
	}

}