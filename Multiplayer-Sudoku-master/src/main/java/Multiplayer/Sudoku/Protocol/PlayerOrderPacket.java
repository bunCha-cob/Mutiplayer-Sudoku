package Multiplayer.Sudoku.Protocol;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;

import Multiplayer.Sudoku.Net.Clients;

public class PlayerOrderPacket extends Packet {
    private ClientInfo[] clientInfo;
    private int[] clientTurns;
    private String[] usernames;

    public PlayerOrderPacket(int[] clientTurns, Clients clients) {
        super(PacketTypes.CLIENTLISTING);

        super.setMessage(createMessage(clientTurns, clients));
    }

    public PlayerOrderPacket(byte[] encoded_message) {
        super(encoded_message);

        byte[] decoded_message = super.getMessage();
        this.setValuesFromMessage(decoded_message);
    }

    public int numOfClients() {
        return clientTurns.length;
    }

    public int[] getTurns() {
        return this.clientTurns;
    }

    public String[] getUsernames() {
        return this.usernames;
    }

    private byte[] createMessage(int[] clientTurns, Clients clients) {
        ClientInfo[] clientInfo = new ClientInfo[clientTurns.length];

        for(int i = 0; i < clientInfo.length; i++)
            clientInfo[i] = new ClientInfo(clientTurns[i], clients.getClientUsername(clientTurns[i]));

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        return gson.toJson(clientInfo).getBytes();
    }

    private void setValuesFromMessage(byte[] message) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        this.clientInfo = gson.fromJson(new String(message), ClientInfo[].class);

        this.clientTurns = new int[clientInfo.length];
        this.usernames = new String[clientInfo.length];

        for(int i = 0; i < clientInfo.length; i++) {
            this.clientTurns[i] = clientInfo[i].clientTurn;
            this.usernames[i] = clientInfo[i].username;
        }
    }

    private class ClientInfo {
        public int clientTurn;
        public String username;

        public ClientInfo(int clientTurn, String username) {
            this.clientTurn = clientTurn;
            this.username = username;
        }
    }
}