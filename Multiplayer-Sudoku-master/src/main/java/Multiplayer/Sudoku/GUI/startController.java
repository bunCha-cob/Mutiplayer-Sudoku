/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Multiplayer.Sudoku.GUI;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import Multiplayer.Sudoku.Net.NetworkClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

/** This is the scene controller for the start screen of the application where the user
 * can select between game modes. This controller is associated with the start.fxml file.
 */

public class startController implements Initializable {
    
	/** These are the variables from the FXML document start.fxml that are objects in the scene
	 * and are used to process user input into the application. 
	 */
    @FXML Button single_player;
    @FXML Button multi_player;
    @FXML Button exit;
    @FXML Label userLabel;
    @FXML Canvas canvas;
    Label label;
    private User user;
    private String name;
    
    /** The initialise() function gets the user variable from MultiplayerSudoku and accesses the
     * username variable stored in that User object.
     */
    @Override
    public void initialize (URL arg0, ResourceBundle arg1) {
    	user = MultiplayerSudoku.getUser();
    	name = user.getUsername();
    	userLabel.setText(name);
    }
    
    /** The singlePlayerPress() function is used when the single player button is pressed. The
     * single player scene resources are loaded then the scene is started in the GUI so the
     * single player game can be played. 
     * @param event is the press event associated with the single player button.
     */
    @FXML public void singlePlayerPress(ActionEvent event) {
    	Parent spRoot = null;;
		try {
			spRoot = FXMLLoader.load(getClass().getClassLoader().getResource("singlePlayer.fxml"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Scene spScene = new Scene(spRoot);
		Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
		stage.setScene(spScene);
		stage.show();
    }
    
    /** The multiplayerPress() function is used when the multiplayer button is pressed. The
     * multiplayer scene resources are loaded then the scene is started in the GUI so the
     * multiplayer game can be played.
     * 
     * @param event is the press event associated with the multiplayer button.
     */
    @FXML public void multiplayerPress(ActionEvent event) {
    	String[] serverAddress = null;

    	TextInputDialog dialog = new TextInputDialog("");
    	dialog.setTitle("Server Input Dialog");
    	dialog.setHeaderText("Please enter the server address including port.");
    	dialog.setContentText("Server:");
    	// Get the user input value.
		Optional<String> result = dialog.showAndWait();
		
		String host = "localhost";
		int port = 25678;

    	if (result.isPresent()){
			serverAddress = result.get().strip().split(":");

			host = serverAddress[0];

			port = serverAddress.length == 1 ? 25678 : Integer.parseInt(serverAddress[1]);
		}
		
		MultiplayerSudoku.setClient(new NetworkClient(this.name, host, port));
    	
    	MultiplayerSudoku.sendLogin(); 
    	
		Parent glRoot = null;
		try {
			glRoot = FXMLLoader.load(getClass().getClassLoader().getResource("multiPlayer.fxml"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Scene lobby = new Scene(glRoot);
		Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
		stage.setScene(lobby);
		stage.show();

    }

    @FXML public void exitPress(ActionEvent event) {
    	MultiplayerSudoku.sendLogout();
    	Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
    	stage.close();
    }
 
}

