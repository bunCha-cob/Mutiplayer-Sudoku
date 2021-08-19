package Multiplayer.Sudoku.Net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;
import Multiplayer.Sudoku.Protocol.*;

public class ClientHandler implements Runnable {

    private final Socket sock;

    private final LinkedBlockingQueue<Packet> input;
    private final LinkedBlockingQueue<Message> output;

    private final int clientID;

    private OutputStream outputstream;

    private String username;

    private int readInCount;

    /**
     * This is the constructor which is used to create a client handler.
     * Each client handler runs as a thread that communicates with the socket.
     * If necessary, the client handler will escalate messages to the given queue.
     * 
     * @param sock Client socket
     * @param outputMessages Queue where messages will be pushed upwards if necessary
     * @param clientID Unique clientID (integer)
     */
    public ClientHandler(Socket sock, LinkedBlockingQueue<Message> outputMessages, int clientID) {
        this.sock = sock;

        // Take specific input
        this.input = new LinkedBlockingQueue<Packet>();

        // Output to centralized queue
        this.output = outputMessages;

        this.clientID = clientID;

        this.readInCount = 0;

        try {
            this.outputstream = this.sock.getOutputStream();
        } catch (IOException ex) {
            System.err.println("Problem creating output stream for client #" + clientID);
            this.outputstream = null;
        }
        
    }

    /**
     * This will add a message to the internal input queue.
     * This queue will be read by the running thread() and any messages
     * contained within it will be sent to the client.
     * 
     * @param packet Packet to send to the client
     */
    public void sendMessage(Packet packet) {
        try {
            System.out.println(clientID + " told to send a packet");
            this.input.put(packet);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }

    /**
     * This is the main thread for the client handler.
     * It process incoming packets when necessary, and send designated
     * packets to the the client. It will make sure, since reading packets is blocking,
     * that all packets have been sent to the client.
     */
    public void run() {
        
        // The client should be sending us their Username, so handle that first
        try {
            byte[] data = recieveData();
            LoginPacket lpacket = new LoginPacket(data);
            this.username = lpacket.getUsername();
        } catch (Exception ex) {
        }

        // Now the client is ready for the main loop
        while (true) {
            if (this.input.size() > 0) {
                // We have some message from our central thread that we need to forward to the
                // client.
                try {
                    sendPacket(this.input.take());
                } catch (Exception ex) {

                }
            }

            // If we know we need messages from the client, then read them in.
            // Do another check to size to make absoultely sure that we are sending packets
            // before blocking
            // and waiting to recieve them.
            if (readInCount > 0 && this.input.size() == 0) {
                try {
                    byte[] data = this.recieveData();

                    switch (new Packet(data).getType()) {
                        case MOVE:
                            this.output.add(new Message(this.clientID, data));
                            break;
                        default:
                            break;
                    }

                } catch (Exception ex) {

                }
                readInCount--;
            }

            // Have a 10ms sleep to ensure that the server is able to send packets at a
            // reasonable pace.
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This function is used to tell the clientHandler's thread to
     * read in a given number of messages.
     * @param readInCount Number of messages for the thread to read in over the socket.
     */
    public void addReadInCount(int readInCount) {
        this.readInCount += readInCount;
    }

    /**
     * Returns the username provided for this specific socket.
     */
    public String getUsernames() {
        return this.username;
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
     * Recieve the data sent from the client.
     * @return byte array containing the client message
     * @throws IOException Sometimes the socket might have an error (such as being closed)
     */
    private byte[] recieveData() throws IOException {
		InputStream inputstrm  = this.sock.getInputStream();
		byte[] input_bytes = new byte[512];

		inputstrm.read(input_bytes, 0, inputstrm.read());

		byte[] message = trim(input_bytes);

		return message;
    }
    
    /**
     * Send a packet to the client across the socket.
     * When sending the packet we first send the length of the packet,
     * and then send the encoded packet.
     * @param packet Packet to send to the client
     * @throws IOException Sometimes the socket might have an error (such as being closed)
     */
    private void sendPacket(Packet packet) throws IOException {
        outputstream.write(packet.getEncoded().length);
        outputstream.write(packet.getEncoded());
    }
}