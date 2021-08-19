/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Multiplayer.Sudoku.GUI;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/** This is the scene controller for the login screen of the application.
 * This controller is associated with the login.fxml file.
 */

public class loginController implements Initializable {
	/** These are the variables from the FXML document login.fxml that are objects in the scene
	 * and are used to process user input into the application. 
	 */
    @FXML TextField usernameEntry;
    @FXML TextField passwordEntry;
    @FXML Label userPassLabel;
    @FXML Button login;
    // The label is empty when the application is loaded.
    Label label;
    
    @Override
    public void initialize (URL arg0, ResourceBundle arg1) {
    	
    }
    
    /** The loginPress() function is used when the login button is pressed. A check is run against 
     * the user input to see if it is not NULL, if it is, the label displays a message to the user
     * notifying them that they need to input a valid username.
     * 
     * @param event is the button press event associated with the login button.
     */
    @FXML public void loginPress(ActionEvent event) {
    	
    	String name = usernameEntry.getText();
    	
    	if (name.length() > 0) {
    		
            // The variables in the main GUI controller MultiplayerSudoku are set based on the input 
            // from the user. The network client is run and a login packet is sent to the server so that
            // network communication can occur.
    		MultiplayerSudoku.setUser(name);

        	// The next scene in the GUI is loaded and started.
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
    	else {
    		// Changes the text parameter of the label userPassLabel to display a message to the user.
    		userPassLabel.setText("Enter a valid username.");
    	}
    }
    
    /** The exitPress() function is used when the exit button is pressed. The correct stage parameters 
     * are accessed so that the entire stage for the application can be closed, terminating the application.
     * 
     * @param event
     */
    @FXML public void exitPress(ActionEvent event) {
    	Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
    	stage.close();
    }
 
}

