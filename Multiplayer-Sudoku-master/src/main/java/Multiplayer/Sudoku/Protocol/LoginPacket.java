package Multiplayer.Sudoku.Protocol;

public class LoginPacket extends Packet {

    private String username;

    public LoginPacket(String username) {
        this(PacketTypes.LOGIN, username);
    }

    public LoginPacket(PacketTypes pt, String username) {
        super(pt);
        this.username = username;
        super.setMessage(createMessage(username));
    }

    public LoginPacket(byte[] encoded_message) {
        super(encoded_message);
        
        byte[] decoded_message = super.getMessage();
        this.setValuesFromMessage(decoded_message);
    }

    public String getUsername() {
        return this.username;
    }

    private byte[] createMessage(String username) {
        return username.getBytes();
    }

    private void setValuesFromMessage(byte[] message) {
        this.username = new String(message);
    }
}