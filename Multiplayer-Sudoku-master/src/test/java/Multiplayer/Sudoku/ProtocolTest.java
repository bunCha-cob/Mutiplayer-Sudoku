/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package Multiplayer.Sudoku;

import org.junit.Test;
import static org.junit.Assert.*;
import Multiplayer.Sudoku.Protocol.*;

public class ProtocolTest {
    @Test public void testLoginAndDisconnect() {
        String username = "username";

        LoginPacket lp = new LoginPacket(username);
        DisconnectPacket dp = new DisconnectPacket(username);

        assertEquals(lp.getUsername(), dp.getUsername());
        assertNotEquals(lp.getId(), dp.getId());
    }
}