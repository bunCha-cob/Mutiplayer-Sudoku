package Multiplayer.Sudoku.Net;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import Multiplayer.Sudoku.Protocol.Packet;

import java.net.Socket;

/**
 * This class is used to manage and communicate with all the different Client Handler threads.
 */
public class Clients {
    protected ConcurrentHashMap<Integer, ClientHandler> connectedClients;
    protected LinkedBlockingQueue<Message> output;

    public Clients() {
        this.connectedClients = new ConcurrentHashMap<Integer, ClientHandler>();
        this.output = new LinkedBlockingQueue<Message>();
    }

    /**
     * This function will add a client to the current internal client hashmap.
     * It does this by:
     * - Creating a client handler for the given socket.
     * - Inserting the client handler into the hashmap so it can be communicated with.
     * - Starting the client handler thread.
     */
    public void addClient(int playerNum, Socket sock) {
        ClientHandler client = new ClientHandler(sock, this.output, playerNum);
        connectedClients.put(playerNum, client);

        new Thread(client).start();
    }

    /**
     * Let a particular clientHandler know that it needs to recieve a certain given number
     * of packets.
     */
    public void addMessageCount(int playerNum, int messageNum) {
        this.connectedClients.get(playerNum).addReadInCount(messageNum);
    }

    /**
     * Pass a packet along to a client handler to be send to the client.
     * 
     * @param playerNum
     * @param packet
     */
    public void sendClientMessage(int playerNum, Packet packet) {
        this.connectedClients.get(playerNum).sendMessage(packet);
    }

    /**
     * Pass a packet along to all connected clients.
     * 
     * @param packet
     */
    public void sendAllMessage(Packet packet) {
        System.out.println("Attempting to send all message");
        
        for(int i = 0; i < this.connectedClients.size(); i++)
            sendClientMessage(i, packet);
    }

    /**
     * Return a Particular client username given that clients ID.
     * 
     * @param playerNum A particular clientID
     * @return Client's username
     */
    public String getClientUsername(int playerNum) {
        return this.connectedClients.get(playerNum).getUsernames();
    }

    /**
     * Queries the internal queue to check whether any clients have escalated
     * messages to be processed by the main thread.
     * @return Returns whether or not any clients have a message for the main thread.
     */
    public boolean hasMessage() {
        return this.output.size() > 0;
    }

    /**
     * This is used by the main server thread to retrieve any messages that the clients
     * have escalated.
     * @return Returns the current message on the queue
     */
    public Message getOutput() {
        try {
            return this.output.take();
        } catch (Exception ex) {
            throw new RuntimeException("Message", ex);
        }
    }
}