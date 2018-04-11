/**
 * @author Michael Schreiber
 */

import java.io.IOException;
import java.net.*;

public class Transmit {
	
	/**
	 * Entry point
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// check args
        if(args.length != 3) {
            System.out.println("> Java [Transmit] -> argument(s): <ip> <port> <packets>");
            return;
        }
        
        // arg[0] -> internet protocoll
        InetAddress ip;
		try {
			ip = InetAddress.getByName(args[0]);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return;
		}
        // arg[1] -> port
        int port = Integer.valueOf(args[1]);
        // arg[2] -> packaets
        int packets = Integer.valueOf(args[2]);
        
        // int -> byte size
        int size = 8;
        
        // packets sequence number
        int counter = -1;
        
        // socket
        DatagramSocket socket = null;
        
        try {
        	// create socket
        	socket = new DatagramSocket();
            
        	System.out.println("> Java [Transmit] -> socket created - ip: " + ip + " port: " + port);
        	
        	// data format:
        	// [0] = current packet number (counter)
        	// [1] = number of total packets (packets) to be transmitted OR 0 if its an exit packet
        	byte[] data;
        	DatagramPacket datagramPacket;
        	
        	// send normal packets:
        	
			while (counter != packets - 1) {
				++counter;
				
				// set data
				data = utilIntToByte(counter, packets, size);
			
			    // create the packet
			    datagramPacket = new DatagramPacket(data, data.length, ip, port);
			
			    //send the message to receiver
			    socket.send(datagramPacket);
			    
			    System.out.println("> Java [Transmit] -> sending packet " + counter);
			}
            
            // send exit packets:
			
            data = utilIntToByte(packets, 0, 8);
            datagramPacket = new DatagramPacket(data, data.length, ip, port);

            for(int i = 0; i < 10; ++i) {
            	socket.send(datagramPacket);
            	System.out.println("> Java [Transmit] -> sending EXIT packet " + i);
            }
	        
	        // shutdown:
            
            socket.close();
            System.exit(0);
            
        } catch (IOException e) {
            System.err.println("IOException " + e);
        }
	}
	
	/**
	 * Convert integer to bytes
	 * 
	 * @param packet
	 * @param packets
	 * @param size
	 * @return
	 */
	private static byte[] utilIntToByte(int packet, int packets, int size) {
		byte[] result = new byte[size];
		
		result[0] = (byte) (packet >> 24);
		result[1] = (byte) (packet >> 16);
		result[2] = (byte) (packet >> 8);
		result[3] = (byte) packet;
		result[4] = (byte) (packets >> 24);
		result[5] = (byte) (packets >> 16);
		result[6] = (byte) (packets >> 8);
		result[7] = (byte) packets;
		
		return result;
	}
}
