package Multiplayer.Sudoku.Net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

import Multiplayer.Sudoku.Protocol.DisconnectPacket;
import Multiplayer.Sudoku.Protocol.LoginPacket;
import Multiplayer.Sudoku.Protocol.Packet;

/**
 * Internal class used by the GUI to communicate with the Server.
 * Provides a bunch of helper functions as well as connecting to the server
 * when it is instantiated.
 * An example usage can be seen in the CLI/TestClient.java file.
 */
public class NetworkClient {
	private Socket socket;
	private DataInputStream input;
	private DataOutputStream output;
	private int port;
	private String hostname;
	private String userName;
	
	/**
	 * Connects to the given server with the given username and port
	 * @param username
	 */
	public NetworkClient(String username, String hostname, int port) {
		System.out.println("The network client has been started.");
		
		this.userName = username;

		try {
			socket = new Socket(hostname, port);
			this.output = new DataOutputStream(socket.getOutputStream());
			this.input = new DataInputStream(socket.getInputStream());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Given a particular packet, encode it and send that encoded message
	 * across the socket. To send the packet we first send the length of the packet,
	 * then the packet itself.
	 * @param packet Packet to send
	 * @throws IOException Error raised if socket is invalid
	 */
	public void sendPacket(Packet packet) throws IOException {
		byte[] message = packet.getEncoded();
		this.output.write(message.length);
		this.output.write(message);
	}

	/**
	 * BLOCKING. This function will wait for a message to come across the network,
	 * and then return it.
	 * @return byte array containing an encoded packet
	 */
	public byte[] recieveData() throws IOException {
		byte[] input_bytes = new byte[1000];

		this.input.read(input_bytes, 0, this.input.read());

		byte[] message = trim(input_bytes);

		return message;
	}

	/**
     * In order to use our input stream we have to copy bytes into a buffer.
     * When this buffer is then read into base64 decoding, it will cause problems
     * if it contains null bytes. This function returns a new buffer containing
     * only the message from the provided buffer.
     * 
     * @param bytes Given byte array to trim
     * @return Copy of given byte array with no leading null-bytes
     */
	private static byte[] trim(byte[] bytes) {
            int i = bytes.length - 1;
            while (i >= 0 && bytes[i] == 0)
                --i;

            return Arrays.copyOf(bytes, i + 1);
        }
	
	/**
	 * This function will send the login packet across the socket.
	 */
	public void sendLogin() {
		try {
			sendPacket(new LoginPacket(this.userName));
			System.out.println("Login sent to server...");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This function will send the logout packet across the socket.
	 */
	public void sendLogout() {
		try {
			sendPacket(new DisconnectPacket(this.userName));
			System.out.println("Logout sent to server...");
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    /**
	 * This function returns the username of this current network client. 
	 */    
    public String getUserName() {
        return this.userName;
    }
}
