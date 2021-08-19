package Multiplayer.Sudoku.Protocol;

public class EndGamePacket extends Packet {
    public EndGamePacket() {
        super(PacketTypes.ENDGAME);
        super.setMessage(new byte[]{});
    }
}