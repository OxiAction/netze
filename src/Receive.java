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
		
		// sockets
		DatagramSocket datagramSocket = null; // UDP
		
		// number of expected packets to be received
		int numOfExpectedPackets = 0;
		
		try {
			// create UDP/IP socket
			datagramSocket = new DatagramSocket(port);
			
			// buffer
			// 65507 is the maximum of UDP packet
			byte[] buffer = new byte[65507];
			DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
			
			System.out.println("> Java [Receive] -> datagramSocket created - listening on port " + port + " for incoming data...");
			
			while (true) {
				// receive incoming data
				datagramSocket.receive(incoming);
				
				// receive data
				byte[] incomingData = incoming.getData();
				// ... convert to byte
				int[] data = decodeDataToParameters(incomingData);
				
				//System.out.println("> Java [Receive] -> data received - value(s): [0] " + data[0] + " [1] " + data[1]);
				
				// transmit
				if(data[1] != 0) {
					if (numOfExpectedPackets == 0) {
						numOfExpectedPackets = data[1];
					}
					
					if (!started) {
						startTime = System.nanoTime();
						started = true;
					}
					
					// acknowledgement:
					// send back the counter of this packet to the transmitter
					buffer = encodeParametersToData(data[0]);
                    DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length, incoming.getAddress(), incoming.getPort());
                    datagramSocket.send(datagramPacket);
					
					++countReceivedPackets;
					
					// increase the count of the packet with that sequence number
					lookupTable[data[0]] = incomingData;
				}
				// end transmit
				else if (countReceivedPackets > 0 && data[1] == 0) {
					
					// evaluate stuff...
					double endTime = (System.nanoTime() - startTime) / 1000000.0;
					int duplicatePackages = countReceivedPackets - data[0];
					int lostPackages = 0;
					
					// write to file
					try (Writer writer = new BufferedWriter(new FileWriter("results" + numOfExpectedPackets + ".txt", true))) {
						for(int i = 0; i < data[0]; i++) {
							if(lookupTable[i] == null) {
								lostPackages++;
							}
						}

						writer.write(endTime + " " + lostPackages + " " + duplicatePackages + "\n");
					}
					
					System.out.println("> Java [Receive] -> finished transmitting - endTime: " + endTime + " lostPackages: " + lostPackages + " countReceivedPackets: " + countReceivedPackets + " duplicatePackages: " + duplicatePackages);
					
					// reset stuff...
					started = false;
					countReceivedPackets = 0;
					lookupTable = new byte[10000][1500];
					numOfExpectedPackets = 0;
				}
			}
			
		} catch (IOException e) {
			System.err.println("IOException " + e);
		}
	}
	
	/**
	 * Decode data bytes to parameters
	 * 
	 * @param b
	 * @return
	 */
	private static int[] decodeDataToParameters(byte[] data) {
		int counter = (data[0] << 24) & 0xff000000 | (data[1] << 16) & 0x00ff0000 | (data[2] << 8) & 0x0000ff00 | (data[3] << 0) & 0x000000ff;
		int packets = (data[4] << 24) & 0xff000000 | (data[5] << 16) & 0x00ff0000 | (data[6] << 8) & 0x0000ff00 | (data[7] << 0) & 0x000000ff;
		
		return new int[] {counter, packets};
	}
	
	/**
	 * Encode parameters to data bytes
	 * 
	 * @param packet
	 * @return
	 */
	private static byte[] encodeParametersToData(int packet) {
		byte[] data = new byte[4];
		data[0] = (byte) (packet >> 24);
		data[1] = (byte) (packet >> 16);
		data[2] = (byte) (packet >> 8);
		data[3] = (byte) packet;
		
		return data;
	}
}
