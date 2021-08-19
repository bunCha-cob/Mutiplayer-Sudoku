package Multiplayer.Sudoku.Protocol;

public class ClientListingPacket extends Packet {

    public ClientListingPacket() {
        super(PacketTypes.CLIENTLISTING);
    }

    //private Clients[];

    // public ClientListingPacket(Client[] clients) {
    //     super(PacketTypes.LOGIN);

    //     this.username = username;

    //     super.setMessage(createMessage(username));
    // }

    // public ClientListingPacket(byte[] encoded_message) {
    //     super(encoded_message);
        
    //     byte[] decoded_message = super.getMessage();
    //     this.setValuesFromMessage(decoded_message);
    // }

    // public Client[] getClients() {
    //     return this.username;
    // }

    // private byte[] createMessage(Client[] clients) {
    //     return clients.getEncoded();
    // }

    // private void setValuesFromMessage(byte[] message) {
    //     this.username = new String(message);
    // }
}