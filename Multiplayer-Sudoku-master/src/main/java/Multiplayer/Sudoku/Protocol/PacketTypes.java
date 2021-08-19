package Multiplayer.Sudoku.Protocol;

public enum PacketTypes {
    //TODO: Needs to be updated to correct ID codes
    INVALID(00), LOGIN(01), DISCONNECT(02), STARTTURN(03), MOVE(04), ENDGAME(05),
    BOARDSTATE(06), GETLOBBY(80), ADDLOBBY(81), DELETELOBBY(82), UPDATELOBBY(83),
    CLIENTLISTING(99);

    private int packetID;

    private PacketTypes(int id){
        this.packetID = id;
    }

    public int getId(){
        return this.packetID;
    }

    public static PacketTypes getType(byte id) {
        for (PacketTypes packetType : PacketTypes.values())
            if(packetType.getId() == (int) id)
                return packetType;
 
        return PacketTypes.INVALID;
    }
}