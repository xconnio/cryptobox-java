package io.xconn.cryptobox;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static io.xconn.cryptobox.SecretBox.box;
import static io.xconn.cryptobox.SecretBox.boxOpen;

import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class SecretBoxTest {

    private static byte[] key;

    @BeforeAll
    public static void setUp() {
        key = Hex.decode("cd281cb85a967c5fc249b31c1c6503a181841526182d4f6e63c81e4213a45fb7");
    }

    @Test
    public void testEncryptAndDecrypt() {
        byte[] message = "Hello, World!".getBytes();
        byte[] encrypted = box(message, key);
        byte[] decrypted = boxOpen(encrypted, key);
        assertArrayEquals(message, decrypted);
    }

    @Test
    public void testEncryptAndDecryptWithNonce() {
        byte[] nonce = Util.generateRandomBytesArray(Util.NONCE_SIZE);
        byte[] message = "Hello, World!".getBytes();
        byte[] encrypted = box(nonce, message, key);
        byte[] decrypted = boxOpen(encrypted, key);
        assertArrayEquals(message, decrypted);
    }

    @Test
    public void testEncryptAndDecryptWithInvalidMAC() {
        byte[] message = "Hello, World!".getBytes();
        byte[] encrypted = box(message, key);
        encrypted[encrypted.length - 1] ^= 0xFF; // Modify last byte
        assertThrows(IllegalArgumentException.class, () -> boxOpen(encrypted, key));
    }

    @Test
    public void testEncryptAndDecryptWithInvalidNonce() {
        byte[] message = "Hello, World!".getBytes();
        byte[] encrypted = box(message, key);
        encrypted[0] ^= 0xFF; // Modify first byte
        assertThrows(IllegalArgumentException.class, () -> boxOpen(encrypted, key));
    }

    @Test
    public void testEncryptAndDecryptWithModifiedCiphertext() {
        byte[] message = "Hello, World!".getBytes();
        byte[] encrypted = box(message, key);
        encrypted[Util.NONCE_SIZE + 1] ^= 0xFF; // Modify the byte next to nonce
        assertThrows(IllegalArgumentException.class, () -> boxOpen(encrypted, key));
    }

}