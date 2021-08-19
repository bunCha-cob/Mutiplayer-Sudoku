package Multiplayer.Sudoku.CLI;

import java.util.Arrays;

import Multiplayer.Sudoku.Net.NetworkClient;
import Multiplayer.Sudoku.Protocol.*;


public class Client {
    public static void main(String[] args) {
		try {
			gameLoop();
		} catch (Exception exception) {
			System.err.println("Exception hit");
			System.err.println(exception.getMessage());
		}
	}
	
	public static void gameLoop() throws Exception {
		NetworkClient nc = new NetworkClient("testUser", "localhost", 25678);
		
		//Let the server know we are here
		nc.sendLogin();

		//Wait for a packet that contains the game board
		BoardPacket initialGameBoard = new BoardPacket(nc.recieveData());
		System.out.println(Arrays.deepToString(initialGameBoard.getBoard()));

		PlayerOrderPacket playerOrder = new PlayerOrderPacket(nc.recieveData());
		System.out.println(Arrays.toString(playerOrder.getUsernames()));

		//Wait for a packet that contains the client listing.
		//Packet clientListing = nc.recievePacket();
		//System.out.printf("New packet with ID : %d\n", packet.getId());
		boolean gameEnded = false;

		while(!gameEnded) {
			byte[] data = nc.recieveData();
			switch (new Packet(data).getType()) {
				case STARTTURN:
					System.out.println("We just got a start turn packet");
					break;
				case BOARDSTATE:
					BoardPacket gameBoard = new BoardPacket(data);
					System.out.println("We just got a copy of the BoardState");
					break;
				case ENDGAME:
					gameEnded = true;
					break;
				default:
					break;
			}
		}

		nc.sendLogout();
	}
}