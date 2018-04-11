/**
 * @author Michael Schreiber
 */

import java.io.*;
import java.net.*;

public class Receive {
	
	/**
	 * Entry point
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// check args
        if(args.length != 1) {
            System.out.println("> Java [Receive] -> argument(s): <port>");
            return;
        }
        
        // arg[0] -> port
        int port = Integer.valueOf(args[0]);
		
        // count received packets
		int countReceivedPackets = 0;
		
		// lookup table for lost / duplicated packets
        byte lookupTable[][] = new byte[10000][1500];
		
		// start time
		long startTime = 0;
		
		// determine if started
		boolean started = false;
		
		// socket
		DatagramSocket socket = null;
		
		try {
			// create socket
			socket = new DatagramSocket(port);
			
			// buffer
            byte[] buffer = new byte[65536];
            DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
            
            System.out.println("> Java [Receive] -> socket created - listening on port " + port + " for incoming data...");
			
            while (true) {
            	// receive incoming data
                socket.receive(incoming);
                
                // receive data
                byte[] incomingData = incoming.getData();
                // ... convert to byte
                int[] data = utilByteToInt(incomingData);
                
                System.out.println("> Java [Receive] -> data received - value(s): [0] " + data[0] + " [1] " + data[1]);
                
                // transmit
                if(data[1] != 0) {
                	if (!started) {
                		startTime = System.nanoTime();
                        started = true;
                	}
                	
                	++countReceivedPackets;
                	
                	//System.out.println("> packet " + countReceivedPackets + " " + incomingData + " " + data);
                	
                	// increase the count of the packet with that sequence number
                    lookupTable[data[0]] = incomingData;
                }
                // end transmit
                else if (countReceivedPackets > 0 && data[1] == 0) {
                	
                	// evaluate stuff...
                	double endTime = (System.nanoTime() - startTime) / 1000000.0;
                	int duplicatePackages = countReceivedPackets - data[0];
                	int lostPackages = 0;
					for(int i = 0; i < data[0]; ++i) {
						if(lookupTable[i] == null) {
							++lostPackages;
						}
					}
                	
					// TODO: write to file?
                	System.out.println("> Java [Receive] -> finished transmitting - endTime: " + endTime + " lostPackages: " + lostPackages + " countReceivedPackets: " + countReceivedPackets + " duplicatePackages: " + duplicatePackages);
					
                	// reset stuff...
                	started = false;
                	countReceivedPackets = 0;
                	lookupTable = new byte[10000][1500];
                }
            }
            
		} catch (IOException e) {
            System.err.println("IOException " + e);
        }
	}
	
	/**
	 * Convert bytes to integers
	 * 
	 * @param b
	 * @return
	 */
	private static int[] utilByteToInt(byte[] b) {
		int packet = (b[0] << 24) & 0xff000000 | (b[1] << 16) & 0x00ff0000 | (b[2] << 8) & 0x0000ff00 | (b[3] << 0) & 0x000000ff;
		int packets = (b[4] << 24) & 0xff000000 | (b[5] << 16) & 0x00ff0000 | (b[6] << 8) & 0x0000ff00 | (b[7] << 0) & 0x000000ff;
	    
		return new int[] {packet, packets};
	}
}
