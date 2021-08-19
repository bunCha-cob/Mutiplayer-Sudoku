package Multiplayer.Sudoku.Net;

import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Random;

import Multiplayer.Sudoku.Board.GameBoard;
import Multiplayer.Sudoku.Protocol.*;

public class GameServer implements Runnable {

    private int port;

    private int playerCount;
    private int clientCount;
    private int diffLevel;

    private Clients clients;

    public GameServer(int port, int playerCount, int diffLevel) {
        this.port = port;
        this.playerCount = playerCount;
        this.clientCount = 0;
        this.diffLevel = diffLevel;
        this.clients = new Clients();
    }

    public void run() {
        
        ServerSocket ssock = openSocket(this.port);

        //while clientCount < needed clients
        //  accept new connections
        while(clientCount < playerCount) {
            Socket sock = connectNewPlayer(ssock);

            System.out.printf("Player #%d Connected\n", clientCount);

            clients.addClient(clientCount, sock);
            //sendAllMessage(clients.getClientListing());

            clientCount++;
        }

        try {
            ssock.close();
        } catch (Exception ex) {

        }
        
        // connect to database
        ConnectDB database = new ConnectDB();
        boolean gameSuccess = false;

        GameBoard gameboard;
        int chain[];
        int score[];

        while(!gameSuccess) {

            int[] clientTurns = getRandomPlayerTurns(this.playerCount);

            gameboard = new GameBoard(this.diffLevel);

        
            clients.sendAllMessage(new BoardPacket(gameboard.getInitial()));

            // timeStamp when game gets started, this is also the gameID
            Timestamp ts_gameStart = new Timestamp(System.currentTimeMillis());

            // add new game to the database
            database.addGame(ts_gameStart, gameboard.getInitial());
            
            clients.sendAllMessage(new PlayerOrderPacket(clientTurns, this.clients));
            
            int turnCount = 0;

            // number of players who want to start new game
            int wantNewGame = 0;

            // to check whether this player already sent new game request 
            boolean requestNewGame[];
            requestNewGame = new boolean[10];
            for (int i=0; i < playerCount; i++ ) requestNewGame[i] = false;

            // to store how many successive right answers the player has made
            chain = new int[10];
            for (int i=0; i < playerCount; i++) chain[i] = 0;

            // to store the gameScore of each player
            score = new int[10];
            for (int i=0; i < playerCount; i++) score[i]=0;
            int currentPlayer, index_cp;

            while(!gameboard.checkForSuccess() && wantNewGame <= (this.playerCount/2)) {
                index_cp = 0;
                currentPlayer = clientTurns[index_cp];
                while (true) {
                    // Send packet to current player saying it's their turn
                    clients.sendClientMessage(currentPlayer, new StartTurnPacket());
                    turnCount++;
                    
                    //Let our client thread know that it should recieve a packet from it's socket
                    clients.addMessageCount(currentPlayer, 1);

                    Instant startTurn = Instant.now();
                    //Wait for response
                    while(!clients.hasMessage()) {}
                    Instant endTurn = Instant.now();
                    Duration duration = Duration.between(startTurn, endTurn);

                    Message message = clients.getOutput();
                    //Double check message to make sure it's from the right client

                    if(message.clientID != currentPlayer)
                        throw new RuntimeException("Something is wrong with client #" + currentPlayer);

                    Packet packet = new Packet(message.data);
                                        
                    if(packet.getType() != PacketTypes.MOVE)
                        throw new RuntimeException("Something is wrong with client #" + currentPlayer);

                    MovePacket mpacket = new MovePacket(message.data);
                    //System.out.println(mpacket.getValue()+ " " + mpacket.getRow() + " " + mpacket.getCol());

                    // update score of the current player
                    if (gameboard.getAnswer()[mpacket.getRow()][mpacket.getCol()]==mpacket.getValue()) {
                        // if the player got the right answer
                        chain[currentPlayer]++;
                        score[currentPlayer] += 100*chain[currentPlayer];
                    } else {
                        if (score[currentPlayer] >= 75) score[currentPlayer]-=75;
                        else score[currentPlayer] = 0;
                        chain[currentPlayer] = 0;
                    }
                    
                    //Process move packet into the gameboard
                    gameboard.modifyPlayer(mpacket.getValue(), mpacket.getRow(), mpacket.getCol());

                    // this means that the player wants to start a new game 
                    if (mpacket.getValue() == -2 && !requestNewGame[currentPlayer]) {
                        // confirm this player requested a new game
                        requestNewGame[currentPlayer] = true;
                        wantNewGame++;
                    }

                    if (wantNewGame > this.playerCount/2) break;

                    // if it is a valid move, add into the database
                    if (mpacket.getValue() > 0) {

                        String moveID = ts_gameStart + " no.Turn: " + Integer.toString(turnCount);
                        String userName = this.clients.getClientUsername(currentPlayer);
                        int nano = duration.getNano();
                        long seconds = duration.getSeconds();
                        int correctness;
                        if (gameboard.getAnswer()[mpacket.getRow()][mpacket.getCol()] == mpacket.getValue()) {
                            correctness = 1; 
                        } else correctness = 0;
                        String timeTaken = Long.toString(seconds) + ":" + Integer.toString(nano);

                        database.addTurn(ts_gameStart, turnCount, moveID);
                        database.addMove(moveID, userName, mpacket.getValue(), mpacket.getRow(), mpacket.getCol(), timeTaken, correctness);
                    }

                    // Update all clients with new board state
                    clients.sendAllMessage(new BoardPacket(gameboard.getFullSudokuBoard()));

                    // Check if the board has been completed.
                    if(gameboard.checkForSuccess()) {
                        gameSuccess = true;
                        break;
                    }

                    if (currentPlayer == clientTurns[playerCount-1]) {
                        index_cp = -1;
                    }

                    index_cp++;
                    currentPlayer = clientTurns[index_cp];
                }

                // addScore to database here
                for (int player : clientTurns) {
                    String userName = this.clients.getClientUsername(player);
                    database.addGameScore(userName, score[player]*gameboard.getDiffLevel());
                }
            }
        }

        //The board is sucessfull, finish up with the clients

        clients.sendAllMessage(new EndGamePacket());

        //Time to log the progress of the game.
    }

    private ServerSocket openSocket(int port) {
        try {
            ServerSocket ssock = new ServerSocket(port);
            return ssock;
        } catch (Exception ex) {
            throw new RuntimeException("Can't open server port: "+ this.port, ex);
        }
    }

    private Socket connectNewPlayer(ServerSocket ssock) {
        try {
            return ssock.accept();
        } catch (Exception ex) {
            throw new RuntimeException("Can not connect client");
        }
    }

    //https://stackoverflow.com/questions/1519736/random-shuffling-of-an-array
    private static int[] getRandomPlayerTurns(int playerCount) {
        int[] ar = new int[playerCount];
        for(int i = 0; i < playerCount; i++)
            ar[i] = i;

        // If running on Java 6 or older, use `new Random()` on RHS here
        Random rnd = ThreadLocalRandom.current();
        for (int i = ar.length - 1; i > 0; i--) {
          int index = rnd.nextInt(i + 1);
          // Simple swap
          int a = ar[index];
          ar[index] = ar[i];
          ar[i] = a;
        }

        return ar;
    }
}