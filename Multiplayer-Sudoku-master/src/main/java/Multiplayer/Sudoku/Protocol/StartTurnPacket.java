package Multiplayer.Sudoku.Protocol;

public class StartTurnPacket extends Packet {
    public StartTurnPacket() {
        super(PacketTypes.STARTTURN);
        super.setMessage(new byte[]{});
    }
}