package Multiplayer.Sudoku.Protocol;

import java.util.Base64;
import java.util.Arrays;

public class Packet {
    
    private byte proto_id;
    private byte[] message;

    public Packet(PacketTypes packet_type) {
        this.proto_id = (byte) packet_type.getId();
    }

    public Packet(byte[] encoded_packet) {
        byte[] decoded_packet = Base64.getDecoder().decode(encoded_packet);

        this.proto_id = decoded_packet[0];
        this.message = Arrays.copyOfRange(decoded_packet, 1, decoded_packet.length);
    }

    public byte[] getEncoded() {
        byte[] data_to_send = new byte[1 + message.length];

        data_to_send[0] = proto_id;

        for (int i = 0; i < message.length; i++)
            data_to_send[1+i] = message[i];

        return Base64.getEncoder().encode(data_to_send);
    }

    public byte[] getMessage() {
        return this.message;
    }

    public void setMessage(byte[] message) {
        this.message = message;
    }

    public static PacketTypes getType(byte[] encoded_packet) {
        byte[] decoded_packet = Base64.getDecoder().decode(encoded_packet);
        return PacketTypes.getType(decoded_packet[0]);
    }

    public PacketTypes getType() {
        return PacketTypes.getType(this.proto_id);
    }

    public int getId() {
        return this.proto_id;
    }
}