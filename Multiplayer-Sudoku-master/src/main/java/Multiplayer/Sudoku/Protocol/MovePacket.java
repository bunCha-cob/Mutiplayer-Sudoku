package Multiplayer.Sudoku.Protocol;

public class MovePacket extends Packet {
    
    private int value; 
    private int row;
    private int col;

    public MovePacket(int value, int row, int col) {
        super(PacketTypes.MOVE);

        this.value = value;
        this.row = row;
        this.col = col;        

        super.setMessage(createMessage(value, row, col));
    }

    public MovePacket(byte[] encoded_message) {
        super(encoded_message);

        byte[] decoded_message = super.getMessage();
        this.setValuesFromMessage(decoded_message);
    }

    public int getValue() {
        return this.value;
    }

    public int getRow() {
        return this.row;
    }

    public int getCol() {
        return this.col;
    }

    private byte[] createMessage(int value, int row, int col) {
        return new byte[]{
            (byte) value, (byte) row, (byte) col
        };
    }

    private void setValuesFromMessage(byte[] message) {
        this.value = message[0];
        this.row = message[1];
        this.col = message[2];
    }
}