/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Multiplayer.Sudoku.GUI;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import static Multiplayer.Sudoku.GUI.MultiplayerSudoku.client;

import Multiplayer.Sudoku.Net.GameServer;
import Multiplayer.Sudoku.Protocol.BoardPacket;
import Multiplayer.Sudoku.Protocol.MovePacket;
import Multiplayer.Sudoku.Protocol.Packet;
import Multiplayer.Sudoku.Protocol.PlayerOrderPacket;
import java.util.Arrays;
import javafx.application.Platform;
import javafx.scene.control.ToggleGroup;

/**
 *
 * @author namph
 */

public class MPController implements Initializable {
    @FXML Button button_1;
    @FXML Button button_2;
    @FXML Button button_3;
    @FXML Button button_4;
    @FXML Button button_5;
    @FXML Button button_6;
    @FXML Button button_7;
    @FXML Button button_8;
    @FXML Button button_9;
    @FXML Canvas canvas;
    @FXML Button endTurn;
    @FXML ProgressBar progressBar;
    @FXML RadioButton rb1;
    @FXML RadioButton rb2;
    @FXML RadioButton rb3;
    @FXML RadioButton rb4;
    @FXML Button erase;
    @FXML Label playerName;
    @FXML Label whosTurn;
    @FXML Label noti;
    @FXML Button newGame;
    @FXML Label diffLevel;
    
    //the initial game state that player receive when joining the game lobby
    private int[][] initialGame = new int[10][10];
    
    // the game state that player receive each turn
    private int[][] gameState = new int[10][10];
    
    // the game state that refelct change that players make each turn 
    private int[][] player_change = new int[10][10];
    
    // this 3-dimension array controls the legal moves
    private boolean[][][] legal = new boolean[10][10][10];
    
    // indicate whether this player is playing
    boolean playing = false;
    
    // indicate whether this player is requesting a new game
    boolean requestNG = false;
    
    int sel_row, sel_col;

    boolean hintUsing = false;
    private boolean turnEnd;
    PlayerOrderPacket playerOrder;
    final ToggleGroup group = new ToggleGroup();
    int times_receiveBoard = 0;
    
