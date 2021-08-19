package Multiplayer.Sudoku.Net;

/**
 * This class is a small wrapper class designed to handle packets
 * being escalated up from client handlers to the main server thread.
 * Since this class has 0 processing logic it does not need any getters/setters,
 * and the internal values can be public.
 */
public class Message {
    public int clientID;
    public byte[] data;

    /**
     * Constructor for our message.
     * @param clientID Integer ClientID
     * @param data Encoded packet.
     */
    public Message(int clientID, byte[] data) {
        this.clientID = clientID;
        this.data = data;
    }
}