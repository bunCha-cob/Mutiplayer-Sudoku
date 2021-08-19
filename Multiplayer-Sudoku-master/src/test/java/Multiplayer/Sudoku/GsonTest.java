package Multiplayer.Sudoku;

import org.junit.Test;
import static org.junit.Assert.*;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;

public class GsonTest {
    
//    @Test public void testLobbyCanBeEncodedAndDecoded() {
//        ConnectedClients clients = null;
//        try {
//            clients = new ConnectedClients();
//        } catch(Exception ex) {
//            System.err.println(ex.getMessage());
//        }
//
//        GsonBuilder builder = new GsonBuilder();
//        Gson gson = builder.create();
//
//        String json_result1 = gson.toJson(clients.getLobbies());
//
//        Type gameLobbyType = new TypeToken<CopyOnWriteArrayList<GameLobby>>() {}.getType();
//
//        CopyOnWriteArrayList<GameLobby> result = gson.fromJson(json_result1, gameLobbyType);
//        
//        String json_result2 = gson.toJson(result);
//
//        assertEquals(json_result1, json_result2);
//
//    }
//    
}