    @Override
    public void initialize (URL arg0, ResourceBundle arg1) {
        
        // initialise location of cell selected by the player
        sel_row = 0;
        sel_col = 0;
        
        enableButtons(false);
        
        for (int i=0; i<9; i++)
            for (int j=0; j<9; j++) gameState[i][j] = 0;
        
        playerName.setText("Hello, " + client.getUserName());
        GraphicsContext context = canvas.getGraphicsContext2D();
        try {
            drawOnCanvas(context);          
        } catch (IOException ex) {
            Logger.getLogger(MPController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        rb1.setToggleGroup(group);
        rb2.setToggleGroup(group);
        rb3.setToggleGroup(group);
        rb4.setToggleGroup(group);
        
        // gameLoop
        Thread thread = new Thread() { 
           
            @Override
            public void run() {
            try {                 
                        
                    gameState = new BoardPacket(client.recieveData()).getBoard();
                    for (int i=0; i<9; i++)
                            for (int j=0; j<9; j++) 
                            {
                                player_change[i][j] = gameState[i][j];
                                initialGame[i][j] = gameState[i][j];
                            }

                    
                    Platform.runLater(
                    () -> {
                        diffLevel.setText(getDiffLevel(gameState));            
                    }
                    );
                              
                    // initialise the legal move board
                    for (int i=0; i<9; i++)
                        for (int j=0; j<9; j++)
                            for (int k=1; k<10; k++) {
                                legal[i][j][k] = true;
                            }
                    
                    setUpLegal();
                                        
                    playerOrder = new PlayerOrderPacket(client.recieveData());
                    System.out.println(Arrays.toString(playerOrder.getUsernames()));
                    
                    redraw(); 
                    Platform.runLater(
                        () -> {
                        rb1.setSelected(true);
                        if (client.getUserName().equals(playerOrder.getUsernames()[0])) {
                            whosTurn.setText("It's your turn");
                        }
                        else whosTurn.setText("It's " + playerOrder.getUsernames()[0] + "'s turn");
                        rb1.setText(playerOrder.getUsernames()[0]);
                        rb2.setText(playerOrder.getUsernames()[1]);
                        rb3.setText(playerOrder.getUsernames()[2]);
                        rb4.setText(playerOrder.getUsernames()[3]);
                        }
                        );
                     
                    times_receiveBoard = 0;

                    //Wait for a packet that contains the client listing.
                    //Packet clientListing = nc.recievePacket();
                    //System.out.printf("New packet with ID : %d\n", packet.getId());
                    
                    boolean gameEnded = false;
                  
                    
                    while(!gameEnded) {
                        byte[] data = client.recieveData();
                        switch (new Packet(data).getType()) {
                            case STARTTURN:
                                System.out.println("We just got a start turn packet");    
                                
                                // the player is allowed to make a move
                                enableButtons(true);
                                playing = true;
                                requestNG = false;
                                
                                // disable illegal move buttons
                                disableIllegal();
                                
                                // the player has not end his turn yet
                                turnEnd = false;
                                double timeTaken = 0;
                                
                                while (!turnEnd && timeTaken < 1 && !requestNG) {
                                    progressBar.setProgress(timeTaken);
                                    Thread.sleep(100);
                                    timeTaken += 0.01;
                                }
                                
                                /* if this player does not make any move in the time period allowed 
                                    or make more than a move in the time period allowed */
                                if (!turnEnd) {
                                    for (int i=0; i<9; i++)
                                        for (int j=0; j<9; j++) player_change[i][j] = gameState[i][j];
                                    
                                    // disable buttons
                                    enableButtons(false);
                                    playing = false;
                                    
                                    // if player request a new game
                                    if (requestNG = true) {
                                        client.sendPacket(new MovePacket(-2,0,0));
                                    } else {
                                        // send a dummy packet that will not change the gameState later
                                        client.sendPacket(new MovePacket(-1,0,0));
                                    }
                                }
                                
                                break;
                            case BOARDSTATE:
                                BoardPacket gameBoard = new BoardPacket(data);
                                System.out.println("We just got a copy of the BoardState");

                                // check if whether it is the whole new board
                                boolean newBoard = false;
                                for (int i=0; i<9; i++)
                                    for (int j=0; j<9; j++)
                                        if (initialGame[i][j]>0 && gameBoard.getBoard()[i][j]==0) {
                                            newBoard = true;
                                        }
                                
                                if (newBoard) {

                                    for (int i=0; i<9; i++)
                                        for (int j=0; j<9; j++) 
                                            {
                                                gameState[i][j] = gameBoard.getBoard()[i][j];
                                            }
                                    
                                    Platform.runLater(
                                    () -> {
                                        diffLevel.setText(getDiffLevel(gameState));            
                                    }
                                    );

                                    for (int i=0; i<9; i++)
                                        for (int j=0; j<9; j++) 
                                            {
                                                player_change[i][j] = gameState[i][j];
                                                initialGame[i][j] = gameState[i][j];
                                            }
                                    
                                    // initialise the legal move board
                                    for (int i=0; i<9; i++)
                                        for (int j=0; j<9; j++)
                                            for (int k=1; k<10; k++) {
                                                legal[i][j][k] = true;
                                            }
                                    
                                    setUpLegal();
                                                        
                                    playerOrder = new PlayerOrderPacket(client.recieveData());
                                    System.out.println(Arrays.toString(playerOrder.getUsernames()));
                                    updateNoti();
                                    redraw(); 
                                    Platform.runLater(
                                        () -> {
                                        rb1.setSelected(true);
                                        if (client.getUserName().equals(playerOrder.getUsernames()[0])) {
                                            whosTurn.setText("It's your turn");
                                        }
                                        else whosTurn.setText("It's " + playerOrder.getUsernames()[0] + "'s turn");
                                        rb1.setText(playerOrder.getUsernames()[0]);
                                        rb2.setText(playerOrder.getUsernames()[1]);
                                        rb3.setText(playerOrder.getUsernames()[2]);
                                        rb4.setText(playerOrder.getUsernames()[3]);
                                        }
                                        );
                                    
                                    times_receiveBoard = 0;  
                                    requestNG = false;      
                                } else {
                                    gameState = gameBoard.getBoard();
                                    for (int i=0; i<9; i++)
                                        for (int j=0; j<9; j++) player_change[i][j] = gameState[i][j];
                                    
                                    updateNoti();
                                    redraw();
                                    
                                    times_receiveBoard++;
                                    Platform.runLater(
                                    () -> {
                                        if (times_receiveBoard % playerOrder.numOfClients()==0){ 
                                            rb1.setSelected(true);
                                            if (client.getUserName().equals(playerOrder.getUsernames()[0])) {
                                                whosTurn.setText("It's your turn");
                                            }
                                            else whosTurn.setText("It's " + playerOrder.getUsernames()[0] + "'s turn");
                                        }
                                        if (times_receiveBoard % playerOrder.numOfClients()==1){ 
                                            rb2.setSelected(true);
                                            if (client.getUserName().equals(playerOrder.getUsernames()[1])) {
                                                whosTurn.setText("It's your turn");
                                            }
                                            else whosTurn.setText("It's " + playerOrder.getUsernames()[1] + "'s turn");
                                        }
                                        if (times_receiveBoard % playerOrder.numOfClients()==2){ 
                                            rb3.setSelected(true);
                                            if (client.getUserName().equals(playerOrder.getUsernames()[2])) {
                                                whosTurn.setText("It's your turn");
                                            }
                                            else whosTurn.setText("It's " + playerOrder.getUsernames()[2] + "'s turn");
                                        }
                                        if (times_receiveBoard % playerOrder.numOfClients()==3){ 
                                            rb4.setSelected(true);
                                            if (client.getUserName().equals(playerOrder.getUsernames()[3])) {
                                                whosTurn.setText("It's your turn");
                                            }
                                            else whosTurn.setText("It's " + playerOrder.getUsernames()[3] + "'s turn");
                                        }
                                    }
                                    );
                                }
              
                                break;
                            case ENDGAME:
                                gameEnded = true;
                                break;
                            default:
                                break;
                        }
                        
                    }                    
            } catch (Exception exception) {
                    System.err.println("Exception hit");
                    System.err.println(exception.getMessage());
            }   
            }     
        };
        
        thread.start();
        
        //client.sendLogout();
    }

    public String getDiffLevel(int[][] gb){
        int emp = 0;
        for (int i=0; i<9; i++)
            for (int j=0; j<9; j++)
                if (gb[i][j]==0) emp++;

        if (emp>=49 && emp<51) return "Easy";
        if (emp>=51 && emp<53) return "Medium";
        if (emp>=53 && emp<=56) return "Hard";
        return null;
    }
    
    public void setUpLegal(){
        for (int i=0; i<9; i++)
            for (int j=0; j<9; j++) {
                
                // row and column
                for (int k=0; k<9; k++) {
                    legal[i][k][initialGame[i][j]] = false;
                    legal[k][j][initialGame[i][j]] = false;
                }
                
                int x = 3*((int) i/3);
                int y = 3*((int) j/3);
                
                // 3x3 cell
                for (int k=0; k<3; k++)
                    for (int l=0; l<3; l++)
                        legal[x+k][y+l][initialGame[i][j]] = false;
            }
    }
    
    public void enableButtons(boolean flag){
        button_1.setDisable(!flag);
        button_2.setDisable(!flag);
        button_3.setDisable(!flag);
        button_4.setDisable(!flag);
        button_5.setDisable(!flag);
        button_6.setDisable(!flag);
        button_7.setDisable(!flag);
        button_8.setDisable(!flag);
        button_9.setDisable(!flag);
        erase.setDisable(!flag);
        newGame.setDisable(!flag);
    }
    
    public void disableIllegal() {
        // disable illegal move buttons
        for (int k=1; k<10; k++) 
            if (!legal[sel_row][sel_col][k]) {
                if (k==1) button_1.setDisable(true);
                if (k==2) button_2.setDisable(true);
                if (k==3) button_3.setDisable(true);
                if (k==4) button_4.setDisable(true);
                if (k==5) button_5.setDisable(true);
                if (k==6) button_6.setDisable(true);
                if (k==7) button_7.setDisable(true);
                if (k==8) button_8.setDisable(true);
                if (k==9) button_9.setDisable(true);
            }
    }
    
    public void drawOnCanvas(GraphicsContext gc) throws IOException {
        
        // clear the context before drawing
        gc.clearRect(0,0,450,450);
        
        for(int row = 0; row<9; row++) {
            for(int col = 0; col<9; col++) {	
                
                // (x,y) coordination of the cell on canvas
		        double position_y = row * 50 + 2;		
		        double position_x = col * 50 + 2;

                        // width and height of squares are the same
		        double width = 46;	

		        // set the fill color to white 
		        gc.setFill(Color.WHITE);

		        // draw a square
		        gc.fillRect(position_x, position_y, width, width);
            }
        }
        
        
        gc.setLineWidth(4);
        
        // draw border of 3x3 blocks
        gc.setStroke(Color.BLACK);
        gc.strokeLine(150, 0, 150, 450);
        gc.strokeLine(300, 0, 300, 450);
        gc.strokeLine(0, 150, 450, 150);
        gc.strokeLine(0, 300, 450, 300);
        
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(5);
        gc.strokeRect(sel_col*50+2,sel_row*50+2,46,46);
        
        // draw the initial board 
        for (int r = 0; r<9; r++){
            for (int c = 0; c<9; c++){
                int y = r*50 + 30;
                int x = c*50 + 20;
                gc.setFill(Color.BLACK);
                gc.setFont(new Font(20));
                if (initialGame[r][c]!=0) gc.fillText(initialGame[r][c] + "",x,y);
            }
        }
        
        // draw the current board 
        for (int r = 0; r<9; r++){
            for (int c = 0; c<9; c++){
                int y = r*50 + 30;
                int x = c*50 + 20;
                gc.setFill(Color.RED);
                gc.setFont(new Font(20));
                if (initialGame[r][c] == 0){
                    if (player_change[r][c]!=0) {
                        gc.fillText(player_change[r][c] + "",x,y);
                    }
                }
            }
        }
       
    }
    
    public void canvasMouseClicked() {
        canvas.setOnMouseClicked((MouseEvent click) -> {
            
            if (playing) enableButtons(true); 
                
            // identify the cell that player clicked
            sel_col = (int)((int)click.getX()/50);
            sel_row = (int)((int)click.getY()/50);
            
            // redraw on the canvas
            try {
                drawOnCanvas(canvas.getGraphicsContext2D());
            } catch (IOException ex) {
                Logger.getLogger(MPController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            // disable illegal move buttons
            disableIllegal();
        });
    }
    
    public void pressErase() {
        // this allow players do erase their own moves or others' moves
        // remind that erase is also counted as a move
        if (initialGame[sel_row][sel_col]==0 && gameState[sel_row][sel_col]==0) 
            player_change[sel_row][sel_col]=0;
        updateNoti();
        redraw();
    }
    
    public void updateNoti(){
        
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (requestNG) {
                    noti.setText("Request for a new game");
                } else {
                    int res = 0;
                    for (int i=0; i<9; i++)
                        for (int j=0; j<9; j++)
                            if (player_change[i][j]==0) res++;
                    
                    noti.setText(moveMade()+" moves made, "+ res + " empty cells left");
                    if (moveMade() > 1) whosTurn.setText("You can only make 1 move");
                }
            }
        });
    }
    
    public int moveMade() {
        int res = 0;
        for (int i=0; i<9; i++)
            for (int j=0; j<9; j++)
                if (gameState[i][j] != player_change[i][j]) res++;
        return res;
    }
   
    public void pressEndTurn() throws IOException {
       
        if (moveMade() == 1 && !requestNG) {
            turnEnd = true;
            
            // identify the move
            int r = 0, c = 0, val = 0;
            for (int i=0; i<9; i++)
                for (int j=0; j<9; j++)
                if (gameState[i][j] != player_change[i][j]) {
                    r = i;
                    c = j;
                    val = player_change[i][j];
                }
            
            gameState[r][c] = player_change[r][c];
            
            //send the move packet
            client.sendPacket(new MovePacket(val, r, c));
            
            // disable buttons
            enableButtons(false);
        } else {
            //notify that the player has made more than 1 move
        }
    }
    
    public void pressNewGame() {
        requestNG = true;
        updateNoti();
    }

    private void press(int value) {
        if (initialGame[sel_row][sel_col] == 0) 
            player_change[sel_row][sel_col] = value;
        updateNoti();
        redraw();
    }
   
    
    public void press1() {
        press(1);
    }
    
    public void press2() {
        press(2);
    }

    public void press3() {
        press(3);
    }

    public void press4() {
        press(4);
    }

    public void press5() {
        press(5);
    }

    public void press6() {
        press(6);
    }

    public void press7() {
        press(7);
    }

    public void press8() {
        press(8);
    }

    public void press9() {
        press(9);
    }

    private void redraw() {
        try {
            drawOnCanvas(canvas.getGraphicsContext2D());
            
        } catch (IOException ex) {
            Logger.getLogger(MPController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML public void exitPress(ActionEvent event) {
    	Parent root = null;
		try {
			root = FXMLLoader.load(getClass().getClassLoader().getResource("startScreen.fxml"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Scene startScene = new Scene(root);
		Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
		stage.setScene(startScene);
		stage.show();
    }
}

