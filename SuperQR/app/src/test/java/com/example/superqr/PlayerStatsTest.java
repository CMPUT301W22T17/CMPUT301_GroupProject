package com.example.superqr;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PlayerStatsTest {

    private QRCode codeOne = new QRCode("BFG5DGW54");
    private QRCode codeTwo = new QRCode("45WGD5GFB");

    private PlayerStats mockPlayerStats() {
        return new PlayerStats();
    }

    @Test
    void testAddQrCode() {
        PlayerStats mockStats = mockPlayerStats();

        assertEquals(0, mockStats.getQrCodes().size());

        mockStats.addQrCode(codeOne);
        assertEquals(1, mockStats.getQrCodes().size());
        assertSame(codeOne, mockStats.getQrCodes().get(0));
    }

    @Test
    void testDeleteQrCode() {
        PlayerStats mockStats = mockPlayerStats();
        mockStats.addQrCode(codeOne);
        mockStats.addQrCode(codeTwo);

        mockStats.deleteQRCode(codeTwo);
        assertEquals(1, mockStats.getQrCodes().size());
        assertFalse(mockStats.getQrCodes().contains(codeTwo));
    }

}
