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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import Multiplayer.Sudoku.Board.*;
import static Multiplayer.Sudoku.GUI.MultiplayerSudoku.client;
import javafx.application.Platform;

/** This is the scene controller for the single player and multiplayer game screens.
 * This controller is associated with the FXML.fxml and singlePlayer.fxml files.
 */

public class Controller implements Initializable {
	/** These are the variables from the FXML document FXML.fxml that are objects in the scene
	 * and are used to process user input into the application. 
	 */
    @FXML Button button_1;
    @FXML Button button_2;
    @FXML Button button_3;
    @FXML Button button_4;
    @FXML Button button_5;
    @FXML Button button_6;
    @FXML Button button_7;
    @FXML Button button_8;
    @FXML Button button_9;
    @FXML ChoiceBox<String> difficultyChoice; 
    @FXML Canvas canvas;
    @FXML Button hint;
    @FXML Label userName;
    @FXML Label boardState;
   
    ObservableList<String> availableChoices = FXCollections.observableArrayList("Easy", "Medium","Hard");
    
    GameBoard gameboard; 
    int sel_row, sel_col;
    static int diffLevel;
    boolean hintUsing = false;
    int SCORE = 0; //Global score. Everyone will have the same score. Will need to be modified to individual scores for competitive mode
    int CHAIN = 1; //Global chain value. Records chain of correct guesses. Will reset to 1 on an incorrect answer
    
    private int[][] initialGame = new int[10][10];
    
    // this 3-dimension array controls the legal moves
    private boolean[][][] legal = new boolean[10][10][10];
           
    /** The initialize() function sets all the initial values in the scene and the associated variables. The game board is
     * populated based on difficulty settings and all required inforamtion is displayed on the scene to the user. 
     */
    @Override
    public void initialize (URL arg0, ResourceBundle arg1) {
        
        // set default values for the difficulty choice box
        difficultyChoice.setValue("Hard");
        difficultyChoice.setItems(availableChoices);

        String sel_choice = (String) difficultyChoice.getSelectionModel().getSelectedItem();
        if (sel_choice.equals("Easy")) diffLevel=1;
        if (sel_choice.equals("Medium")) diffLevel=2;
        if (sel_choice.equals("Hard")) diffLevel=3;

        userName.setText("Hello, " + MultiplayerSudoku.getUser().getUsername());
        
        gameboard = new GameBoard(diffLevel);
        for (int i=0; i<9; i++)
            for (int j=0; j<9; j++) {
                initialGame[i][j] = gameboard.getInitial()[i][j];
            }
        
        // initialise the legal move board
        for (int i=0; i<9; i++)
            for (int j=0; j<9; j++)
                for (int k=1; k<10; k++) {
                    legal[i][j][k] = true;
                }
                    
        setUpLegal();        
        

        GraphicsContext context = canvas.getGraphicsContext2D();
        
        try {
            drawOnCanvas(context);
        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // initialise location of cell selected by the player
        sel_row = 0;
        sel_col = 0;
        
        // disable illegal move buttons
        disableIllegal();
    }
    
    /** The setupLegal() function creates a background game board based on the current game being played
     * to govern legal and non-legal moves in the game. 
     */
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
    
    /** The drawOnCanvas() function populates the game board in the scene, displaying it to the user.
     * This is called when the game is initialised and when a new game is started.
     * @param gc
     * @throws IOException 
     */
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
        
        // get the randomly generated board and its answer
        int[][] initial = gameboard.getInitial();
        int[][] answer = gameboard.getAnswer();
        
        // get the board that player changed
        int[][] player = gameboard.getPlayer();
        
        // highlight the selected cell 
        if (!hintUsing){ // if hint is NOT being used

            gc.setStroke(Color.BLACK);
            gc.setLineWidth(5);
            gc.strokeRect(sel_col*50+2,sel_row*50+2,46,46);

        } else { // if hint is being used

            // highlight the 1x1 cell with orange color if that is wrong answer
            if (player[sel_row][sel_col]!=answer[sel_row][sel_col]) {
                gc.setStroke(Color.RED);
            } else {
                // highlight the 1x1 cell with green color if that is right answer
                gc.setStroke(Color.GREEN);
            }

            gc.setLineWidth(5);
            gc.strokeRect(sel_col*50+2,sel_row*50+2,46,46);
        }
        
        // draw initial board on the canvas
        for(int row = 0; row<9; row++) {
            for(int col = 0; col<9; col++) {
                // identify the location of the cell on the canvas
		        int position_y = row * 50 + 30;
		        int position_x = col * 50 + 20;

		        // set the fill color to black 
		        gc.setFill(Color.BLACK);

		        // set the font, from a new font, constructed from the system one, with size 20
		        gc.setFont(new Font(20));

		        // check if value of coressponding array position is not 0
		        if(initial[row][col]!=0) {
                    // draw the number
                    gc.fillText(initial[row][col] + "", position_x, position_y);
		        }
            }
	    }
        
        // draw the board that player has changed
        for (int r = 0; r<9; r++){
            for (int c = 0; c<9; c++){
                int y = r*50 + 30;
                int x = c*50 + 20;
                gc.setFill(Color.RED);
                gc.setFont(new Font(20));
                if (player[r][c]!=0) gc.fillText(player[r][c] + "",x,y);
            }
        }
        
        // check the game for success
        if(gameboard.checkForSuccess()) {
            // clear the canvas
            gc.clearRect(0, 0, 450, 450);
            // set the fill color to green
            gc.setFill(Color.GREEN);
            // set the font to 36pt
            gc.setFont(new Font(36));
            // display SUCCESS text on the screen
            gc.fillText("SUCCESS!", 150, 250);
	    }
    }
    
