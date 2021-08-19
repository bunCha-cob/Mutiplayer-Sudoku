package Multiplayer.Sudoku.Protocol;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;

public class BoardPacket extends Packet {

    private int[][] game_board;

    public BoardPacket(int[][] game_board) {
        super(PacketTypes.BOARDSTATE);

        this.game_board = game_board;

        super.setMessage(createMessage(this.game_board));
    }

    public BoardPacket(byte[] encoded_message) {
        super(encoded_message);

        byte[] decoded_message = super.getMessage();
        this.setValuesFromMessage(decoded_message);
    }

    public int[][] getBoard() {
        return this.game_board;
    }

    private byte[] createMessage(int[][] game_board) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        return gson.toJson(game_board).getBytes();
    }

    private void setValuesFromMessage(byte[] message) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        this.game_board = gson.fromJson(new String(message), int[][].class);
    }
}