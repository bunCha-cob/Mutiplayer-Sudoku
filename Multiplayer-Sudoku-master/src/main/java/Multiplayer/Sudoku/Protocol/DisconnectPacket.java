package Multiplayer.Sudoku.Protocol;

public class DisconnectPacket extends LoginPacket {

    public DisconnectPacket(String username) {
        super(PacketTypes.DISCONNECT, username);
    }

    public DisconnectPacket(byte[] encoded_message) {
        super(encoded_message);
    }
}