package com.example.superqr;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {

    private Player mockPlayer() {

        Player mockPlayer = new Player("tempName", "tempNumber", "tempMail");
        mockPlayer.setPlayerLocation(43.643, 79.388);
        return mockPlayer;
    }

    @Test
    void testGenerateID() {

        Player mockPlayer = mockPlayer();
        char[] mockID = mockPlayer.getPlayerID().toCharArray();

        assertNotNull(mockID);
        assertEquals(mockID.length, 10);

        String upperAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerAlphabet = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String alphaNumeric = upperAlphabet + lowerAlphabet + numbers;

        for (int i = 0; i < mockID.length; i++) {
            assertNotEquals(alphaNumeric.indexOf(mockID[i]), -1);
        }

    }

}
