package Multiplayer.Sudoku.GUI;

import Multiplayer.Sudoku.Net.NetworkClient;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** The MultiplayerSudoku class manages the overall operation of the Graphical User Interface (GUI)
 * for the application. The starting conditions for the GUI are set and global variables used by each
 * scene (screen) of the GUI are managed from this class. *
 */

public class MultiplayerSudoku extends Application {
	
	/** Global variables that can be accessed by each scene of the applications GUI
	 * through the use of setter/getter functions.
	 */
	private static User user = null;
	public static NetworkClient client;
	
	/** The start() function sets the initial scene of the GUI - what the user sees first
	 * when the application is run.
	 * @param initialStage. This is the initial stage of the GUI, ie the container that 
	 * all scenes are run in. If a scene is run in the stage, it belongs to the application. 
	 */
	@Override
	public void start(Stage initialStage) throws Exception {
		
		Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("login.fxml"));
		Scene startScene = new Scene(root);
		initialStage.setTitle("Multiplayer Sudoku");	
		initialStage.setScene(startScene);		
		initialStage.show();			
	}
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}
	
	/** This is the getter function for the User type variable called user.
	 * @return the current contents of the user variable.
	 */
	public static User getUser() {
		return user;
	}
	
	/** This is the setter function for the User type variable called user.
	 * @param name. This is the username that is input by the user.
	 */
	public static void setUser(String name) {
		user = new User(name);
	}
	
	/** This is the setter function for the NetworkClient type variable called client. 
	 * @param newClient. This is a new NetworkClient object that is set to the client 
	 * variable.
	 */
	public static void setClient(NetworkClient newClient) {
		client = newClient;
	}
	
	/** This function accesses the sendLogin() function that resides in the client object.
	 * This was created as the client object is Private and cannot be accessed directly from
	 * outside of this class. 
	 */
	public static void sendLogin() {
		client.sendLogin();
	}
	
	/**This function accesses the sendLogout() function that resides in the client object.
	 * This was created as the client object is Private and cannot be accessed directly from
	 * outside of this class.
	 */
	public static void sendLogout() {
		client.sendLogout();
	}
	
	/** This is the getter function for the username variable within the NetworkClient object
	 * that is stored in the NetworkClient type variable called client. This is done by calling
	 * the getUsername() function within that object.
	 * @return the username variable in the client object.
	 */
	public static String getUsername() {
		return user.getUsername();
	}
}
