package Multiplayer.Sudoku.GUI;

import javax.swing.JOptionPane;

public class User {
	private String username;
	
	public User(String username) {
		this.setUsername(username);
	}
	
	public void setUsername() {
		this.username = JOptionPane.showInputDialog(this, "Please enter a username");
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
