package com.example.superqr;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class QRCodeTest {
    // Writing tests for private methods https://www.youtube.com/watch?v=HjvEkexsrWk
    final static String defaultCode = "0"; // For initializing QRCode for testing private methods
    final static String codeOne = "BFG5DGW54"; // Only duplicates under 9
    final static String codeTwo = "45WGD5GFB"; // Has duplicates of 0 and over 10
    final static String codeOneHash = "8227ad036b504e39fe29393ce170908be2b1ea636554488fa86de5d9d6cd2c32";
    final static String codeTwoHash = "31b4d0092b53fc0aa94af848719351c64cd7addc4bac97b8d257fa33b7360193";

    private QRCode mockQRCode() {
        return new QRCode(codeOne);
    }

    @Test
    void testHashCode() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method hashCode = QRCode.class.getDeclaredMethod("hashCode", String.class);
        hashCode.setAccessible(true);
        QRCode qrCode = new QRCode(defaultCode);
        assertEquals(hashCode.invoke(qrCode, codeOne), mockQRCode().getHash()); // same hash
        assertNotEquals(hashCode.invoke(qrCode, codeTwo), mockQRCode().getHash()); // different hash
    }

    @Test
    void testCalculateScore() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method calculateScore = QRCode.class.getDeclaredMethod("calculateScore", String.class);
        calculateScore.setAccessible(true);
        QRCode qrCode = new QRCode(defaultCode);
        assertEquals(calculateScore.invoke(qrCode, codeOneHash), mockQRCode().getScore()); // same scores
        assertNotEquals(calculateScore.invoke(qrCode, codeTwoHash), mockQRCode().getScore()); // different scores
    }

    @Test
    void testAddComment() {
        QRCode qrCode = mockQRCode();
        String comment = "Hello";

        assertEquals(0, qrCode.getComments().size());

        qrCode.addComment(comment);
        assertEquals(1, qrCode.getComments().size());
        assertTrue(qrCode.getComments().contains(comment));
        assertEquals(qrCode.getComments().get(0), "Hello");
    }
}
