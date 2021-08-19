/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Multiplayer.Sudoku.GUI;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Set;

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
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 *
 * @author jd
 */

public class lobbyController implements Initializable {
    
    @FXML Button create_lobby;
    @FXML Button delete_lobby;
    @FXML Button join_lobby;
    @FXML Button leave_lobby;
    @FXML Button exit;
    @FXML Canvas canvas;
    @FXML ListView<String> lobbies;
    @FXML ListView<String> playersInLobby;
    
   
    protected HashMap<String, ArrayList<String>> lobbyMap = new HashMap<>();
    ArrayList<String> temp1 = new ArrayList<>();
    ArrayList<String> temp2 = new ArrayList<>();
    
    @Override
    public void initialize (URL arg0, ResourceBundle arg1) {
        
    	temp1.add("one");
        temp1.add("two");
        temp1.add("three");
        temp2.add("four");
        temp2.add("five");
        temp2.add("six");
        
        lobbyMap.put("Sudoku Kings", temp1);
        lobbyMap.put("Sudoku4Ever", temp2);
        lobbyMap.put("Sudoku_Anarchy", new ArrayList<>());
        
        lobbies.setItems(getKeys());
        
    }
    
    @FXML public void handleMouseClick(MouseEvent arg0) {
        String key = lobbies.getSelectionModel().getSelectedItem();
        playersInLobby.setItems(getValues(key));
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
    
    
    public ObservableList<String> getKeys(){
		ArrayList<String> tempList = new ArrayList<>();
		Set<String> keys = lobbyMap.keySet();
        for (String k : keys) {
            tempList.add(k);
        }
        ObservableList<String> data = FXCollections.observableList(tempList);
    	return data;
    }
    
    public ObservableList<String> getValues(String key){
		ArrayList<String> tempList = lobbyMap.get(key);
        ObservableList<String> data = FXCollections.observableList(tempList);
    	return data;
    }

}

