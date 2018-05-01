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
		if(args.length < 4) {
			System.out.println("> Java [Transmit] -> argument(s): <ip> <port> <packets> <size> <repeat (optional)>");
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
		// arg[2] -> packets
		int packets = Integer.valueOf(args[2]);
		// arg[3] -> size (bytes)
		int size = Integer.valueOf(args[3]);
		// number of repeats
		int repeat = 1;
		if (args.length > 4) {
			repeat = Integer.valueOf(args[4]);
		}
		
		// packets sequence number
		int counter = -1;
		
		// create UDP/IP socket
		DatagramSocket datagramSocket = null;
		
		try {
			// create socket
			datagramSocket = new DatagramSocket();
			
			// buffer
			// 65507 is the maximum of UDP packet
			byte[] buffer = new byte[65507];
			DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
			
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
				data = encodeParametersToData(counter, packets, size);
			
				// create the packet
				datagramPacket = new DatagramPacket(data, data.length, ip, port);
			
				//send the message to receiver
				datagramSocket.send(datagramPacket);
				
				//System.out.println("> Java [Transmit] -> sending packet " + counter);
				
				// acknowledgement:
				// receive incoming data before sending the next packet
				datagramSocket.receive(incoming);
			}
			
			
			// send exit packets:
			data = encodeParametersToData(packets, 0, 8);
			datagramPacket = new DatagramPacket(data, data.length, ip, port);

			for(int i = 0; i < 10; ++i) {
				datagramSocket.send(datagramPacket);
				//System.out.println("> Java [Transmit] -> sending EXIT packet " + i);
			}
			
			// shutdown UDP/IP socket:
			datagramSocket.disconnect();
			datagramSocket.close();
			
			// repeat or exit
			
			if (repeat > 1) {
				args[4] = String.valueOf(repeat - 1);
				main(args);
			} else {
				System.exit(0);
			}
			
		} catch (IOException e) {
			System.err.println("IOException " + e);
		}
	}
	
	/**
	 * Encode parameters to data bytes
	 * 
	 * @param counter	current packet index
	 * @param packets	number of total packets
	 * @param size		size of the encoded data
	 * @return
	 */
	private static byte[] encodeParametersToData(int counter, int packets, int size) {
		byte[] data = new byte[size];
		data[0] = (byte) (counter >> 24);
		data[1] = (byte) (counter >> 16);
		data[2] = (byte) (counter >> 8);
		data[3] = (byte) counter;
		data[4] = (byte) (packets >> 24);
		data[5] = (byte) (packets >> 16);
		data[6] = (byte) (packets >> 8);
		data[7] = (byte) packets;
		
		return data;
	}
}