    /** The enableButtons() function enables/disables the buttons on the users screen depending on the value of
     * the flag input variable. This is to managed turn-based gameplay of the system. 
     * @param flag
     */
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
        hint.setDisable(!flag);
    }
    
    /** The canvasMouseClicked() function helps manage gameplay by recording the box on the 
     * Sudoku game board that the user clicks. 
     */
    public void canvasMouseClicked() {
        canvas.setOnMouseClicked((MouseEvent click) -> {
            
            enableButtons(true);
            
            // identify the cell that player clicked
            sel_col = (int)((int)click.getX()/50);
            sel_row = (int)((int)click.getY()/50);
            
            // redraw on the canvas
            try {
                drawOnCanvas(canvas.getGraphicsContext2D());
            } catch (IOException ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            // disable illegal move buttons
            disableIllegal();
        });
    }
    
    /** The disableIllegal() function disables the illegal move buttons. 
     */
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
    
    /** The press() function record what number button the user presses and calculates the number
     * of empty cells remianing in the game. 
     * @param value is the value corresponding to the butoon that was pressed by the user, numbering 1 to 9.
     */
    private void press(int value) {
        gameboard.modifyPlayer(value, sel_row, sel_col);
        redraw();
        Platform.runLater(
        () -> {
            int empty = 0;
            for (int i=0; i<9; i++)
                for (int j=0; j<9; j++)
                    if (gameboard.getFullSudokuBoard()[i][j]==0) empty++;
            if (empty > 10) {
                boardState.setText("There are "+empty+" empty cells to fill in");
            } else {
                boardState.setText("Good work! Only "+empty+" empty cells left");
            }
        }          
        );
    }
    
    /** The press1() functions passes the int 1 to the press() function above, corresponding to the
     * number 1 button being pressed.
     */
    public void press1() {
        press(1);
    }
    
    /** The press2() functions passes the int 2 to the press() function above, corresponding to the
     * number 2 button being pressed.
     */
    public void press2() {
        press(2);
    }
    
    /** The press3() functions passes the int 3 to the press() function above, corresponding to the
     * number 3 button being pressed.
     */
    public void press3() {
        press(3);
    }
    
    /** The press4() functions passes the int 4 to the press() function above, corresponding to the
     * number 4 button being pressed.
     */
    public void press4() {
        press(4);
    }
    
    /** The press5() functions passes the int 5 to the press() function above, corresponding to the
     * number 5 button being pressed.
     */
    public void press5() {
        press(5);
    }
    
    /** The press6() functions passes the int 6 to the press() function above, corresponding to the
     * number 6 button being pressed.
     */
    public void press6() {
        press(6);
    }
    
    /** The press7() functions passes the int 7 to the press() function above, corresponding to the
     * number 7 button being pressed.
     */
    public void press7() {
        press(7);
    }
    
    /** The press8() functions passes the int 8 to the press() function above, corresponding to the
     * number 8 button being pressed.
     */
    public void press8() {
        press(8);
    }
    
    /** The press9() functions passes the int 9 to the press() function above, corresponding to the
     * number 9 button being pressed.
     */
    public void press9() {
        press(9);
    }
    
    /** The pressNewGame() function is used when the new game button is pressed on the GUI scene.
     * Based on the difficulty selection, a new game board is generated and the new game populates
     * the required fields. 
     */
    public void pressNewGame() {
        String sel_choice = (String) difficultyChoice.getSelectionModel().getSelectedItem();
        if (sel_choice.equals("Easy")) diffLevel=1;
        if (sel_choice.equals("Medium")) diffLevel=2;
        if (sel_choice.equals("Hard")) diffLevel=3;

        gameboard = new GameBoard(diffLevel);
        redraw();
    }
    
    /** The pressErase() function is used when the erase button is pressed and it clears the selected
     * box on he Sudoku board.
     */
    public void pressErase() {
        gameboard.modifyPlayer(0, sel_row, sel_col);
        redraw();
    }
    
    /** The pressHint() function is used when the hint button is pressed. The hintUsing variable is 
     * changed to true and the redraw() function is called to provide the hint information to the user. 
     */
    public void pressHint() {
        hintUsing = true;
        redraw();
        hintUsing = false;
    }

    /** The redraw() function calls the drawOnCanvas() function to reload the game board. This is used
     * to continually populate the board during multiplayer games and provide hint information to the user. 
     */
    private void redraw() {
        try {
            drawOnCanvas(canvas.getGraphicsContext2D());
        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /** The exitPress() function is used when the exit button is pressed. It gets the required scene resources
     * that are in the stage for the GUI and reloads the start screen so that another game mode can be selected,
     * or a different multi-player game can be accessed.
     * @param event is the even of pressing the exit button on the scene.
     */
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
	
	
	
	/*
	//Basic scoring functionality to be called when the player clicks the End Turn button.
	public void setSCORE() { //Sets score and chain value as appropriate. To be run at end of each turn.
		if (player[sel_row][sel_col]==answer[sel_row][sel_col]) {
			if (!hintUsing){ // if hint is NOT being used
				this.SCORE += (100*chain);
				this.CHAIN++;
			} else { // if hint is being used
				this.SCORE += (50*chain);
				this.CHAIN++;
			}
		} else {   
			this.SCORE -= 75;
			this.CHAIN = 1; 
		}
	}

	public int getSCORE() {
		return SCORE;
	}

	public int getCHAIN() {
		return CHAIN;
	}
        */
}

