package Multiplayer.Sudoku.Net;

import java.sql.*;
import com.google.gson.GsonBuilder;
import com.google.gson.Gson;

public class ConnectDB {

    public ConnectDB() {}

    public void addGame(Timestamp ts, int[][] gameBoard) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mariadb://localhost/", "root", "root");
            String sql = "INSERT INTO sudokudb.game(gameID, initialBoard)"
                    + " VALUES(?,?)";
            
            PreparedStatement pps = connection.prepareStatement(sql);

            // convert gameboard to string
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            String gb = gson.toJson(gameBoard).toString();

            pps.setString(1, ts.toString());
            pps.setString(2, gb);

            pps.execute();

            connection.close();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }

    public void addMove(String moveID, String userName, int val, int row, int col, String timeTaken, int correctness) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mariadb://localhost/", "root", "root");
            
            String sql = "INSERT INTO sudokudb.move (moveID, userName, timeTaken, value, row, col, correctness)"  
                        + " VALUES (?,?,?,?,?,?,?)";            
            PreparedStatement pps = connection.prepareStatement(sql);

            pps.setString(1, moveID);
            pps.setString(2, userName);
            pps.setString(3, timeTaken);
            pps.setInt(4, val);
            pps.setInt(5, row);
            pps.setInt(6, col);
            pps.setInt(7, correctness);         

            pps.execute();

            connection.close();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    };

    public void addTurn(Timestamp ts, int turnCount, String moveID) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mariadb://localhost/", "root", "root");
            String sql = "INSERT INTO sudokudb.turn(gameID, turnCount, moveID)"
                    + " VALUES(?,?,?)";
            
            PreparedStatement pps = connection.prepareStatement(sql);

            pps.setString(1, ts.toString());
            pps.setInt(2, turnCount);
            pps.setString(3, moveID);

            pps.execute();

            connection.close();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    };

    public Boolean check(String userName) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mariadb://localhost/", "root", "root");

            // check if the player is already in the leaderBoard table
            String sql = "SELECT * FROM sudokudb.leaderboard WHERE userName = ?";
            PreparedStatement pps = connection.prepareStatement(sql);

            pps.setString(1, userName);
            ResultSet resultSet = pps.executeQuery();
            while (resultSet.next()) {
                return true;
            }

            connection.close();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return false;
    }

    public int getNumGame(String userName) {
        int numGame=0;
        try {
            Connection connection = DriverManager.getConnection("jdbc:mariadb://localhost/", "root", "root");

            // check if the player is already in the leaderBoard table
            String sql = "SELECT * FROM sudokudb.leaderboard WHERE userName = ?;";
            PreparedStatement pps = connection.prepareStatement(sql);
            pps.setString(1, userName);

            ResultSet rs = pps.executeQuery();
            if (rs.next()) {
                numGame = rs.getInt("numGame");
            }
            connection.close();

            return numGame;

        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return numGame;
    }

    public int getScore(String userName) {
        int score=0;
        try {
            Connection connection = DriverManager.getConnection("jdbc:mariadb://localhost/", "root", "root");

            // check if the player is already in the leaderBoard table
            String sql = "SELECT * FROM sudokudb.leaderboard WHERE userName = ?;";
            PreparedStatement pps = connection.prepareStatement(sql);
            pps.setString(1, userName);

            ResultSet rs = pps.executeQuery();
            if (rs.next()) {
                score = rs.getInt("gameScore");
            }
            connection.close();
            return score;

        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return score;
    }

    public void addGameScore(String userName, int gameScore) {
        try {
            
            if (!check(userName)){
                // if the player does not exist in the leaderboard table

                Connection connection = DriverManager.getConnection("jdbc:mariadb://localhost/", "root", "root");
                String sql = "INSERT INTO sudokudb.leaderboard(userName, gameScore, numGame, averageScore)"
                        + " VALUES(?,?,?,?);";
                
                PreparedStatement pps = connection.prepareStatement(sql);

                pps.setString(1, userName);
                pps.setInt(2, gameScore);
                pps.setInt(3, 1);
                pps.setDouble(4, gameScore);

                pps.execute();
                connection.close();
            } else {

                int old_gameScore = getScore(userName);
                int old_numGame = getNumGame(userName);

                Connection connection = DriverManager.getConnection("jdbc:mariadb://localhost/", "root", "root");
                String sql = "UPDATE sudokudb.leaderboard SET gameScore=? , numGame=?, averageScore=? WHERE userName=?;";
                PreparedStatement pps = connection.prepareStatement(sql);

                pps.setInt(1, gameScore + old_gameScore);
                pps.setInt(2, old_numGame + 1);
                pps.setDouble(3, (gameScore + old_gameScore)/(old_numGame+1) );
                pps.setString(4, userName);
        
                pps.execute();
                connection.close();
            }

        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    };
}